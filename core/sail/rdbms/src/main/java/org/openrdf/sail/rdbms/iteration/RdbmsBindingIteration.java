/* 
 * Licensed to Aduna under one or more contributor license agreements.  
 * See the NOTICE.txt file distributed with this work for additional 
 * information regarding copyright ownership. 
 *
 * Aduna licenses this file to you under the terms of the Aduna BSD 
 * License (the "License"); you may not use this file except in compliance 
 * with the License. See the LICENSE.txt file distributed with this work 
 * for the full License.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.openrdf.sail.rdbms.iteration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.evaluation.QueryBindingSet;
import org.openrdf.sail.rdbms.RdbmsValueFactory;
import org.openrdf.sail.rdbms.algebra.ColumnVar;
import org.openrdf.sail.rdbms.exceptions.RdbmsQueryEvaluationException;
import org.openrdf.sail.rdbms.iteration.base.RdbmIterationBase;
import org.openrdf.sail.rdbms.model.RdbmsResource;
import org.openrdf.sail.rdbms.model.RdbmsValue;
import org.openrdf.sail.rdbms.schema.IdSequence;
import org.openrdf.sail.rdbms.schema.ValueTable;

/**
 * Converts a {@link ResultSet} into a {@link BindingSet} in an iteration.
 * 
 * @author James Leigh
 * 
 */
public class RdbmsBindingIteration extends RdbmIterationBase<BindingSet, QueryEvaluationException> {

	private BindingSet bindings;

	private Collection<ColumnVar> projections;

	private RdbmsValueFactory vf;

	private IdSequence ids;

	public RdbmsBindingIteration(PreparedStatement stmt)
		throws SQLException
	{
		super(stmt);
	}

	public void setBindings(BindingSet bindings) {
		this.bindings = bindings;
	}

	public void setProjections(Collection<ColumnVar> proj) {
		this.projections = proj;
	}

	public void setValueFactory(RdbmsValueFactory vf) {
		this.vf = vf;
	}

	public void setIdSequence(IdSequence ids) {
		this.ids = ids;
	}

	@Override
	protected BindingSet convert(ResultSet rs)
		throws SQLException
	{
		QueryBindingSet result = new QueryBindingSet(bindings);
		for (ColumnVar var : projections) {
			String name = var.getName();
			if (var != null && !result.hasBinding(name)) {
				Value value = var.getValue();
				if (value == null) {
					value = createValue(rs, var.getIndex() + 1);
				}
				if (value != null) {
					result.addBinding(var.getName(), value);
				}
			}
		}
		return result;
	}

	@Override
	protected QueryEvaluationException convertSQLException(SQLException e) {
		return new RdbmsQueryEvaluationException(e);
	}

	private RdbmsResource createResource(ResultSet rs, int index)
		throws SQLException
	{
		Number id = ids.idOf(rs.getLong(index));
		if (id.longValue() == ValueTable.NIL_ID)
			return null;
		return vf.getRdbmsResource(id, rs.getString(index + 1));
	}

	private RdbmsValue createValue(ResultSet rs, int index)
		throws SQLException
	{
		Number id = ids.idOf(rs.getLong(index));
		if (ids.isLiteral(id)) {
			String label = rs.getString(index + 1);
			String language = rs.getString(index + 2);
			String datatype = rs.getString(index + 3);
			return vf.getRdbmsLiteral(id, label, language, datatype);
		}
		return createResource(rs, index);
	}

}

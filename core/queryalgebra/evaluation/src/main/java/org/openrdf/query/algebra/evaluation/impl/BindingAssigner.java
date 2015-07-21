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
package org.openrdf.query.algebra.evaluation.impl;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.evaluation.QueryOptimizer;
import org.openrdf.query.algebra.helpers.AbstractQueryModelVisitor;

/**
 * Assigns values to variables based on a supplied set of bindings.
 * 
 * @author Arjohn Kampman
 */
public class BindingAssigner implements QueryOptimizer {

	public void optimize(TupleExpr tupleExpr, Dataset dataset, BindingSet bindings) {
		if (bindings.size() > 0) {
			tupleExpr.visit(new VarVisitor(bindings));
		}
	}

	protected static class VarVisitor extends AbstractQueryModelVisitor<RuntimeException> {

		protected BindingSet bindings;

		public VarVisitor(BindingSet bindings) {
			this.bindings = bindings;
		}

		@Override
		public void meet(Var var) {
			if (!var.hasValue() && bindings.hasBinding(var.getName())) {
				Value value = bindings.getValue(var.getName());
				var.setValue(value);
			}
		}
	}
}

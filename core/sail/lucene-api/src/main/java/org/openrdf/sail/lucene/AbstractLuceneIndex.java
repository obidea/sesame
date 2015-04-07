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
package org.openrdf.sail.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Literal;

public abstract class AbstractLuceneIndex implements SearchIndex {
	private static final List<String> REJECTED_DATATYPES = new ArrayList<String>();

	static {
		REJECTED_DATATYPES.add("http://www.w3.org/2001/XMLSchema#float");
	}

	/**
	 * keep a lit of old monitors that are still iterating but not closed (open
	 * iterators), will be all closed on shutdown items are removed from list by
	 * ReaderMnitor.endReading() when closing
	 */
	protected final Collection<AbstractReaderMonitor> oldmonitors = new LinkedList<AbstractReaderMonitor>();

	protected abstract AbstractReaderMonitor getCurrentMonitor();

	@Override
	public void beginReading()
	{
		getCurrentMonitor().beginReading();
	}

	@Override
	public void endReading() throws IOException
	{
		getCurrentMonitor().endReading();
	}

	public Collection<AbstractReaderMonitor> getOldMonitors()
	{
		return oldmonitors;
	}

	/**
	 * Returns whether the provided literal is accepted by the LuceneIndex to be
	 * indexed. It for instance does not make much since to index xsd:float.
	 * 
	 * @param literal
	 *        the literal to be accepted
	 * @return true if the given literal will be indexed by this LuceneIndex
	 */
	@Override
	public boolean accept(Literal literal) {
		// we reject null literals
		if (literal == null)
			return false;

		// we reject literals that are in the list of rejected data types
		if ((literal.getDatatype() != null)
				&& (REJECTED_DATATYPES.contains(literal.getDatatype().stringValue())))
			return false;

		return true;
	}
}

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
package org.openrdf.query.algebra.evaluation.util;

import static org.junit.Assert.*;
import static org.openrdf.query.algebra.Compare.CompareOp.*;

import org.junit.Before;
import org.junit.Test;

import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.algebra.Compare.CompareOp;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;

/**
 * @author Jeen Broekstra
 */
public class QueryEvaluationUtilTest {

	private ValueFactory f = ValueFactoryImpl.getInstance();

	private Literal arg1simple;

	private Literal arg2simple;

	private Literal arg1en;

	private Literal arg2en;

	private Literal arg1cy;

	private Literal arg2cy;

	private Literal arg1string;

	private Literal arg2string;

	private Literal arg1int;

	private Literal arg2int;

	private Literal arg1year;

	private Literal arg2year;

	@Before
	public void setUp()
		throws Exception
	{
		arg1simple = f.createLiteral("abc");
		arg2simple = f.createLiteral("b");

		arg1en = f.createLiteral("abc", "en");
		arg2en = f.createLiteral("b", "en");

		arg1cy = f.createLiteral("abc", "cy");
		arg2cy = f.createLiteral("b", "cy");

		arg1string = f.createLiteral("abc", XMLSchema.STRING);
		arg2string = f.createLiteral("b", XMLSchema.STRING);

		arg1year = f.createLiteral("2007", XMLSchema.GYEAR);
		arg2year = f.createLiteral("2009", XMLSchema.GYEAR);

		arg1int = f.createLiteral(10);
		arg2int = f.createLiteral(1);
	}

	@Test
	public void testCompatibleArguments()
		throws Exception
	{

		assertTrue(QueryEvaluationUtil.compatibleArguments(arg1simple, arg2simple));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1simple, arg2en));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1simple, arg2cy));
		assertTrue(QueryEvaluationUtil.compatibleArguments(arg1simple, arg2string));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1simple, arg2int));

		assertTrue(QueryEvaluationUtil.compatibleArguments(arg1en, arg2simple));
		assertTrue(QueryEvaluationUtil.compatibleArguments(arg1en, arg2en));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg2en, arg2cy));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1en, arg2cy));
		assertTrue(QueryEvaluationUtil.compatibleArguments(arg1en, arg2string));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1en, arg2int));

		assertTrue(QueryEvaluationUtil.compatibleArguments(arg1cy, arg2simple));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1cy, arg2en));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg2cy, arg2en));
		assertTrue(QueryEvaluationUtil.compatibleArguments(arg1cy, arg2cy));
		assertTrue(QueryEvaluationUtil.compatibleArguments(arg1cy, arg2string));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1cy, arg2int));

		assertTrue(QueryEvaluationUtil.compatibleArguments(arg1string, arg2simple));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1string, arg2en));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1string, arg2cy));
		assertTrue(QueryEvaluationUtil.compatibleArguments(arg1string, arg2string));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1string, arg2int));

		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1int, arg2simple));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1int, arg2en));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1int, arg2cy));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1int, arg2string));
		assertFalse(QueryEvaluationUtil.compatibleArguments(arg1int, arg2int));

	}

	@Test
	public void testCompareEQ()
		throws Exception
	{
		assertCompareFalse(arg1simple, arg2simple, EQ);
		assertCompareFalse(arg1simple, arg2en, EQ);
		assertCompareFalse(arg1simple, arg2cy, EQ);
		assertCompareFalse(arg1simple, arg2string, EQ);
		assertCompareException(arg1simple, arg2int, EQ);
		assertCompareException(arg1simple, arg2year, EQ);

		assertCompareFalse(arg1en, arg2simple, EQ);
		assertCompareFalse(arg1en, arg2en, EQ);
		assertCompareFalse(arg2en, arg2cy, EQ);
		assertCompareFalse(arg1en, arg2cy, EQ);
		assertCompareFalse(arg1en, arg2string, EQ);
		assertCompareFalse(arg1en, arg2int, EQ);

		assertCompareFalse(arg1cy, arg2simple, EQ);
		assertCompareFalse(arg1cy, arg2en, EQ);
		assertCompareFalse(arg2cy, arg2en, EQ);
		assertCompareFalse(arg1cy, arg2cy, EQ);
		assertCompareFalse(arg1cy, arg2string, EQ);
		assertCompareFalse(arg1cy, arg2int, EQ);

		assertCompareFalse(arg1string, arg2simple, EQ);
		assertCompareFalse(arg1string, arg2en, EQ);
		assertCompareFalse(arg1string, arg2cy, EQ);
		assertCompareFalse(arg1string, arg2string, EQ);
		assertCompareException(arg1string, arg2int, EQ);
		assertCompareException(arg1string, arg2year, EQ);

		assertCompareException(arg1int, arg2simple, EQ);
		assertCompareFalse(arg1int, arg2en, EQ);
		assertCompareFalse(arg1int, arg2cy, EQ);
		assertCompareException(arg1int, arg2string, EQ);
		assertCompareFalse(arg1int, arg2int, EQ);
		assertCompareException(arg1int, arg2year, EQ);

	}

	@Test
	public void testCompareNE()
		throws Exception
	{
		assertCompareTrue(arg1simple, arg2simple, NE);
		assertCompareTrue(arg1simple, arg2en, NE);
		assertCompareTrue(arg1simple, arg2cy, NE);
		assertCompareTrue(arg1simple, arg2string, NE);
		assertCompareException(arg1simple, arg2int, NE);
		assertCompareException(arg1simple, arg2year, NE);

		assertCompareTrue(arg1en, arg2simple, NE);
		assertCompareTrue(arg1en, arg2en, NE);
		assertCompareTrue(arg2en, arg2cy, NE);
		assertCompareTrue(arg1en, arg2cy, NE);
		assertCompareTrue(arg1en, arg2string, NE);
		assertCompareTrue(arg1en, arg2int, NE);

		assertCompareTrue(arg1cy, arg2simple, NE);
		assertCompareTrue(arg1cy, arg2en, NE);
		assertCompareTrue(arg2cy, arg2en, NE);
		assertCompareTrue(arg1cy, arg2cy, NE);
		assertCompareTrue(arg1cy, arg2string, NE);
		assertCompareTrue(arg1cy, arg2int, NE);

		assertCompareTrue(arg1string, arg2simple, NE);
		assertCompareTrue(arg1string, arg2en, NE);
		assertCompareTrue(arg1string, arg2cy, NE);
		assertCompareTrue(arg1string, arg2string, NE);
		assertCompareException(arg1string, arg2int, NE);
		assertCompareException(arg1string, arg2year, NE);

		assertCompareException(arg1int, arg2simple, NE);
		assertCompareTrue(arg1int, arg2en, NE);
		assertCompareTrue(arg1int, arg2cy, NE);
		assertCompareException(arg1int, arg2string, NE);
		assertCompareTrue(arg1int, arg2int, NE);
		assertCompareException(arg1int, arg2year, NE);

	}

	/**
	 * Assert that there is an exception as a result of comparing the two
	 * literals with the given operator.
	 * 
	 * @param lit1
	 *        The left literal
	 * @param lit2
	 *        The right literal
	 * @param op
	 *        The operator for the comparison
	 */
	private void assertCompareException(Literal lit1, Literal lit2, CompareOp op)
		throws Exception
	{
		try {
			boolean returnValue = QueryEvaluationUtil.compareLiterals(lit1, lit2, op);
			fail("Did not receive expected ValueExprEvaluationException (return value was " + returnValue
					+ ") for " + lit1.toString() + op.getSymbol() + lit2.toString());
		}
		catch (ValueExprEvaluationException e) {
			// Expected exception
		}
	}

	/**
	 * Assert that there is no exception as a result of comparing the two
	 * literals with the given operator and it returns false.
	 * 
	 * @param lit1
	 *        The left literal
	 * @param lit2
	 *        The right literal
	 * @param op
	 *        The operator for the comparison
	 */
	private void assertCompareFalse(Literal lit1, Literal lit2, CompareOp op)
		throws Exception
	{
		assertFalse("Compare did not return false for " + lit1.toString() + op.getSymbol() + lit2.toString(),
				QueryEvaluationUtil.compareLiterals(lit1, lit2, op));
	}

	/**
	 * Assert that there is no exception as a result of comparing the two
	 * literals with the given operator and it returns true.
	 * 
	 * @param lit1
	 *        The left literal
	 * @param lit2
	 *        The right literal
	 * @param op
	 *        The operator for the comparison
	 */
	private void assertCompareTrue(Literal lit1, Literal lit2, CompareOp op)
		throws Exception
	{
		assertTrue("Compare did not return true for " + lit1.toString() + op.getSymbol() + lit2.toString(),
				QueryEvaluationUtil.compareLiterals(lit1, lit2, op));
	}

}

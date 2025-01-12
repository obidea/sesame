/* Licensed to Aduna under one or more contributor license agreements.  
 * See the NOTICE.txt file distributed with this work for additional 
 * information regarding copyright ownership. Aduna licenses this file
 * to you under the terms of the Aduna BSD License (the "License"); 
 * you may not use this file except in compliance with the License. See 
 * the LICENSE.txt file distributed with this work for the full License.
 *
 * Unless required by applicable law or agreed to in writing,software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.  See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.openrdf.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.util.iterators.Iterators;

/**
 * Utility functions for working with {@link Model}s and other {@link Statement}
 * collections.
 * 
 * @author Jeen Broekstra
 * @author Arjohn Kampman
 * @since 2.8.0
 * @see org.openrdf.model.Model
 */
public class Models {

	/*
	 * hidden constructor to avoid instantiation
	 */
	protected Models() {
	}

	/**
	 * Retrieves any object value from the given model. If more than one possible
	 * value exists, any one value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve an object value.
	 * @return an object value from the given model, or <code>null</code> if no
	 *         such value exists.
	 * @see Model#objectValue()
	 */
	public static Value anyObject(Model m) {
		Value result = null;
		final Set<Value> objects = m.objects();
		if (objects != null && !objects.isEmpty()) {
			result = objects.iterator().next();
		}

		return result;
	}

	/**
	 * Retrieves any object Literal value from the given model. If more than one
	 * possible value exists, any one value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve an object Literal value.
	 * @return an object Literal value from the given model, or <code>null</code>
	 *         if no such value exists.
	 * @see Model#objectLiteral()
	 */
	public static Literal anyObjectLiteral(Model m) {
		Literal result = null;
		final Set<Value> objects = m.objects();
		if (objects != null && !objects.isEmpty()) {
			for (Value v : objects) {
				if (v instanceof Literal) {
					result = (Literal)v;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Retrieves any object Resource value from the given model. If more than one
	 * possible value exists, any one value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve an object Resource value.
	 * @return an object Resource value from the given model, or
	 *         <code>null</code> if no such value exists.
	 * @see Model#objectResource()
	 */
	public static Resource anyObjectResource(Model m) {
		Resource result = null;
		final Set<Value> objects = m.objects();
		if (objects != null && !objects.isEmpty()) {
			for (Value v : objects) {
				if (v instanceof Resource) {
					result = (Resource)v;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Retrieves any object URI value from the given model. If more than one
	 * possible value exists, any one value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve an object URI value.
	 * @return an object URI value from the given model, or <code>null</code> if
	 *         no such value exists.
	 * @see Model#objectURI()
	 */
	public static URI anyObjectURI(Model m) {
		URI result = null;
		final Set<Value> objects = m.objects();
		if (objects != null && !objects.isEmpty()) {
			for (Value v : objects) {
				if (v instanceof URI) {
					result = (URI)v;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Retrieves any subject from the given model. If more than one possible
	 * value exists, any one value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve a subject value.
	 * @return an subject value from the given model, or <code>null</code> if no
	 *         such value exists.
	 * @see Model#subjectResource()
	 */
	public static Resource anySubject(Model m) {
		Resource result = null;
		final Set<Resource> subjects = m.subjects();
		if (subjects != null && !subjects.isEmpty()) {
			result = subjects.iterator().next();
		}

		return result;
	}

	/**
	 * Retrieves any subject URI from the given model. If more than one possible
	 * value exists, any one value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve a subject URI value.
	 * @return an subject URI value from the given model, or <code>null</code> if
	 *         no such value exists.
	 * @see Model#subjectURI()
	 */
	public static URI anySubjectURI(Model m) {
		URI result = null;
		final Set<Resource> objects = m.subjects();
		if (objects != null && !objects.isEmpty()) {
			for (Value v : objects) {
				if (v instanceof URI) {
					result = (URI)v;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Retrieves any subject BNode from the given model. If more than one
	 * possible value exists, any one value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve a subject BNode value.
	 * @return a subject BNode value from the given model, or <code>null</code>
	 *         if no such value exists.
	 * @see Model#subjectBNode()
	 */
	public static BNode anySubjectBNode(Model m) {
		BNode result = null;
		final Set<Resource> objects = m.subjects();
		if (objects != null && !objects.isEmpty()) {
			for (Value v : objects) {
				if (v instanceof BNode) {
					result = (BNode)v;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Retrieves any predicate from the given model. If more than one possible
	 * value exists, any one value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve a predicate value.
	 * @return a predicate value from the given model, or <code>null</code> if no
	 *         such value exists.
	 */
	public static URI anyPredicate(Model m) {
		URI result = null;
		final Set<URI> predicates = m.predicates();
		if (predicates != null && !predicates.isEmpty()) {
			result = predicates.iterator().next();
		}
		return result;
	}

	/**
	 * Sets the property value for the given subject to the given object value,
	 * replacing any existing value(s) for the subject's property. This method
	 * updates the original input Model and then returns that same Model object.
	 * 
	 * @param m
	 *        the model in which to set the property value. May not be null.
	 * @param subject
	 *        the subject for which to set/replace the property value. May not be
	 *        null.
	 * @param property
	 *        the property for which to set/replace the value. May not be null.
	 * @param value
	 *        the value to set for the given subject and property. May not be
	 *        null.
	 * @param contexts
	 *        the context(s) in which to set/replace the property value. Optional
	 *        vararg argument. If not specified the operations works on the
	 *        entire Model.
	 * @return the Model object, containing the updated property value.
	 * @since 2.8.0
	 */
	public static Model setProperty(Model m, Resource subject, URI property, Value value, Resource... contexts)
	{
		if (m == null) {
			throw new IllegalArgumentException("m may not be null");
		}

		if (subject == null) {
			throw new IllegalArgumentException("subject may not be null");
		}

		if (property == null) {
			throw new IllegalArgumentException("property may not be null");
		}

		if (value == null) {
			throw new IllegalArgumentException("value may not be null");
		}

		if (m.contains(subject, property, null, contexts)) {
			m.remove(subject, property, null, contexts);
		}
		m.add(subject, property, value, contexts);
		return m;
	}

	/**
	 * Compares two RDF models, and returns <tt>true</tt> if they contain
	 * isomorphic graphs. RDF models are isomorphic graphs if statements from one
	 * model can be mapped 1:1 on to statements in the other model. In this
	 * mapping, blank nodes are not considered mapped when having an identical
	 * internal id, but are mapped from one model to the other by looking at the
	 * statements in which the blank nodes occur.
	 * 
	 * @see <a href="http://www.w3.org/TR/rdf11-concepts/#graph-isomorphism">RDF
	 *      Concepts &amp; Abstract Syntax, section 3.6 (Graph Comparison)</a>
	 * @since 2.8.0
	 */
	public static boolean isomorphic(Iterable<? extends Statement> model1, Iterable<? extends Statement> model2)
	{
		Set<? extends Statement> set1 = toSet(model1);
		Set<? extends Statement> set2 = toSet(model2);
		// Compare the number of statements in both sets
		if (set1.size() != set2.size()) {
			return false;
		}

		return isSubsetInternal(set1, set2);
	}

	/**
	 * Compares two RDF models, defined by two statement collections, and returns
	 * <tt>true</tt> if they are equal. Models are equal if they contain the same
	 * set of statements. Blank node IDs are not relevant for model equality,
	 * they are mapped from one model to the other by using the attached
	 * properties.
	 * 
	 * @deprecated since 2.8.0. Use {@link Models#isomorphic(Iterable, Iterable)}
	 *             instead.
	 */
	@Deprecated
	public static boolean equals(Iterable<? extends Statement> model1, Iterable<? extends Statement> model2) {
		return isomorphic(model1, model2);
	}

	/**
	 * Compares two RDF models, and returns <tt>true</tt> if the first model is a
	 * subset of the second model, using graph isomorphism to map statements
	 * between models.
	 */
	public static boolean isSubset(Iterable<? extends Statement> model1, Iterable<? extends Statement> model2)
	{
		// Filter duplicates
		Set<? extends Statement> set1 = toSet(model1);
		Set<? extends Statement> set2 = toSet(model2);

		return isSubset(set1, set2);
	}

	/**
	 * Compares two RDF models, and returns <tt>true</tt> if the first model is a
	 * subset of the second model, using graph isomorphism to map statements
	 * between models.
	 */
	public static boolean isSubset(Set<? extends Statement> model1, Set<? extends Statement> model2) {
		// Compare the number of statements in both sets
		if (model1.size() > model2.size()) {
			return false;
		}

		return isSubsetInternal(model1, model2);
	}

	private static boolean isSubsetInternal(Set<? extends Statement> model1, Set<? extends Statement> model2) {
		// try to create a full blank node mapping
		return matchModels(model1, model2);
	}

	private static boolean matchModels(Set<? extends Statement> model1, Set<? extends Statement> model2) {
		// Compare statements without blank nodes first, save the rest for later
		List<Statement> model1BNodes = new ArrayList<Statement>(model1.size());

		for (Statement st : model1) {
			if (st.getSubject() instanceof BNode || st.getObject() instanceof BNode) {
				model1BNodes.add(st);
			}
			else {
				if (!model2.contains(st)) {
					return false;
				}
			}
		}

		return matchModels(model1BNodes, model2, new HashMap<BNode, BNode>(), 0);
	}

	/**
	 * A recursive method for finding a complete mapping between blank nodes in
	 * model1 and blank nodes in model2. The algorithm does a depth-first search
	 * trying to establish a mapping for each blank node occurring in model1.
	 * 
	 * @param model1
	 * @param model2
	 * @param bNodeMapping
	 * @param idx
	 * @return true if a complete mapping has been found, false otherwise.
	 */
	private static boolean matchModels(List<? extends Statement> model1, Iterable<? extends Statement> model2,
			Map<BNode, BNode> bNodeMapping, int idx)
	{
		boolean result = false;

		if (idx < model1.size()) {
			Statement st1 = model1.get(idx);

			List<Statement> matchingStats = findMatchingStatements(st1, model2, bNodeMapping);

			for (Statement st2 : matchingStats) {
				// Map bNodes in st1 to bNodes in st2
				Map<BNode, BNode> newBNodeMapping = new HashMap<BNode, BNode>(bNodeMapping);

				if (st1.getSubject() instanceof BNode && st2.getSubject() instanceof BNode) {
					newBNodeMapping.put((BNode)st1.getSubject(), (BNode)st2.getSubject());
				}

				if (st1.getObject() instanceof BNode && st2.getObject() instanceof BNode) {
					newBNodeMapping.put((BNode)st1.getObject(), (BNode)st2.getObject());
				}

				// FIXME: this recursive implementation has a high risk of
				// triggering a stack overflow

				// Enter recursion
				result = matchModels(model1, model2, newBNodeMapping, idx + 1);

				if (result == true) {
					// models match, look no further
					break;
				}
			}
		}
		else {
			// All statements have been mapped successfully
			result = true;
		}

		return result;
	}

	private static List<Statement> findMatchingStatements(Statement st, Iterable<? extends Statement> model,
			Map<BNode, BNode> bNodeMapping)
	{
		List<Statement> result = new ArrayList<Statement>();

		for (Statement modelSt : model) {
			if (statementsMatch(st, modelSt, bNodeMapping)) {
				// All components possibly match
				result.add(modelSt);
			}
		}

		return result;
	}

	private static boolean statementsMatch(Statement st1, Statement st2, Map<BNode, BNode> bNodeMapping) {
		URI pred1 = st1.getPredicate();
		URI pred2 = st2.getPredicate();

		if (!pred1.equals(pred2)) {
			// predicates don't match
			return false;
		}

		Resource subj1 = st1.getSubject();
		Resource subj2 = st2.getSubject();

		if (subj1 instanceof BNode && subj2 instanceof BNode) {
			BNode mappedBNode = bNodeMapping.get(subj1);

			if (mappedBNode != null) {
				// bNode 'subj1' was already mapped to some other bNode
				if (!subj2.equals(mappedBNode)) {
					// 'subj1' and 'subj2' do not match
					return false;
				}
			}
			else {
				// 'subj1' was not yet mapped. we need to check if 'subj2' is a
				// possible mapping candidate
				if (bNodeMapping.containsValue(subj2)) {
					// 'subj2' is already mapped to some other value.
					return false;
				}
			}
		}
		else {
			// subjects are not (both) bNodes
			if (!subj1.equals(subj2)) {
				return false;
			}
		}

		Value obj1 = st1.getObject();
		Value obj2 = st2.getObject();

		if (obj1 instanceof BNode && obj2 instanceof BNode) {
			BNode mappedBNode = bNodeMapping.get(obj1);

			if (mappedBNode != null) {
				// bNode 'obj1' was already mapped to some other bNode
				if (!obj2.equals(mappedBNode)) {
					// 'obj1' and 'obj2' do not match
					return false;
				}
			}
			else {
				// 'obj1' was not yet mapped. we need to check if 'obj2' is a
				// possible mapping candidate
				if (bNodeMapping.containsValue(obj2)) {
					// 'obj2' is already mapped to some other value.
					return false;
				}
			}
		}
		else {
			// objects are not (both) bNodes
			if (!obj1.equals(obj2)) {
				return false;
			}
		}

		return true;
	}

	private static <S extends Statement> Set<S> toSet(Iterable<S> iterable) {
		Set<S> set = null;
		if (iterable instanceof Set) {
			set = (Set<S>)iterable;
		}
		else {
			// Filter duplicates
			set = new HashSet<S>();
			Iterators.addAll(iterable.iterator(), set);
		}
		return set;
	}
}
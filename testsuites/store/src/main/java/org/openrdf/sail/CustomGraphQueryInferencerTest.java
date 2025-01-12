package org.openrdf.sail;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import info.aduna.io.ResourceUtil;
import info.aduna.iteration.Iterations;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.inferencer.fc.CustomGraphQueryInferencer;

@RunWith(Parameterized.class)
public abstract class CustomGraphQueryInferencerTest {

	protected static class Expectation {

		private final int initialCount, countAfterRemove, subjCount, predCount, objCount;

		public Expectation(int initialCount, int countAfterRemove, int subjCount, int predCount, int objCount) {
			this.initialCount = initialCount;
			this.countAfterRemove = countAfterRemove;
			this.subjCount = subjCount;
			this.predCount = predCount;
			this.objCount = objCount;
		}
	}

	private static final String TEST_DIR_PREFIX = "/testcases/custom-query-inferencing/";

	private static final String BASE = "http://foo.org/bar#";

	private static final String PREDICATE = "predicate";

	@Parameters(name = "{0}")
	public static final Collection<Object[]> parameters() {
		Expectation predExpect = new Expectation(8, 2, 0, 2, 0);
		return Arrays.asList(new Object[][] {
				{ PREDICATE, predExpect, QueryLanguage.SPARQL },
				{ "resource", new Expectation(4, 2, 2, 0, 2), QueryLanguage.SPARQL },
				{ "predicate-serql", predExpect, QueryLanguage.SERQL } });
	}

	private String initial;

	private String delete;

	private String resourceFolder;

	private Expectation testData;

	private QueryLanguage language;

	protected void runTest(final CustomGraphQueryInferencer inferencer)
		throws RepositoryException, RDFParseException, IOException, MalformedQueryException,
		UpdateExecutionException
	{
		// Initialize
		Repository sail = new SailRepository(inferencer);
		sail.initialize();
		RepositoryConnection connection = sail.getConnection();
		try {
			connection.begin();
			connection.clear();
			connection.add(new StringReader(initial), BASE, RDFFormat.TURTLE);

			// Test initial inferencer state
			Collection<Value> watchPredicates = inferencer.getWatchPredicates();
			assertThat(watchPredicates.size(), is(equalTo(testData.predCount)));
			Collection<Value> watchObjects = inferencer.getWatchObjects();
			assertThat(watchObjects.size(), is(equalTo(testData.objCount)));
			Collection<Value> watchSubjects = inferencer.getWatchSubjects();
			assertThat(watchSubjects.size(), is(equalTo(testData.subjCount)));
			ValueFactory factory = connection.getValueFactory();
			if (resourceFolder.startsWith(PREDICATE)) {
				assertThat(watchPredicates.contains(factory.createURI(BASE, "brotherOf")), is(equalTo(true)));
				assertThat(watchPredicates.contains(factory.createURI(BASE, "parentOf")), is(equalTo(true)));
			}
			else {
				URI bob = factory.createURI(BASE, "Bob");
				URI alice = factory.createURI(BASE, "Alice");
				assertThat(watchSubjects.contains(bob), is(equalTo(true)));
				assertThat(watchSubjects.contains(alice), is(equalTo(true)));
				assertThat(watchObjects.contains(bob), is(equalTo(true)));
				assertThat(watchObjects.contains(alice), is(equalTo(true)));
			}

			// Test initial inferencing results
			assertThat(Iterations.asSet(connection.getStatements(null, null, null, true)).size(),
					is(equalTo(testData.initialCount)));

			// Test results after removing some statements
			connection.prepareUpdate(QueryLanguage.SPARQL, delete).execute();
			assertThat(Iterations.asSet(connection.getStatements(null, null, null, true)).size(),
					is(equalTo(testData.countAfterRemove)));

			// Tidy up. Storage gets re-used for subsequent tests, so must clear here,
			// in order to properly clear out any inferred statements.
			connection.clear();
			connection.commit();
		}
		finally {
			connection.close();
		}
		sail.shutDown();
	}

	public CustomGraphQueryInferencerTest(String resourceFolder, Expectation testData, QueryLanguage language)
	{
		this.resourceFolder = resourceFolder;
		this.testData = testData;
		this.language = language;
	}

	protected CustomGraphQueryInferencer createRepository(boolean withMatchQuery)
		throws IOException, MalformedQueryException, UnsupportedQueryLanguageException, RepositoryException,
		SailException, RDFParseException
	{
		String testFolder = TEST_DIR_PREFIX + resourceFolder;
		String rule = ResourceUtil.getString(testFolder + "/rule.rq");
		String match = withMatchQuery ? ResourceUtil.getString(testFolder + "/match.rq") : "";
		initial = ResourceUtil.getString(testFolder + "/initial.ttl");
		delete = ResourceUtil.getString(testFolder + "/delete.ru");

		NotifyingSail store = newSail();

		return new CustomGraphQueryInferencer(store, language, rule, match);
	}

	/**
	 * Gets an instance of the Sail that should be tested. The returned
	 * repository must not be initialized.
	 * 
	 * @return an uninitialized NotifyingSail.
	 */
	protected abstract NotifyingSail newSail();

	@Test
	public void testCustomQueryInference()
		throws RepositoryException, RDFParseException, MalformedQueryException, UpdateExecutionException,
		IOException, UnsupportedQueryLanguageException, SailException
	{
		runTest(createRepository(true));
	}

	@Test
	public void testCustomQueryInferenceImplicitMatcher()
		throws RepositoryException, RDFParseException, MalformedQueryException, UpdateExecutionException,
		IOException, UnsupportedQueryLanguageException, SailException
	{
		runTest(createRepository(false));
	}

}
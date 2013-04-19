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
package org.openrdf.rio.rdfjson;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.helpers.RDFJSONParserSettings;
import org.openrdf.rio.helpers.RDFParserBase;

/**
 * {@link RDFParser} implementation for the RDF/JSON format
 * 
 * @author Peter Ansell
 * @since 2.7.0
 */
public class RDFJSONParser extends RDFParserBase implements RDFParser {

	private final RDFFormat actualFormat;

	/**
	 * Creates a parser using {@link RDFFormat#RDFJSON} to identify the parser.
	 */
	public RDFJSONParser() {
		this.actualFormat = RDFFormat.RDFJSON;
	}

	/**
	 * Creates a parser using the given RDFFormat to self identify.
	 * 
	 * @param actualFormat
	 */
	public RDFJSONParser(final RDFFormat actualFormat) {
		this.actualFormat = actualFormat;
	}

	@Override
	public RDFFormat getRDFFormat() {
		return this.actualFormat;
	}

	@Override
	public void parse(final InputStream inputStream, final String baseUri)
		throws IOException, RDFParseException, RDFHandlerException
	{
		if (this.rdfHandler == null) {
			throw new IllegalStateException("RDF handler has not been set");
		}

		this.rdfHandler.startRDF();
		JsonParser jp = null;

		try {
			jp = RDFJSONUtility.JSON_FACTORY.createJsonParser(inputStream);
			rdfJsonToHandlerInternal(this.rdfHandler, this.valueFactory, jp);
		}
		catch (final IOException e) {
			if (jp != null) {
				reportFatalError("Found IOException during parsing", e, jp.getCurrentLocation());
			}
			else {
				reportFatalError(e);
			}
		}
		finally {
			if (jp != null) {
				try {
					jp.close();
				}
				catch (final IOException e) {
					reportFatalError("Found exception while closing JSON parser", e, jp.getCurrentLocation());
				}
			}
		}
		this.rdfHandler.endRDF();
	}

	protected void reportError(String msg, Throwable e, JsonLocation location, RioSetting<Boolean> setting)
		throws RDFParseException
	{
		reportError(msg, location.getLineNr(), location.getColumnNr(), setting);
	}

	protected void reportError(String msg, JsonLocation location, RioSetting<Boolean> setting)
		throws RDFParseException
	{
		reportError(msg, location.getLineNr(), location.getColumnNr(), setting);
	}

	protected void reportFatalError(String msg, Throwable e, JsonLocation location)
		throws RDFParseException
	{
		reportFatalError(msg, location.getLineNr(), location.getColumnNr());
	}

	protected void reportFatalError(String msg, JsonLocation location)
		throws RDFParseException
	{
		reportFatalError(msg, location.getLineNr(), location.getColumnNr());
	}

	@Override
	public void parse(final Reader reader, final String baseUri)
		throws IOException, RDFParseException, RDFHandlerException
	{
		if (this.rdfHandler == null) {
			throw new IllegalStateException("RDF handler has not been set");
		}

		this.rdfHandler.startRDF();
		JsonParser jp = null;

		try {
			jp = RDFJSONUtility.JSON_FACTORY.createJsonParser(reader);
			rdfJsonToHandlerInternal(this.rdfHandler, this.valueFactory, jp);
		}
		catch (final IOException e) {
			if (jp != null) {
				reportFatalError("Found IOException during parsing", e, jp.getCurrentLocation());
			}
			else {
				reportFatalError(e);
			}
		}
		finally {
			if (jp != null) {
				try {
					jp.close();
				}
				catch (final IOException e) {
					reportFatalError("Found exception while closing JSON parser", e, jp.getCurrentLocation());
				}
			}
		}
		this.rdfHandler.endRDF();
	}

	private void rdfJsonToHandlerInternal(final RDFHandler handler, final ValueFactory vf, final JsonParser jp)
		throws IOException, JsonParseException, RDFParseException, RDFHandlerException
	{
		if (jp.nextToken() != JsonToken.START_OBJECT) {
			reportFatalError("Expected RDF/JSON document to start with an Object", jp.getCurrentLocation());
		}

		while (jp.nextToken() != JsonToken.END_OBJECT) {
			final String subjStr = jp.getCurrentName();
			Resource subject = null;

			subject = subjStr.startsWith("_:") ? vf.createBNode(subjStr.substring(2)) : vf.createURI(subjStr);
			if (jp.nextToken() != JsonToken.START_OBJECT) {
				reportFatalError("Expected subject value to start with an Object", jp.getCurrentLocation());
			}

			boolean foundPredicate = false;
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				final String predStr = jp.getCurrentName();

				final URI predicate = vf.createURI(predStr);
				foundPredicate = true;

				if (jp.nextToken() != JsonToken.START_ARRAY) {
					reportFatalError("Expected predicate value to start with an array", jp.getCurrentLocation());
				}

				boolean foundObject = false;

				while (jp.nextToken() != JsonToken.END_ARRAY) {
					if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
						reportFatalError("Expected object value to start with an Object: subject=<" + subjStr
								+ "> predicate=<" + predStr + ">", jp.getCurrentLocation());
					}

					String nextValue = null;
					String nextType = null;
					String nextDatatype = null;
					String nextLanguage = null;
					final Set<String> nextContexts = new HashSet<String>(2);

					while (jp.nextToken() != JsonToken.END_OBJECT) {
						final String fieldName = jp.getCurrentName();
						if (RDFJSONUtility.VALUE.equals(fieldName)) {
							if (nextValue != null) {
								reportError("Multiple values found for a single object: subject=" + subjStr
										+ " predicate=" + predStr, jp.getCurrentLocation(),
										RDFJSONParserSettings.FAIL_ON_MULTIPLE_OBJECT_VALUES);
							}

							jp.nextToken();

							nextValue = jp.getText();
						}
						else if (RDFJSONUtility.TYPE.equals(fieldName)) {
							if (nextType != null) {
								reportError("Multiple types found for a single object: subject=" + subjStr
										+ " predicate=" + predStr, jp.getCurrentLocation(),
										RDFJSONParserSettings.FAIL_ON_MULTIPLE_OBJECT_TYPES);
							}

							jp.nextToken();

							nextType = jp.getText();
						}
						else if (RDFJSONUtility.LANG.equals(fieldName)) {
							if (nextLanguage != null) {
								reportError("Multiple languages found for a single object: subject=" + subjStr
										+ " predicate=" + predStr, jp.getCurrentLocation(),
										RDFJSONParserSettings.FAIL_ON_MULTIPLE_OBJECT_LANGUAGES);
							}

							jp.nextToken();

							nextLanguage = jp.getText();
						}
						else if (RDFJSONUtility.DATATYPE.equals(fieldName)) {
							if (nextDatatype != null) {
								reportError("Multiple datatypes found for a single object: subject=" + subjStr
										+ " predicate=" + predStr, jp.getCurrentLocation(),
										RDFJSONParserSettings.FAIL_ON_MULTIPLE_OBJECT_DATATYPES);
							}

							jp.nextToken();

							nextDatatype = jp.getText();
						}
						else if (RDFJSONUtility.GRAPHS.equals(fieldName)) {
							if (jp.nextToken() != JsonToken.START_ARRAY) {
								reportError("Expected graphs to start with an array", jp.getCurrentLocation(),
										RDFJSONParserSettings.SUPPORT_GRAPHS_EXTENSION);
							}

							while (jp.nextToken() != JsonToken.END_ARRAY) {
								final String nextGraph = jp.getText();
								nextContexts.add(nextGraph);
							}
						}
						else {
							reportError("Unrecognised JSON field name for object: subject=" + subjStr
									+ " predicate=" + predStr + " fieldname=" + fieldName, jp.getCurrentLocation(),
									RDFJSONParserSettings.FAIL_ON_UNKNOWN_PROPERTY);
						}
					}

					Value object = null;

					if (nextType == null) {
						reportFatalError("No type for object: subject=" + subjStr + " predicate=" + predStr,
								jp.getCurrentLocation());
					}

					if (nextValue == null) {
						reportFatalError("No value for object: subject=" + subjStr + " predicate=" + predStr,
								jp.getCurrentLocation());
					}

					if (RDFJSONUtility.LITERAL.equals(nextType)) {
						if (nextLanguage != null) {
							object = this.createLiteral(nextValue, nextLanguage, null);
						}
						else if (nextDatatype != null) {
							object = this.createLiteral(nextValue, null, this.createURI(nextDatatype));
						}
						else {
							object = this.createLiteral(nextValue, null, null);
						}
					}
					else if (RDFJSONUtility.BNODE.equals(nextType)) {
						if (nextLanguage != null) {
							reportFatalError("Language was attached to a blank node object: subject=" + subjStr
									+ " predicate=" + predStr, jp.getCurrentLocation());
						}
						if (nextDatatype != null) {
							reportFatalError("Datatype was attached to a blank node object: subject=" + subjStr
									+ " predicate=" + predStr, jp.getCurrentLocation());
						}
						object = vf.createBNode(nextValue.substring(2));
					}
					else if (RDFJSONUtility.URI.equals(nextType)) {
						if (nextLanguage != null) {
							reportFatalError("Language was attached to a uri object: subject=" + subjStr
									+ " predicate=" + predStr, jp.getCurrentLocation());
						}
						if (nextDatatype != null) {
							reportFatalError("Datatype was attached to a uri object: subject=" + subjStr
									+ " predicate=" + predStr, jp.getCurrentLocation());
						}
						object = vf.createURI(nextValue);
					}
					foundObject = true;

					if (!nextContexts.isEmpty()) {
						for (final String nextContext : nextContexts) {
							final Resource context = nextContext.equals(RDFJSONUtility.NULL) ? null
									: vf.createURI(nextContext);
							handler.handleStatement(vf.createStatement(subject, predicate, object, context));
						}
					}
					else {
						handler.handleStatement(vf.createStatement(subject, predicate, object));
					}

				}
				if (!foundObject) {
					reportFatalError("No object for predicate: subject=" + subjStr + " predicate=" + predStr,
							jp.getCurrentLocation());
				}
			}

			if (!foundPredicate) {
				reportFatalError("No predicate for object: subject=" + subjStr, jp.getCurrentLocation());
			}
		}
	}

}
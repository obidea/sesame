/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2008.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.repository.sail;

import static org.openrdf.query.parser.QueryParserRegistry.getInstance;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.QueryParserFactory;
import org.openrdf.repository.RepositoryMetaData;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParserFactory;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.sail.SailMetaData;
import org.openrdf.sail.helpers.SailMetaDataWrapper;

/**
 * @author James Leigh
 */
public class SailRepositoryMetaData extends SailMetaDataWrapper implements RepositoryMetaData {

	public SailRepositoryMetaData(SailMetaData delegate) {
		super(delegate);
	}

	public RDFFormat[] getAddRDFFormats() {
		Collection<RDFParserFactory> parsers = RDFParserRegistry.getInstance().getAll();
		RDFFormat[] formats = new RDFFormat[parsers.size()];
		List<RDFFormat> list = Arrays.asList(formats);
		for (RDFParserFactory parser : parsers) {
			list.add(parser.getRDFFormat());
		}
		return formats;
	}

	public RDFFormat[] getExportRDFFormats() {
		Collection<RDFWriterFactory> writers = RDFWriterRegistry.getInstance().getAll();
		RDFFormat[] formats = new RDFFormat[writers.size()];
		List<RDFFormat> list = Arrays.asList(formats);
		for (RDFWriterFactory writer : writers) {
			list.add(writer.getRDFFormat());
		}
		return formats;
	}

	public QueryLanguage[] getQueryLanguages() {
		Collection<QueryParserFactory> parsers = getInstance().getAll();
		QueryLanguage[] languages = new QueryLanguage[parsers.size()];
		List<QueryLanguage> list = Arrays.asList(languages);
		for (QueryParserFactory parser : parsers) {
			list.add(parser.getQueryLanguage());
		}
		return languages;
	}

	public boolean isRemoteDatasetSupported() {
		return false;
	}

}
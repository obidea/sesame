/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.sail.rdbms.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.openrdf.sail.rdbms.schema.HashTable;
import org.openrdf.sail.rdbms.schema.ValueTable;


/**
 *
 * @author james
 */
public class PgSqlHashtable extends HashTable {
	private String execute;
	private Connection conn;

	public PgSqlHashtable(ValueTable table) {
		super(table);
	}

	@Override
	public void close()
		throws SQLException
	{
		if (execute != null) {
			Statement stmt = conn.createStatement();
			try {
				stmt.execute("DEALLOCATE " + getName() + "_select");
			} finally {
				stmt.close();
			}
		}
		super.close();
	}

	@Override
	protected PreparedStatement prepareSelect(Connection conn, String sql)
		throws SQLException
	{
		if (execute == null) {
			this.conn = conn;
			StringBuilder sb = new StringBuilder();
			sb.append("PREPARE ").append(getName()).append("_select (");
			for (int i = 0, n = getSelectChunkSize(); i < n; i++) {
				sb.append("bigint,");
			}
			sb.setCharAt(sb.length() - 1, ')');
			sb.append(" AS\n");
			sb.append("SELECT id, value\nFROM ").append(getName());
			sb.append("\nWHERE value IN (");
			for (int i = 0, n = getSelectChunkSize(); i < n; i++) {
				sb.append("$").append(i + 1).append(",");
			}
			sb.setCharAt(sb.length() - 1, ')');
			Statement stmt = conn.createStatement();
			try {
				stmt.execute(sb.toString());
			} finally {
				stmt.close();
			}
			sb.delete(0, sb.length());
			sb.append("EXECUTE ").append(getName()).append("_select (");
			for (int i = 0, n = getSelectChunkSize(); i < n; i++) {
				sb.append("?,");
			}
			sb.setCharAt(sb.length() - 1, ')');
			execute = sb.toString();
		}
		return super.prepareSelect(conn, execute);
	}

}

package com.github.xzzpig.pigapi.sql;

import java.sql.Connection;
import java.sql.Statement;

public abstract class TSql {
	public enum Type {
		MYSQL
	}

	protected Connection conn;
	protected Statement stmt;

	protected Type type;

	public TSql() {
		while (true) {
			try {
				build();
				break;
			} catch (Exception e) {
			}
		}
	}

	protected abstract void build();

	public abstract void close();

	public abstract TSql connect(String host, int port, String username, String password, String database)
			throws Exception;

	public Connection getConn() {
		return conn;
	}

	public Statement getStmtement() {
		return stmt;
	}

	public Type getType() {
		return type;
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	public void setStmtement(Statement stmt) {
		this.stmt = stmt;
	}
}

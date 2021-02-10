package com.cp.fwk.util.query;

public enum QueryTypeFilter {

	EQUAL("="),
	
	NOTEQUAL("<>"),

	BETWEEN("BETWEEN"),
	
	CONTAINS("LIKE"),
	
	GREATER(">"),
	GREATEREQUAL(">="),
	LESS("<"),
	LESSEQUAL("<="),
	
	ORDERBY("ORDER BY")
	
	;
	
	private final String sql;

	QueryTypeFilter(String sql) {
		this.sql = sql;
	}
	
	public String getSql() {
		return sql;
	}
}

package com.cp.fwk.util.query;

public enum QueryTypeCondition {

	AND("AND"),
	
	OR("OR"),
	
	INITBLOCK("("),
	
	ENDBLOCK(")"),
	
	ASC("ASC"),
	DESC("DESC")
	
	;
	
	private final String sql;

	QueryTypeCondition(String sql) {
		this.sql = sql;
	}
	
	public String getSql() {
		return sql;
	}
}

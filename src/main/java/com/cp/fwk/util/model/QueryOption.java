package com.cp.fwk.util.model;

import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;

public class QueryOption {
	
	private String field;
	private QueryTypeFilter filter;
	private String[] contents;
	private QueryTypeCondition condition;
	
	public QueryOption(String field, QueryTypeFilter filter, String[] contents, QueryTypeCondition condition) {	
		this.field = field;
		this.filter = filter;
		this.contents = contents;
		this.condition = condition;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public QueryTypeFilter getFilter() {
		return filter;
	}

	public void setFilter(QueryTypeFilter filter) {
		this.filter = filter;
	}

	public String[] getContents() {
		return contents;
	}

	public void setContents(String[] contents) {
		this.contents = contents;
	}

	public QueryTypeCondition getCondition() {
		return condition;
	}

	public void setCondition(QueryTypeCondition condition) {
		this.condition = condition;
	}
}

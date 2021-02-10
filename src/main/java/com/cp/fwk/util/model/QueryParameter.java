package com.cp.fwk.util.model;

import java.util.ArrayList;
import java.util.List;

import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;

public class QueryParameter {
	
	List<QueryOption> queryOption = new ArrayList<QueryOption>();
	
	public void addSingleParameter (String field, QueryTypeFilter filter, int content, QueryTypeCondition condition) {
		addSingleParameter(field, filter, String.valueOf(content), condition);
	}

	public void addSingleParameter (String field, QueryTypeFilter filter, String content, QueryTypeCondition condition) {
		String[] contents = {content};
		addQueryOption(field, filter, contents, condition);
	}
	
	public void addBetweenParameter (String field, String[] contents, QueryTypeCondition condition) {
		addQueryOption(field, QueryTypeFilter.BETWEEN, contents, condition);
	}
	
	private void addQueryOption(String field, QueryTypeFilter filter, String[] contents, QueryTypeCondition condition) {
		if (field == null) {
			GeneralFunctions.showLog("QueryParameter: Campo nulo");
			return;
		}
		if (field.isEmpty()) {
			GeneralFunctions.showLog("QueryParameter: Campo vazio");
			return;
		}

		if (contents[0].isEmpty()) {
			GeneralFunctions.showLog("QueryParameter: Contents 1 nulo.");
			return;
		}

		if (filter.equals(QueryTypeFilter.BETWEEN)) {
			
			if (contents.length != 2) {
				GeneralFunctions.showLog("QueryParameter: Contents necessita possuir 2 fields.");
				return;
			}

			if (contents[1].isEmpty()) {
				GeneralFunctions.showLog("QueryParameter: Contents 2 nulo.");
				return;
			}
		}

		queryOption.add(new QueryOption(field, filter, contents, condition));
	}
	
	public void addOrderByOption(String field, QueryTypeFilter filter, QueryTypeCondition condition) {
		if (field == null) {
			GeneralFunctions.showLog("QueryParameter: Campo nulo");
			return;
		}
		if (field.isEmpty()) {
			GeneralFunctions.showLog("QueryParameter: Campo vazio");
			return;
		}
		queryOption.add(new QueryOption(field, filter, null, condition));
	}

	public List<QueryOption> getQueryOption () {
		return this.queryOption;
	}

	public void clearQuery() {
		queryOption.clear();
	}

	public boolean isEmpty() {
		return queryOption.isEmpty();
	}
}

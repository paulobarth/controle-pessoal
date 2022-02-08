package com.cp.fwk.util.sql;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cp.constants.generalInfo;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryOption;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.xpath.internal.WhitespaceStrippingElementMatcher;

public class SQLUtil {

	private static StringBuilder sqlBuilder = new StringBuilder();
	private static StringBuilder whereParamBuilder = new StringBuilder();
//	private static String whereParam = "";
	private static Gson gson = new Gson();

	public static String selectAllCommand(final Class clazz, String whereClause) {

		sqlContentClear();
		sqlContentAdd("SELECT * FROM ");
		sqlContentAdd(getTableName(clazz));
//		Map<String, String> sqlMap = getTablesFromClass(clazz);
//		
//		String sqlFrom = "";
//		for(int i = 1; i <= sqlMap.size(); i++) {
//			
//			sqlMap.get
//			
//			if (sql)
//			if (!sqlFrom.isEmpty()) {
//				sqlFrom += " ,";
//			}
//			sqlFrom = sqlM
//		}

		if (!whereClause.isEmpty()) {
			sqlContentAdd(" WHERE ");
			sqlContentAdd(whereClause);
		}

		return sqlBuilder.toString();
	}

//	private static <T> List<String[2]> getTablesFromClass(Class clazz) {
//		Map<String, String> sqlMap = new HashMap<String, String>();
//		sqlMap.put("FROM1", getTableName(clazz) + " t1");
//		
//		Field[] fields = clazz.getDeclaredFields();
//
//		int qtdeTables = 1;
//		for (int i = 0; i < fields.length; i++) {
//
//			if (fields[i].getType().getPackageName().startsWith("com.cp.model")) {
//				Class<T> relatedClass = (Class) fields[i].getType();
//
//				qtdeTables++;
//				sqlMap.put("FROM" + String.valueOf(qtdeTables),
//							getTableName(relatedClass) + " t" + String.valueOf(qtdeTables));
//				sqlMap.put("JOIN" + String.valueOf(qtdeTables),
//									" t" + String.valueOf(qtdeTables - 1) +
//									".id" + getTableName(relatedClass) +
//									" = " +
//									" t" + String.valueOf(qtdeTables) +
//									".id");
//			}
//		}
//
//		return sqlMap;
//	}

	public static String selectIdCommand(Class clazz, int id) {

		sqlContentClear();
		sqlContentAdd("SELECT * FROM ");
		sqlContentAdd(getTableName(clazz));
		sqlContentAdd(" WHERE id=");
		sqlContentAdd(id);

		return sqlBuilder.toString();
	}

	public static String insertCommand(Object obj, Class clazz, int topId) {

		String content;

		sqlContentClear();

		sqlContentAdd("INSERT INTO ");
		sqlContentAdd(getTableName(clazz));

		Field[] fields = clazz.getDeclaredFields();

		// Step 1 - Add field names
		sqlContentAdd(" (");
		for (int i = 0; i < fields.length; i++) {

//			if (fields[i].getType().getPackageName().equals(generalInfo.PACKAGE_MODEL_NAME)) {
//				continue;
//			}

			if (i > 0) {
				sqlContentAdd(",");
			}

			sqlContentAdd(fields[i].getName());
		}
		sqlContentAdd(") VALUES");

		// Step 2 - For each item in json array, add field by model class
		JsonArray dataset = gson.toJsonTree(obj).getAsJsonArray();
		Iterator<JsonElement> iterator = dataset.iterator();

		int count = 0;
		while (iterator.hasNext()) {

			JsonObject jObj = iterator.next().getAsJsonObject();

			if (count > 0) {
				sqlContentAdd(",");
			}

			sqlContentAdd("(");

			for (int i = 0; i < fields.length; i++) {

//				if (fields[i].getType().getPackageName().equals(generalInfo.PACKAGE_MODEL_NAME)) {
//					continue;
//				}

				if (i > 0) {
					sqlContentAdd(",");
				}

				sqlContentAdd("\"");
				if (fields[i].getName().equals("id")) {
					topId++;
					sqlContentAdd(topId);
				} else {
					try {
						content = jObj.get(fields[i].getName()).getAsString();
						if (content.isEmpty()) {
							sqlContentAdd(" ");
						} else {
							sqlContentAdd(content);
						}
					} catch (Exception e) {
						sqlContentAdd(" ");
					}
				}
				sqlContentAdd("\"");
			}
			sqlContentAdd(")");
			count++;
		}

		return sqlBuilder.toString();
	}

	public static String updateIdCommand(JsonObject jObj, Class clazz) {

		String whereClause = "";

		sqlContentClear();

		sqlContentAdd("UPDATE ");
		sqlContentAdd(getTableName(clazz));
		sqlContentAdd(" SET ");

		Field[] fields = clazz.getDeclaredFields();

		int count = 0;
		for (int i = 0; i < fields.length; i++) {

			String fieldName = fields[i].getName();

			try {
				String content = jObj.get(fieldName).getAsString();

				if (content != null) {

					if (fieldName.equals("id")) {
						whereClause = " WHERE id = " + content;
						continue;
					}

					if (count > 0) {
						sqlContentAdd(", ");
					}

					sqlContentAdd(fieldName);
					sqlContentAdd(" = ");
					sqlContentAdd("\"");
					if (content.isEmpty()) {
						sqlContentAdd(" ");
					} else {
						sqlContentAdd(content);
					}
					sqlContentAdd("\"");

					count++;
				}

			} catch (Exception e) {

			}

		}

		if (whereClause.isEmpty()) {
			sqlContentAdd(" WHERE id=0");
		} else {
			sqlContentAdd(whereClause);
		}

		return sqlBuilder.toString();
	}

	public static String deleteCommand(Class clazz) {

		sqlContentClear();

		sqlContentAdd("DELETE FROM ");
		sqlContentAdd(getTableName(clazz));

		return sqlBuilder.toString();
	}

	public static String deleteCommand(JsonObject jObj, Class clazz) {

		sqlContentClear();

		sqlContentAdd("DELETE FROM ");
		sqlContentAdd(getTableName(clazz));
		sqlContentAdd(" WHERE ");

		Field[] fields = clazz.getDeclaredFields();

		int count = 0;
		for (int i = 0; i < fields.length; i++) {

			String fieldName = fields[i].getName();

			try {
				String content = jObj.get(fieldName).getAsString();

				if (content != null && !content.isEmpty()) {

					if (count > 0) {
						sqlContentAdd(" AND ");
					}

					sqlContentAdd(fieldName);
					sqlContentAdd(" = ");
					sqlContentAdd("\"");
					sqlContentAdd(content);
					sqlContentAdd("\"");

					count++;
				}

			} catch (Exception e) {

			}

		}

		return sqlBuilder.toString();
	}

	public static String deleteIdCommand(Class clazz, int id) {

		sqlContentClear();

		sqlContentAdd("DELETE FROM ");
		sqlContentAdd(getTableName(clazz));
		sqlContentAdd(" WHERE id=");
		sqlContentAdd(id);

		return sqlBuilder.toString();
	}

	public static String selectMaxIdCommand(final Class clazz) {

		sqlContentClear();
		sqlContentAdd("SELECT MAX(id) FROM ");
		sqlContentAdd(getTableName(clazz));

		return sqlBuilder.toString();

	}

	private static String getTableName(final Class clazz) {

		return clazz.getSimpleName().toLowerCase();
	}

	private static void sqlContentClear() {
		sqlBuilder.setLength(0);
	}

	private static void sqlContentAdd(String content) {
		sqlBuilder.append(content);
	}

	private static void sqlContentAdd(int content) {
		sqlBuilder.append(content);
	}

	public static String getSQLQuery(QueryParameter qp) {

		StringBuilder orderParamBuilder = new StringBuilder();
		whereParamClear();
		
		if (qp == null || qp.isEmpty()) {
			return "";
		}

		for (QueryOption option : qp.getQueryOption()) {

			if (option.getFilter().equals(QueryTypeFilter.ORDERBY)) {
				
				if (orderParamBuilder.length() == 0) {
					orderParamBuilder.append(" ");
					orderParamBuilder.append(QueryTypeFilter.ORDERBY.getSql());
				} else {
					orderParamBuilder.append(",");
				}

				orderParamBuilder.append(" ");
				orderParamBuilder.append(option.getField());
				orderParamBuilder.append(" ");
				orderParamBuilder.append(option.getCondition().getSql());
			} else {

				if (whereParamBuilder.length() > 0) {
					whereParamAdd(option.getCondition().getSql());
				}

				if (option.getFilter().equals(QueryTypeFilter.BETWEEN)) {

					if (option.getContents().length == 2) {

						whereParamAdd(option.getField());
						whereParamAdd(option.getFilter().getSql());
						whereParamAddQuoted(option.getContents()[0]);
						whereParamAdd("AND");
						whereParamAddQuoted(option.getContents()[1]);
					}

				} else {

					if (option.getFilter().equals(QueryTypeFilter.CONTAINS)) {

						whereParamAdd(option.getField());
						whereParamAdd(option.getFilter().getSql());
						whereParamAddQuoted("%" + option.getContents()[0] + "%");

					} else {

						whereParamAdd(option.getField());
						whereParamAdd(option.getFilter().getSql());
						if (option.getContents().length == 2 && option.getContents()[1].equals("field")) {
							whereParamAdd(option.getContents()[0]);
						} else {
							whereParamAddQuoted(option.getContents()[0]);
						}
					}
				}
			}
		}

		if (orderParamBuilder.length() > 0) {
			if (whereParamBuilder.length() == 0) {
				whereParamAdd(" 1 = 1 ");
			}
			whereParamBuilder.append(orderParamBuilder.toString());
		}

		return whereParamBuilder.toString();
	}

	private static void whereParamClear() {
		whereParamBuilder.setLength(0);
	}

	private static void whereParamAdd(String clause) {
		whereParamBuilder.append(" ");
		whereParamBuilder.append(clause);
	}

	private static void whereParamAddQuoted(String clause) {
		whereParamBuilder.append(" '");
		whereParamBuilder.append(clause);
		whereParamBuilder.append("'");
	}

}

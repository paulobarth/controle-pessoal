package com.cp.fwk.data;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import com.cp.constants.databaseInfo;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.sql.SQLUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DataManager {

	private static Connection conn = null;

	private static Gson gson = new Gson();

	public static <T> T selectList(final Class arrayClazz) {
		return (T) executeSelectList(arrayClazz, "");
	}

	public static <T> T selectList(final Class arrayClazz, QueryParameter qp) {
		String whereClause = SQLUtil.getSQLQuery(qp);
		return executeSelectList(arrayClazz, whereClause);
	}

	public static <T> T selectList(final Class arrayClazz, String whereClause) {
		return executeSelectList(arrayClazz, whereClause);
	}

	private static <T> T executeSelectList(Class arrayClazz, String whereClause) {

		Class modelClazz = arrayClazz.getComponentType();

		String json = executeSelect(modelClazz, SQLUtil.selectAllCommand(modelClazz, whereClause));
		if (json.isEmpty()) {
			return null;
		}

		String jFinal = "[" + json + "]";

		GeneralFunctions.showLog("selectList --> " + jFinal);

		return (T) gson.fromJson(jFinal, arrayClazz);
	}

	public static <T> T selectId(final Class clazz, int id, QueryParameter qp) {
		String whereClause = SQLUtil.getSQLQuery(qp);
		return selectId(clazz, id, whereClause);
	}

	public static <T> T selectId(final Class clazz, int id) {
		return selectId(clazz, id, "");
	}

	private static <T> T selectId(final Class clazz, int id, String whereClause) {

		String json = executeSelect(clazz, SQLUtil.selectIdCommand(clazz, id));

		GeneralFunctions.showLog("selectId --> " + json);

		return (T) gson.fromJson(json, clazz);
	}

	public static boolean isDataUnique(final Class arrayClazz, QueryParameter qp) {
		String whereClause = SQLUtil.getSQLQuery(qp);
		Class modelClazz = arrayClazz.getComponentType();
		String json = executeSelect(modelClazz, SQLUtil.selectAllCommand(modelClazz, whereClause));

		return json.isEmpty();
	}

	private static String executeSelect(Class clazz, String sql) {

		String fieldName;
		StringBuilder jsonBuilder = new StringBuilder();
		boolean isFirstColumn;
		Field[] fields = clazz.getDeclaredFields();
		int countReg = 0;

		GeneralFunctions.showLog("");
		GeneralFunctions.showLog(sql);
		GeneralFunctions.showCurrentTimestamp(1001);
		if (connect()) {
			GeneralFunctions.showCurrentTimestamp(1002);

			try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
				GeneralFunctions.showCurrentTimestamp(1003);
				
				while (rs.next()) {
					
					countReg++;

					if (jsonBuilder.length() != 0) {
						jsonBuilder.append(",");
					}

					jsonBuilder.append("{");

					isFirstColumn = true;
					for (int i = 0; i < fields.length; i++) {

						try {

							fieldName = fields[i].getName();

							if (!isFirstColumn) {
								jsonBuilder.append(",");
							} else {
								isFirstColumn = false;
							}

							jsonBuilder.append("\"");
							jsonBuilder.append(fieldName);
							jsonBuilder.append("\"");
							jsonBuilder.append(":");
							jsonBuilder.append("\"");
							jsonBuilder.append(rs.getString(fieldName));
							jsonBuilder.append("\"");
						} catch (Exception e) {
							GeneralFunctions.showLog("ERROR SELECT: " + e.getMessage());
						}
					}
					jsonBuilder.append("}");
				}
				GeneralFunctions.showCurrentTimestamp(1004);
				GeneralFunctions.showLog(countReg + " registros.");
			} catch (SQLException e) {
				GeneralFunctions.showLog(e.getMessage());
			}

			disconnect();
		}
		return jsonBuilder.toString();
	}

	public static int insert(final Class clazz, List obj) {

		int topId = selectTopId(clazz);

		String sql = SQLUtil.insertCommand(obj, clazz, topId);

		if (connect()) {

			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

				conn.setAutoCommit(false);

				pstmt.executeUpdate();
				GeneralFunctions.showLog("Inserido: " + sql);

				conn.commit();

			} catch (SQLException e) {

				// conn.rollback();
				GeneralFunctions.showLog("Erro Insert: " + sql);
				GeneralFunctions.showLog(e.getMessage());
				topId = -1;
			}

			disconnect();
		}

		return topId;
	}

	public static void updateId(final Class clazz, Object obj) {

		if (connect()) {

			if (obj.getClass().getTypeName().contains("ArrayList")) {

				JsonArray dataset = gson.toJsonTree(obj).getAsJsonArray();
				Iterator<JsonElement> iterator = dataset.iterator();

				while (iterator.hasNext()) {

					JsonObject jObj = iterator.next().getAsJsonObject();

					String sql = SQLUtil.updateIdCommand(jObj, clazz);

					executeUpdate(sql);
				}
			} else {

				JsonObject jObj = gson.toJsonTree(obj).getAsJsonObject();

				String sql = SQLUtil.updateIdCommand(jObj, clazz);

				executeUpdate(sql);
			}

			disconnect();
		}
	}

	private static void executeUpdate(String sql) {

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.executeUpdate();
			GeneralFunctions.showLog("Atualizando: " + sql);

		} catch (SQLException e) {
			GeneralFunctions.showLog(e.getMessage());
		}
	}

	public static void delete(final Class clazz, Object obj) {

		if (connect()) {

			JsonArray dataset = gson.toJsonTree(obj).getAsJsonArray();
			Iterator<JsonElement> iterator = dataset.iterator();

			while (iterator.hasNext()) {

				JsonObject jObj = iterator.next().getAsJsonObject();

				String sql = SQLUtil.deleteCommand(jObj, clazz);

				try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

					pstmt.executeUpdate();
					GeneralFunctions.showLog("Deletado: " + sql);

				} catch (SQLException e) {
					GeneralFunctions.showLog(e.getMessage());
				}
			}

			disconnect();
		}
	}

	public static void deleteAll(final Class clazz) {

		if (connect()) {

			String sql = SQLUtil.deleteCommand(clazz);

			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

				pstmt.executeUpdate();
				GeneralFunctions.showLog("Deletado: " + sql);

			} catch (SQLException e) {
				GeneralFunctions.showLog(e.getMessage());
			}
			disconnect();
		}
	}

	public static void deleteId(final Class clazz, int id) {

		if (connect()) {

			String sql = SQLUtil.deleteIdCommand(clazz, id);

			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

				pstmt.executeUpdate();
				GeneralFunctions.showLog("Deletado: " + sql);

			} catch (SQLException e) {
				GeneralFunctions.showLog("Commando com erro: " + sql);
				GeneralFunctions.showLog(e.getMessage());
			}

			disconnect();
		}
	}

	public static void prepareDatabase() {
		createNewTables();
	}

	private static void createNewTables() {

        String sql;
        String version = "";

    	if ("2.0".equals(version)) {

	        sql = "CREATE TABLE IF NOT EXISTS stocks (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + "	codStock text NOT NULL,\n"
	                + "	name text NOT NULL,\n"
	                + "	companyName text NOT NULL,\n"
	                + "	actualPrice real NOT NULL,\n"
	                + "	updateAt text\n"
	                + ");";
	        
	        executeSqlStatement(sql);
    	}

    	if ("1.1".equals(version)) {

	        sql = "CREATE TABLE IF NOT EXISTS stocksGain (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + "	codStock text NOT NULL,\n"
	                + "	typeGain text NOT NULL,\n"
	                + "	datGain text,\n"
	                + "	valGain real\n"
	                + ");";
	        
	        executeSqlStatement(sql);
    	}

        if ("1.0".equals(version)) {
        	
	        sql = "CREATE TABLE IF NOT EXISTS stocksOperation (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + "	codStock text NOT NULL,\n"
	                + "	typeOperation text NOT NULL,\n"
	                + "	datOperation text,\n"
	                + "	quantity integer,\n"
	                + "	valStock real,\n"
	                + "	valCost real\n"
	                + ");";
	        
	        executeSqlStatement(sql);
	
	        sql = "CREATE TABLE IF NOT EXISTS budget (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + "	codBudget text NOT NULL,\n"
	                + "	datIni text,\n"
	                + "	datEnd text,\n"
	                + "	version text\n"
	                + ");";
	        
	        executeSqlStatement(sql);
	
	        sql = "CREATE TABLE IF NOT EXISTS budgetShortcut (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + "	shortcut text NOT NULL,\n"
	                + "	splitter text,\n"
	                + "	codItem text,\n"	//Campo da BudgetItem
	                + "	grpItem text\n"		//Campo da BudgetItem
	                + ");";
	        
	        executeSqlStatement(sql);
			
	
	        sql = "CREATE TABLE IF NOT EXISTS budgetItem (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + "	idBudget text NOT NULL,\n"
	                + "	codItem text NOT NULL,\n"
	                + " valItem real,\n"
	                + "	grpItem text,\n"
	                + "	type text,\n"
	                + "	dayVencto integer\n"
	                + ");";
	        
	        executeSqlStatement(sql);
	
	        sql = "CREATE TABLE IF NOT EXISTS movement (\n"
	                + "	id integer PRIMARY KEY,\n"
	                + "	description text NOT NULL,\n"
	                + "	datMovement text NOT NULL,\n"
	                + "	datFinancial text,\n"
	                + " origin text,\n"
	                + "	valMovement real,\n"
	                + "	typeMovement text,\n"
	                + "	documentNumber text,\n"
	                + "	splitted integer,\n"
	                + "	valTotal real,\n"
	                + "	codItem text,\n"	//Campo da BudgetItem
	                + "	grpItem text\n"		//Campo da BudgetItem
	                + ");";        
	
	        executeSqlStatement(sql);
		}
	}

	public static void executeSqlStatement(String sql) {

		if (connect()) {

			try (Statement stmt = conn.createStatement()) {

				Boolean result = stmt.execute(sql);
				GeneralFunctions.showLog("Statement excetuded: " + sql);
				GeneralFunctions.showLog("Statement excetuded: " + result);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

			disconnect();
		}
	}

	private static boolean connect() {

		String url = "jdbc:sqlite:" + databaseInfo.DB_DIR + databaseInfo.DB_NAME;

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(url);
			if (conn != null) {

				DatabaseMetaData meta = conn.getMetaData();
				// GeneralFunctions.showLog("The driver name is " + meta.getDriverName());
				GeneralFunctions.showLog("Database connected.");
				return true;
			}
		} catch (SQLException e) {
			GeneralFunctions.showLog(e.getMessage());
		} catch (Exception e) {
			GeneralFunctions.showLog(e.getMessage());
		}

		return false;
	}

	private static void disconnect() {

		try {
			if (conn != null) {
				conn.close();
                GeneralFunctions.showLog("Database disconnected.");
			}
		} catch (SQLException ex) {
			GeneralFunctions.showLog(ex.getMessage());
		}
	}

	private static int selectTopId(final Class clazz) {

		int topId = 0;

		String sql = SQLUtil.selectMaxIdCommand(clazz);

		if (connect()) {

			try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

				while (rs.next()) {
					topId = rs.getInt(1);
				}

			} catch (SQLException e) {
				GeneralFunctions.showLog(e.getMessage());
			}

			disconnect();
		}

		return topId;
	}
}

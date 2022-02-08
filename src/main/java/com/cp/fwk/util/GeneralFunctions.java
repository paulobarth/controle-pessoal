package com.cp.fwk.util;

import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class GeneralFunctions {

	private static final boolean showLog = false;
	private static final String[] TEXTMONTHLIST = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
	public static final String[] ORIGIN_LIST = new String[]{ "Conta Corrente - Santander", "Cartão Crédito - Santander",
			"Cartão Crédito - NuBank Jaque", "Cartão Crédito - Porto Seguro" };
	public static final String GAIN = "GAIN";
	public static final String LOSS = "LOSS";

	public static String stringDatetoSql(String date) {
		if (date == null) {
			return "";
		}
		if (date.isEmpty()) {
			return "";
		}
    	java.sql.Date dateSql = new Date(new java.util.Date(date).getTime());
		return dateSql.toString();
	}
	
	public static String sqlDateToString(String date) {
		
		String text = "";

		String[] dataSplit = date.split("-");
		
		if (dataSplit.length == 3) {
			
			java.sql.Date dateSql = new java.sql.Date(
					Integer.parseInt(dataSplit[0]) - 2000 + 100,
					Integer.parseInt(dataSplit[1]) - 1,
					Integer.parseInt(dataSplit[2]));
		
	    	DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	    	
	    	text = df.format(dateSql);
		}
		return text;
	}

	public static String getTodaySqlDate() {
		return new java.sql.Date(new java.util.Date().getTime()).toString();
	}
	
	public static void showLog (String text ) {
		if (showLog) {
			System.out.println(text);
		}
	}

	public static String getMonthOfDate (String stringDate) {
		return getMonthOfSqlDate(stringDatetoSql(stringDate));
	}

	public static String getYearMonthOfDate (String stringDate) {
		return getYearMonthOfSqlDate(stringDatetoSql(stringDate));
	}

	public static String getDayOfSqlDate(String sqlDate) {
		return getInfoFromSqlDate(sqlDate, 2);
	}

	public static String getMonthOfSqlDate (String sqlDate) {
		return getInfoFromSqlDate(sqlDate, 1);
	}

	private static String getInfoFromSqlDate (String sqlDate, int position) {
		String[] dataSplit = sqlDate.split("-");
		if (dataSplit.length == 3) {
			return dataSplit[position]; 
		}
		return "";
	}

	public static String getYearMonthOfSqlDate (String sqlDate) {
		String[] dataSplit = sqlDate.split("-");
		
		if (dataSplit.length == 3) {
			return dataSplit[0] + dataSplit[1]; 
		}
		
		return "";
	}

	public static double truncDouble(double d, int casas_decimais) {

		int var1 = (int) d; // Remove a parte decimal do número... 2.3777 fica 2
		double var2 = var1 * Math.pow(10, casas_decimais); // adiciona zeros..2.0 fica 200.0
		double var3 = (d - var1) * Math.pow(10,
				casas_decimais); /**
									 * Primeiro retira a parte decimal fazendo 2.3777 - 2 ..fica 0.3777, depois
									 * multiplica por 10^(casas decimais) por exemplo se o número de casas decimais
									 * que queres considerar for 2, então fica 0.3777*10^2 = 37.77
									 **/
		int var4 = (int) var3; // Remove a parte decimal da var3, ficando 37
		int var5 = (int) var2; // Só para não haver erros de precisão: 200.0 passa a 200
		int resultado = var5 + var4; // O resultado será 200+37 = 237
		double resultado_final = resultado / Math.pow(10, casas_decimais); // Finalmente divide-se o resultado pelo
																			// número de casas decimais, 237/100 = 2.37
		return resultado_final; // Retorna o resultado_final :P
	}
	
//	Converte de dd/mm/aaaa para mm/dd/aaaa
	public static String convertDateBRToUS (String date) {
		String[] splitDate = date.split("/");
		return splitDate[1] + "/" + splitDate[0] + "/" + splitDate[2];
	}

	public static String convertMonthToText (String month) {
		return convertMonthToText(Integer.parseInt(month));
	}

	public static String convertMonthToText (int month) {
		return TEXTMONTHLIST[month - 1];
	}

	public static Double round(double d, int i) {
		DecimalFormat df = new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return Double.valueOf(df.format(d));
	}
	
	private static long lastTime = 0;
	public static void showCurrentTimestamp(int i) {
		long now = System.currentTimeMillis();
		if (lastTime == 0) {
			lastTime = now;
		}
		System.out.print("Step: " + i + " - " + new Timestamp(now));
		System.out.println(" - " + ((now - lastTime)) + " segundos após.");
		lastTime = now;
	}

}

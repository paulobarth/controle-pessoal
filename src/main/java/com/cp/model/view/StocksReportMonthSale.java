package com.cp.model.view;

import java.util.ArrayList;
import java.util.List;

import com.cp.fwk.util.GeneralFunctions;

public class StocksReportMonthSale {

	private String period;
	private Double value;
	private boolean exceeded;
	private String periodDescription;
	private List<SalesPerStocks> salesPerStocks = new ArrayList<SalesPerStocks>();

	private static Double LIMITVALUE = 20000.0;

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
		String year = period.substring(0, 4);
		String month = period.substring(4, 6);

		this.periodDescription = GeneralFunctions.convertMonthToText(month) + " de " + year;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
		this.exceeded = this.value >= LIMITVALUE;
	}

	public void sumValue(Double value) {
		setValue(getValue() + value);
	}

	public boolean isExceeded() {
		return exceeded;
	}

	public String getPeriodDescription() {
		return periodDescription;
	}

	public List<SalesPerStocks> getSalesPerStocks() {
		return salesPerStocks;
	}

	public void setSalesPerStocks(List<SalesPerStocks> salesPerStocks) {
		this.salesPerStocks = salesPerStocks;
	}

	public void addStockSale(String codStock, String datOperation, Double valSale) {
		SalesPerStocks sale = new SalesPerStocks();
		sale.setCodStock(codStock);
		sale.setDatOperation(datOperation);
		sale.setValue(valSale);
		this.getSalesPerStocks().add(sale);
	}

	public class SalesPerStocks {

		private String codStock;
		private String datOperation;
		private Double value;
		private String dayOperation;

		public String getCodStock() {
			return codStock;
		}

		public void setCodStock(String codStock) {
			this.codStock = codStock;
		}

		public String getDatOperation() {
			return GeneralFunctions.sqlDateToString(datOperation);
		}

		public void setDatOperation(String datOperation) {
			this.datOperation = GeneralFunctions.stringDatetoSql(datOperation);
			this.dayOperation = GeneralFunctions.getDayOfSqlDate(this.datOperation);
		}

		public Double getValue() {
			return value;
		}

		public void setValue(Double value) {
			this.value = value;
		}

		public String getDayOperation() {
			return dayOperation;
		}
	}
}

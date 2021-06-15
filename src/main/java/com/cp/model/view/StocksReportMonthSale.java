package com.cp.model.view;

import java.util.ArrayList;
import java.util.List;

import com.cp.fwk.util.GeneralFunctions;

public class StocksReportMonthSale {

	private String period;
	private Double value;
	private boolean exceeded;
	private String periodDescription;
	private boolean showTaxButton;
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

	public boolean isShowTaxButton() {
		return showTaxButton;
	}

	public void setShowTaxButton(boolean showTaxButton) {
		this.showTaxButton = showTaxButton;
	}

	public List<SalesPerStocks> getSalesPerStocks() {
		return salesPerStocks;
	}

	public void setSalesPerStocks(List<SalesPerStocks> salesPerStocks) {
		this.salesPerStocks = salesPerStocks;
	}

	public void addStockSale(String codStock, String datOperation, String datSettlement, Double valSale, Double resultSell) {
		SalesPerStocks sale = new SalesPerStocks();
		sale.setCodStock(codStock);
		sale.setDatSettlement(datSettlement);
		sale.setDatOperation(datOperation);
		sale.setValue(valSale);
		sale.setResultSell(resultSell);
		this.getSalesPerStocks().add(sale);
	}

	public class SalesPerStocks {

		private String codStock;
		private String datOperation;
		private String datSettlement;
		private Double value;
		private Double resultSell;
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
			defineDatyOperation();
		}

		public String getDatSettlement() {
			return GeneralFunctions.sqlDateToString(datSettlement);
		}

		public void setDatSettlement(String datSettlement) {
			this.datSettlement = GeneralFunctions.stringDatetoSql(datSettlement);
			defineDatyOperation();
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

		private void defineDatyOperation() {
			if (this.datSettlement != null && this.datOperation != null) {
				this.dayOperation = GeneralFunctions.getDayOfSqlDate(this.datSettlement);
				if (!this.datSettlement.equals(this.datOperation)) {
					this.dayOperation += "  Oper. " + GeneralFunctions.getDayOfSqlDate(this.datOperation) + "/" +
													  GeneralFunctions.getMonthOfSqlDate(this.datOperation);
				}
			}
		}

		public Double getResultSell() {
			return resultSell;
		}

		public void setResultSell(Double resultSell) {
			this.resultSell = resultSell;
		}
	}
}

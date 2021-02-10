package com.cp.model.view;

import java.util.ArrayList;
import java.util.List;

import com.cp.model.StocksOperation;

public class StocksReportOperation {

	private String codStock;
	private List<StocksOper> stocksList = new ArrayList<StocksOper>();

	public String getCodStock() {
		return codStock;
	}
	public void setCodStock(String codStock) {
		this.codStock = codStock;
	}
	public List<StocksOper> getStocksList() {
		return stocksList;
	}
	public void setStocksList(StocksOper stocksList) {
		this.stocksList.add(stocksList);
	}

	public class StocksOper extends StocksOperation {

		private Double totalOperation;
		private Double totalOperCost;
		private Double medPrice;
		private Double resultUnit;
		private Double resultTotal;
		private Double resultSell;

		public Double getTotalOperation() {
			return totalOperation;
		}

		public void setTotalOperation(Double totalOperation) {
			this.totalOperation = totalOperation;
		}

		public Double getTotalOperCost() {
			return totalOperCost;
		}

		public void setTotalOperCost(Double totalOperCost) {
			this.totalOperCost = totalOperCost;
		}

		public Double getMedPrice() {
			return medPrice;
		}

		public void setMedPrice(Double medPrice) {
			this.medPrice = medPrice;
		}

		public Double getResultUnit() {
			return resultUnit;
		}

		public void setResultUnit(Double resultUnit) {
			this.resultUnit = resultUnit;
		}

		public Double getResultTotal() {
			return resultTotal;
		}

		public void setResultTotal(Double resultTotal) {
			this.resultTotal = resultTotal;
		}

		public Double getResultSell() {
			return resultSell;
		}

		public void setResultSell(Double resultSell) {
			this.resultSell = resultSell;
		}
	}
}

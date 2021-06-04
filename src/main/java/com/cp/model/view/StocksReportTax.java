package com.cp.model.view;

import java.util.ArrayList;
import java.util.List;

import com.cp.model.StocksOperation;
import com.cp.model.StocksTax;

public class StocksReportTax {
	
	private TaxReport stocksTax;
	private List<StocksOperation> stocksSellList = new ArrayList<StocksOperation>();
	private List<StocksOperation> stocksDeductionList = new ArrayList<StocksOperation>();

	public TaxReport getStocksTax() {
		return stocksTax;
	}
	public void setStocksTax(TaxReport stocksTax) {
		this.stocksTax = stocksTax;
	}
	public List<StocksOperation> getStocksSellList() {
		return stocksSellList;
	}
	public void addStocksSell(StocksOperation stocksOperationSell) {
		this.stocksSellList.add(stocksOperationSell);
	}
	public List<StocksOperation> getStocksDeductionList() {
		return stocksDeductionList;
	}
	public void addStocksDeductionList(StocksOperation stocksOperationDeduction) {
		this.stocksDeductionList.add(stocksOperationDeduction);
	}

	public class TaxReport extends StocksTax {
		private double totalSell = 0.0;
		private double profitSell = 0.0;
		private double resultSell = 0.0;
		private double resultWithDeduction = 0.0;
		public double getTotalSell() {
			return totalSell;
		}
		public void setTotalSell(double totalSell) {
			this.totalSell = totalSell;
		}
		public double getProfitSell() {
			return profitSell;
		}
		public void setProfitSell(double profitSell) {
			this.profitSell = profitSell;
		}
		public double getResultSell() {
			return resultSell;
		}
		public void setResultSell(double resultSell) {
			this.resultSell = resultSell;
		}
		public double getResultWithDeduction() {
			return resultWithDeduction;
		}
		public void setResultWithDeduction(double resultWithDeduction) {
			this.resultWithDeduction = resultWithDeduction;
		}
	}
}
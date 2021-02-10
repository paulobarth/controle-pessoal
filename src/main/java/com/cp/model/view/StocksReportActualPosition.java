package com.cp.model.view;

public class StocksReportActualPosition {

	private String codStock;
	private int actualQuantity;
	private Double medPrice;

	// Dados para calculo percentual LP ao longo do tempo
	private Double totalBuy = 0.0;
	private Double totalSell = 0.0;
	private Double totalSellMedPrice = 0.0;
	private Double totalResultSell = 0.0;
	private Double result;
	private boolean dangerResult;

	public String getCodStock() {
		return codStock;
	}

	public void setCodStock(String codStock) {
		this.codStock = codStock;
	}

	public int getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(int actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public Double getMedPrice() {
		return medPrice;
	}

	public void setMedPrice(Double medPrice) {
		this.medPrice = medPrice;
	}

	public Double getTotalBuy() {
		return totalBuy;
	}

	public void addTotalBuy(Double totalBuy) {
		this.totalBuy += totalBuy;
	}

	public Double getTotalSell() {
		return totalSell;
	}

	public void addTotalSell(Double qtTotalSell) {
		this.totalSell += qtTotalSell;
	}

	public Double getTotalSellMedPrice() {
		return totalSellMedPrice;
	}

	public void addTotalSellMedPrice(Double totalSellMedPrice) {
		this.totalSellMedPrice += totalSellMedPrice;
	}

	public Double getTotalResultSell() {
		return totalResultSell;
	}

	public void addTotalResultSell(Double totalResultSell) {
		this.totalResultSell += totalResultSell;
	}

	public Double getResult() {
		return result;
	}

	public boolean isDangerResult() {
		return dangerResult;
	}

	public void setDangerResult(boolean dangerResult) {
		this.dangerResult = dangerResult;
	}

	public void calculateResult() {
		this.result = 0.0;
		if (getTotalSell() != 0) {
			this.result = (getTotalResultSell() / getTotalSellMedPrice());
			this.result = this.result * 100;
		}
		if (this.result < 0) {
			setDangerResult(true);
		}
	}

}

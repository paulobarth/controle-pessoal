package com.cp.model;

import com.cp.fwk.util.GeneralFunctions;

public class StocksTaxOperation {

	private int id;
	private int idStocksTax;
	private int idStocksOperation;
	private String taxOperationType;  // GAIN LOSS
	private double valIRLossConsumed;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdStocksTax() {
		return idStocksTax;
	}
	public void setIdStocksTax(int idStocksTax) {
		this.idStocksTax = idStocksTax;
	}
	public int getIdStocksOperation() {
		return idStocksOperation;
	}
	public void setIdStocksOperation(int idStocksOperation) {
		this.idStocksOperation = idStocksOperation;
	}
	public String getTaxOperationType() {
		return taxOperationType;
	}
	public void setTaxOperationGainType() {
		setTaxOperationType(GeneralFunctions.GAIN);
	}
	public void setTaxOperationLossType() {
		setTaxOperationType(GeneralFunctions.LOSS);
	}
	private void setTaxOperationType(String taxOperationType) {
		this.taxOperationType = taxOperationType;
	}
	public double getValIRLossConsumed() {
		return valIRLossConsumed;
	}
	public void setValIRLossConsumed(double valIRLossConsumed) {
		this.valIRLossConsumed = valIRLossConsumed;
	}
}

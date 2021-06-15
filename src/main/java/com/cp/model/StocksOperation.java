package com.cp.model;

import com.cp.fwk.util.GeneralFunctions;

public class StocksOperation {

	private int id;
	private String codStock;
	private String typeOperation;
	private String datOperation;
	private String datSettlement;
	private int quantity;
	private double valStock;
	private double valCost;
	private double valResultSell;
	private double valTotalIRLossConsumed;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTypeOperation() {
		return typeOperation;
	}
	public void setTypeOperation(String typeOperation) {
		this.typeOperation = typeOperation;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public void setQuantity(String quantity) {
		if (quantity.isEmpty()) {
			quantity = "0";
		}
		setQuantity(Integer.parseInt(quantity));
	}
	public double getValStock() {
		return valStock;
	}
	public void setValStock(double valStock) {
		this.valStock = valStock;
	}
	public void setValStock(String valStock) {
		if (valStock.isEmpty()) {
			valStock = "0";
		}
		setValStock(Double.parseDouble(valStock));
	}
	public double getValCost() {
		return valCost;
	}
	public void setValCost(double valCost) {
		this.valCost = valCost;
	}
	public void setValCost(String valCost) {
		if (valCost.isEmpty()) {
			valCost = "0";
		}
		setValCost(Double.parseDouble(valCost));
	}
	public String getDatOperation() {
		return GeneralFunctions.sqlDateToString(datOperation);
	}
	public void setDatOperation(String datOperation) {
		this.datOperation = GeneralFunctions.stringDatetoSql(datOperation);
	}
	public String getDatSettlement() {
		return GeneralFunctions.sqlDateToString(datSettlement);
	}
	public void setDatSettlement(String datSettlement) {
		this.datSettlement = GeneralFunctions.stringDatetoSql(datSettlement);
	}
	public String getCodStock() {
		return codStock;
	}
	public void setCodStock(String codStock) {
		this.codStock = codStock;
	}
	public double getValResultSell() {
		return valResultSell;
	}
	public void setValResultSell(double valResultSell) {
		this.valResultSell = valResultSell;
	}
	public double setValTotalIRLossConsumed(Double valTotalIRLossConsumed) {
		return this.valTotalIRLossConsumed = valTotalIRLossConsumed;
	}	
	public double getValTotalIRLossConsumed() {
		return valTotalIRLossConsumed;
	}	
	public double consumeValIRLoss(double valIR) {
		if ((getValResultSell() >= 0) || (valIR >= 0)) {
			return 0.0;
		}
		double newValIRLossConsumed = 0.0; 
		if (valIR + getValTotalIRLossConsumed() > getValResultSell()) {
			newValIRLossConsumed = valIR;
		} else {
			double sdo = getValResultSell() - getValTotalIRLossConsumed();
			if (sdo <= valIR) {
				newValIRLossConsumed = valIR;
			} else {
				newValIRLossConsumed = sdo;
			}
		}
		this.valTotalIRLossConsumed += newValIRLossConsumed;
		return newValIRLossConsumed;
	}
}

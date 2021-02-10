package com.cp.model;

import com.cp.fwk.util.GeneralFunctions;

public class StocksOperation {

	private int id;
	private String codStock;
	private String typeOperation;
	private String datOperation;
	private int quantity;
	private Double valStock;
	private Double valCost;

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
	public Double getValStock() {
		return valStock;
	}
	public void setValStock(Double valStock) {
		this.valStock = valStock;
	}
	public void setValStock(String valStock) {
		if (valStock.isEmpty()) {
			valStock = "0";
		}
		setValStock(Double.parseDouble(valStock));
	}
	public Double getValCost() {
		return valCost;
	}
	public void setValCost(Double valCost) {
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
	public String getCodStock() {
		return codStock;
	}
	public void setCodStock(String codStock) {
		this.codStock = codStock;
	}
}

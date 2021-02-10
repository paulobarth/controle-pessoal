package com.cp.model;

import com.cp.fwk.util.GeneralFunctions;

public class StocksGain {

	private int id;
	private String codStock;
	private String typeGain;
	private String datGain;
	private Double valGain;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCodStock() {
		return codStock;
	}
	public void setCodStock(String codStock) {
		this.codStock = codStock;
	}
	public String getTypeGain() {
		return typeGain;
	}
	public void setTypeGain(String typeGain) {
		this.typeGain = typeGain;
	}
	public Double getValGain() {
		return valGain;
	}
	public void setValGain(Double valGain) {
		this.valGain = valGain;
	}
	public void setValGain(String valGain) {
		if (valGain.isEmpty()) {
			valGain = "0";
		}
		setValGain(Double.parseDouble(valGain));
	}
	public String getDatGain() {
		return GeneralFunctions.sqlDateToString(datGain);
	}
	public void setDatGain(String datGain) {
		this.datGain = GeneralFunctions.stringDatetoSql(datGain);
	}
}
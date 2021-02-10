package com.cp.model;

import com.cp.fwk.util.GeneralFunctions;

public class Movement {
	
	private int id;
	private String description;
	private String datMovement;
	private String datFinancial;
	private String origin;
	private Double valMovement;
	private String typeMovement;
	private String documentNumber;
	private int splitted;
	private Double valTotal;
	private String codItem;
	private String grpItem;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDatMovement() {
		return GeneralFunctions.sqlDateToString(datMovement);
	}
	public void setDatMovement(String datMovement) {
		this.datMovement = GeneralFunctions.stringDatetoSql(datMovement);
	}
	public String getDatFinancial() {
		return GeneralFunctions.sqlDateToString(datFinancial);
	}
	public void setDatFinancial(String datFinancial) {
		this.datFinancial = GeneralFunctions.stringDatetoSql(datFinancial);
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public Double getValMovement() {
		return valMovement;
	}
	public void setValMovement(Double valMovement) {
		this.valMovement = valMovement;
	}
	public void setValMovement(String valMovement) {
		if (valMovement.isEmpty()) {
			valMovement = "0";
		}
		this.valMovement = Double.parseDouble(valMovement);
	}
	public String getTypeMovement() {
		return typeMovement;
	}
	public void setTypeMovement(String typeMovement) {
		this.typeMovement = typeMovement;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	public int getSplitted() {
		return splitted;
	}
	public void setSplitted(int splitted) {
		this.splitted = splitted;
	}
	public void setSplitted(String splitted) {
		if (splitted.isEmpty()) {
			splitted = "0";
		}
		this.splitted = Integer.parseInt(splitted);
	}	
	public Double getValTotal() {
		return valTotal;
	}
	public void setValTotal(Double valTotal) {
		this.valTotal = valTotal;
	}
	public void setValTotal(String valTotal) {
		if (valTotal.isEmpty()) {
			valTotal = "0";
		}
		this.valTotal = Double.parseDouble(valTotal);
	}
	public String getCodItem() {
		return codItem;
	}
	public void setCodItem(String codItem) {
		this.codItem = codItem;
	}
	public String getGrpItem() {
		return grpItem;
	}
	public void setGrpItem(String grpItem) {
		this.grpItem = grpItem;
	}
}

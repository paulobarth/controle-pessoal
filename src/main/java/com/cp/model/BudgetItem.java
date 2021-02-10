package com.cp.model;

public class BudgetItem {
	
	private int id;
	private int idBudget;
	private String codItem;
	private Double valItem;
	private String grpItem;
	private String type;
	private int dayVencto;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdBudget() {
		return idBudget;
	}
	public void setIdBudget(int idBudget) {
		this.idBudget = idBudget;
	}
	public String getCodItem() {
		return codItem;
	}
	public void setCodItem(String codItem) {
		this.codItem = codItem;
	}
	public Double getValItem() {
		return valItem;
	}
	public void setValItem(Double valItem) {
		this.valItem = valItem;
	}
	public void setValItem(String valItem) {
		this.valItem = Double.valueOf(valItem);
	}
	public String getGrpItem() {
		return grpItem;
	}
	public void setGrpItem(String grpItem) {
		this.grpItem = grpItem;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getDayVencto() {
		return dayVencto;
	}
	public void setDayVencto(int dayVencto) {
		this.dayVencto = dayVencto;
	}
	public void setDayVencto(String dayVencto) {
		if (dayVencto.isEmpty()) {
			dayVencto = "0";
		}
		this.dayVencto = Integer.parseInt(dayVencto);
	}
}

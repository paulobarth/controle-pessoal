package com.cp.model.view;

public class BudgetMovementDetail {

	private String description;
	private int month;
	private BudgetMovementValue[] listValue;

	public BudgetMovementDetail(int qtdMonths) {
		this.listValue = new BudgetMovementValue[qtdMonths + 1];
		for (int month = 0; month <= qtdMonths; month++) {
			
			this.listValue[month] = new BudgetMovementValue();
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BudgetMovementValue[] getListValue() {
		return listValue;
	}

	public void setValue(int month, int day, Double valMovement) {
		this.listValue[month].setValMovement(valMovement);
		this.listValue[month].setDay(day);
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}
}

package com.cp.model.view;

import java.math.BigDecimal;

public class BudgetMovementValue {

	private BigDecimal valMovement = new BigDecimal(0.00);
	private int day;

	public BigDecimal getValMovement() {
		return valMovement;
	}
	public void setValMovement(Double valMovement) {
		BigDecimal newValue = new BigDecimal(Double.toString(valMovement));
		this.valMovement = this.valMovement.add(newValue);
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
}

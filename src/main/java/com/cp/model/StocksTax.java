package com.cp.model;

public class StocksTax {

	private int id;
	private String taxType;
	private int month;
	private int year;
	private Double valTotalPayment;
	private Double valTotalDeduction;
	private int paymentStatus; // 1 Calculado  2 Pago

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTaxType() {
		return taxType;
	}
	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Double getValTotalPayment() {
		return valTotalPayment;
	}
	public void setValTotalPayment(Double valTotalPayment) {
		this.valTotalPayment = valTotalPayment;
	}
	public Double getValTotalDeduction() {
		return valTotalDeduction;
	}
	public void setValTotalDeduction(Double valTotalDeduction) {
		this.valTotalDeduction = valTotalDeduction;
	}
	public int getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}	
}

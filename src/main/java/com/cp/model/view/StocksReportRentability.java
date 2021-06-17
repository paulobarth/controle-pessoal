package com.cp.model.view;

public class StocksReportRentability {
	
	private String period;
	private double maxCashInvested = 0.0;
	private double periodResult = 0.0;
	private double periodRentability = 0.0;
	private double accumRentability = 0.0;
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public double getMaxCashInvested() {
		return maxCashInvested;
	}
	public void setMaxCashInvested(double maxCashInvested) {
		this.maxCashInvested = maxCashInvested;
	}
	public double getPeriodResult() {
		return periodResult;
	}
	public void sumPeriodResult(double periodResult) {
		this.periodResult += periodResult;
	}
	public double getPeriodRentability() {
		return periodRentability;
	}
	public void setPeriodRentability(double periodRentability) {
		this.periodRentability = periodRentability;
	}
	public double getAccumRentability() {
		return accumRentability;
	}
	public void setAccumRentability(double accumRentability) {
		this.accumRentability = accumRentability;
	}
}

package com.cp.model;

import com.cp.fwk.util.GeneralFunctions;

public class Budget {
	
	private int id;
	private String codBudget;
	private String version;
	private String datIni;
	private String datEnd;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCodBudget() {
		return codBudget;
	}
	public void setCodBudget(String codBudget) {
		this.codBudget = codBudget;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDatIni() {
		return GeneralFunctions.sqlDateToString(datIni);
	}
	public void setDatIni(String datIni) {
		this.datIni = GeneralFunctions.stringDatetoSql(datIni);
	}
	public String getDatEnd() {
		return GeneralFunctions.sqlDateToString(datEnd);
	}
	public void setDatEnd(String datEnd) {
		this.datEnd = GeneralFunctions.stringDatetoSql(datEnd);
	}
}

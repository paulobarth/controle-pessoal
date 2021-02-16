package com.cp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseControllerImpl implements BaseController {
	
	protected static HttpServletRequest request;
	protected static HttpServletResponse response;
	protected static String option;

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response, String option)
			throws ServletException, IOException {

		this.request = request;
		this.response = response;
		this.option = option;
		
		switch (option) {
		case "list":
			list();
			break;
		case "save":
			save();
			break;
		case "update":
			update();
			break;
		case "delete":
			delete();
			break;
		case "filter":
			filter();
			break;

		default:
			specificOptionToExecute();
			break;
		}

		executeCallBack();
	}

	protected void specificOptionToExecute() {};
	protected void list() {};
	protected void save() {};
	protected void update() {};
	protected void delete() {};
	protected void filter() {};
}


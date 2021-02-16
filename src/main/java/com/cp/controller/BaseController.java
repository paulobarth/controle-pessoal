package com.cp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface BaseController {

	void execute(HttpServletRequest request, HttpServletResponse response, String option) 
			throws ServletException, IOException;

	void executeCallBack() throws ServletException, IOException;	
}

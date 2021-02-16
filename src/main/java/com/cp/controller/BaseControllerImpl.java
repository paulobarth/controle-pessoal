package com.cp.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseControllerImpl implements BaseController {
	
	protected static HttpServletRequest request;
	protected static HttpServletResponse response;
	protected static String option;
	protected Map<String, String> filterMap = new HashMap<String, String>();

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
	protected void filter() {
		storeRequestFilterParameters();
		request.setAttribute("filterCollapsed", "true");
		sendMapFilterParametersToRequest();
		applyFilterMovement();
	};

	protected void storeRequestFilterParameters() {

		filterMap.clear();
		Enumeration<?> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();

			// Get the value of the attribute
			Object value = request.getParameter(name);

//			if (value instanceof Map) {
//				for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
//					System.out.println(entry.getKey() + "=" + entry.getValue());
//				}
//			} else if (value instanceof List) {
//				for (Object element : (List) value) {
//					System.out.println(element);
//				}
//			}
			if (value instanceof String) {

				if (name.startsWith("filter")) {
//					request.setAttribute(name, value);
					filterMap.put(name, value.toString());
//					System.out.println(value);
				}
			}
		}
	}

	protected void sendMapFilterParametersToRequest() {
		for (String key : filterMap.keySet()) {
			request.setAttribute(key, filterMap.get(key));
		}
	}

	protected void applyFilterMovement() {};
}


package com.cp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.fwk.data.DataManager;
import com.cp.model.Budget;

public class BudgetController {

	public static void execute(HttpServletRequest request, HttpServletResponse response, String option)
			throws ServletException, IOException {

		switch (option) {
		case "list":
			selectAll(request, response);
			break;
		case "save":
			saveBudget(request, response);
			break;
		case "update":
			updateBudget(request, response);
			break;
		case "delete":
			deleteBudget(request, response);
			break;

		default:
			break;
		}

		if (option.equals("list") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/budget.jsp").forward(request, response);
		} else if (option.equals("item")) {

			String bi = request.getParameter("id");

//			request.getRequestDispatcher("/WEB-INF/views/budgetItem.jsp").forward(request, response);
			response.sendRedirect("/controle-pessoal/budgetItem.list?budgetId=" + bi);
		} else {
			response.sendRedirect("/controle-pessoal/budget.list");
		}

	}

	public static void selectAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Budget[] budgetList = DataManager.selectList(Budget[].class);

		request.setAttribute("budgetList", budgetList);
	}

	public static void saveBudget(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Budget budget = new Budget();
		budget.setCodBudget(request.getParameter("codBudget"));
		budget.setVersion(request.getParameter("version"));
		budget.setDatIni(request.getParameter("datIni"));
		budget.setDatEnd(request.getParameter("datEnd"));
		
		String idParam = request.getParameter("id");

		if (idParam.isEmpty()) {
			List<Budget> lBudget = new ArrayList<Budget>();
			lBudget.add(budget);
			DataManager.insert(Budget.class, lBudget);
		} else {
			budget.setId(Integer.parseInt(request.getParameter("id")));
			DataManager.updateId(Budget.class, budget);
		}
	}

	private static void updateBudget(HttpServletRequest request, HttpServletResponse response) {

		Budget budget = DataManager.selectId(Budget.class, Integer.parseInt(request.getParameter("id")));

		request.setAttribute("budget", budget);

		List<Budget> budgetList = new ArrayList<Budget>();
		budgetList.add(budget);

		request.setAttribute("budgetList", budgetList);

	}

	private static void deleteBudget(HttpServletRequest request, HttpServletResponse response) {
		DataManager.deleteId(Budget.class, Integer.parseInt(request.getParameter("id")));
	}
}

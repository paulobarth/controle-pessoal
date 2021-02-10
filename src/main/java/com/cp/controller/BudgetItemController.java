package com.cp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.dao.dataManager;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.Budget;
import com.cp.model.BudgetItem;
import com.cp.servlet.MainServlet;

public class BudgetItemController {

	private static int budgetId = 0;
	private static Budget budgetSelected;

	public static void execute(HttpServletRequest request, HttpServletResponse response, String option) throws ServletException, IOException {

		switch (option) {
		case "list":

			try {
				budgetId = Integer.parseInt(request.getParameter("budgetId"));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			select(request, response);
			break;
		case "save":
			saveItemBudget(request, response);
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
			request.getRequestDispatcher("/WEB-INF/views/budgetItem.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/budgetItem.list");
		}
		
	}

	public static void select(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		QueryParameter qp = new QueryParameter();
		qp.addSingleParameter("idBudget", QueryTypeFilter.EQUAL, budgetId, QueryTypeCondition.AND);
		qp.addOrderByOption("grpItem", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		qp.addOrderByOption("codItem", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);

		BudgetItem[] budgetItemList = dataManager.selectList(BudgetItem[].class, qp);
		budgetSelected = dataManager.selectId(Budget.class, budgetId);
		
		request.setAttribute("budgetItemList", budgetItemList);
		request.setAttribute("budget", budgetSelected);
	}

	public static void saveItemBudget(HttpServletRequest request, HttpServletResponse response) throws IOException {

		BudgetItem budgetItem = new BudgetItem();
		budgetItem.setIdBudget(budgetId);
		budgetItem.setCodItem(request.getParameter("codItem"));
		budgetItem.setValItem(request.getParameter("valItem"));
		budgetItem.setGrpItem(request.getParameter("grpItem"));
		budgetItem.setType(request.getParameter("type"));
		budgetItem.setDayVencto(request.getParameter("dayVencto"));

		String idParam = request.getParameter("id");

		if (idParam.isEmpty()) {
			List<BudgetItem> lBudgetItem = new ArrayList<BudgetItem>();
			lBudgetItem.add(budgetItem);
			dataManager.insert(BudgetItem.class, lBudgetItem);
		} else {
			budgetItem.setId(Integer.parseInt(request.getParameter("id")));
			dataManager.updateId(BudgetItem.class, budgetItem);
		}
	}

	private static void updateBudget(HttpServletRequest request, HttpServletResponse response) {
		
		BudgetItem budgetItem = dataManager.selectId(BudgetItem.class, Integer.parseInt(request.getParameter("id")));
		
		request.setAttribute("budgetItem", budgetItem);

		List<BudgetItem> budgetItemList = new ArrayList<BudgetItem>();
		budgetItemList.add(budgetItem);
		
		request.setAttribute("budgetItemList", budgetItemList);
		request.setAttribute("budget", budgetSelected);
	}

	private static void deleteBudget(HttpServletRequest request, HttpServletResponse response) {
		dataManager.deleteId(BudgetItem.class, Integer.parseInt(request.getParameter("id")));
	}
}

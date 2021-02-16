package com.cp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.fwk.data.DataManager;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.Budget;
import com.cp.model.BudgetItem;

public class BudgetItemController extends BaseControllerImpl {

	private static int budgetId = 0;
	private static Budget budgetSelected;

	@Override
	public void executeCallBack() throws ServletException, IOException {
		if (option.equals("list") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/budgetItem.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/budgetItem.list");
		}
	}
	@Override
	public void list() {
		try {
			budgetId = Integer.parseInt(request.getParameter("budgetId"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		QueryParameter qp = new QueryParameter();
		qp.addSingleParameter("idBudget", QueryTypeFilter.EQUAL, budgetId, QueryTypeCondition.AND);
		qp.addOrderByOption("grpItem", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		qp.addOrderByOption("codItem", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);

		BudgetItem[] budgetItemList = DataManager.selectList(BudgetItem[].class, qp);
		budgetSelected = DataManager.selectId(Budget.class, budgetId);

		request.setAttribute("budgetItemList", budgetItemList);
		request.setAttribute("budget", budgetSelected);
	}

	@Override
	public void save() {
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
			DataManager.insert(BudgetItem.class, lBudgetItem);
		} else {
			budgetItem.setId(Integer.parseInt(request.getParameter("id")));
			DataManager.updateId(BudgetItem.class, budgetItem);
		}		
	}
	@Override
	public void update() {
		BudgetItem budgetItem = DataManager.selectId(BudgetItem.class, Integer.parseInt(request.getParameter("id")));
		
		request.setAttribute("budgetItem", budgetItem);

		List<BudgetItem> budgetItemList = new ArrayList<BudgetItem>();
		budgetItemList.add(budgetItem);
		
		request.setAttribute("budgetItemList", budgetItemList);
		request.setAttribute("budget", budgetSelected);		
	}
	@Override
	public void delete() {
		DataManager.deleteId(BudgetItem.class, Integer.parseInt(request.getParameter("id")));
	}
}

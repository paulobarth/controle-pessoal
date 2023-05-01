package com.cp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import com.cp.fwk.data.DataManager;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.Budget;
import com.cp.model.BudgetItem;

public class BudgetController extends BaseControllerImpl {

	@Override
	public void executeCallBack() throws ServletException, IOException {

		if (option.equals("list") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/budget.jsp").forward(request, response);
		} else if (option.equals("item")) {
			response.sendRedirect("/controle-pessoal/budgetItem.list?budgetId=" + request.getParameter("id"));
		} else if (option.equals("duplicate")) {
			duplicate();
			response.sendRedirect("/controle-pessoal/budget.list");
		} else {
			response.sendRedirect("/controle-pessoal/budget.list");
		}
	}

	private void duplicate() {
		Budget budget = DataManager.selectId(Budget.class, Integer.parseInt(request.getParameter("id")));
		
		budget.setCodBudget(budget.getCodBudget() + " Copy");

		String today = GeneralFunctions.sqlDateToString(GeneralFunctions.getTodaySqlDate());

		budget.setDatIni(today);
		budget.setDatEnd(today);
		
		List<Budget> lBudget = new ArrayList<Budget>();
		lBudget.add(budget);
		int newBudgetId = DataManager.insert(Budget.class, lBudget) + 1;
		
//		Copiar itens
		QueryParameter qp = new QueryParameter();
		qp.addSingleNotEmptyParameter("idBudget", QueryTypeFilter.EQUAL, String.valueOf(budget.getId()), QueryTypeCondition.AND);
		BudgetItem[] budgetItemList = DataManager.selectList(BudgetItem[].class, qp);

		List<BudgetItem> lBudgetItem = new ArrayList<BudgetItem>();

		for (BudgetItem item : budgetItemList) {
			lBudgetItem.clear();
			item.setIdBudget(newBudgetId);
			item.setSeqOrder(item.getSeqOrder() + 100);
			
			lBudgetItem.add(item);
			DataManager.insert(BudgetItem.class, lBudgetItem);
		}
	}

	@Override
	public void list() {
		Budget[] budgetList = DataManager.selectList(Budget[].class);

		request.setAttribute("budgetList", budgetList);	}

	@Override
	public void save() {
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

	@Override
	public void update() {
		Budget budget = DataManager.selectId(Budget.class, Integer.parseInt(request.getParameter("id")));

		request.setAttribute("budget", budget);

		List<Budget> budgetList = new ArrayList<Budget>();
		budgetList.add(budget);

		request.setAttribute("budgetList", budgetList);
	}

	@Override
	public void delete() {
		DataManager.deleteId(Budget.class, Integer.parseInt(request.getParameter("id")));
	}
}

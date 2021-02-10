package com.cp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.dao.dataBO;
import com.cp.dao.dataManager;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.model.Budget;
import com.cp.model.BudgetItem;
import com.cp.model.BudgetShortcut;

public class BudgetShortcutController {
	
	private static BudgetItem[] budgetItemList; 

	public static void execute(HttpServletRequest request, HttpServletResponse response, String option)
			throws ServletException, IOException {

		switch (option) {
		case "list":
			selectAll(request, response);
			break;
		case "save":
			saveBudgetShortcut(request, response);
			break;
		case "update":
			updateBudgetShortcut(request, response);
			break;
		case "delete":
			deleteBudgetShortcut(request, response);
			break;

		default:
			break;
		}

		if (option.equals("list") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/budgetShortcut.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/budgetShortcut.list");
		}

	}

	public static void selectAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		budgetItemList = null;
		setBudgetItemListAttribute(request);

		BudgetShortcut[] budgetShortcutList = dataManager.selectList(BudgetShortcut[].class);
		
		request.setAttribute("budgetShortcutList", budgetShortcutList);
	}

	private static void setBudgetItemListAttribute(HttpServletRequest request) {
		
		if (budgetItemList == null) {
			
			budgetItemList = dataBO.getCurrentBudgetItemList();
		}
		request.setAttribute("budgetItemList", budgetItemList);
	}

	public static void saveBudgetShortcut(HttpServletRequest request, HttpServletResponse response) throws IOException {

		BudgetShortcut budgetShortcut = new BudgetShortcut();
		budgetShortcut.setShortcut(request.getParameter("shortcut"));
		budgetShortcut.setSplitter(request.getParameter("splitter"));

		int idBudgetItem = Integer.parseInt(request.getParameter("idBudgetItem"));
		
		for(int pos = 0; pos < budgetItemList.length; pos++) {

			if (budgetItemList[pos].getId() == idBudgetItem) {

				budgetShortcut.setCodItem(budgetItemList[pos].getCodItem());
				budgetShortcut.setGrpItem(budgetItemList[pos].getGrpItem());
				break;
			}
		}

		String idParam = request.getParameter("id");

		if (idParam.isEmpty()) {
			List<BudgetShortcut> lBudgetShortcut = new ArrayList<BudgetShortcut>();
			lBudgetShortcut.add(budgetShortcut);
			dataManager.insert(BudgetShortcut.class, lBudgetShortcut);
		} else {
			budgetShortcut.setId(Integer.parseInt(request.getParameter("id")));
			dataManager.updateId(BudgetShortcut.class, budgetShortcut);
		}
	}

	private static void updateBudgetShortcut(HttpServletRequest request, HttpServletResponse response) {
		
		setBudgetItemListAttribute(request);

		BudgetShortcut budgetShortcut = dataManager.selectId(BudgetShortcut.class, Integer.parseInt(request.getParameter("id")));

		request.setAttribute("budgetShortcut", budgetShortcut);

		List<BudgetShortcut> budgetShortcutList = new ArrayList<BudgetShortcut>();
		budgetShortcutList.add(budgetShortcut);

		request.setAttribute("budgetShortcutList", budgetShortcutList);

	}

	private static void deleteBudgetShortcut(HttpServletRequest request, HttpServletResponse response) {
		dataManager.deleteId(BudgetShortcut.class, Integer.parseInt(request.getParameter("id")));
	}
}

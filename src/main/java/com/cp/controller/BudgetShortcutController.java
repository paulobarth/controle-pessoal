package com.cp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.dao.GeneralDataBO;
import com.cp.fwk.data.DataManager;
import com.cp.model.BudgetItem;
import com.cp.model.BudgetShortcut;

public class BudgetShortcutController extends BaseControllerImpl {
	
	private static BudgetItem[] budgetItemList; 

	@Override
	public void executeCallBack() throws ServletException, IOException {
		if (option.equals("list") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/budgetShortcut.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/budgetShortcut.list");
		}		
	}

	@Override
	protected void list() {
		budgetItemList = null;
		setBudgetItemListAttribute(request);

		BudgetShortcut[] budgetShortcutList = DataManager.selectList(BudgetShortcut[].class);
		
		request.setAttribute("budgetShortcutList", budgetShortcutList);		
	}

	@Override
	public void save() {
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
			DataManager.insert(BudgetShortcut.class, lBudgetShortcut);
		} else {
			budgetShortcut.setId(Integer.parseInt(request.getParameter("id")));
			DataManager.updateId(BudgetShortcut.class, budgetShortcut);
		}		
	}

	@Override
	public void update() {
		setBudgetItemListAttribute(request);

		BudgetShortcut budgetShortcut = DataManager.selectId(BudgetShortcut.class, Integer.parseInt(request.getParameter("id")));

		request.setAttribute("budgetShortcut", budgetShortcut);

		List<BudgetShortcut> budgetShortcutList = new ArrayList<BudgetShortcut>();
		budgetShortcutList.add(budgetShortcut);

		request.setAttribute("budgetShortcutList", budgetShortcutList);
	}

	@Override
	public void delete() {
		DataManager.deleteId(BudgetShortcut.class, Integer.parseInt(request.getParameter("id")));		
	}
	

	private static void setBudgetItemListAttribute(HttpServletRequest request) {
		
		if (budgetItemList == null) {
			
			budgetItemList = GeneralDataBO.getCurrentBudgetItemList();
		}
		request.setAttribute("budgetItemList", budgetItemList);
	}

	public static void saveBudgetShortcut(HttpServletRequest request, HttpServletResponse response) throws IOException {

	}
}

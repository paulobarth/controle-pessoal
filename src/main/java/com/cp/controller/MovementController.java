package com.cp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.dao.GeneralDataBO;
import com.cp.fwk.data.DataManager;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.BudgetItem;
import com.cp.model.Movement;

public class MovementController extends BaseControllerImpl {

	private BudgetItem[] budgetItemList;
	private Movement[] movementFilteredList;
	private Map<String, String> filterMap = new HashMap<String, String>();

	@Override
	protected void specificOptionToExecute() {
		shortcutMovement();
	}

	@Override
	public void executeCallBack() throws ServletException, IOException {
		if (option.equals("list")) {
			filterMap.clear();
		} else if ("save,delete,cancel,shortgen,shortrest,shortnew".indexOf(option) >= 0) {

			if (!filterMap.isEmpty()) {
				option = "filter";
				applyFilterMovement(request);
			}
		}

		if (option.equals("list") || option.equals("filter") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/movement.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/movement.list");
		}		
	}

	@Override
	protected void list() {
		budgetItemList = null;
		setBudgetItemListAttribute(request);

		Movement[] movementList = DataManager.selectList(Movement[].class);

		request.setAttribute("movementList", movementList);
	}

	@Override
	protected void save() {
		Movement movement = new Movement();
		movement.setDescription(request.getParameter("description"));
		movement.setDatMovement(request.getParameter("datMovement"));
		movement.setDatFinancial(request.getParameter("datFinancial"));
		movement.setOrigin(request.getParameter("origin"));
		movement.setValMovement(request.getParameter("valMovement"));
		movement.setTypeMovement(request.getParameter("typeMovement"));
		movement.setSplitted(request.getParameter("splitted"));
		movement.setValTotal(request.getParameter("valTotal"));
		movement.setDocumentNumber("");

		int idBudgetItem = Integer.parseInt(request.getParameter("idBudgetItem"));

		for (int pos = 0; pos < budgetItemList.length; pos++) {

			if (budgetItemList[pos].getId() == idBudgetItem) {

				movement.setCodItem(budgetItemList[pos].getCodItem());
				movement.setGrpItem(budgetItemList[pos].getGrpItem());
				break;
			}
		}

		String idParam = request.getParameter("id");

		if (idParam.isEmpty()) {
			List<Movement> lMovement = new ArrayList<Movement>();
			lMovement.add(movement);
			DataManager.insert(Movement.class, lMovement);
		} else {
			movement.setId(Integer.parseInt(request.getParameter("id")));
			DataManager.updateId(Movement.class, movement);
		}
	}

	@Override
	protected void update() {
		setBudgetItemListAttribute(request);

		Movement movement = DataManager.selectId(Movement.class, Integer.parseInt(request.getParameter("id")));

		request.setAttribute("movement", movement);

		List<Movement> movementList = new ArrayList<Movement>();
		movementList.add(movement);

		request.setAttribute("movementList", movementList);
	}
	@Override
	protected void delete() {
		DataManager.deleteId(Movement.class, Integer.parseInt(request.getParameter("id")));
	}

	private void setBudgetItemListAttribute(HttpServletRequest request) {
		if (budgetItemList == null) {

			budgetItemList = GeneralDataBO.getCurrentBudgetItemList();
		}
		request.setAttribute("budgetItemList", budgetItemList);

		String[] originList = { "Conta Corrente - Santander", "Cartão Crédito - Santander",
				"Cartão Crédito - NuBank Jaque", "Cartão Crédito - Porto Seguro" };
		request.setAttribute("originList", originList);
	}

	@Override
	protected void filter() {
		storeRequestFilterParameters(request);

		applyFilterMovement(request);
	}

	/*
	public static void execute(HttpServletRequest request, HttpServletResponse response, String option)
			throws ServletException, IOException {

		switch (option) {
		case "list":
			selectAll(request, response);
			break;
		case "save":
			saveMovement(request, response);
			break;
		case "update":
			updateMovement(request, response);
			break;
		case "delete":
			deleteMovement(request, response);
			break;
		case "filter":
			filterMovement(request, response);
			break;
		case "shortgen":
		case "shortrest":
		case "shortnew":
			shortcutMovement(request, response, option);
			break;

		default:
			break;
		}

		if (option.equals("list")) {
			filterMap.clear();
		} else if ("save,delete,cancel,shortgen,shortrest,shortnew".indexOf(option) >= 0) {

			if (!filterMap.isEmpty()) {
				option = "filter";
				applyFilterMovement(request);
			}
		}

		if (option.equals("list") || option.equals("filter") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/movement.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/movement.list");
		}

	}
*/

	private void applyFilterMovement(HttpServletRequest request) {

		setBudgetItemListAttribute(request);

		Movement[] movementList = DataManager.selectList(Movement[].class, getQueryParameters(request));

		request.setAttribute("movementList", movementList);
		request.setAttribute("filterCollapsed", "true");

		movementFilteredList = movementList;

		sendMapFilterParametersToRequest(request);
	}

	private void storeRequestFilterParameters(HttpServletRequest request) {

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

	private void sendMapFilterParametersToRequest(HttpServletRequest request) {

		for (String key : filterMap.keySet()) {
			request.setAttribute(key, filterMap.get(key));
		}
	}

	private QueryParameter getQueryParameters(HttpServletRequest request) {

		QueryParameter qp = new QueryParameter();

		qp.addSingleParameter("description", QueryTypeFilter.CONTAINS, filterMap.get("filterDescription"),
				QueryTypeCondition.AND);

		qp.addBetweenParameter("datFinancial",
				new String[] { GeneralFunctions.stringDatetoSql(filterMap.get("filterDatFinancialIni")),
						GeneralFunctions.stringDatetoSql(filterMap.get("filterDatFinancialEnd")) },
				QueryTypeCondition.AND);

		qp.addSingleParameter("typeMovement", QueryTypeFilter.EQUAL, filterMap.get("filterTypeMovement"),
				QueryTypeCondition.AND);

		qp.addSingleParameter("origin", QueryTypeFilter.EQUAL, filterMap.get("filterOrigin"), QueryTypeCondition.AND);

		String filterBudgetItem = filterMap.get("filterBudgetItem");
		if (filterBudgetItem.equals("undefined")) {
			qp.addSingleParameter("codItem", QueryTypeFilter.EQUAL, " ", QueryTypeCondition.AND);
		} else {

			qp.addSingleParameter("codItem", QueryTypeFilter.EQUAL, filterMap.get("filterBudgetItem"),
					QueryTypeCondition.AND);
		}

		return qp;
	}

	private void shortcutMovement() {

		switch (option) {
		case "shortgen":
			GeneralDataBO.applyShortcutRulesToMovement((Movement[]) DataManager.selectList(Movement[].class));
			break;

		case "shortrest":
			if (movementFilteredList != null) {
				GeneralDataBO.applyShortcutRulesToMovement(movementFilteredList);
			}
			break;

		case "shortnew":
			
			String budgetItem = request.getParameter("shortCutNewBudgetItem");
			
			String[] sc = {"",""};
			if (budgetItem.isEmpty()) {
				return;
			} else if (!budgetItem.equals("clean")) {
				sc = getShortcutFromScreen(budgetItem);
			}
			
			if (sc != null && sc.length == 2) {
				GeneralDataBO.applyShortcutRulesToMovement(movementFilteredList, sc[0], sc[1]);
			}
			break;

		default:
			break;
		}

	}

	private String[] getShortcutFromScreen(String shortcut) {
		
		String[] sc = shortcut.split("-");
		if (sc.length == 2) {
			sc[0] = sc[0].trim();
			sc[1] = sc[1].trim();
			return sc;
		}
		
		return null;
	}
}

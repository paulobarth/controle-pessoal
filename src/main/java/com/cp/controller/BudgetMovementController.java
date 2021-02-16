package com.cp.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.cp.model.view.BudgetMovement;

public class BudgetMovementController extends BaseControllerImpl {

	@Override
	public void executeCallBack() throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/budgetMovement.jsp").forward(request, response);
	}

	@Override
	protected void list() {
//		selectByPeriod(request, GeneralFunctions.getTodaySqlDate());
		super.list();
	}
	
	@Override
	protected void filter() {
		selectByPeriod(request, GeneralFunctions.stringDatetoSql(request.getParameter("filterDatMovementIni")),
				GeneralFunctions.stringDatetoSql(request.getParameter("filterDatMovementEnd"))
				);
	}

	private static void selectByPeriod(HttpServletRequest request, String sqlDateIni, String sqlDateEnd) {

		List<String> monthList = new ArrayList<String>();

		int monthIni = Integer.valueOf(getDate(sqlDateIni, "month"));
		int monthEnd = Integer.valueOf(getDate(sqlDateEnd, "month"));
		int qtdMonths = monthEnd - monthIni;
		for (int cont = monthIni; cont <= monthEnd; cont ++) {
			
			monthList.add("Mes " + String.valueOf(cont));
		}
		
		BudgetMovement bmUnrecReceita = new BudgetMovement(qtdMonths);
		bmUnrecReceita.setId(9998);
		bmUnrecReceita.setGrpItem("Receita");
		bmUnrecReceita.setCodItem("Receita não reconhecida");
		bmUnrecReceita.setValItem(0.00);
		BudgetMovement bmUnrecDespesa = new BudgetMovement(qtdMonths);
		bmUnrecDespesa.setId(9999);
		bmUnrecDespesa.setGrpItem("Despesa");
		bmUnrecDespesa.setCodItem("Despesa não reconhecida");
		bmUnrecDespesa.setValItem(0.00);
		for (int month = 0; month <= qtdMonths; month++) {
			bmUnrecReceita.setValMovement("Receita", month, 0.00);
			bmUnrecDespesa.setValMovement("Despesa", month, 0.00);
		}
		
		BudgetItem[] budgetItemList = GeneralDataBO.getCurrentBudgetItemList();
		
		Map<String, BudgetMovement> budgetMovList = new HashMap<String, BudgetMovement>();
		
		for (int pos = 0; pos < budgetItemList.length; pos++) {
			
			BudgetItem budgetItem = budgetItemList[pos];
			
			BudgetMovement budgetMovement = new BudgetMovement(qtdMonths);
			budgetMovement.setId(budgetItem.getId());
			budgetMovement.setCodItem(budgetItem.getCodItem());
			budgetMovement.setGrpItem(budgetItem.getGrpItem());
			budgetMovement.setValItem(budgetItem.getValItem());
			for (int month = 0; month <= qtdMonths; month++) {
				budgetMovement.setValMovement("Despesa", month, 0.0);
			}	

			budgetMovList.put(budgetItem.getCodItem(), budgetMovement);
		}

		String datIni = getDate(sqlDateIni, "ini");
		String datEnd = getDate(sqlDateEnd, "end");
		QueryParameter qpMovement = new QueryParameter();
		qpMovement.addBetweenParameter("datFinancial",
				new String[] {datIni, datEnd},
				QueryTypeCondition.AND);
		qpMovement.addOrderByOption("description", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		
		Movement[] movementList = DataManager.selectList(Movement[].class, qpMovement);

		for (int pos = 0; pos < movementList.length; pos++) {
			
			Movement movement = movementList[pos];
			int month = GeneralFunctions.getMonthOfSqlDate(movement.getDatFinancial());
			month--;
			
			try {
				
				BudgetMovement budgetMovement = budgetMovList.get(movement.getCodItem());

				budgetMovement.setValMovement(movement.getTypeMovement(), month, movement.getValMovement());
				budgetMovement.setMovement(movement);

				budgetMovList.put(movement.getCodItem(), budgetMovement);

			} catch (Exception e) {
				
				if (movement.getTypeMovement().equals("Receita")) {
					bmUnrecReceita.setValMovement(movement.getTypeMovement(), month, movement.getValMovement());
					bmUnrecReceita.setMovement(movement);
				} else if (movement.getTypeMovement().equals("Despesa")) {
					bmUnrecDespesa.setValMovement(movement.getTypeMovement(), month, movement.getValMovement());
					bmUnrecDespesa.setMovement(movement);
				}
			}
		}
		
		ArrayList<BudgetMovement> bM = new ArrayList<BudgetMovement>();
		for (int pos = 0; pos < budgetItemList.length; pos++) {
			
			BudgetItem budgetItem = budgetItemList[pos];
			
			bM.add(budgetMovList.get(budgetItem.getCodItem()));
		}
		
		for (int month = 0; month <= qtdMonths; month++) {
			if (bmUnrecDespesa.getValMovement()[month].compareTo(new BigDecimal(0)) != 0) {
				bM.add(bmUnrecDespesa);
				break;
			}
		}
		for (int month = 0; month <= qtdMonths; month++) {
			if (bmUnrecReceita.getValMovement()[month].compareTo(new BigDecimal(0)) != 0) {
				bM.add(bmUnrecReceita);
				break;
			}
		}
		
		for (BudgetMovement teste : bM) {
			
			for (int month = 0; month <= qtdMonths; month++) {
				teste.negateValMovement(month);
			}
		}

		request.setAttribute("movementList", movementList);
		request.setAttribute("budgetMovList", bM);
		request.setAttribute("filterDatMovementIni", GeneralFunctions.sqlDateToString(sqlDateIni));
		request.setAttribute("filterDatMovementEnd", GeneralFunctions.sqlDateToString(sqlDateEnd));
		request.setAttribute("filterCollapsed", "true");
		request.setAttribute("monthList", monthList);
	}

	private static String getDate(String sqlDate, String type) {
		String[] sqlSplit = sqlDate.split("-");
		String result = "";
		result = sqlSplit[0] + "-" + sqlSplit[1] + "-";
		switch (type) {
		case "ini":
			result += "01";
			break;
		case "end":
			result += "31";
			break;
		case "month":
			result = sqlSplit[1];
			break;
		default:
			break;
		}

		return result;
	}

}

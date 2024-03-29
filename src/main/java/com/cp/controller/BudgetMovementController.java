package com.cp.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.dao.GeneralDataBO;
import com.cp.fwk.data.DataManager;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.Budget;
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
		applyFilterMovement();
		super.list();
	}

	@Override
	protected void applyFilterMovement() {
		selectByPeriod(request, GeneralFunctions.stringDatetoSql(request.getParameter("filterDatMovementIni")),
				GeneralFunctions.stringDatetoSql(request.getParameter("filterDatMovementEnd"))
				);
		request.setAttribute("originList", GeneralFunctions.ORIGIN_LIST);
	}

	private static void selectByPeriod(HttpServletRequest request, String sqlDateIni, String sqlDateEnd) {
		
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);

		if ("".equals(sqlDateIni)) {
			sqlDateIni = currentYear + "-01-01";
		}
		if ("".equals(sqlDateEnd)) {
			sqlDateEnd = currentYear + "-12-31";
		}
		
		List<String> monthList = new ArrayList<String>();
		int month;

		int monthIni = Integer.valueOf(getDate(sqlDateIni, "month"));
		int monthEnd = Integer.valueOf(getDate(sqlDateEnd, "month"));
		int qtdMonths = monthEnd - monthIni;
		int ref = 0;
		HashMap<Integer, Integer> monthReference = new HashMap<Integer, Integer>();
		for (int cont = monthIni; cont <= monthEnd; cont ++) {
			monthList.add("Mes " + String.valueOf(cont));
			monthReference.put(cont, ref);
			ref++;
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
		for (int monthCont = 0; monthCont <= qtdMonths; monthCont++) {
			bmUnrecReceita.setValMovement("Receita", monthCont, 0.00);
			bmUnrecDespesa.setValMovement("Despesa", monthCont, 0.00);
		}
		
		BudgetItem[] budgetItemList = GeneralDataBO.getCurrentBudgetItemListSeqOrderValid();
		
		Map<String, BudgetMovement> budgetMovList = new HashMap<String, BudgetMovement>();
		
		for (int pos = 0; pos < budgetItemList.length; pos++) {
			
			BudgetItem budgetItem = budgetItemList[pos];
			
			BudgetMovement budgetMovement = new BudgetMovement(qtdMonths);
			budgetMovement.setId(budgetItem.getId());
			budgetMovement.setCodItem(budgetItem.getCodItem());
			budgetMovement.setGrpItem(budgetItem.getGrpItem());
			budgetMovement.setValItem(budgetItem.getValItem());
			for (int monthCont = 0; monthCont <= qtdMonths; monthCont++) {
				budgetMovement.setValMovement("Despesa", monthCont, 0.0);
			}	

			budgetMovList.put(budgetItem.getCodItem(), budgetMovement);
		}
		String datIni = getDate(sqlDateIni, "ini");
		String datEnd = getDate(sqlDateEnd, "end");
		QueryParameter qpMovement = new QueryParameter();
//		qpMovement.addBetweenParameter("datMovement",
//				new String[] {datIni, datEnd},
//				QueryTypeCondition.AND);
		qpMovement.addSingleParameter("datMovement", QueryTypeFilter.GREATEREQUAL, datIni, QueryTypeCondition.AND);
		qpMovement.addSingleParameter("datMovement", QueryTypeFilter.LESSEQUAL, datEnd, QueryTypeCondition.AND);
//		qpMovement.addOrderByOption("description", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);

		String filterOrigin = request.getParameter("filterOrigin");
		if (filterOrigin != null && !"".equals(filterOrigin)) {
			qpMovement.addSingleParameter("origin", QueryTypeFilter.EQUAL, filterOrigin, QueryTypeCondition.AND);
		}

		Movement[] movementList = DataManager.selectList(Movement[].class, qpMovement);

		for (int pos = 0; pos < movementList.length; pos++) {
			
			Movement movement = movementList[pos];
			month = monthReference.get(Integer.parseInt(GeneralFunctions.getMonthOfDate(movement.getDatMovement())));
			
			try {
				
				BudgetMovement budgetMovement = budgetMovList.get(movement.getCodItem());

				budgetMovement.setValMovement(movement.getTypeMovement(), month, movement.getValMovement());
				budgetMovement.setMovement(movement, monthReference);

//				budgetMovList.put(movement.getCodItem(), budgetMovement);

			} catch (Exception e) {
				
				if (movement.getTypeMovement().equals("Receita")) {
					bmUnrecReceita.setValMovement(movement.getTypeMovement(), month, movement.getValMovement());
					bmUnrecReceita.setMovement(movement, monthReference);
				} else if (movement.getTypeMovement().equals("Despesa")) {
					bmUnrecDespesa.setValMovement(movement.getTypeMovement(), month, movement.getValMovement());
					bmUnrecDespesa.setMovement(movement, monthReference);
				}
			}
		}

		ArrayList<BudgetMovement> bMovRecDesp 	= new ArrayList<BudgetMovement>();
		ArrayList<BudgetMovement> bMovExtra		= new ArrayList<BudgetMovement>();

		Map<String, Budget> budgetList = returnBudgetList(budgetItemList);
		
		for (String grp : new String[] {"Despesa", "Receita", "Investimento"}) {		
		
			for (String key : budgetList.keySet()) {
				Budget budget = budgetList.get(key);
				
				if (!key.startsWith(grp)) {
					continue;
				}
	
				Double totalPrevisto = 0.00;
				BudgetMovement aaa = new BudgetMovement(qtdMonths);
				for (int pos = 0; pos < budgetItemList.length; pos++) {
					BudgetItem budgetItem = budgetItemList[pos];
					String budgetItemKey = budgetItem.getGrpItem() + String.valueOf(budgetItem.getIdBudget());
					if (!budgetItemKey.equals(key)) {
						continue;
					}
					
					if (aaa.getCodItem() == null) {
						aaa.setCodItem("TOTAL " + budget.getCodBudget());
						aaa.setGrpItem(budgetItem.getGrpItem());
					}
	
					BudgetMovement budgetMovement = budgetMovList.get(budgetItem.getCodItem());
	
					try {
						for (int posMov = 0; posMov < budgetMovement.getValMovement().length; posMov++) {
							aaa.setValMovement(budgetItem.getGrpItem(), posMov, budgetMovement.getValMovement()[posMov].doubleValue());
						}
					} catch (Exception e) {
					}
					totalPrevisto += budgetMovement.getValItem();
					if (grp.equals("Investimento")) {
						bMovExtra.add(budgetMovList.get(budgetItem.getCodItem()));
					} else {
						bMovRecDesp.add(budgetMovList.get(budgetItem.getCodItem()));
					}
				}
				aaa.setValItem(totalPrevisto);
				if (!grp.equals("Investimento")) {
					bMovRecDesp.add(aaa);
				}
			}
		}

		for (int monthCont = 0; monthCont <= qtdMonths; monthCont++) {
			if (bmUnrecDespesa.getValMovement()[monthCont].compareTo(new BigDecimal(0)) != 0) {
				bMovExtra.add(bmUnrecDespesa);
				break;
			}
		}
		for (int monthCont = 0; monthCont <= qtdMonths; monthCont++) {
			if (bmUnrecReceita.getValMovement()[monthCont].compareTo(new BigDecimal(0)) != 0) {
				bMovExtra.add(bmUnrecReceita);
				break;
			}
		}

//		request.setAttribute("movementList", movementList);
		request.setAttribute("budgetMovList", bMovRecDesp);
		request.setAttribute("budgetMovExtraList", bMovExtra);
		request.setAttribute("filterDatMovementIni", GeneralFunctions.sqlDateToString(sqlDateIni));
		request.setAttribute("filterDatMovementEnd", GeneralFunctions.sqlDateToString(sqlDateEnd));
//		request.setAttribute("filterCollapsed", "true");
		request.setAttribute("monthList", monthList);

	}

	private static HashMap<String, Budget>  returnBudgetList(BudgetItem[] budgetItemList) {
		HashMap<Integer, Budget> budgetMap = new HashMap<Integer, Budget>();
		Budget[] budgetList  = DataManager.selectList(Budget[].class);		
		for (int pos = 0; pos < budgetList.length; pos++) {
			budgetMap.put(budgetList[pos].getId(), budgetList[pos]);	
		}
		HashMap<String, Budget> budgetItemMap = new HashMap<String, Budget>();
		for (BudgetItem budgetItem : budgetItemList) {
			Budget budget = budgetMap.get(budgetItem.getIdBudget());
			budgetItemMap.put(budgetItem.getGrpItem() + String.valueOf(budget.getId()), budget);
		}
		return budgetItemMap;
	}

//	private static Map<Integer, String> returnBudgetList() {
//		HashMap<Integer, String> budgetMap = new HashMap<Integer, String>();
//		Budget[] budgetList  = DataManager.selectList(Budget[].class);		
//		for (int pos = 0; pos < budgetList.length; pos++) {
//			budgetMap.put(budgetList[pos].getId(), budgetList[pos].getCodBudget());
//		}
//		return budgetMap;
//	}

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

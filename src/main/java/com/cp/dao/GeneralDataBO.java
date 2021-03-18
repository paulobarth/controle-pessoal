package com.cp.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cp.fwk.data.DataManager;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.Budget;
import com.cp.model.BudgetItem;
import com.cp.model.BudgetShortcut;
import com.cp.model.Movement;

public class GeneralDataBO {
	
	private static BudgetShortcut[] budgetShortcutList;

	public static BudgetItem[] getCurrentBudgetItemListSeqOrderValid() {
		return getCurrentBudgetItemList("validSeqOrder");
	}

	public static BudgetItem[] getCurrentBudgetItemListNoSeqOrder() {
		return getCurrentBudgetItemList("noSeqOrder");
	}

	public static BudgetItem[] getCurrentBudgetItemList() {
		return getCurrentBudgetItemList("");
	}

	private static BudgetItem[] getCurrentBudgetItemList(String typeSeqOrderFilder) {

		String todayDate = GeneralFunctions.getTodaySqlDate();

		Budget[] budgetList  = DataManager.selectList(Budget[].class, "'" + todayDate + "' BETWEEN datIni AND datEnd ");

		QueryParameter qp = new QueryParameter();
		for (Budget budget : budgetList) {
			qp.addSingleParameter("idBudget", QueryTypeFilter.EQUAL, budget.getId(), QueryTypeCondition.OR);
		}
		if (typeSeqOrderFilder.equals("validSeqOrder")) {
			qp.addSingleParameter("seqOrder", QueryTypeFilter.GREATER, 0, QueryTypeCondition.AND);
		} else if (typeSeqOrderFilder.equals("noSeqOrder")) {
			qp.addSingleParameter("seqOrder", QueryTypeFilter.EQUAL, 0, QueryTypeCondition.AND);
		}
		qp.addOrderByOption("seqOrder", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		BudgetItem[] budgetItemList = DataManager.selectList(BudgetItem[].class, qp);

		return budgetItemList;
	}

	public static void applyShortcutRulesToMovement(Movement[] movementList) {
		applyShortcutRulesToMovement(movementList, null, null);
	}
	
	public static void applyShortcutRulesToMovement(Movement[] movementList, String grpItem, String codItem) {

		budgetShortcutList = DataManager.selectList(BudgetShortcut[].class);
		
		for (int pos = 0; pos < movementList.length; pos++) {
			
			Movement movement = movementList[pos];
			
			if (grpItem == null || codItem == null) {
				setCompleteShortcutRule(movement);
			} else {
				saveShortcutToMovement(movement, grpItem, codItem);
			}				
		}
	}

	private static void setCompleteShortcutRule(Movement movement) {
		
		boolean regEncountered = false;

		for (int cont = 0; cont < budgetShortcutList.length; cont++) {
			
			BudgetShortcut budgetShortcut = budgetShortcutList[cont];

			List<String> terms = new ArrayList<String>();

			if (!budgetShortcut.getSplitter().trim().isEmpty()) {
				String[] split = budgetShortcut.getShortcut().split(budgetShortcut.getSplitter());
				for (int cont2 = 0; cont2 < split.length; cont2++) {
					terms.add(split[cont2]);
				}
			} else {
				terms.add(budgetShortcut.getShortcut());
			}
			
			Iterator<String> termIterator = terms.iterator();
			while (termIterator.hasNext()) {
				
				String term = termIterator.next();
				
				if (movement.getDescription().contains(term)) {
					regEncountered = true;
				} else {
					regEncountered = false;
					break;
				}
			}
			
			if (regEncountered) {
				saveShortcutToMovement(movement, budgetShortcut.getGrpItem(), budgetShortcut.getCodItem());
				break;
			}				
		}
	}

	private static void saveShortcutToMovement(Movement movement, String grpItem, String codItem) {
		movement.setGrpItem(grpItem);
		movement.setCodItem(codItem);
		DataManager.updateId(Movement.class, movement);
	}
}

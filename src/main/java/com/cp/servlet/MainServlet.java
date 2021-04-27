package com.cp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.controller.BaseController;
import com.cp.controller.BudgetController;
import com.cp.controller.BudgetItemController;
import com.cp.controller.BudgetMovementController;
import com.cp.controller.BudgetShortcutController;
import com.cp.controller.ImportMovementController;
import com.cp.controller.MovementController;
import com.cp.controller.stocks.StocksController;
import com.cp.controller.stocks.StocksGainController;
import com.cp.controller.stocks.StocksOperationController;

@WebServlet(urlPatterns = {	"/budget.list", 		"/budget.save", 		"/budget.update", 			"/budget.delete",
							"/budget.item",
							"/budgetItem.list", 	"/budgetItem.save", 	"/budgetItem.update", 		"/budgetItem.delete", "/budgetItem.filter",
							"/movement.list", 		"/movement.save", 		"/movement.update",			"/movement.delete",
							"/movement.filter",		"/movement.shortgen",	"/movement.shortrest", 		"/movement.shortnew",
							"/movement.cancel",
							"/budgetShortcut.list", "/budgetShortcut.save", "/budgetShortcut.update", 	"/budgetShortcut.delete",
							"/budgetMovement.list", "/budgetMovement.filter",
							"/importMovement.option",	"/importMovement.import",
							"/stocks.list",  			"/stocks.save", 			"/stocks.update",			"/stocks.delete",
							"/stocksOperation.list",  	"/stocksOperation.save", 	"/stocksOperation.update",	"/stocksOperation.delete",
							"/stocksOperation.filter",  "/stocksOperation.reportList", "/stocksOperation.reportFilter",
							"/stocksOperation.costs", "/stocksOperation.costsCalculation",
							"/stocksGain.list",  		"/stocksGain.save", 		"/stocksGain.update",		"/stocksGain.delete"
							})
public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = -1347841828092604269L;
	
	private BaseController budgetController;
	private BaseController budgetItemController;
	private BaseController budgetShortcutController;
	private BaseController movementController;
	private BaseController budgetMovementController;
	private BaseController importMovementController;
	private BaseController stocksController;
	private BaseController stocksGainController;
	private BaseController stocksOperationController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		executeController(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		executeController(request, response);
	}

	private void executeController(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String[] options = request.getServletPath().replace(".", ";").split(";");

		request.getSession().setAttribute("acao", options[0].replace("/", ""));

		try {
			getController(options[0]).execute(request, response, options[1]);
		} catch (Exception e) {
		}
	}

	private BaseController getController(String option) {
		BaseController baseController = null;
		switch (option) {
		case "/budget":
			if (budgetController == null) {
				budgetController = new BudgetController();
			}
			return budgetController;

		case "/budgetItem":
			if (budgetItemController == null) {
				budgetItemController = new BudgetItemController();
			}
			return budgetItemController;
			
		case "/budgetShortcut":
			if (budgetShortcutController == null) {
				budgetShortcutController = new BudgetShortcutController();
			}
			return budgetShortcutController;

		case "/movement":
			if (movementController == null) {
				movementController = new MovementController();
			}
			return movementController;
			
		case "/budgetMovement":
			if (budgetMovementController == null) {
				budgetMovementController = new BudgetMovementController();
			}
			return budgetMovementController;

		case "/importMovement":
			if (importMovementController == null) {
				importMovementController = new ImportMovementController();
			}
			return importMovementController;

		case "/stocks":
			if (stocksController == null) {
				stocksController = new StocksController();
			}
			return stocksController;

		case "/stocksOperation":
			if (stocksOperationController == null) {
				stocksOperationController = new StocksOperationController();
			}
			return stocksOperationController;

		case "/stocksGain":
			if (stocksGainController == null) {
				stocksGainController = new StocksGainController();
			}
			return stocksGainController;
		default:
			break;
		}
		return baseController;
	}
}

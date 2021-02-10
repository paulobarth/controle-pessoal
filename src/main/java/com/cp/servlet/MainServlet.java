package com.cp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.controller.BudgetController;
import com.cp.controller.BudgetItemController;
import com.cp.controller.BudgetMovementController;
import com.cp.controller.BudgetShortcutController;
import com.cp.controller.ImportMovementController;
import com.cp.controller.MovementController;
import com.cp.controller.stocks.StocksGainController;
import com.cp.controller.stocks.StocksOperationController;

@WebServlet(urlPatterns = {	"/budget.list", 		"/budget.save", 		"/budget.update", 			"/budget.delete",
							"/budget.item",
							"/budgetItem.list", 	"/budgetItem.save", 	"/budgetItem.update", 		"/budgetItem.delete",
							"/movement.list", 		"/movement.save", 		"/movement.update",			"/movement.delete",
							"/movement.filter",		"/movement.shortgen",	"/movement.shortrest", 		"/movement.shortnew",
							"/movement.cancel",
							"/budgetShortcut.list", "/budgetShortcut.save", "/budgetShortcut.update", 	"/budgetShortcut.delete",
							"/budgetMovement.list", "/budgetMovement.filter",
							"/importMovement.option",	"/importMovement.import",
							"/stocksOperation.list",  	"/stocksOperation.save", 	"/stocksOperation.update",	"/stocksOperation.delete",
							"/stocksOperation.reportList", "/stocksOperation.reportFilter",
							"/stocksGain.list",  		"/stocksGain.save", 		"/stocksGain.update",		"/stocksGain.delete"
							})
public class MainServlet extends HttpServlet {
	
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
		
		switch (options[0].toString()) {
		case "/budget":
			BudgetController.execute(request, response, options[1]);
			break;

		case "/budgetItem":
			BudgetItemController.execute(request, response, options[1]);
			break;
			
		case "/budgetShortcut":
			BudgetShortcutController.execute(request, response, options[1]);
			break;

		case "/movement":
			MovementController.execute(request, response, options[1]);
			break;
			
		case "/budgetMovement":
			BudgetMovementController.execute(request, response, options[1]);
			break;

		case "/importMovement":
			ImportMovementController.execute(request, response, options[1]);
			break;

		case "/stocksOperation":
			StocksOperationController.execute(request, response, options[1]);
			break;

		case "/stocksGain":
			StocksGainController.execute(request, response, options[1]);
			break;
		default:
			break;
		} 
	}
}

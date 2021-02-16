package com.cp.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.load.ImportDataCSV;

public class ImportMovementController extends BaseControllerImpl {

	private static String[] originList = new String[] { "Conta Corrente - Santander", "Cartão Crédito - Santander",
			"Cartão Crédito - NuBank Jaque", "Cartão Crédito - Porto Seguro" };
	
	private static Map<String, String> fileNameValidation;
	
	static {
		fileNameValidation = new HashMap<String, String>();
		fileNameValidation.put(originList[0], "Santander CC");
		fileNameValidation.put(originList[1], "Santander CR");
		fileNameValidation.put(originList[2], "nubank-");
		fileNameValidation.put(originList[3], "porto");
	}

	@Override
	public void executeCallBack() throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/importMovement.jsp").forward(request, response);		
	}

	@Override
	protected void specificOptionToExecute() {
		switch (option) {
		case "option":
			optionPage(request, response);
			break;

		case "import":
			importFile(request, response);
			break;

		default:
			break;
		}
	}

	private static void optionPage(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("originList", originList);
	}

	private static void importFile(HttpServletRequest request, HttpServletResponse response) {

		String selectedFile = request.getParameter("selectedFile");
		String datFinancial = request.getParameter("datFinancial");
		String selectedOrigin = request.getParameter("selectedOrigin");
		String msg = "";

		String initials = fileNameValidation.get(selectedOrigin);
		if (!selectedFile.startsWith(initials)) {
			msg = "ERRO: Nome do arquivo inválido, deve iniciar com " + initials + ".";
		} else {

			selectedFile = "/Users/paulobarth/Downloads/" + selectedFile;

			switch (selectedOrigin) {
			case "Cartão Crédito - NuBank Jaque":
				msg = ImportDataCSV.importMovementFromNuBank(selectedFile, selectedOrigin, datFinancial);
				break;

			case "Cartão Crédito - Santander":
				msg = ImportDataCSV.importMovementStandardCSV(selectedFile, selectedOrigin, datFinancial, true);
				break;

			default:
				msg = ImportDataCSV.importMovementStandardCSV(selectedFile, selectedOrigin, datFinancial, false);
				break;
			}
		}

		request.setAttribute("importResultMessage", msg);

		optionPage(request, response);
	}

}

package com.cp.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.load.ImportDataCSV;
import com.cp.load.ImportDataExcel;

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
		if (selectedFile == null) {
			msg = "ERRO: Arquivo inválido.";
		} else if (!selectedFile.startsWith(initials)) {
			msg = "ERRO: Nome do arquivo inválido, deve iniciar com " + initials + ".";
		} else if (selectedOrigin.contains("Cartão Crédito") && datFinancial.isEmpty()) {
			msg = "ERRO: Favor informar data financeira.";
		} else {

			selectedFile = "/Users/paulobarth/Downloads/" + selectedFile;

			switch (selectedOrigin) {
			case "Conta Corrente - Santander":
//				msg = ImportDataCSV.importMovementCCSantanderCSV(selectedFile, selectedOrigin, datFinancial, false);
				try {
					msg = ImportDataExcel.importMovementDebitSantander(selectedFile, selectedOrigin);
				} catch (IOException e) {
					msg = e.getMessage();
					e.printStackTrace();
				}
				break;

			case "Cartão Crédito - NuBank Jaque":
//				msg = ImportDataCSV.importMovementFromNuBank(selectedFile, selectedOrigin, datFinancial);
				break;

			case "Cartão Crédito - Santander":
				try {
					msg = ImportDataExcel.importMovementCreditSantander(selectedFile, selectedOrigin, datFinancial);
				} catch (IOException e) {
					msg = e.getMessage();
					e.printStackTrace();
				}
				break;

			default:
//				msg = ImportDataCSV.importMovementStandardCSV(selectedFile, selectedOrigin, datFinancial, false);
				break;
			}
		}
		
		if (msg.startsWith("ERRO:")) {
			request.setAttribute("selectedFile", selectedFile);
			request.setAttribute("datFinancial", datFinancial);
			request.setAttribute("selectedOrigin", selectedOrigin);
		}

		request.setAttribute("importResultMessage", msg);

		optionPage(request, response);
	}

}

package com.cp.load;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cp.dao.dataManager;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.load.LoadCSV;
import com.cp.model.BudgetItem;
import com.cp.model.Movement;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ImportDataCSV {

	public static String importMovementStandardCSV(String file, String origin, String datFinancial,
			boolean cartaoCredito) {

		Double val;

		JsonArray datasets = LoadCSV.readFileToJson(file, ";");

		Iterator<JsonElement> iterator = datasets.iterator();

		while (iterator.hasNext()) {

			JsonObject jObj = iterator.next().getAsJsonObject();

			val = Double.parseDouble(jObj.get("valMovement").getAsString().replace(".", "").replace(",", "."));

			List<Movement> lMovement = new ArrayList<Movement>();
			Movement movement = new Movement();

			movement.setDescription(jObj.get("description").getAsString());

			String[] splitDate = jObj.get("datMovement").getAsString().split("/");
			// mm/dd/aaaa
			movement.setDatMovement(splitDate[1] + "/" + splitDate[0] + "/" + splitDate[2]);

			datFinancial = calculateDatFinancial(movement.getDescription(), movement.getDatMovement());
			if (!datFinancial.isEmpty()) {
				movement.setDatFinancial(datFinancial);
			} else {
				movement.setDatFinancial(movement.getDatMovement());
			}
//			if (datFinancial.isEmpty()) {
//				movement.setDatFinancial(movement.getDatMovement());
//			} else {
//				movement.setDatFinancial(datFinancial);
//			}

			movement.setOrigin(origin);
			movement.setValMovement(GeneralFunctions.truncDouble(val, 2));
			movement.setSplitted(0);
			movement.setDocumentNumber(jObj.get("documentNumber").getAsString());

			if (cartaoCredito) {

				if (movement.getValMovement() < 0) {
					movement.setTypeMovement("Receita");
					movement.setValMovement(movement.getValMovement() * -1);
				} else {
					movement.setTypeMovement("Despesa");
				}
			} else {
				if (movement.getValMovement() < 0) {
					movement.setTypeMovement("Despesa");
					movement.setValMovement(movement.getValMovement() * -1);
				} else {
					movement.setTypeMovement("Receita");
				}
			}
			movement.setValTotal(movement.getValMovement());

			if (isMovementUnique(movement)) {
				lMovement.add(movement);
				dataManager.insert(Movement.class, lMovement);
			}
		}

		return "OK";
	}

	private static String calculateDatFinancial(String description, String datMovement) {

		String newDate = "";

		int initPos = description.indexOf("(") + 1;
		if (initPos > 0) {

			int months = Integer.parseInt(description.substring(initPos, initPos + 2));

			if (months > 1) {

				months--;

				String[] splitDate = datMovement.split("/");

				int newMonth = Integer.parseInt(splitDate[0]) + months;

				if (newMonth > 12) {
					newDate = String.valueOf(newMonth - 12) + "/" + splitDate[1] + "/"
							+ String.valueOf(Integer.parseInt(splitDate[2]) + 1);
				} else {
					newDate = newMonth + "/" + splitDate[1] + "/" + splitDate[2];
				}
			}
		}

		return newDate;
	}

	public static String importMovementFromNuBank(String file, String origin, String datFinancial) {

		Double val;

		JsonArray datasets = LoadCSV.readFileToJson(file, ",");

		Iterator<JsonElement> iterator = datasets.iterator();

		while (iterator.hasNext()) {

			JsonObject jObj = iterator.next().getAsJsonObject();

			val = Double.parseDouble(jObj.get("amount").getAsString().replace(",", ""));

			if (val < 0) {
				continue;
			}

			List<Movement> lMovement = new ArrayList<Movement>();
			Movement movement = new Movement();

			movement.setDescription(jObj.get("title").getAsString());

			String[] splitDate = jObj.get("date").getAsString().split("-");
			// aaaa-mm-dd
			movement.setDatMovement(splitDate[1] + "/" + splitDate[2] + "/" + splitDate[0]);

			if (datFinancial.isEmpty()) {
				movement.setDatFinancial(movement.getDatMovement());
			} else {
				movement.setDatFinancial(datFinancial);
			}

			movement.setOrigin(origin);
			movement.setValMovement(GeneralFunctions.truncDouble(val, 2));
			movement.setTypeMovement("Despesa");
			movement.setValMovement(jObj.get("amount").getAsString().replace(",", ""));
			movement.setSplitted(0);
			movement.setDocumentNumber("");
			movement.setValTotal(movement.getValMovement());

			if (isMovementUnique(movement)) {
				lMovement.add(movement);
				dataManager.insert(Movement.class, lMovement);
			}
		}
		return "OK";
	}

	private static boolean isMovementUnique(Movement movement) {

		QueryParameter qp = new QueryParameter();

		qp.addSingleParameter("description", QueryTypeFilter.EQUAL, movement.getDescription(), QueryTypeCondition.AND);
		qp.addSingleParameter("datFinancial", QueryTypeFilter.EQUAL,
				GeneralFunctions.stringDatetoSql(movement.getDatFinancial()), QueryTypeCondition.AND);
		qp.addSingleParameter("valMovement", QueryTypeFilter.EQUAL, String.valueOf(movement.getValMovement()),
				QueryTypeCondition.AND);

		return dataManager.isDataUnique(Movement[].class, qp);
	}
}

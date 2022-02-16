package com.cp.model.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cp.model.Movement;

public class BudgetMovement {

	private int id;
	private String codItem;
	private Double valItem;
	private String grpItem;
	private List<BudgetMovementDetail> listMovement = new ArrayList<BudgetMovementDetail>();
	private int qtdMonths;

	private BigDecimal[] valMovement;

	public BudgetMovement(int qtdMonths) {
		this.valMovement = new BigDecimal[qtdMonths + 1];
		for (int month = 0; month <= qtdMonths; month++) {

			this.valMovement[month] = new BigDecimal(0.00);
		}
		this.qtdMonths = qtdMonths;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodItem() {
		return codItem;
	}

	public void setCodItem(String codItem) {
		this.codItem = codItem;
	}

	public Double getValItem() {
		return valItem;
	}

	public void setValItem(Double valItem) {
		this.valItem = valItem;
	}

	public String getGrpItem() {
		return grpItem;
	}

	public void setGrpItem(String grpItem) {
		this.grpItem = grpItem;
	}

	public BigDecimal[] getValMovement() {
		return valMovement;
	}

	public void setValMovement(String typeMovement, int month, Double valMovement) {
		BigDecimal newValue = new BigDecimal(Double.toString(valMovement));
		
		if (typeMovement.equals("Receita")) {
			this.valMovement[month] = this.valMovement[month].add(newValue);
		} else if (typeMovement.equals("Despesa")) {
			this.valMovement[month] = this.valMovement[month].subtract(newValue);
		}
	}

	public void negateValMovement(int month) {
		if (this.valMovement[month].doubleValue() < 0) {

			BigDecimal newValue = new BigDecimal(this.valMovement[month].negate().toString());

			this.valMovement[month] = this.valMovement[month].add(newValue);
			this.valMovement[month] = this.valMovement[month].add(newValue);
		}
	}

	public List<BudgetMovementDetail> getListMovement() {
		return listMovement;
	}

	public void setMovement(Movement movement, HashMap<Integer, Integer> monthReference) {

		String source;
		String target;

		BudgetMovementDetail bmd = new BudgetMovementDetail(qtdMonths);
		bmd.setDescription(movement.getDescription());

		String[] mList = movement.getDatMovement().split("/");

		if (mList.length == 3) {

			int month = monthReference.get(Integer.parseInt(mList[0]));
			int day = Integer.parseInt(mList[1]);

			for (BudgetMovementDetail item : this.listMovement) {

				source = getDescriptionPart(movement.getDescription());
				target = getDescriptionPart(item.getDescription());

				if (source.equals(target)) {

					if (item.getMonth() != month) {

						if (item.getListValue()[month].getValMovement().compareTo(new BigDecimal(0)) == 0) {
							item.getListValue()[month].setValMovement(movement.getValMovement());
							item.getListValue()[month].setDay(day);
							item.getListValue()[month].setTypeMovement(movement.getTypeMovement());
							return;
						}
					}
				}
			}

			bmd.setValue(month, day, movement.getValMovement(), movement.getTypeMovement());

			this.listMovement.add(bmd);
		}
	}

	private String getDescriptionPart(String description) {
		String text = "";
		if (description.contains("(")) {
			text = description.substring(0, description.indexOf("(") - 1);					
		} else {
			text = description;
		}
		return text.trim();
	}
}
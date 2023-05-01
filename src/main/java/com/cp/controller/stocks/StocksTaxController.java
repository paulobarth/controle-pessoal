package com.cp.controller.stocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.soap.SOAPBinding;
import javax.servlet.ServletException;

import com.cp.constants.generalInfo;
import com.cp.controller.BaseControllerImpl;
import com.cp.fwk.data.DataManager;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.StocksOperation;
import com.cp.model.StocksTax;
import com.cp.model.StocksTaxOperation;
import com.cp.model.view.StocksReportTax;

public class StocksTaxController extends BaseControllerImpl {

	private static final String LIST  		= "LIST";
	private static final String SIMULATION  = "SIMULATION";
	private static final String CALCULATION = "CALCULATION";

	@Override
	public void executeCallBack() throws ServletException, IOException {
		if (option.equals("list")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksReportTax.jsp").forward(request, response);
		} else if (option.startsWith("taxSimulation")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksReportTax.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/stocksTax.list");
		}		
	}

	@Override
	protected void specificOptionToExecute() {
		switch (option) {
		case "taxSimulation":
			applyTaxToStocksSell(SIMULATION);
			break;
		case "taxCalculation":
			applyTaxToStocksSell(CALCULATION);
			break;
		case "taxPayment":
			paymentPeriod();
			break;
		case "taxCancel":
			cancelTax();
			break;

		default:
			break;
		}
	}

	private void cancelTax() {
		int id = Integer.parseInt(request.getParameter("id"));
		StocksTax stocksTax = DataManager.selectId(StocksTax.class, id);
		
		if (stocksTax.getPaymentStatus() != 1) {
			return;
		}

		QueryParameter qp = new QueryParameter();
		qp.addSingleParameter("idStocksTax", QueryTypeFilter.EQUAL, stocksTax.getId(), QueryTypeCondition.AND);
		StocksTaxOperation[] stocksTaxOperationList = DataManager.selectList(StocksTaxOperation[].class, qp);
		if (stocksTaxOperationList != null) {
			for (StocksTaxOperation stocksTaxOperation : stocksTaxOperationList) {
				if (stocksTaxOperation.getTaxOperationType().equals(GeneralFunctions.LOSS)) {
					StocksOperation stocksOperationLoss = DataManager.selectId(StocksOperation.class, stocksTaxOperation.getIdStocksOperation());
					stocksOperationLoss.setValTotalIRLossConsumed(stocksOperationLoss.getValTotalIRLossConsumed() +
							(stocksTaxOperation.getValIRLossConsumed() * -1));
					DataManager.updateId(StocksOperation.class, stocksOperationLoss);
				}
				DataManager.deleteId(StocksTaxOperation.class, stocksTaxOperation.getId());
			}
		}
		DataManager.deleteId(StocksTax.class, stocksTax.getId());
	}

	private void paymentPeriod() {
		int id = Integer.parseInt(request.getParameter("id"));
		StocksTax stocksTax = DataManager.selectId(StocksTax.class, id);
		stocksTax.setPaymentStatus(2);
		DataManager.updateId(StocksTax.class, stocksTax);
	}

	@Override
	protected void list() {
		StocksTax[] stocksTaxList = DataManager.selectList(StocksTax[].class);
		List<StocksReportTax> stocksReportTaxList = new ArrayList<StocksReportTax>();
		if (stocksTaxList == null) {
			return;
		}
		for (StocksTax stocksTax : stocksTaxList) {
			StocksReportTax stocksReportTax = new StocksReportTax();
			StocksReportTax.TaxReport taxReport = stocksReportTax.new TaxReport();
	
//			Busca Vendas com lucro realizadas no período
			List<StocksOperation> stocksAllSellList = getStockOperationListFromStockTaxOperation(stocksTax.getId(), GeneralFunctions.GAIN);
	
//			Acumula o valor do lucro menos custos e seta a venda como imposto calculado
			double totalSell = 0.0;  //Total das vendas no mês
			double profitSell = 0.0; //Total das vendas com lucro
			double resultSell = 0.0; //Total do lucro com as vendas
	
			for (StocksOperation stocksOperation : stocksAllSellList) {
				double sellValue = (stocksOperation.getQuantity() * stocksOperation.getValStock()) - stocksOperation.getValCost();
				totalSell += sellValue;
				profitSell += sellValue;
				resultSell += stocksOperation.getValResultSell();
	
				stocksReportTax.addStocksSell(stocksOperation);
			}
			totalSell = GeneralFunctions.round(totalSell, 2);
			profitSell = GeneralFunctions.round(profitSell, 2);
			resultSell = GeneralFunctions.round(resultSell, 2);
			totalSell = GeneralFunctions.round(totalSell, 2);

//			Guardou a posição antes das deduções
			taxReport.setTotalSell(totalSell);
			taxReport.setProfitSell(profitSell);
			taxReport.setResultSell(resultSell);

			double resultWithDeduction = resultSell;

			QueryParameter qp = new QueryParameter();
			qp.addSingleParameter("idStocksTax", QueryTypeFilter.EQUAL, stocksTax.getId(), QueryTypeCondition.AND);
			qp.addSingleParameter("taxOperationType", QueryTypeFilter.EQUAL, GeneralFunctions.LOSS, QueryTypeCondition.AND);
			StocksTaxOperation[] stocksTaxOperationList = DataManager.selectList(StocksTaxOperation[].class, qp);
			if (stocksTaxOperationList != null) {
				for (StocksTaxOperation stocksTaxOperation : stocksTaxOperationList) {
					StocksOperation stocksOperationLoss = DataManager.selectId(StocksOperation.class, stocksTaxOperation.getIdStocksOperation());
					resultWithDeduction += stocksTaxOperation.getValIRLossConsumed();
//					Para o relatório deve setar o valor relacionado ao imposto em questão, apenas para apresentação na tela.
					stocksOperationLoss.setValTotalIRLossConsumed(stocksTaxOperation.getValIRLossConsumed());
					stocksReportTax.addStocksDeductionList(stocksOperationLoss);
				}
			}
	
			taxReport.setId(stocksTax.getId());
			taxReport.setMonth(stocksTax.getMonth());
			taxReport.setYear(stocksTax.getYear());
			taxReport.setPaymentStatus(stocksTax.getPaymentStatus());
			taxReport.setTaxType(stocksTax.getTaxType());

			taxReport.setResultWithDeduction(resultWithDeduction);
			taxReport.setValTotalDeduction(stocksTax.getValTotalDeduction());
			taxReport.setValTotalPayment(stocksTax.getValTotalPayment());
	
			
			switch (stocksTax.getPaymentStatus()) {
			case 1:
				taxReport.setStatusTax("Pendente");
				break;
			case 2:
				taxReport.setStatusTax("Pago");
				break;

			default:
				break;
			}
			
			stocksReportTax.setStocksTax(taxReport);
			stocksReportTaxList.add(stocksReportTax);
		}
		
		request.setAttribute("stocksReportTaxList", stocksReportTaxList);
	}

	private List<StocksOperation> getStockOperationListFromStockTaxOperation(int id, String type) {
		QueryParameter qp = new QueryParameter();
		qp.addSingleParameter("idStocksTax", QueryTypeFilter.EQUAL, id, QueryTypeCondition.AND);
		qp.addSingleParameter("taxOperationType", QueryTypeFilter.EQUAL, type, QueryTypeCondition.AND);
		StocksTaxOperation[] stocksTaxOperationList = DataManager.selectList(StocksTaxOperation[].class, qp);
		List<StocksOperation> stocksOperationList = new ArrayList<StocksOperation>();
		for (StocksTaxOperation stocksTaxOperation : stocksTaxOperationList) {
			StocksOperation stocksOperation = DataManager.selectId(StocksOperation.class, stocksTaxOperation.getIdStocksOperation());
			stocksOperationList.add(stocksOperation);
		}
		return stocksOperationList;
	}

	private void applyTaxToStocksSell(String typeAction) {
		String period = request.getParameter("period");
		period = period.substring(0, 4) + "-" + period.substring(4, 6);
		String sqlPeriod = period + "%";
		int monthPeriod = Integer.parseInt(period.split("-")[1]);
		int yearPeriod = Integer.parseInt(period.split("-")[0]);

//		Busca se já há um cálculo de imposto no período para atualizar
		QueryParameter qp = new QueryParameter();
		qp.addSingleParameter("taxType", QueryTypeFilter.EQUAL, generalInfo.TAX_IR, QueryTypeCondition.AND);
		qp.addSingleParameter("month", QueryTypeFilter.EQUAL, monthPeriod, QueryTypeCondition.AND);
		qp.addSingleParameter("year", QueryTypeFilter.EQUAL, yearPeriod, QueryTypeCondition.AND);
		StocksTax[] stocksTaxList = DataManager.selectList(StocksTax[].class, qp);
		StocksTax stocksTax = null;
		if (typeAction == CALCULATION && stocksTaxList != null && stocksTaxList.length > 0) {
			return;
		}

//		Busca Vendas com lucro realizadas no período
		qp.clearQuery();
		qp.addSingleParameter("datSettlement", QueryTypeFilter.CONTAINS, sqlPeriod, QueryTypeCondition.AND);
		qp.addSingleParameter("typeOperation", QueryTypeFilter.EQUAL, generalInfo.STOCK_SELL, QueryTypeCondition.AND);
//		qp.addSingleParameter("valResultSell", QueryTypeFilter.GREATER, "0", QueryTypeCondition.AND);
		StocksOperation[] stocksAllSellList = DataManager.selectList(StocksOperation[].class, qp);

		if (!typeAction.equals(LIST)) {
			stocksTax = new StocksTax();
			stocksTax.setTaxType(generalInfo.TAX_IR);
			stocksTax.setMonth(monthPeriod);
			stocksTax.setYear(yearPeriod);
		}

		List<StocksTaxOperation> stocksTaxOperationList = new ArrayList<StocksTaxOperation>();
		StocksReportTax stocksReportTax = new StocksReportTax();
		StocksReportTax.TaxReport taxReport = stocksReportTax.new TaxReport();

//		Acumula o valor do lucro menos custos e seta a venda como imposto calculado
//		System.out.println("\nVENDAS COM LUCRO");
		double totalSell = 0.0;  //Total das vendas no mês
		double profitSell = 0.0; //Total das vendas com lucro
		double resultSell = 0.0; //Total do lucro com as vendas

		for (StocksOperation stocksOperation : stocksAllSellList) {
			double sellValue = (stocksOperation.getQuantity() * stocksOperation.getValStock()) - stocksOperation.getValCost();

			totalSell += sellValue;
			if (stocksOperation.getValResultSell() < 0) {
				continue;
			}
			
			profitSell += sellValue;
			resultSell += stocksOperation.getValResultSell();

			stocksReportTax.addStocksSell(stocksOperation);

			if (typeAction == CALCULATION) {
				StocksTaxOperation stocksTaxOperation = new StocksTaxOperation();
				stocksTaxOperation.setIdStocksOperation(stocksOperation.getId());
				stocksTaxOperation.setTaxOperationGainType();
				stocksTaxOperationList.add(stocksTaxOperation);
			}

//			System.out.print(stocksOperation.getCodStock());
//			System.out.print(" | ");
//			System.out.print(stocksOperation.getDatSettlement());
//			System.out.print(" | ");
//			System.out.print(stocksOperation.getTypeOperation());
//			System.out.print(" | ");
//			System.out.print(stocksOperation.getQuantity());
//			System.out.print(" | ");
//			System.out.print(stocksOperation.getValStock());
//			System.out.print(" | ");
//			System.out.println(stocksOperation.getValResultSell());
		}
		totalSell = GeneralFunctions.round(totalSell, 2);
		profitSell = GeneralFunctions.round(profitSell, 2);
		resultSell = GeneralFunctions.round(resultSell, 2);
		totalSell = GeneralFunctions.round(totalSell, 2);
		
//		Guardou a posição antes das deduções
		taxReport.setTotalSell(totalSell);
		taxReport.setProfitSell(profitSell);
		taxReport.setResultSell(resultSell);
		
		System.out.print("\n\nTotal Lucro sem dedução: ");
		System.out.println(resultSell);
		System.out.print("Valor Imposto: ");
		System.out.println(GeneralFunctions.round(resultSell * generalInfo.TAX_IR_PERC, 2));
		
//		Pegar vendas com prejuízo
//		System.out.println("\nVENDAS COM PREJUIZO");
		StocksOperation[] stocksOperationLossList;
		qp.clearQuery();
		qp.addSingleParameter("datSettlement", QueryTypeFilter.LESSEQUAL, period + "-31", QueryTypeCondition.AND);
		qp.addSingleParameter("typeOperation", QueryTypeFilter.EQUAL, generalInfo.STOCK_SELL, QueryTypeCondition.AND);
		qp.addFieldParameter("valTotalIRLossConsumed", QueryTypeFilter.GREATER, "valResultSell", QueryTypeCondition.AND);		
		qp.addSingleParameter("valResultSell", QueryTypeFilter.LESS, "0", QueryTypeCondition.AND);
		stocksOperationLossList = DataManager.selectList(StocksOperation[].class, qp);

		double valIRconsumed = 0.0;
		double totalDeduction = 0.0;
		double resultWithDeduction = resultSell;

		if (stocksOperationLossList != null) {
			for (StocksOperation stocksOperationLoss : stocksOperationLossList) {
	
				if (resultWithDeduction > Math.abs(stocksOperationLoss.getValResultSell())) {
					valIRconsumed = stocksOperationLoss.consumeValIRLoss(stocksOperationLoss.getValResultSell());
				} else if (resultWithDeduction > 0) {
	//				Tratar parcialidade
					valIRconsumed = stocksOperationLoss.consumeValIRLoss(resultWithDeduction * -1);
				}
				totalDeduction += valIRconsumed;
				resultWithDeduction += valIRconsumed;
	
				stocksReportTax.addStocksDeductionList(stocksOperationLoss);

				if (typeAction == CALCULATION) {
					StocksTaxOperation stocksTaxOperation = new StocksTaxOperation();
					stocksTaxOperation.setIdStocksOperation(stocksOperationLoss.getId());
					stocksTaxOperation.setTaxOperationLossType();
					stocksTaxOperation.setValIRLossConsumed(valIRconsumed);
					stocksTaxOperationList.add(stocksTaxOperation);
				}

//				System.out.print(stocksOperationLoss.getId());
//				System.out.print(" | ");
//				System.out.print(stocksOperationLoss.getCodStock());
//				System.out.print(" | ");
//				System.out.print(stocksOperationLoss.getDatSettlement());
//				System.out.print(" | ");
//				System.out.print(stocksOperationLoss.getTypeOperation());
//				System.out.print(" | ");
//				System.out.print(stocksOperationLoss.getQuantity());
//				System.out.print(" | ");
//				System.out.print(stocksOperationLoss.getValStock());
//				System.out.print(" | ");
//				System.out.print(stocksOperationLoss.getValResultSell());
//				System.out.print(" | ");			
//				System.out.print(valIRconsumed);
//				System.out.print(" | ");
//				System.out.println(stocksOperationLoss.getValTotalIRLossConsumed());
				
				if (resultWithDeduction <= 0) {
					break;
				}
			}
		}

		stocksTax.setValTotalPayment(GeneralFunctions.round(resultWithDeduction * generalInfo.TAX_IR_PERC, 2));
		stocksTax.setValTotalDeduction(totalDeduction);
		if (typeAction == CALCULATION) {
			stocksTax.setPaymentStatus(1);
			saveCalculatedData(stocksTax, stocksTaxOperationList, stocksOperationLossList);
		}

//		System.out.print("\nPeríodo: ");
//		System.out.println(sqlPeriod);
//		System.out.print("Total das Vendas: ");
//		System.out.println(totalSell);
//		System.out.print("Total das Deduções: ");
//		System.out.println(totalDeduction);
//		System.out.print("Total Lucro: ");
//		System.out.println(resultWithDeduction);
//		System.out.print("Valor Imposto: ");
//		System.out.println(stocksTax.getValTotalPayment());
		
		taxReport.setId(stocksTax.getId());
		taxReport.setMonth(stocksTax.getMonth());
		taxReport.setYear(stocksTax.getYear());
		taxReport.setPaymentStatus(stocksTax.getPaymentStatus());
		taxReport.setTaxType(stocksTax.getTaxType());
		
		
		taxReport.setResultWithDeduction(resultWithDeduction);
		taxReport.setValTotalDeduction(stocksTax.getValTotalDeduction());
		taxReport.setValTotalPayment(stocksTax.getValTotalPayment());

		stocksReportTax.setStocksTax(taxReport);
		
		List<StocksReportTax> stocksReportTaxList = new ArrayList<StocksReportTax>();
		stocksReportTaxList.add(stocksReportTax);
		request.setAttribute("stocksReportTaxList", stocksReportTaxList);
		if (typeAction.equals(SIMULATION)) {
			request.setAttribute("showCalcutationButton", true);
			request.setAttribute("period", request.getParameter("period"));
		}
	}

	private void saveCalculatedData(StocksTax stocksTax, List<StocksTaxOperation> stocksTaxOperationList, StocksOperation[] stocksOperationLossList) {
		List<StocksTax> stocksTaxAdd = new ArrayList<>();
		stocksTaxAdd.add(stocksTax);
		DataManager.insert(StocksTax.class, stocksTaxAdd);

		QueryParameter qp = new QueryParameter();
		qp.addSingleParameter("taxType", QueryTypeFilter.EQUAL, generalInfo.TAX_IR, QueryTypeCondition.AND);
		qp.addSingleParameter("month", QueryTypeFilter.EQUAL, stocksTax.getMonth(), QueryTypeCondition.AND);
		qp.addSingleParameter("year", QueryTypeFilter.EQUAL, stocksTax.getYear(), QueryTypeCondition.AND);
		StocksTax[] stocksTaxList = DataManager.selectList(StocksTax[].class, qp);
		
		if (stocksTaxList != null && stocksTaxList.length > 0) {
			List<StocksTaxOperation> newStocksTaxOperationList = new ArrayList<StocksTaxOperation>();
			int id = stocksTaxList[0].getId();
			for (StocksTaxOperation stocksTaxOperation : stocksTaxOperationList) {
				stocksTaxOperation.setIdStocksTax(id);
				newStocksTaxOperationList.clear();
				newStocksTaxOperationList.add(stocksTaxOperation);
				DataManager.insert(StocksTaxOperation.class, newStocksTaxOperationList);
			}

//			Atualizar o valor consumido de cada operação de venda com prejuízo
			if (stocksOperationLossList != null) {
				for (StocksOperation stocksOperation : stocksOperationLossList) {
					DataManager.updateId(StocksOperation.class, stocksOperation);
				}
			}
		}
	}
}
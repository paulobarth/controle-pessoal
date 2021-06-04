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
import com.cp.model.view.StocksReportTax;

public class StocksTaxController extends BaseControllerImpl {

	private static final String SIMULATION  = "SIMULATION";
	private static final String CALCULATION = "CALCULATION";

	@Override
	public void executeCallBack() throws ServletException, IOException {
		if (option.equals("list")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksReportTax.jsp").forward(request, response);
		} else if (option.startsWith("taxSimulation") || option.equals("taxCalculation")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksReportTax.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/stocksReportTax.list");
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

		default:
			break;
		}
	}
	
	@Override
	protected void list() {
		super.list();
	}

	private void applyTaxToStocksSell(String typeAction) {
		String period = request.getParameter("period");
		period = period.substring(0, 4) + "-" + period.substring(4, 6);
		String sqlPeriod = period + "%";

		StocksReportTax stocksReportTax = new StocksReportTax();
		StocksReportTax.TaxReport taxReport = stocksReportTax.new TaxReport();

//		Busca Vendas com lucro realizadas no período
		QueryParameter qp = new QueryParameter();
		qp.addSingleParameter("datOperation", QueryTypeFilter.CONTAINS, sqlPeriod, QueryTypeCondition.AND);
		qp.addSingleParameter("typeOperation", QueryTypeFilter.EQUAL, generalInfo.STOCK_SELL, QueryTypeCondition.AND);
//		qp.addSingleParameter("valResultSell", QueryTypeFilter.GREATER, "0", QueryTypeCondition.AND);
		StocksOperation[] stocksAllSellList = DataManager.selectList(StocksOperation[].class, qp);

//		Busca se já há um cálculo de imposto no período para atualizar
		StocksTax stocksTax = new StocksTax();
		int monthPeriod = Integer.parseInt(period.split("-")[1]);
		int yearPeriod = Integer.parseInt(period.split("-")[0]);
		qp.clearQuery();
		qp.addSingleParameter("taxType", QueryTypeFilter.EQUAL, generalInfo.TAX_IR, QueryTypeCondition.AND);
		qp.addSingleParameter("month", QueryTypeFilter.EQUAL, monthPeriod, QueryTypeCondition.AND);
		qp.addSingleParameter("year", QueryTypeFilter.EQUAL, yearPeriod, QueryTypeCondition.AND);
		StocksTax[] stocksTaxList = null;
		if (typeAction == CALCULATION) {
			stocksTaxList = DataManager.selectList(StocksTax[].class, qp);
		}
		if (stocksTaxList != null && stocksTaxList.length > 0) {
			stocksTax = stocksTaxList[0];
		} else {
			stocksTax = new StocksTax();
			stocksTax.setTaxType(generalInfo.TAX_IR);
			stocksTax.setMonth(monthPeriod);
			stocksTax.setYear(yearPeriod);
		}

//		Acumula o valor do lucro menos custos e seta a venda como imposto calculado
		System.out.println("\nVENDAS COM LUCRO");
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

			stocksOperation.setOperationGainIRCalculated("S");
			
			stocksReportTax.addStocksSell(stocksOperation);

			System.out.print(stocksOperation.getCodStock());
			System.out.print(" | ");
			System.out.print(stocksOperation.getDatOperation());
			System.out.print(" | ");
			System.out.print(stocksOperation.getTypeOperation());
			System.out.print(" | ");
			System.out.print(stocksOperation.getQuantity());
			System.out.print(" | ");
			System.out.print(stocksOperation.getValStock());
			System.out.print(" | ");
			System.out.println(stocksOperation.getValResultSell());
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
		System.out.print("\n");
		
//		Pegar vendas com prejuízo
		System.out.println("\nVENDAS COM PREJUIZO");
		qp.clearQuery();
		qp.addSingleParameter("datOperation", QueryTypeFilter.LESSEQUAL, period + "-31", QueryTypeCondition.AND);
		qp.addSingleParameter("typeOperation", QueryTypeFilter.EQUAL, generalInfo.STOCK_SELL, QueryTypeCondition.AND);
		qp.addFieldParameter("valIRLossConsumed", QueryTypeFilter.GREATER, "valResultSell", QueryTypeCondition.AND);
		
		qp.addSingleParameter("valResultSell", QueryTypeFilter.LESS, "0", QueryTypeCondition.AND);
		StocksOperation[] stocksOperationLossList = DataManager.selectList(StocksOperation[].class, qp);
		double valIRconsumed = 0.0;
		double totalDeduction = 0.0;
		double resultWithDeduction = 0.0;

		for (StocksOperation stocksOperationLoss : stocksOperationLossList) {

			if (resultSell > Math.abs(stocksOperationLoss.getValResultSell())) {
				valIRconsumed = stocksOperationLoss.consumeValIRLoss(stocksOperationLoss.getValResultSell());
			} else if (resultSell > 0) {
//				Tratar parcialidade
				valIRconsumed = stocksOperationLoss.consumeValIRLoss(resultSell * -1);
			}
			totalDeduction += valIRconsumed;
			resultSell += valIRconsumed;

			stocksOperationLoss.setMonthIRLossConsumed(period);
			
			stocksReportTax.addStocksDeductionList(stocksOperationLoss);

			System.out.print(stocksOperationLoss.getId());
			System.out.print(" | ");
			System.out.print(stocksOperationLoss.getCodStock());
			System.out.print(" | ");
			System.out.print(stocksOperationLoss.getDatOperation());
			System.out.print(" | ");
			System.out.print(stocksOperationLoss.getTypeOperation());
			System.out.print(" | ");
			System.out.print(stocksOperationLoss.getQuantity());
			System.out.print(" | ");
			System.out.print(stocksOperationLoss.getValStock());
			System.out.print(" | ");
			System.out.print(stocksOperationLoss.getValResultSell());
			System.out.print(" | ");			
			System.out.print(stocksOperationLoss.getValIRLossConsumed());
			System.out.print(" | ");
			System.out.println(stocksOperationLoss.getMonthIRLossConsumed());
			
			if (resultSell <= 0) {
				break;
			}
		}

		stocksTax.setValTotalPayment(GeneralFunctions.round(resultSell * generalInfo.TAX_IR_PERC, 2));
		stocksTax.setValTotalDeduction(totalDeduction);

		System.out.print("\nPeríodo: ");
		System.out.println(sqlPeriod);
		System.out.print("Total das Vendas: ");
		System.out.println(totalSell);
		System.out.print("Total das Deduções: ");
		System.out.println(totalDeduction);
		System.out.print("Total Lucro: ");
		System.out.println(resultSell);
		System.out.print("Valor Imposto: ");
		System.out.println(stocksTax.getValTotalPayment());

		
		
		taxReport.setId(stocksTax.getId());
		taxReport.setMonth(stocksTax.getMonth());
		taxReport.setYear(stocksTax.getYear());
		taxReport.setPaymentStatus(stocksTax.getPaymentStatus());
		taxReport.setTaxType(stocksTax.getTaxType());
		
		
		taxReport.setResultWithDeduction(resultSell);
		taxReport.setValTotalDeduction(stocksTax.getValTotalDeduction());
		taxReport.setValTotalPayment(stocksTax.getValTotalPayment());

		stocksReportTax.setStocksTax(taxReport);
		
		List<StocksReportTax> stocksReportTaxList = new ArrayList<StocksReportTax>();
		stocksReportTaxList.add(stocksReportTax);
		request.setAttribute("stocksReportTaxList", stocksReportTaxList);
	}
}
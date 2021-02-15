package com.cp.controller.stocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.fwk.data.DataManager;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.StocksGain;
import com.cp.model.view.StocksReportOperation;

public class StocksGainController {

	public static void execute(HttpServletRequest request, HttpServletResponse response, String option)
			throws ServletException, IOException {

		switch (option) {
		case "list":
			selectAll(request, response);
			break;
		case "save":
			saveStocksGain(request, response);
			break;
		case "update":
			updateStocksGain(request, response);
			break;
		case "delete":
			deleteStocksGain(request, response);
			break;

		case "reportList":
		case "reportFilter":
			reportListStocksGain(request, response);
			break;

		default:
			break;
		}

		if (option.equals("list") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksGain.jsp").forward(request, response);
		} else if (option.startsWith("report")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksReportGain.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/stocksGain.list");
		}
	}

	public static void selectAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		QueryParameter qp = new QueryParameter();
		qp.addOrderByOption("codStock", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		qp.addOrderByOption("datGain", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		StocksGain[] stocksOperationList = DataManager.selectList(StocksGain[].class, qp);

		request.setAttribute("stocksOperationList", stocksOperationList);
	}

	public static void saveStocksGain(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		StocksGain StocksGain = new StocksGain();
		StocksGain.setTypeGain(request.getParameter("typeGain"));
		StocksGain.setCodStock(request.getParameter("codStock"));
		StocksGain.setDatGain(request.getParameter("datGain"));
		StocksGain.setValGain(request.getParameter("valGain"));

		String idParam = request.getParameter("id");

		if (idParam.isEmpty()) {
			List<StocksGain> lStocksGain = new ArrayList<StocksGain>();
			lStocksGain.add(StocksGain);
			DataManager.insert(StocksGain.class, lStocksGain);
		} else {
			StocksGain.setId(Integer.parseInt(request.getParameter("id")));
			DataManager.updateId(StocksGain.class, StocksGain);
		}
	}

	private static void updateStocksGain(HttpServletRequest request, HttpServletResponse response) {

		StocksGain stocksGain = DataManager.selectId(StocksGain.class,
				Integer.parseInt(request.getParameter("id")));

		request.setAttribute("stocksGain", stocksGain);

		List<StocksGain> stocksGainList = new ArrayList<StocksGain>();
		stocksGainList.add(stocksGain);

		request.setAttribute("stocksGainList", stocksGainList);

	}

	private static void deleteStocksGain(HttpServletRequest request, HttpServletResponse response) {
		DataManager.deleteId(StocksGain.class, Integer.parseInt(request.getParameter("id")));
	}

	private static void reportListStocksGain(HttpServletRequest request, HttpServletResponse response) {

		String lastTypeOperation = "";
		boolean bRecalcLastMedPrice = false;
		Double lastMedPrice = 0.00;
		int acumQuantity = 0;
		Double acumMedPrice = 0.00;
		QueryParameter qp = new QueryParameter();

		List<StocksReportOperation> listSRPO = new ArrayList<StocksReportOperation>();
		
		String filterStockItem = request.getParameter("filterStockItem");
		
		if (filterStockItem != null) {
			request.setAttribute("filterStockItem", filterStockItem);

		}
		StocksGain[] stocksOperationList = DataManager.selectList(StocksGain[].class, qp);
		
		Set<String> stocksList = getListOfStocks(stocksOperationList);

		/*
		for (String stock : stocksList) {
			
			if (filterStockItem != null && !filterStockItem.isEmpty() &&
					!stock.equals(filterStockItem)) {
				continue;
			}

			lastTypeOperation = "";
			bRecalcLastMedPrice = false;
			lastMedPrice = 0.00;
			acumQuantity = 0;
			acumMedPrice = 0.00;

			StocksReportOperation sRPO = new StocksReportOperation();
			sRPO.setCodStock(stock);

			qp.clearQuery();
			qp.addSingleParameter("codStock", QueryTypeFilter.EQUAL, stock, QueryTypeCondition.AND);
			qp.addOrderByOption("datGain", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);

			stocksOperationList = dataManager.selectList(StocksGain[].class, qp);

			for (StocksGain teste : stocksOperationList) {

				if (!lastTypeOperation.isEmpty() && !lastTypeOperation.equals(teste.getTypeOperation())) {

					addResultLine(sRPO, teste.getCodStock(), acumQuantity, lastMedPrice);
				}

				StocksReportOperation.StocksOper stockOper = sRPO.new StocksOper();

				stockOper.setCodStock(teste.getCodStock());
				stockOper.setTypeOperation(teste.getTypeOperation());
				stockOper.setDatOperation(teste.getDatOperation());
				stockOper.setQuantity(teste.getQuantity());
				stockOper.setValCost(teste.getValCost());
				stockOper.setValStock(teste.getValStock());

//				Total da operação sem custo
				stockOper.setTotalOperation(teste.getQuantity() * teste.getValStock());

//				Total da operação mais custo
				if (teste.getTypeOperation().equals("Compra")) {
					stockOper.setTotalOperCost(stockOper.getTotalOperation() + teste.getValCost());
				} else {
					stockOper.setTotalOperCost(stockOper.getTotalOperation() - teste.getValCost());

					stockOper.setResultSell(stockOper.getTotalOperCost() - (lastMedPrice * teste.getQuantity()));
				}

//				Preço unitário dividido
				stockOper.setMedPrice(stockOper.getTotalOperCost() / teste.getQuantity());
				
				sRPO.setStocksList(stockOper);

//				Acumula e guarda dados para a próxima linha
				if (teste.getTypeOperation().equals("Compra")) {

					acumQuantity += teste.getQuantity();

					if (bRecalcLastMedPrice) {
						lastMedPrice = ((lastMedPrice * (acumQuantity - teste.getQuantity()))
								+ stockOper.getTotalOperCost()) / acumQuantity;

						acumMedPrice = lastMedPrice * acumQuantity;
					} else {

						acumMedPrice += stockOper.getTotalOperCost();

						if (!bRecalcLastMedPrice) {
							lastMedPrice = acumMedPrice / acumQuantity;
						}
					}

					bRecalcLastMedPrice = false;

				} else {
					bRecalcLastMedPrice = true;
					acumQuantity -= teste.getQuantity();
					acumMedPrice -= stockOper.getTotalOperCost();
				}

				lastTypeOperation = teste.getTypeOperation();
			}

			if (sRPO.getStocksList().size() > 0) {
				addResultLine(sRPO, stock, acumQuantity, lastMedPrice);
			}

			listSRPO.add(sRPO);

		}
		*/

		request.setAttribute("listSRPO", listSRPO);
		request.setAttribute("stockList", stocksList);

	}

	private static Set<String> getListOfStocks(StocksGain[] stocksOperationList) {

//		Map<String, String> list = new HashMap<String, String>();
		Set<String> list = new TreeSet<>();

		for (StocksGain teste : stocksOperationList) {
			list.add(teste.getCodStock());
		}

		return list;
	}

	private static void addResultLine(StocksReportOperation sRPO, String codStock, int acumQuantity,
			Double lastMedPrice) {

		StocksReportOperation.StocksOper stockOper = sRPO.new StocksOper();

		stockOper.setTypeOperation("R");
		stockOper.setCodStock(codStock);
		stockOper.setQuantity(acumQuantity);
		stockOper.setMedPrice(lastMedPrice);

		stockOper.setDatOperation("01/01/2020");
		stockOper.setValCost(0.00);
		stockOper.setValStock(0.00);
		stockOper.setTotalOperation(0.00);
		stockOper.setTotalOperCost(0.00);

		sRPO.setStocksList(stockOper);
	}
}

package com.cp.controller.stocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.constants.generalInfo;
import com.cp.dao.dataManager;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.StocksOperation;
import com.cp.model.view.StocksReportActualPosition;
import com.cp.model.view.StocksReportMonthSale;
import com.cp.model.view.StocksReportOperation;

public class StocksOperationController {

	public static void execute(HttpServletRequest request, HttpServletResponse response, String option)
			throws ServletException, IOException {

		switch (option) {
		case "list":
			selectAll(request, response);
			break;
		case "save":
			saveStocksOperation(request, response);
			break;
		case "update":
			updateStocksOperation(request, response);
			break;
		case "delete":
			deleteStocksOperation(request, response);
			break;

		case "reportList":
		case "reportFilter":
			reportListStocksOperation(request, response);
			break;

		default:
			break;
		}

		if (option.equals("list") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksOperation.jsp").forward(request, response);
		} else if (option.startsWith("report")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksReportOperation.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/stocksOperation.list");
		}
	}

	public static void selectAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		QueryParameter qp = new QueryParameter();
		qp.addOrderByOption("codStock", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		qp.addOrderByOption("datOperation", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		StocksOperation[] stocksOperationList = dataManager.selectList(StocksOperation[].class, qp);

		request.setAttribute("stocksOperationList", stocksOperationList);
	}

	public static void saveStocksOperation(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		StocksOperation StocksOperation = new StocksOperation();
		StocksOperation.setTypeOperation(request.getParameter("typeOperation"));
		StocksOperation.setCodStock(request.getParameter("codStock"));
		StocksOperation.setDatOperation(request.getParameter("datOperation"));
		StocksOperation.setQuantity(request.getParameter("quantity"));
		StocksOperation.setValStock(request.getParameter("valStock"));
		StocksOperation.setValCost(request.getParameter("valCost"));

		String idParam = request.getParameter("id");

		if (idParam.isEmpty()) {
			List<StocksOperation> lStocksOperation = new ArrayList<StocksOperation>();
			lStocksOperation.add(StocksOperation);
			dataManager.insert(StocksOperation.class, lStocksOperation);
		} else {
			StocksOperation.setId(Integer.parseInt(request.getParameter("id")));
			dataManager.updateId(StocksOperation.class, StocksOperation);
		}
	}

	private static void updateStocksOperation(HttpServletRequest request, HttpServletResponse response) {

		StocksOperation stocksOperation = dataManager.selectId(StocksOperation.class,
				Integer.parseInt(request.getParameter("id")));

		request.setAttribute("stocksOperation", stocksOperation);

		List<StocksOperation> stocksOperationList = new ArrayList<StocksOperation>();
		stocksOperationList.add(stocksOperation);

		request.setAttribute("stocksOperationList", stocksOperationList);

	}

	private static void deleteStocksOperation(HttpServletRequest request, HttpServletResponse response) {
		dataManager.deleteId(StocksOperation.class, Integer.parseInt(request.getParameter("id")));
	}

	private static void reportListStocksOperation(HttpServletRequest request, HttpServletResponse response) {

		String lastTypeOperation = "";
		boolean bRecalcLastMedPrice = false;
		Double lastMedPrice = 0.00;
		int acumQuantity = 0;
		Double acumMedPrice = 0.00;
		QueryParameter qp = new QueryParameter();
		
//		ConsumoApplication.execute();
		

		List<StocksReportOperation> listSRPO = new ArrayList<StocksReportOperation>();
		List<StocksReportActualPosition> listActualPosition = new ArrayList<StocksReportActualPosition>();

		String filterStockItem = request.getParameter("filterStockItem");
		String showOnlyOpened = request.getParameter("showOnlyOpenedItem");

		if (filterStockItem != null) {
			request.setAttribute("filterStockItem", filterStockItem);
		}

		qp.addOrderByOption("datOperation", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		StocksOperation[] stocksOperationList = dataManager.selectList(StocksOperation[].class, qp);

		request.setAttribute("listMonthSales", makeMonthSaleView(stocksOperationList).values());

		Set<String> stocksList = getListOfStocks(stocksOperationList);

		for (String stock : stocksList) {

			if (filterStockItem != null && !filterStockItem.isEmpty() && !stock.equals(filterStockItem)) {
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
			qp.addOrderByOption("datOperation", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);

			stocksOperationList = dataManager.selectList(StocksOperation[].class, qp);

			for (StocksOperation teste : stocksOperationList) {

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
				if (teste.getTypeOperation().equals(generalInfo.STOCK_BUY)) {
					stockOper.setTotalOperCost(stockOper.getTotalOperation() + teste.getValCost());
				} else {
					stockOper.setTotalOperCost(stockOper.getTotalOperation() - teste.getValCost());

					stockOper.setResultSell(stockOper.getTotalOperCost() - (lastMedPrice * teste.getQuantity()));
				}

//				Preço unitário dividido
				if (teste.getTypeOperation().equals(generalInfo.STOCK_BUY)) {
					stockOper.setMedPrice(stockOper.getTotalOperCost() / teste.getQuantity());
				} else {
					stockOper.setMedPrice(lastMedPrice);
				}

				sRPO.setStocksList(stockOper);

//				Acumula e guarda dados para a próxima linha
				if (teste.getTypeOperation().equals(generalInfo.STOCK_BUY)) {

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

			if (sRPO.getStocksList().get(sRPO.getStocksList().size() - 1).getQuantity() == 0) {
//				continue;
			}

			addActualPosition(listActualPosition, sRPO, acumQuantity, lastMedPrice);

			listSRPO.add(sRPO);
		}

		request.setAttribute("listSRPO", listSRPO);
		request.setAttribute("stockList", stocksList);
		request.setAttribute("listActualPosition", listActualPosition);

	}

	private static void addActualPosition(List<StocksReportActualPosition> listActualPosition,
			StocksReportOperation sRPO, int acumQuantity, Double acumMedPrice) {

		StocksReportActualPosition actualPosition = new StocksReportActualPosition();
		actualPosition.setCodStock(sRPO.getCodStock());
		actualPosition.setActualQuantity(acumQuantity);
		actualPosition.setMedPrice(acumMedPrice);
		
		for (StocksReportOperation.StocksOper itemStock : sRPO.getStocksList()) {
			
			if (itemStock.getTypeOperation().equals(generalInfo.STOCK_BUY)) {
				actualPosition.addTotalBuy(itemStock.getTotalOperCost());
			} else if (itemStock.getTypeOperation().equals(generalInfo.STOCK_SELL)) {
				actualPosition.addTotalSellMedPrice(itemStock.getQuantity() * itemStock.getMedPrice());
				actualPosition.addTotalResultSell(itemStock.getResultSell());
				actualPosition.addTotalSell(itemStock.getTotalOperCost());
			}
		}

		actualPosition.calculateResult();

		listActualPosition.add(actualPosition);
	}

	private static Map<String, StocksReportMonthSale> makeMonthSaleView(StocksOperation[] stocksOperationList) {
		String period;
		Double totalAmount = 0.0;
		Map<String, StocksReportMonthSale> monthSales = new TreeMap<String, StocksReportMonthSale>();

		for (StocksOperation stocksOperation : stocksOperationList) {
//			Procura para cada venda se já há ano/mês e empresa para somar a venda.

			if (!"Venda".equals(stocksOperation.getTypeOperation())) {
				continue;
			}

			period = GeneralFunctions.getYearMonthOfDate(stocksOperation.getDatOperation());

			StocksReportMonthSale sale = monthSales.get(period);

			totalAmount = stocksOperation.getQuantity() * stocksOperation.getValStock();

			if (sale == null) {
				sale = new StocksReportMonthSale();

				sale.setPeriod(period);
				sale.setValue(totalAmount);

				monthSales.put(period, sale);
			} else {
				sale.sumValue(totalAmount);
			}
			sale.addStockSale(stocksOperation.getCodStock(), stocksOperation.getDatOperation(), totalAmount);
		}

		return monthSales;
	}

	private static Set<String> getListOfStocks(StocksOperation[] stocksOperationList) {

//		Map<String, String> list = new HashMap<String, String>();
		Set<String> list = new TreeSet<>();

		for (StocksOperation teste : stocksOperationList) {
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

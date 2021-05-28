package com.cp.controller.stocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.constants.generalInfo;
import com.cp.controller.BaseControllerImpl;
import com.cp.fwk.data.DataManager;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.Stocks;
import com.cp.model.StocksOperation;
import com.cp.model.StocksTax;
import com.cp.model.view.StocksReportActualPosition;
import com.cp.model.view.StocksReportMonthSale;
import com.cp.model.view.StocksReportOperation;
import com.cp.rest.WebService;
import com.cp.rest.WebService.Dados;
import com.cp.rest.WebService.Results;

public class StocksOperationController extends BaseControllerImpl {

	@Override
	public void executeCallBack() throws ServletException, IOException {
		if (option.equals("list") || option.equals("filter") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksOperation.jsp").forward(request, response);
		} else if (option.startsWith("report") || option.equals("taxCalculation")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksReportOperation.jsp").forward(request, response);
		} else if (option.equals("costs")) {
			request.getRequestDispatcher("/WEB-INF/views/stocksOperationCosts.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/stocksOperation.list");
		}		
	}
	
	@Override
	protected void specificOptionToExecute() {
		switch (option) {
		case "reportList":
			applyFilterMovement();
			break;
		case "reportFilter":
			super.filter();
			break;
		case "costs":
			costsList();
			break;
		case "costsCalculation":
			applyCostsToStocksOperations();
			break;
		case "taxCalculation":
			applyTaxToStocksSell();
			break;

		default:
			break;
		}
	}

	@Override
	protected void list() {
		applyFilterListMovement("", "");
	}
	
	@Override
	protected void save() {
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
			DataManager.insert(StocksOperation.class, lStocksOperation);
		} else {
			StocksOperation.setId(Integer.parseInt(request.getParameter("id")));
			DataManager.updateId(StocksOperation.class, StocksOperation);
		}
	}
	
	@Override
	protected void update() {
		StocksOperation stocksOperation = DataManager.selectId(StocksOperation.class,
				Integer.parseInt(request.getParameter("id")));

		request.setAttribute("stocksOperation", stocksOperation);

		List<StocksOperation> stocksOperationList = new ArrayList<StocksOperation>();
		stocksOperationList.add(stocksOperation);

		request.setAttribute("stocksOperationList", stocksOperationList);
	}
	
	@Override
	protected void delete() {
		DataManager.deleteId(StocksOperation.class, Integer.parseInt(request.getParameter("id")));
	}

	@Override
	protected void applyFilterMovement() {
		if (option.startsWith("report")) {
			applyReportFilterMovement();
		} else {
			applyFilterListMovement(GeneralFunctions.stringDatetoSql(request.getParameter("filterDatOperationIni")),
					GeneralFunctions.stringDatetoSql(request.getParameter("filterDatOperationEnd")));
		}
	}
	
	private void applyFilterListMovement(String datIni, String datEnd) {
		QueryParameter qp = new QueryParameter();
		if (!"".equals(datIni) || !"".equals(datEnd)) {
			if ("".equals(datEnd)) {
				qp.addSingleParameter("datOperation", QueryTypeFilter.EQUAL, datIni,
						QueryTypeCondition.AND);
			} else {
				qp.addBetweenParameter("datOperation", new String[] {datIni, datEnd}, QueryTypeCondition.AND);
			}
		}
		
		qp.addOrderByOption("codStock", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		qp.addOrderByOption("datOperation", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		request.setAttribute("stocksOperationList", DataManager.selectList(StocksOperation[].class, qp));
	}

	private void applyReportFilterMovement() {
		String lastTypeOperation = "";
		boolean bRecalcLastMedPrice = false;
		Double lastMedPrice = 0.00;
		int acumQuantity = 0;
		Double acumMedPrice = 0.00;
		Stocks[] stocksPrices;
		QueryParameter qp = new QueryParameter();
		Stocks[] currentStocks;
		boolean showAllStocks = true;
		
		List<StocksReportOperation> listSRPO = new ArrayList<StocksReportOperation>();
		List<StocksReportActualPosition> listActualPosition = new ArrayList<StocksReportActualPosition>();

		String filterStockItem = request.getParameter("filterStockItem");
		String filterCodPortfolio = request.getParameter("filterCodPortfolio");
		String filterYearOperation = request.getParameter("filterYearOperation");
		boolean filterShowOnlyOpened = Boolean.parseBoolean(request.getParameter("filterShowOnlyOpened"));
		boolean filterStockPrice = Boolean.parseBoolean(request.getParameter("filterStockPrice"));
		
		showAllStocks = filterStockItem == null && filterCodPortfolio == null && filterYearOperation == null;

		qp.addSingleNotEmptyParameter("codStock", QueryTypeFilter.EQUAL, filterStockItem, QueryTypeCondition.AND);
		qp.addSingleNotEmptyParameter("codPortfolio", QueryTypeFilter.EQUAL, filterCodPortfolio, QueryTypeCondition.AND);
		currentStocks = DataManager.selectList(Stocks[].class, qp);
		
		qp.clearQuery();
		qp.addOrderByOption("datOperation", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		if (filterYearOperation != null && !filterYearOperation.isEmpty()) {
			qp.addBetweenParameter("datOperation",
					new String[] {GeneralFunctions.stringDatetoSql("01/01/1960"),
								  GeneralFunctions.stringDatetoSql(filterYearOperation)
					}, QueryTypeCondition.AND);
		}
		StocksOperation[] stocksOperationList = DataManager.selectList(StocksOperation[].class, qp);

		request.setAttribute("listMonthSales", makeMonthSaleView(stocksOperationList).values());

		Set<String> stocksList = getListOfStocksFromStocksOperation(stocksOperationList, currentStocks, showAllStocks);

		if (filterStockPrice) {
			stocksPrices = updateStockPrice(stocksList, currentStocks);
		} else {
			stocksPrices = DataManager.selectList(Stocks[].class);
		}

		request.setAttribute("updatedAtInfo", formatUpdatedAtInfo(stocksPrices));

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

			stocksOperationList = DataManager.selectList(StocksOperation[].class, qp);

			for (StocksOperation stockOperation : stocksOperationList) {

				if (!lastTypeOperation.isEmpty() && !lastTypeOperation.equals(stockOperation.getTypeOperation())) {

					addResultLine(sRPO, stockOperation.getCodStock(), acumQuantity, lastMedPrice);
				}

				StocksReportOperation.StocksOper stockOper = sRPO.new StocksOper();

				stockOper.setCodStock(stockOperation.getCodStock());
				stockOper.setTypeOperation(stockOperation.getTypeOperation());
				stockOper.setDatOperation(stockOperation.getDatOperation());
				stockOper.setQuantity(stockOperation.getQuantity());
				stockOper.setValCost(stockOperation.getValCost());
				stockOper.setValStock(stockOperation.getValStock());

//				Total da operação sem custo
				stockOper.setTotalOperation(stockOperation.getQuantity() * stockOperation.getValStock());

//				Total da operação mais custo
				if (stockOperation.getTypeOperation().equals(generalInfo.STOCK_BUY)) {
					stockOper.setTotalOperCost(stockOper.getTotalOperation() + stockOperation.getValCost());
				} else {
					stockOper.setTotalOperCost(stockOper.getTotalOperation() - stockOperation.getValCost());
//					ResultSell lucro ou prejuízo
//					TODO: gravar o preço médio e o valor do lucro ou prejuízo no cadastro da operação
					if (stockOperation.getValResultSell() == 0) {
						stockOperation.setValResultSell(GeneralFunctions.round(stockOper.getTotalOperCost() - (lastMedPrice * stockOperation.getQuantity()), 2));
						updateStockOperationResultSell(stockOperation);
					}
					stockOper.setResultSell(stockOperation.getValResultSell());
				}

//				Preço unitário dividido
				if (stockOperation.getTypeOperation().equals(generalInfo.STOCK_BUY)) {
					stockOper.setMedPrice(stockOper.getTotalOperCost() / stockOperation.getQuantity());
				} else {
					stockOper.setMedPrice(lastMedPrice);
				}

				sRPO.setStocksList(stockOper);

//				Acumula e guarda dados para a próxima linha
				if (stockOperation.getTypeOperation().equals(generalInfo.STOCK_BUY)) {

					acumQuantity += stockOperation.getQuantity();

					if (bRecalcLastMedPrice) {
						lastMedPrice = ((lastMedPrice * (acumQuantity - stockOperation.getQuantity()))
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
					acumQuantity -= stockOperation.getQuantity();
					acumMedPrice -= stockOper.getTotalOperCost();
				}

				lastTypeOperation = stockOperation.getTypeOperation();
			}

			if (sRPO.getStocksList().size() > 0) {
				addResultLine(sRPO, stock, acumQuantity, lastMedPrice);
			}

			if (filterShowOnlyOpened && sRPO.getStocksList().get(sRPO.getStocksList().size() - 1).getQuantity() == 0) {
				continue;
			}

			Double actualPrice = 0.0;
			for (Stocks item : stocksPrices) {
				if (item.getCodStock().equals(stock)) {
					actualPrice = item.getActualPrice();
					break;
				}
			}

			addActualPosition(listActualPosition, sRPO, acumQuantity, lastMedPrice, actualPrice);

			listSRPO.add(sRPO);
		}

		request.setAttribute("listSRPO", listSRPO);
		request.setAttribute("stockList", stocksList);
		request.setAttribute("listActualPosition", listActualPosition);
		
		double totalLoss = 0.0;
		double totalGain = 0.0;
		double totalDifference = 0.0;
		double totalFuture = 0.0;
		for (StocksReportActualPosition aaa : listActualPosition) {
			if (aaa.getResult() < 0) {
				totalLoss += aaa.getTotalResultSell();
			} else {
				totalGain += aaa.getTotalResultSell();
			}
			if (aaa.getTotalActualResult() != null) {
				totalFuture += aaa.getTotalActualResult();
			}
		}
		totalDifference = totalGain + totalLoss;
		
		request.setAttribute("totalLoss", totalLoss);
		request.setAttribute("totalGain", totalGain);
		request.setAttribute("totalDifference", totalDifference);
		request.setAttribute("totalFuture", totalFuture);

	}

	private void updateStockOperationResultSell(StocksOperation stockOperation) {
		DataManager.updateId(StocksOperation.class, stockOperation);
		System.out.println("Result Sell Update: " +
				stockOperation.getCodStock() + "  |  " +
				stockOperation.getDatOperation() + "  |  " +
				stockOperation.getTypeOperation() + "  |  " +
				stockOperation.getValResultSell() + "  |  "
				);
		
		
	}

	private static void addActualPosition(List<StocksReportActualPosition> listActualPosition,
			StocksReportOperation sRPO, int acumQuantity, Double acumMedPrice, Double actualPrice) {

		StocksReportActualPosition actualPosition = new StocksReportActualPosition();
		actualPosition.setCodStock(sRPO.getCodStock());
		actualPosition.setActualQuantity(acumQuantity);
		actualPosition.setMedPrice(acumMedPrice);
		actualPosition.setActualPrice(actualPrice);

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

	private static Stocks[] updateStockPrice(Set<String> stocksList, Stocks[] currentStocks) {

		Double actualPrice = 0.0;
		List<Stocks> stockPriceList = new ArrayList<Stocks>();
		Stocks newStock;
		boolean isNewStock;
		
		for (String codStock : stocksList) {

			Dados stockWSData = WebService.getStockData(codStock);
			actualPrice = 0.0;
			try {
				actualPrice = Double.parseDouble(stockWSData.getResults().getStock().getPrice());
			} catch (Exception e) {
			}
			if (actualPrice == 0) {
				continue;
			}
			newStock = null;
			isNewStock = true;
			for (Stocks stock : currentStocks) {
				if (stock.getCodStock().equals(codStock)) {
					newStock = stock;
					isNewStock = false;
					break;
				}
			}
			if (newStock == null) {
				newStock = new Stocks();
				newStock.setCodStock(codStock);
			}
			newStock.setName(stockWSData.getResults().getStock().getName());
			newStock.setCompanyName(stockWSData.getResults().getStock().getCompany_name());
			newStock.setActualPrice(actualPrice);
			newStock.setUpdateAt(stockWSData.getResults().getStock().getUpdated_at());

			stockPriceList.add(newStock);

			if (isNewStock) {
				List<Stocks> inserStockList = new ArrayList<Stocks>();
				inserStockList.add(newStock);
				DataManager.insert(Stocks.class, inserStockList);
			} else {
				DataManager.updateId(Stocks.class, newStock);
			}
		}

		Stocks[] stocksArray = new Stocks[stockPriceList.size()];

		return stockPriceList.toArray(stocksArray);
	}

	private static String formatUpdatedAtInfo(Stocks[] stocksPrices) {

		String result = "";
		if (stocksPrices != null && stocksPrices.length > 0) {
			try {
				for (Stocks itemStock : stocksPrices) {
					if (itemStock.getUpdateAt() != null && !"".equals(itemStock.getUpdateAt().trim())) {
						String[] split = itemStock.getUpdateAt().split(" ");
						String[] date = split[0].split("-");
						result = date[2] + " de " + GeneralFunctions.convertMonthToText(date[1]) + " de " + date[0]
								+ " " + split[1];
						break;
					}
				}
			} catch (Exception e) {
			}
		}
		return result;

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
			sale.addStockSale(stocksOperation.getCodStock(), stocksOperation.getDatOperation(), totalAmount, stocksOperation.getValResultSell());
		}

		return monthSales;
	}

	private static Set<String> getListOfStocksFromStocksOperation(StocksOperation[] stocksOperationList, Stocks[] stocksList, boolean showAllStocks) {
		Set<String> list = new TreeSet<>();

		boolean found;
		for (StocksOperation teste : stocksOperationList) {
			found = false;
			if (!showAllStocks) {
				for (Stocks stock : stocksList) {
					if (teste.getCodStock().equals(stock.getCodStock())) {
						found = true;
						break;
					}
				}
				if (!found) {
					continue;
				}
			}
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

	private void costsList() {
		QueryParameter qp = new QueryParameter();
		qp.addSingleParameter("valCost", QueryTypeFilter.EQUAL, 0, QueryTypeCondition.AND);
		qp.addOrderByOption("datOperation", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		StocksOperation[] stocksOperationList = DataManager.selectList(StocksOperation[].class, qp);

		request.setAttribute("stocksOperationList", stocksOperationList);
	}

	private void applyCostsToStocksOperations() {
		QueryParameter qp = new QueryParameter();
		qp.addSingleParameter("datOperation", QueryTypeFilter.EQUAL, GeneralFunctions.stringDatetoSql(request.getParameter("datOperation")),
				QueryTypeCondition.AND);
		qp.addSingleParameter("valCost", QueryTypeFilter.EQUAL, 0, QueryTypeCondition.AND);
		StocksOperation[] stocksOperationList = DataManager.selectList(StocksOperation[].class, qp);
		
		applyGeneralCost(stocksOperationList);
		applyIRRFCost(stocksOperationList);
	}

	private void applyGeneralCost(StocksOperation[] stocksOperationList) {
		double costValue = 0.0;
		try {
			costValue = Double.parseDouble(request.getParameter("taxa1")) +
						Double.parseDouble(request.getParameter("taxa2")) +
						Double.parseDouble(request.getParameter("taxa3"));
		} catch (Exception e) {
			return;
		}
		if (costValue == 0) {
			return;
		}		
		applyCalculationCost(stocksOperationList, costValue);
	}

	private void applyIRRFCost(StocksOperation[] stocksOperationList) {
		double costValue = 0.0;
		try {
			costValue = Double.parseDouble(request.getParameter("taxaIRRF"));
		} catch (Exception e) {
			return;
		}
		if (costValue == 0) {
			return;
		}
		int i = 0;
		for (StocksOperation stock : stocksOperationList) {
			if (stock.getTypeOperation().equals("Venda")) {
				i++;
			}
		}
		StocksOperation[] salesStocksOperationList = new StocksOperation[i];
		i = 0;
		for (StocksOperation stock : stocksOperationList) {
			if (stock.getTypeOperation().equals("Venda")) {
				salesStocksOperationList[i++] = stock;
			}
		}
		applyCalculationCost(salesStocksOperationList, costValue);
	}

	private void applyCalculationCost(StocksOperation[] stocksOperationList, double costValue) {
		double totalStocksValue = getTotalStocksValue(stocksOperationList);
		if (totalStocksValue == 0) {
			return;
		}

		double calculatedTotalCost = 0.0;
		double valCost = 0.0;
		double topVal = 0.0;
		int pos = 0;
		int i = 0;
		
		for (StocksOperation stock : stocksOperationList) {
			valCost = GeneralFunctions.truncDouble(((stock.getQuantity() * stock.getValStock()) / totalStocksValue) * costValue, 2);
			stock.setValCost(stock.getValCost() + valCost);
			DataManager.updateId(StocksOperation.class, stock);

			calculatedTotalCost += valCost;
			if (topVal < (stock.getQuantity() * stock.getValStock())) {
				topVal = stock.getQuantity() * stock.getValStock();
				pos = i;
			}
			i++;
		}
		
		if (calculatedTotalCost != costValue) {
			StocksOperation stock = stocksOperationList[pos];
			stock.setValCost(stock.getValCost() + costValue - calculatedTotalCost);
			DataManager.updateId(StocksOperation.class, stock);
		}		
	}

	private void applyTaxToStocksSell() {
		String period = request.getParameter("period");
		period = period.substring(0, 4) + "-" + period.substring(4, 6);
		String sqlPeriod = period + "%";
		
		QueryParameter qp = new QueryParameter();
		qp.addSingleParameter("datOperation", QueryTypeFilter.CONTAINS, sqlPeriod, QueryTypeCondition.AND);
		qp.addSingleParameter("typeOperation", QueryTypeFilter.EQUAL, generalInfo.STOCK_SELL, QueryTypeCondition.AND);
		qp.addSingleParameter("valResultSell", QueryTypeFilter.GREATER, "0", QueryTypeCondition.AND);
		qp.addSingleParameter("valResultSell", QueryTypeFilter.LESS, "900", QueryTypeCondition.AND);
		StocksOperation[] stocksOperationsList = DataManager.selectList(StocksOperation[].class, qp);

		StocksTax stocksTax = new StocksTax();
		int monthPeriod = Integer.parseInt(period.split("-")[1]);
		int yearPeriod = Integer.parseInt(period.split("-")[0]);
		qp.clearQuery();
		qp.addSingleParameter("taxType", QueryTypeFilter.EQUAL, generalInfo.TAX_IR, QueryTypeCondition.AND);
		qp.addSingleParameter("month", QueryTypeFilter.EQUAL, monthPeriod, QueryTypeCondition.AND);
		qp.addSingleParameter("year", QueryTypeFilter.EQUAL, yearPeriod, QueryTypeCondition.AND);
		StocksTax[] stocksTaxList = DataManager.selectList(StocksTax[].class, qp);
		if (stocksTaxList != null && stocksTaxList.length > 0) {
			stocksTax = stocksTaxList[0];
		} else {
			stocksTax = new StocksTax();
			stocksTax.setTaxType(generalInfo.TAX_IR);
			stocksTax.setMonth(monthPeriod);
			stocksTax.setYear(yearPeriod);
		}

		System.out.println("\nVENDAS COM LUCRO");
		Double totalSell = 0.0;
		Double totalResultSell = 0.0;
		Double totalDeduction = 0.0;
		int cont = 0;
		for (StocksOperation stocksOperation : stocksOperationsList) {
			totalSell += (stocksOperation.getQuantity() * stocksOperation.getValStock()) - stocksOperation.getValCost();
			totalResultSell += stocksOperation.getValResultSell();

			stocksOperation.setOperationGainIRCalculated("S");

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
			cont += 1;
			if (cont == 3) {
				break;
			}
		}
		
		System.out.print("\n\nTotal Lucro sem dedução: ");
		System.out.println(totalResultSell);
		System.out.print("Valor Imposto: ");
		System.out.println(GeneralFunctions.round(totalResultSell * generalInfo.TAX_IR_PERC, 2));
		System.out.print("\n");
		
//		Pegar vendas com prejuízo
		System.out.println("\nVENDAS COM PREJUIZO");
		qp.clearQuery();
		qp.addSingleParameter("typeOperation", QueryTypeFilter.EQUAL, generalInfo.STOCK_SELL, QueryTypeCondition.AND);
		qp.addFieldParameter("valIRLossConsumed", QueryTypeFilter.GREATER, "valResultSell", QueryTypeCondition.AND);
		
		qp.addSingleParameter("valResultSell", QueryTypeFilter.LESS, "0", QueryTypeCondition.AND);
		StocksOperation[] stocksOperationLossList = DataManager.selectList(StocksOperation[].class, qp);
		for (StocksOperation stocksOperationLoss : stocksOperationLossList) {
			
			if (totalResultSell > Math.abs(stocksOperationLoss.getValResultSell())) {
				stocksOperationLoss.setValIRLossConsumed(stocksOperationLoss.getValResultSell());
				totalDeduction += stocksOperationLoss.getValResultSell();
				totalResultSell += stocksOperationLoss.getValResultSell();
			} else if (totalResultSell > 0) {
//				Tratar parcialidade
				totalResultSell *= -1;
				stocksOperationLoss.setValIRLossConsumed(totalResultSell);
				totalDeduction += totalResultSell;
//				break;
				totalResultSell = 0.0;
			}

			stocksOperationLoss.setMonthIRLossConsumed(period);

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
			
			if (totalResultSell == 0) {
				break;
			}
		}

		stocksTax.setValTotalPayment(GeneralFunctions.round(totalResultSell * generalInfo.TAX_IR_PERC, 2));

		System.out.print("\nPeríodo: ");
		System.out.println(sqlPeriod);
		System.out.print("Total das Vendas: ");
		System.out.println(totalSell);
		System.out.print("Total das Deduções: ");
		System.out.println(totalDeduction);
		System.out.print("Total Lucro: ");
		System.out.println(totalResultSell);
		System.out.print("Valor Imposto: ");
		System.out.println(stocksTax.getValTotalPayment());
	}

	private double getTotalStocksValue(StocksOperation[] stocksOperationList) {
		double total = 0.0;
		for (StocksOperation stock : stocksOperationList) {
			total += stock.getQuantity() * stock.getValStock();
		}
		return total;
	}
}
package com.cp.controller.stocks;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.ServletException;

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
import com.cp.model.view.StocksReportRentability;
import com.cp.rest.WebService;
import com.cp.rest.WebService.Dados;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;


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
//			try {
//				exportCSV();
//			} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
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
		StocksOperation.setDatSettlement(request.getParameter("datSettlement"));
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

		String filterStockItem = request.getParameter("filterStockItem");
		qp.addSingleNotEmptyParameter("codStock", QueryTypeFilter.EQUAL, filterStockItem, QueryTypeCondition.AND);
		qp.addOrderByOption("codStock", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		qp.addOrderByOption("datOperation", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		StocksOperation[] stocksOperationList = DataManager.selectList(StocksOperation[].class, qp);
		if (stocksOperationList != null && stocksOperationList.length > 0) {
			request.setAttribute("stocksOperationList", stocksOperationList);
	
//			Set<String> stocksList = getListOfStocksFromStocksOperation(stocksOperationList, null, true);
//			request.setAttribute("stockList", stocksList);
		}
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

		showAllStocks = (filterStockItem == null || ("".equals(filterStockItem))) &&
				(filterCodPortfolio == null || ("".equals(filterCodPortfolio))) &&
				(filterYearOperation == null || ("".equals(filterYearOperation)));

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
		if (stocksOperationList == null ||
			(stocksOperationList != null && stocksOperationList.length == 0)) {
			return;
		}

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

			addActualPosition(listActualPosition, sRPO, acumQuantity, lastMedPrice, getStockPrice(stocksPrices, stock));

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

		rentabilityReport();
	}

	private double getStockPrice(Stocks[] stocksPrices, String stock) {
		if (stocksPrices != null && stocksPrices.length > 0) {
			for (Stocks item : stocksPrices) {
				if (item.getCodStock().equals(stock)) {
					return item.getActualPrice();
				}
			}
		}
		return 0.0;
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
			newStock = null;
			isNewStock = true;
			if (currentStocks != null && currentStocks.length > 0) {
				for (Stocks stock : currentStocks) {
					if (stock.getCodStock().equals(codStock)) {
						newStock = stock;
						isNewStock = false;
						break;
					}
				}
			}
			if (newStock == null) {
				newStock = new Stocks();
				newStock.setCodStock(codStock);
				newStock.setActualPrice(0.0);
			}
			newStock.setName(stockWSData.getResults().getStock().getName());
			newStock.setCompanyName(stockWSData.getResults().getStock().getCompany_name());
			if (actualPrice > 0) {
				newStock.setActualPrice(actualPrice);
			}
			newStock.setUpdateAt(stockWSData.getResults().getStock().getUpdated_at());

			stockPriceList.add(newStock);

			if (isNewStock) {
				List<Stocks> insertStockList = new ArrayList<Stocks>();
				insertStockList.add(newStock);
				DataManager.insert(Stocks.class, insertStockList);
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

			period = GeneralFunctions.getYearMonthOfDate(stocksOperation.getDatSettlement());

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
			sale.addStockSale(stocksOperation.getCodStock(), stocksOperation.getDatOperation(), stocksOperation.getDatSettlement(), totalAmount, stocksOperation.getValResultSell());

			if (sale.isExceeded()) {
				sale.setShowTaxButton(getTaxStatusByPeriod(period));
			}
		}

		return monthSales;
	}

	private static boolean getTaxStatusByPeriod(String period) {
		QueryParameter qp = new QueryParameter();
		String year = period.substring(0, 4);
		String month = period.substring(5, 6);
		qp.addSingleParameter("year", QueryTypeFilter.EQUAL, year, QueryTypeCondition.AND);
		qp.addSingleParameter("month", QueryTypeFilter.EQUAL, month, QueryTypeCondition.AND);
		StocksTax[] stocksTax = DataManager.selectList(StocksTax[].class, qp);
		if (stocksTax != null) {
			return false;
		}
		return true;
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

		String datSettlement = request.getParameter("datSettlement");
		double calculatedTotalCost = 0.0;
		double valCost = 0.0;
		double topVal = 0.0;
		int pos = 0;
		int i = 0;
		
		for (StocksOperation stock : stocksOperationList) {
			valCost = GeneralFunctions.truncDouble(((stock.getQuantity() * stock.getValStock()) / totalStocksValue) * costValue, 2);
			stock.setValCost(stock.getValCost() + valCost);
			if (datSettlement != null) {
				stock.setDatSettlement(datSettlement);
			}
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

	private double getTotalStocksValue(StocksOperation[] stocksOperationList) {
		double total = 0.0;
		for (StocksOperation stock : stocksOperationList) {
			total += stock.getQuantity() * stock.getValStock();
		}
		return total;
	}

	private void rentabilityReport() {
		QueryParameter qp = new QueryParameter();

		String filterCodPortfolio = request.getParameter("filterCodPortfolio");
		qp.addSingleNotEmptyParameter("codPortfolio", QueryTypeFilter.EQUAL, filterCodPortfolio, QueryTypeCondition.AND);
		Stocks[] stocksList = DataManager.selectList(Stocks[].class, qp);
		
		qp.clearQuery();
		qp.addOrderByOption("datOperation", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		StocksOperation[] stocksOperationArr = DataManager.selectList(StocksOperation[].class, qp);
		
		List<StocksOperation> stocksOperationList = new ArrayList<StocksOperation>();
		boolean found;
		for (StocksOperation item : stocksOperationArr) {
			found = false;
			if (stocksList != null && stocksList.length > 0) {
				for (Stocks stock: stocksList) {
					if (item.getCodStock().equals(stock.getCodStock())) {
						found = true;
						break;
					}
				}
			}
			if (found) {
				stocksOperationList.add(item);
			}
		}
		
		List<StocksReportRentability> rentabilityList = new ArrayList<StocksReportRentability>();
		StocksReportRentability rentability = null; 
		String period;
		String oldPeriod = "INICIO";
		double maxCashInvestedPeriod = 0.0;
		double accumCashInvestedPeriod = 0.0;
		double accumRentability = 0.0;
		double totalStockOperation = 0.0;
		for (StocksOperation stockOperation : stocksOperationList) {
			period = GeneralFunctions.getYearMonthOfDate(stockOperation.getDatOperation());

			if (!period.equals(oldPeriod)) {

				if (!"INICIO".equals(oldPeriod)) {
					rentabilityList.add(rentability);
				}

				rentability = new StocksReportRentability();
				rentability.setPeriod(period);
				oldPeriod = period;
			}

			rentability.sumPeriodResult(stockOperation.getValResultSell());
			accumRentability += stockOperation.getValResultSell();

			totalStockOperation = stockOperation.getValStock() * stockOperation.getQuantity();
			if (stockOperation.getTypeOperation().equals(generalInfo.STOCK_SELL)) {
				totalStockOperation *= -1;
			}
			accumCashInvestedPeriod += totalStockOperation;

			if (accumCashInvestedPeriod > maxCashInvestedPeriod) {
				maxCashInvestedPeriod = accumCashInvestedPeriod;
			}

			rentability.setMaxCashInvested(maxCashInvestedPeriod);
			rentability.setPeriodRentability(GeneralFunctions.round((rentability.getPeriodResult() / maxCashInvestedPeriod) * 100, 2));
			rentability.setAccumRentability(GeneralFunctions.round((accumRentability / maxCashInvestedPeriod) * 100, 2));
		}
		if (rentability != null) {
			rentabilityList.add(rentability);
		}

		request.setAttribute("rentabilityList", rentabilityList);
	}

	private void exportCSV() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		
		QueryParameter qp = new QueryParameter();
		qp.addOrderByOption("datOperation", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		
		StocksOperation[] stocksOperationArr = DataManager.selectList(StocksOperation[].class, qp);

        Writer writer = Files.newBufferedWriter(Paths.get("/Users/paulobarth/Downloads/acoes.csv"));
        StatefulBeanToCsv<StocksOperation> beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
        
        List<StocksOperation> stocksOperationList = new ArrayList<StocksOperation>();
        for (StocksOperation item : stocksOperationArr) {
        	stocksOperationList.add(item);
        }

        beanToCsv.write(stocksOperationList);

        writer.flush();
        writer.close();
		
	}
}
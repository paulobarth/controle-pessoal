package com.cp.controller.stocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.controller.BaseControllerImpl;
import com.cp.fwk.data.DataManager;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.Stocks;
import com.cp.model.StocksGain;
import com.cp.model.view.StocksReportOperation;

public class StocksController extends BaseControllerImpl {

	@Override
	public void executeCallBack() throws ServletException, IOException {

		if (option.equals("list") || option.equals("filter") || option.equals("update")) {
			request.getRequestDispatcher("/WEB-INF/views/stocks.jsp").forward(request, response);
		} else {
			response.sendRedirect("/controle-pessoal/stocks.list");
		}
	}

	@Override
	protected void list() {
		QueryParameter qp = new QueryParameter();
		qp.addOrderByOption("codStock", QueryTypeFilter.ORDERBY, QueryTypeCondition.ASC);
		Stocks[] stocksList = DataManager.selectList(Stocks[].class, qp);

		request.setAttribute("stocksList", stocksList);
	}
	
	@Override
	protected void save() {
		Stocks stocks = new Stocks();
		stocks.setCodStock(request.getParameter("codStock"));
		stocks.setName(request.getParameter("name"));
		stocks.setCompanyName(request.getParameter("companyName"));
		stocks.setCodPortfolio(request.getParameter("codPortfolio"));
		stocks.setActualPrice(Double.parseDouble(request.getParameter("actualPrice")));

		String idParam = request.getParameter("id");

		if (idParam.isEmpty()) {
			List<Stocks> lStocks = new ArrayList<Stocks>();
			lStocks.add(stocks);
			DataManager.insert(Stocks.class, lStocks);
		} else {
			stocks.setId(Integer.parseInt(request.getParameter("id")));
			DataManager.updateId(Stocks.class, stocks);
		}
	}
	
	@Override
	protected void update() {
		Stocks stocks = DataManager.selectId(Stocks.class,
				Integer.parseInt(request.getParameter("id")));

		request.setAttribute("stocks", stocks);

		List<Stocks> stocksList = new ArrayList<Stocks>();
		stocksList.add(stocks);

		request.setAttribute("stocksList", stocksList);
	}
	
	@Override
	protected void delete() {
		DataManager.deleteId(Stocks.class, Integer.parseInt(request.getParameter("id")));
	}
}

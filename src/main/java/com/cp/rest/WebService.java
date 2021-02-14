package com.cp.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;

public class WebService {

	public static Dados getStockData(String stock) {
		Dados dados = null;
		Gson gson = new Gson();
		try {

			URL url = new URL("https://api.hgbrasil.com/finance/stock_price?key=d62c7d40&symbol=" + stock);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				System.out.print("deu erro... HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output, json = "";
			while ((output = br.readLine()) != null) {
				json += output;
			}
			
			json = json.replace(stock, "stock");
			System.out.println(json);
			conn.disconnect();
			
			dados = gson.fromJson(json, Dados.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dados;
	}

	public class Dados {

		private String by;
		private String valid_key;
		private Results results;
		private String execution_time;
		private String from_cache;
		public String getBy() {
			return by;
		}
		public void setBy(String by) {
			this.by = by;
		}
		public String getValid_key() {
			return valid_key;
		}
		public void setValid_key(String valid_key) {
			this.valid_key = valid_key;
		}
		public Results getResults() {
			return results;
		}
		public void setResults(Results results) {
			this.results = results;
		}
		public String getExecution_time() {
			return execution_time;
		}
		public void setExecution_time(String execution_time) {
			this.execution_time = execution_time;
		}
		public String getFrom_cache() {
			return from_cache;
		}
		public void setFrom_cache(String from_cache) {
			this.from_cache = from_cache;
		}
		

	}
	
	public class Results {
		private Stock stock;

		public Stock getStock() {
			return stock;
		}

		public void setStock(Stock stock) {
			this.stock = stock;
		}
	}

	public class Stock {

		private String symbol;
		private String name;
		private String company_name;
		private String document;
		private String description;
		private String website;
		private String region;
		private String currency;
		private MarketTime market_time;
		private String market_cap;
		private String price;
		private String change_percent;
		private String updated_at;
		public String getSymbol() {
			return symbol;
		}
		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCompany_name() {
			return company_name;
		}
		public void setCompany_name(String company_name) {
			this.company_name = company_name;
		}
		public String getDocument() {
			return document;
		}
		public void setDocument(String document) {
			this.document = document;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getWebsite() {
			return website;
		}
		public void setWebsite(String website) {
			this.website = website;
		}
		public String getRegion() {
			return region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		public String getCurrency() {
			return currency;
		}
		public void setCurrency(String currency) {
			this.currency = currency;
		}
		public MarketTime getMarket_time() {
			return market_time;
		}
		public void setMarket_time(MarketTime market_time) {
			this.market_time = market_time;
		}
		public String getMarket_cap() {
			return market_cap;
		}
		public void setMarket_cap(String market_cap) {
			this.market_cap = market_cap;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getChange_percent() {
			return change_percent;
		}
		public void setChange_percent(String change_percent) {
			this.change_percent = change_percent;
		}
		public String getUpdated_at() {
			return updated_at;
		}
		public void setUpdated_at(String updated_at) {
			this.updated_at = updated_at;
		}
		
		

	}

	public class MarketTime {
		private String open;
		private String close;
		private String timezone;
		public String getOpen() {
			return open;
		}
		public void setOpen(String open) {
			this.open = open;
		}
		public String getClose() {
			return close;
		}
		public void setClose(String close) {
			this.close = close;
		}
		public String getTimezone() {
			return timezone;
		}
		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}
		
		

	}
}
//	
//	{
//	    "by": "symbol",
//	    "valid_key": true,
//	    "results": {
//	        "PETR4": {
//	            "symbol": "PETR4",
//	            "name": "Petrobras",
//	            "company_name": "Petroleo Brasileiro S.A. Petrobras",
//	            "document": "33.000.167/0001-01",
//	            "description": "Pesquisa. Lavra. Refinação. Processamento. Comércio E Transporte de Petróleo. de Seus Derivados. de Gás Natural E de Outros Hidrocarbonetos Fluidos. Além Das Atividades Vinculadas à Energia.",
//	            "website": "http://www.petrobras.com.br/",
//	            "region": "Brazil/Sao Paulo",
//	            "currency": "BRL",
//	            "market_time": {
//	                "open": "10:00",
//	                "close": "17:30",
//	                "timezone": -3
//	            },
//	            "market_cap": 68129.6,
//	            "price": 28.12,
//	            "change_percent": 1.15,
//	            "updated_at": "2021-02-11 15:36:04"
//	        }
//	    },
//	    "execution_time": 0.03,
//	    "from_cache": false
//	}
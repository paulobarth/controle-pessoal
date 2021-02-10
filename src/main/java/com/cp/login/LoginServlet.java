package com.cp.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/login.do")
public class LoginServlet extends HttpServlet {
	
	private LoginService loginService = new LoginService();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Get Parameter from the request ?name=Paulo
		//String name = request.getParameter("name");
		
		//Set attribute to the next page
		//request.setAttribute("name", name);
		
		if (request.getSession().getAttribute("name") != null) {
			
			response.sendRedirect("/controle-pessoal/budget.list");
		} else {
			request.getSession().setAttribute("acao", "");
			request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String name 	= request.getParameter("name");
		String password = request.getParameter("password");
		
		if (loginService.isUserValid(name, password)) {
			request.getSession().setAttribute("name", name);
			response.sendRedirect("/controle-pessoal/budget.list");
			loginService.prepareDatabase();
		} else {
			request.setAttribute("error", "Wrong Credentials");
			request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
		}
	}

}

package com.cp.login;

import com.cp.dao.dataManager;

public class LoginService {

	public boolean isUserValid (String user, String password) {
		
		if(user.contentEquals("barth") && password.equals("123")) {
			return true;
		}
		
		return false;
	}

	public void prepareDatabase() {
		dataManager.prepareDatabase();
	}
	
}

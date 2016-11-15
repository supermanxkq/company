package com.ccservice.elong.inter;

import java.rmi.RemoteException;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;

public class ELongLogin {
	public static void main(String[] args) throws RemoteException {
		getLoginToken();
		System.out.println("LoginToken=" + getLoginToken());
	}

	public static String getLoginToken() throws RemoteException {
		String loginToken = "";
		NorthBoundAPIServiceStub stub = new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.Login login = new NorthBoundAPIServiceStub.Login();
		NorthBoundAPIServiceStub.LoginRequest loginRequest = new NorthBoundAPIServiceStub.LoginRequest();
		NorthBoundAPIServiceStub.LoginResponseE loginResponse = new NorthBoundAPIServiceStub.LoginResponseE();
   
		
//		//测试用户名
//		String UserName = "agent10";
//		//测试 密码
//		String Password = "agent10";
		
		//正式用户名密码AP0024620
		//苍南用户名AP0029035
		String UserName = "AP0024620";
		String Password = "AP0024620";
        try {
			UserName=PropertyUtil.getValue("name");
			Password=PropertyUtil.getValue("pwd");
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		loginRequest.setUserName(UserName);
		loginRequest.setPassword(Password);

		login.setLoginRequest(loginRequest);

		loginResponse = stub.login(login);

		loginToken = loginResponse.getLoginResult().getLoginToken().toString();

		// System.out.println(loginResponse.getLoginResult().getLoginToken());
		// System.out.println(loginResponse.getLoginResult().getResponseHead().getResultCode());
		// System.out.println(loginResponse.getLoginResult().getResponseHead().getResultMessage());
		// System.out.println(loginResponse.getLoginResult().getLoginTokenExpiredTime());
		return loginToken;
	}
}

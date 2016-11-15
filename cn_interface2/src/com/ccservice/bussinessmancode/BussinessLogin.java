package com.ccservice.bussinessmancode;

import java.rmi.RemoteException;

import net.sf.json.JSONObject;

import org.apache.axis2.AxisFault;

import com.ccservice.bussinessman.DDS2Stub;
import com.ccservice.bussinessman.DDS2Stub.Login;
import com.ccservice.bussinessman.DDS2Stub.LoginResponse;

/**
 * 
 * @author 王战朝 生意人登录接口 获取LoginToken
 */
public class BussinessLogin {
	public static String getLoginToken() {
		try {
			DDS2Stub stub = new DDS2Stub();
			LoginResponse response = new LoginResponse();
			Login login = new Login();
			login.setUserName("bj325");
			login.setPassWord("hthy1688");
			login.setMii01Id(959);
			response = stub.login(login);
			String resultjson = response.getLoginResult();
			JSONObject json = JSONObject.fromObject(resultjson);
			String LoginToken = json.getString("LoginToken");
			return LoginToken;
		} catch (AxisFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		System.out.println(getLoginToken());
	}
}

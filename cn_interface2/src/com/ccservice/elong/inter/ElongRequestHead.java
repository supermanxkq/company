package com.ccservice.elong.inter;

import java.rmi.RemoteException;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;

public class ElongRequestHead {
	public static NorthBoundAPIServiceStub.RequestHead getRequestHead(String GUID) throws RemoteException{
		NorthBoundAPIServiceStub.RequestHead requestHead=new NorthBoundAPIServiceStub.RequestHead();
		//唯一表示ID
		//GUID="6F9619FF-8B86-D011-B42D-00C04FC964FF";
		//接口语言
		String Language="CN";
		//版本号
		String Version="1";
		//测试开关
		int TestMode=0;
		//访问凭证
		String LoginToken=ELongLogin.getLoginToken();
		
		//唯一标识ID
		requestHead.setGUID(GUID);
		//接口语言
		requestHead.setLanguage(Language);
		//访问凭证
		requestHead.setLoginToken(LoginToken);
		requestHead.setTestMode(TestMode);
		requestHead.setVersion(Version);
		return requestHead;
	}
}

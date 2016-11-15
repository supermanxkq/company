package com.ccservice.bussinessmancode;

import java.rmi.RemoteException;

import com.ccservice.bussinessman.DDS2Stub;
import com.ccservice.bussinessman.DDS2Stub.GetHotelInfo;
import com.ccservice.bussinessman.DDS2Stub.GetHotelInfoResponse;

/**
 * 
 * @author wzc 单记录酒店信息查询接口
 * 
 */
public class BussGetHotelInfo {
	public static void main(String[] args) throws RemoteException {
		GetHotelInfo(12403);
	}

	/**
	 * 
	 * @param LoginToken
	 *            登录凭证
	 * @param FHotelId
	 *            酒店Id
	 * @throws RemoteException
	 */
	public static void GetHotelInfo(int FHotelId) throws RemoteException {
		DDS2Stub stub = new DDS2Stub();
		GetHotelInfoResponse response = new GetHotelInfoResponse();
		GetHotelInfo getHotelInfo = new GetHotelInfo();
		getHotelInfo.setLoginToken(BussinessLogin.getLoginToken());
		getHotelInfo.setFHotelId(FHotelId);
		response = stub.getHotelInfo(getHotelInfo);
		
		System.out.println();
	}
}

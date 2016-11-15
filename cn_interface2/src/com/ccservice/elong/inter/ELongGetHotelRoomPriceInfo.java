package com.ccservice.elong.inter;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;

/**
 * 获取价格数据接口
 * 
 * @author 师卫林
 * 
 */
public class ELongGetHotelRoomPriceInfo {
	public static void main(String[] args) throws RemoteException {
		// 入住时间
		String startdate = "2012-3-27";
		// 酒店Id
		String HotelId = "40101025";
		// 离店时间
		String enddate = "2012-3-28";
		getPriceInfo(startdate, enddate, HotelId);
	}

	public static NorthBoundAPIServiceStub.GetHotelRoomPriceInfoResponse getPriceInfo(String startdate, String enddate, String HotelId) throws RemoteException {
		NorthBoundAPIServiceStub stub = new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.GetHotelRoomPriceRequest request = new NorthBoundAPIServiceStub.GetHotelRoomPriceRequest();
		NorthBoundAPIServiceStub.GetHotelRoomPriceInfo getHotelRoomPriceInfo = new NorthBoundAPIServiceStub.GetHotelRoomPriceInfo();
		NorthBoundAPIServiceStub.GetHotelRoomPriceInfoResponse response = new NorthBoundAPIServiceStub.GetHotelRoomPriceInfoResponse();
		// 入住时间
		Calendar StartDate = DateSwitch.SwitchCalendar2(startdate);
		// 离店时间
		Calendar EndDate = DateSwitch.SwitchCalendar2(enddate);

		request.setEndDate(EndDate);
		request.setHotelId(HotelId);
		request.setRequestHead(ElongRequestHead.getRequestHead(""));
		request.setStartDate(StartDate);

		getHotelRoomPriceInfo.setRequest(request);

		response = stub.getHotelRoomPriceInfo(getHotelRoomPriceInfo);
		return response;
	}
}

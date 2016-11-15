package com.ccservice.elong.inter;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;

/**
 * 获取房态接口
 * 
 * @author 师卫林
 * 
 */
public class ELongGetHotelInventory {
	public static void main(String[] args) throws ParseException,
			RemoteException {

		// 入住时间
		String startDate = "2012-3-23";

		// 离开时间
		String endDate = "2012-3-25";

		// 酒店ID
		String HotelId = "00101008";
		gethotelInventory(startDate, endDate, HotelId);
		// System.out.println("HOTELID:"+gethotelInventory(startDate, endDate,
		// HotelId).length);
	}

	public static NorthBoundAPIServiceStub.GetHotelInventoryResponseE gethotelInventory(String startDate, String endDate,
			String HotelId) throws RemoteException {
		NorthBoundAPIServiceStub stub = new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.GetHotelInventory inventory = new NorthBoundAPIServiceStub.GetHotelInventory();
		NorthBoundAPIServiceStub.GetHotelInventoryRequest request = new NorthBoundAPIServiceStub.GetHotelInventoryRequest();
		NorthBoundAPIServiceStub.GetHotelInventoryResponseE response = new NorthBoundAPIServiceStub.GetHotelInventoryResponseE();
		// 入住时间
		Calendar StartDate = DateSwitch.SwitchCalendar2(startDate);
		// 离开时间
		Calendar EndDate = DateSwitch.SwitchCalendar2(endDate);

		request.setEndDate(EndDate);
		request.setHotelId(HotelId);
		request.setRequestHead(ElongRequestHead.getRequestHead(""));
		request.setStartDate(StartDate);

		inventory.setRequest(request);
		response = stub.getHotelInventory(inventory);
		return response;
	}
}

package com.ccservice.elong.inter;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Calendar;
import com.ccservice.elong.base.NorthBoundAPIServiceStub;

/**
 * 酒店价格查询接口
 * 
 * @author 师卫林
 * 
 */
public class ELongGetHotelList {
	public static void main(String[] args) throws RemoteException, SQLException {
		String HotelName = "湖湘驿国际青年旅舍（长沙岳麓山店）";
		String HotelId = "01901220,40101578";
		String checkInDate="2013-3-29";
		String checkOutDate="2013-3-30";
		String CityId = "1901";
		// getHotelPrice(checkInDate, checkOutDate,HotelName, HotelId,CityId);
		getHotelPrice(CityId,HotelName, HotelId,checkInDate,checkOutDate );
	}

	public static NorthBoundAPIServiceStub.GetHotelListResponseE getHotelPrice(String cityId, String hotelName, String hotelId, String checkInDate,
			String checkOutDate) throws RemoteException, SQLException {
		NorthBoundAPIServiceStub stub = new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.GetHotelConditionForGetHotelList conditionForGetHotelList = new NorthBoundAPIServiceStub.GetHotelConditionForGetHotelList();
		NorthBoundAPIServiceStub.GetHotelListRequest request = new NorthBoundAPIServiceStub.GetHotelListRequest();
		NorthBoundAPIServiceStub.GetHotelListResponseE response = new NorthBoundAPIServiceStub.GetHotelListResponseE();
		NorthBoundAPIServiceStub.GetHotelList getList = new NorthBoundAPIServiceStub.GetHotelList();
		// 入住时间
		Calendar CheckInDate = DateSwitch.SwitchCalendar2(checkInDate);
		// 离开时间
		Calendar CheckOutDate = DateSwitch.SwitchCalendar2(checkOutDate);

		conditionForGetHotelList.setCheckInDate(CheckInDate);
		conditionForGetHotelList.setCheckOutDate(CheckOutDate);
		conditionForGetHotelList.setStartLongitude(BigDecimal.valueOf(0));
		conditionForGetHotelList.setStartLatitude(BigDecimal.valueOf(0));
		conditionForGetHotelList.setEndLatitude(BigDecimal.valueOf(0));
		conditionForGetHotelList.setEndLongitude(BigDecimal.valueOf(0));
		conditionForGetHotelList.setOpeningDate(DateSwitch.SwitchCalendar("0001-01-01 00:00:00"));
		conditionForGetHotelList.setDecorationDate(DateSwitch.SwitchCalendar("0001-01-01 00:00:00"));
		conditionForGetHotelList.setCityId(cityId);
		conditionForGetHotelList.setHotelId(hotelId);
		conditionForGetHotelList.setHotelName(hotelName);

		request.setRequestHead(ElongRequestHead.getRequestHead(""));
		request.setGetHotelCondition(conditionForGetHotelList);

		getList.setGetHotelListRequest(request);

		response = stub.getHotelList(getList);
		return response;
	}
	
}

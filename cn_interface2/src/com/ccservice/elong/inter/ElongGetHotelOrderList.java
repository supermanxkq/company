package com.ccservice.elong.inter;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;

/**
 * 订单查询接口
 * 
 * @author 师卫林
 * 
 */
public class ElongGetHotelOrderList {
	public static void main(String[] args) throws RemoteException {
		// 酒店名称
		String HotelName = "北京聚丰速8酒店";
		// 酒店Id
		String HotelId = "00101480";
		// 房型Id
		String RoomTypeId = "0003";
		// RatePlanId
		int RatePlanId = 117553;
		// 客人姓名
		String CustomerName = "石伟林";
		// 入住日期起始时间
		String earlyArriveDate = "2012-3-10";
		// 入住日期截止时间
		String lateArriveDate = "2012-3-15";
		// 离店日期起始时间
		String earlyLeaveDate = "2012-3-14";
		// 离店日期截止时间
		String lateLeaveDate = "2012-3-15";
		// 预订日期起始时间
		String startCreateTime = "2012-3-9";
		// 预订日期截止时间
		String endCreateTime = "2012-3-11";
		// 最后更新时间
		String lastUpdateTime = "2012-2-27";
		getOrderList(HotelName, HotelId, RoomTypeId, RatePlanId, CustomerName, earlyArriveDate, lateArriveDate, earlyLeaveDate, lateLeaveDate, startCreateTime, endCreateTime, lastUpdateTime);
	}

	public static void getOrderList(String HotelName, String HotelId, String RoomTypeId, int RatePlanId, String CustomerName, String earlyArriveDate, String lateArriveDate, String earlyLeaveDate,
			String lateLeaveDate, String startCreateTime, String endCreateTime, String lastUpdateTime) throws RemoteException {
		NorthBoundAPIServiceStub stub = new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.GetHotelOrderListRequest listRequest = new NorthBoundAPIServiceStub.GetHotelOrderListRequest();
		NorthBoundAPIServiceStub.GetHotelOrderCondition orderCondition = new NorthBoundAPIServiceStub.GetHotelOrderCondition();
		NorthBoundAPIServiceStub.GetHotelOrderList getHotelOrderList = new NorthBoundAPIServiceStub.GetHotelOrderList();
		NorthBoundAPIServiceStub.GetHotelOrderListResponseE responseE = new NorthBoundAPIServiceStub.GetHotelOrderListResponseE();
		NorthBoundAPIServiceStub.HotelOrderPartialForGetHotelOrderList partialForGetHotelOrderList = new NorthBoundAPIServiceStub.HotelOrderPartialForGetHotelOrderList();
		NorthBoundAPIServiceStub.RoomForGetHotelOrderList roomForGetHotelOrderList = new NorthBoundAPIServiceStub.RoomForGetHotelOrderList();

		Calendar EarlyArriveDate = DateSwitch.SwitchCalendar(earlyArriveDate);
		Calendar LateArriveDate = DateSwitch.SwitchCalendar(lateArriveDate);
		Calendar EarlyLeaveDate = DateSwitch.SwitchCalendar(earlyLeaveDate);
		Calendar LateLeaveDate = DateSwitch.SwitchCalendar(lateLeaveDate);
		Calendar StartCreateTime = DateSwitch.SwitchCalendar(startCreateTime);
		Calendar EndCreateTime = DateSwitch.SwitchCalendar(endCreateTime);
		Calendar LastUpdateTime = DateSwitch.SwitchCalendar(lastUpdateTime);
		orderCondition.setCustomerName(CustomerName);
		orderCondition.setEarlyArriveDate(EarlyArriveDate);
		orderCondition.setEarlyLeaveDate(EarlyLeaveDate);
		orderCondition.setHotelId(HotelId);
		orderCondition.setHotelName(HotelName);
		orderCondition.setLateArriveDate(LateArriveDate);
		orderCondition.setLateLeaveDate(LateLeaveDate);
		orderCondition.setRatePlanID(RatePlanId);
		orderCondition.setRoomTypeID(RoomTypeId);
		orderCondition.setStartCreateTime(StartCreateTime);
		orderCondition.setEndCreateTime(EndCreateTime);
		orderCondition.setLastUpdateTime(LastUpdateTime);

		listRequest.setGetHotelOrderCondition(orderCondition);
		listRequest.setRequestHead(ElongRequestHead.getRequestHead(""));

		getHotelOrderList.setGetHotelOrderListRequest(listRequest);
		responseE = stub.getHotelOrderList(getHotelOrderList);

		System.out.println("结果代码:" + responseE.getGetHotelOrderListResult().getResponseHead().getResultCode());
		System.out.println("结果信息:" + responseE.getGetHotelOrderListResult().getResponseHead().getResultMessage());

		NorthBoundAPIServiceStub.HotelOrderPartialForGetHotelOrderList[] hotelOrderPartialForGetHotelOrderLists = responseE.getGetHotelOrderListResult().getHotelOrderPartials().getHotelOrderPartial();
		if (hotelOrderPartialForGetHotelOrderLists != null && hotelOrderPartialForGetHotelOrderLists.length > 0) {
			for (int i = 0; i < hotelOrderPartialForGetHotelOrderLists.length; i++) {
				partialForGetHotelOrderList = hotelOrderPartialForGetHotelOrderLists[i];
				// 订单ID
				System.out.println(partialForGetHotelOrderList.getHotelOrderId());
				// 订单状态
				System.out.println(partialForGetHotelOrderList.getOrderStatusCode());
				// 货币代码
				System.out.println(partialForGetHotelOrderList.getCurrencyCode());
				// 订单总价
				System.out.println(partialForGetHotelOrderList.getOrderTotalPrice());
				roomForGetHotelOrderList = partialForGetHotelOrderList.getRoomGroupsForGetHotelOrderList().getRoom()[0];
				// 订单确认语言
				System.out.println(roomForGetHotelOrderList.getConfirmLanguageCode());
				// 订单确认方式
				System.out.println(roomForGetHotelOrderList.getConfirmTypeCode());
				// 货币代码
				System.out.println(roomForGetHotelOrderList.getCurrencyCode());
				// 酒店ID
				System.out.println(roomForGetHotelOrderList.getHotelId());
				// 房型ID
				System.out.println(roomForGetHotelOrderList.getRoomTypeId());
				// RatePlanID
				System.out.println(roomForGetHotelOrderList.getRatePlanID());
				// 入住时间
				System.out.println("入住时间:" + DateSwitch.SwitchString(roomForGetHotelOrderList.getCheckInDate()));
				// 离店日期
				System.out.println("离店时间:" + DateSwitch.SwitchString(roomForGetHotelOrderList.getCheckOutDate()));
				// 宾客类型代码
				System.out.println(roomForGetHotelOrderList.getGuestTypeCode());
				// 房间数量
				System.out.println(roomForGetHotelOrderList.getRoomAmount());
				// 入住人数
				System.out.println(roomForGetHotelOrderList.getRoomAmount());
				// 支付方式
				System.out.println(roomForGetHotelOrderList.getPaymentTypeCode());
				// 最早到店时间
				System.out.println("最早到店时间:" + roomForGetHotelOrderList.getArrivalEarlyTime());
				// 最晚到店时间
				System.out.println("最晚到店时间:" + roomForGetHotelOrderList.getArrivalLateTime());
				// Room总价
				System.out.println(roomForGetHotelOrderList.getRoomTotalPrice());
				// // 给酒店备注
				// System.out.println(roomForGetHotelOrderList.getNoteToHotel());
				// // 给代理备注
				// System.out.println(roomForGetHotelOrderList.getNoteToElong());
			}
		}
	}
}

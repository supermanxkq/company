package com.ccservice.elong.inter;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;

/**
 * 判断订单是否需要信用卡担保接口 目前API接口只支持"不担保"和"信用卡担保"
 * 
 * @author 师卫林
 * 
 */
public class ELongHotelProductVouchForAPI {
	public static void main(String[] args) throws RemoteException {
		// 酒店ID
		String HotelId = "00101480";
		// 房型ID
		String RoomTypeId = "0003";
		// RatePlanId
		int RatePlanId =117553;
		// 入住时间
		String checkindate = "2012-3-15";
		// 离店时间
		String checkoutdate = "2012-3-20";
		// 最早到达时间
		String arrivalearlytime = "2012-3-16";
		// 最晚入住时间
		String arrivalatetime = "2012-3-16";
		// 房间数量
		int RoomNum = 1;
		isVouch(HotelId, RoomTypeId, RatePlanId, checkindate, checkoutdate,
				arrivalearlytime, arrivalatetime, RoomNum);
	}

	public static NorthBoundAPIServiceStub.GetHotelProductVouchResponse isVouch(String HotelId, String RoomTypeId,
			int RatePlanId, String checkindate, String checkoutdate,
			String arrivalearlytime, String arrivalatetime, int RoomNum)
			throws RemoteException {
		NorthBoundAPIServiceStub stub = new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.GetHotelProductVouchForAPIRequest request = new NorthBoundAPIServiceStub.GetHotelProductVouchForAPIRequest();
		NorthBoundAPIServiceStub.VouchCondition vouchCondition = new NorthBoundAPIServiceStub.VouchCondition();
		NorthBoundAPIServiceStub.GetHotelProductVouchResponse forAPIResponse = new NorthBoundAPIServiceStub.GetHotelProductVouchResponse();
		NorthBoundAPIServiceStub.GetHotelProductVouch hotelProductVouch = new NorthBoundAPIServiceStub.GetHotelProductVouch();

		Calendar CheckInDate = DateSwitch.SwitchCalendar(checkindate);

		Calendar CheckOutDate = DateSwitch.SwitchCalendar(checkoutdate);

		Calendar ArrivalEarlyTime = DateSwitch
				.SwitchCalendar(arrivalearlytime);

		Calendar ArrivalLateTime = DateSwitch
				.SwitchCalendar(arrivalatetime);

		vouchCondition.setArriveEarlyTime(ArrivalEarlyTime);
		vouchCondition.setArriveLaterTime(ArrivalLateTime);
		vouchCondition.setCheckInDate(CheckInDate);
		vouchCondition.setCheckOutDate(CheckOutDate);
		vouchCondition.setHotelId(HotelId);
		vouchCondition.setRatePlanId(RatePlanId);
		vouchCondition.setRoomNum(RoomNum);
		vouchCondition.setRoomTypeId(RoomTypeId);

		request.setRequestHead(ElongRequestHead.getRequestHead(""));
		request.setVouchCondition(vouchCondition);

		hotelProductVouch.setRequest(request);

		forAPIResponse = stub.getHotelProductVouch(hotelProductVouch);
//		// 结果代码
//		System.out.println("结果代码:"+forAPIResponse.getGetHotelProductVouchResult()
//				.getResponseHead().getResultCode());
//		// 結果信息
//		System.out.println("结果信息:"+forAPIResponse.getGetHotelProductVouchResult()
//				.getResponseHead().getResultMessage());
//
//		NorthBoundAPIServiceStub.VochInfo vochInfo = forAPIResponse
//				.getGetHotelProductVouchResult().getVouchInfo();
//		// 是否需要担保 0－不担保1－担保
//		System.out.println("是否需要担保:"+vochInfo.getIsVouch());
//		String isVouch = vochInfo.getIsVouch();
		// //担保类型 1－首晚担保2－全额担保
		// System.out.println(vochInfo.getVouchMoneyType());
		// //担保中文描述
		// System.out.println(vochInfo.getCNDescription());
		// //担保英文描述
		// System.out.println(vochInfo.getENDescription());
		// //最晚取消变更时间
		// System.out.println("最晚取消时间:"+DateTimeSwitch.SwitchString(vochInfo.getLastCannelTime()));
		return forAPIResponse;
	}
}

package com.ccservice.elong.inter;

import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;

/**
 * 订单取消接口
 * 
 * @author 师卫林
 * 
 */
public class ElongCancelHotelOrderById {
	public static void main(String[] args) throws RemoteException, NoSuchAlgorithmException {
		int HotelOrderId = 100087683;
		cancelOrder(HotelOrderId);
	}

	public static NorthBoundAPIServiceStub.CancelHotelOrderByIdResponseE cancelOrder(int HotelOrderId) throws RemoteException {
		NorthBoundAPIServiceStub stub = new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.CancelHotelOrderById cancelHotelOrderById = new NorthBoundAPIServiceStub.CancelHotelOrderById();
		NorthBoundAPIServiceStub.CancelHotelOrderByIdRequest cancelHotelOrderByIdRequest = new NorthBoundAPIServiceStub.CancelHotelOrderByIdRequest();
		NorthBoundAPIServiceStub.CancelHotelOrderByIdResponseE cancelResult = new NorthBoundAPIServiceStub.CancelHotelOrderByIdResponseE();

		cancelHotelOrderByIdRequest.setHotelOrderId(HotelOrderId);
		// cancelHotelOrderByIdRequest.setCancelCode("21");
		// cancelHotelOrderByIdRequest.setCancelReason("");

		cancelHotelOrderByIdRequest.setRequestHead(ElongRequestHead.getRequestHead(""));
		cancelHotelOrderById.setCancelHotelOrderByIdRequest(cancelHotelOrderByIdRequest);
		cancelResult = stub.cancelHotelOrderById(cancelHotelOrderById);
//		// 结果信息
//		System.out.println("结果信息:" + cancelResult.getCancelHotelOrderByIdResult().getResponseHead().getResultMessage());
//		// 结果编号
//		System.out.println("结果编号:" + cancelResult.getCancelHotelOrderByIdResult().getResponseHead().getResultCode());
//		// 结果生成的时间
//		System.out.println("结果生成的时间:"
//				+ DateTimeSwitch.SwitchString(cancelResult.getCancelHotelOrderByIdResult().getResponseHead().getTimeStamp()));
		return cancelResult;
	}
}

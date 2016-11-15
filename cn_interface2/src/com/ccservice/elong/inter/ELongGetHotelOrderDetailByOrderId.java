package com.ccservice.elong.inter;

import java.rmi.RemoteException;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.GetHotelOrderDetailByIdResponse;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.GetHotelOrderDetailByOrderIdRequest;

/**
 * 根据OrderID获取订单
 * 
 * @author 师卫林
 * 
 */
public class ELongGetHotelOrderDetailByOrderId {
	public static void main(String[] args) throws RemoteException {
		// 订单ID
		int HotelOrderId = 100087683;
		getDetail(HotelOrderId);
	}

	public static GetHotelOrderDetailByIdResponse getDetail(int HotelOrderId) throws RemoteException {
		NorthBoundAPIServiceStub stub = new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.GetHotelOrderDetailById detailById = new NorthBoundAPIServiceStub.GetHotelOrderDetailById();
		GetHotelOrderDetailByIdResponse byOrderIdResponse = new GetHotelOrderDetailByIdResponse();
		GetHotelOrderDetailByOrderIdRequest byOrderIdRequest = new GetHotelOrderDetailByOrderIdRequest();

		byOrderIdRequest.setHotelOrderID(HotelOrderId);
		byOrderIdRequest.setRequestHead(ElongRequestHead.getRequestHead(""));

		detailById.setGetHotelOrderDetailByIdRequest(byOrderIdRequest);

		byOrderIdResponse = stub.getHotelOrderDetailById(detailById);
//		System.out.println("结果代码:"+byOrderIdResponse.getGetHotelOrderDetailByIdResult()
//				.getResponseHead().getResultCode());
//		System.out.println("结果信息:"+byOrderIdResponse.getGetHotelOrderDetailByIdResult()
//				.getResponseHead().getResultMessage());
		return byOrderIdResponse;
		
	}
}

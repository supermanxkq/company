package com.ccservice.elong.inter;

import java.rmi.RemoteException;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;

/**
 * 获取订单及时确认信息接口
 * @author 师卫林
 *
 */
public class ELongInstantCofirm {
	public static void main(String[] args) throws RemoteException {
		//订单ID
		int OrderId=100087683;
		confirm(OrderId);
	}
	public static NorthBoundAPIServiceStub.InstantConfirmResponse confirm(int OrderId) throws RemoteException{
		NorthBoundAPIServiceStub stub =new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.InstantConfirm instantConfirm=new NorthBoundAPIServiceStub.InstantConfirm();
		NorthBoundAPIServiceStub.GetInstantConfirmRequest request=new NorthBoundAPIServiceStub.GetInstantConfirmRequest();
		NorthBoundAPIServiceStub.InstantConfirmResponse confirmResponse=new NorthBoundAPIServiceStub.InstantConfirmResponse();
		
		request.setRequestHead(ElongRequestHead.getRequestHead(""));
		request.setOrderId(OrderId);
		
		instantConfirm.setRequest(request);
		
		confirmResponse=stub.instantConfirm(instantConfirm);
//		//结果代码
//		System.out.println("结果代码:"+confirmResponse.getInstantConfirmResult().getResponseHead().getResultCode());
//		//结果信息
//		System.out.println("结果信息:"+confirmResponse.getInstantConfirmResult().getResponseHead().getResultMessage());
//		NorthBoundAPIServiceStub.GetInstantConfirmInfo confirmInfo=confirmResponse.getInstantConfirmResult().getInstantConfirmInfo();
//		//订单即时确认信息
//		System.out.println("订单即时确认信息:"+confirmInfo.getInstantConfirm());
//		//订单状态 1-即时确认订单   0-非即时确认订单,  2-查询超时  3-输入的订单号无效
//		System.out.println("订单状态:"+confirmInfo.getOrderState());
		return confirmResponse;
		
	}
}

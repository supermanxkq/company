package com.ccservice.elong.inter;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import com.ccservice.elong.base.NorthBoundAPIServiceStub;

/**
 * 根据订单号ID获得多个订单信
 * 
 * @author 师卫林
 * 
 */
public class ELongGetHotelOrderListById {
	public static void main(String[] args) throws RemoteException {
		// 多个订单号ID
		String OrderList = "54352635,51989134,53974369,54094156,53971143";
		getOrderList(OrderList);
	}

	public static Map<String, String> getOrderList(String OrderList)
			throws RemoteException {
		Map<String, String> result = new HashMap<String, String>();
		NorthBoundAPIServiceStub stub = new NorthBoundAPIServiceStub();
		NorthBoundAPIServiceStub.GetHotelOrderListByIdRequest request = new NorthBoundAPIServiceStub.GetHotelOrderListByIdRequest();
		NorthBoundAPIServiceStub.GetHotelOrderListByIdResponseE responseE = new NorthBoundAPIServiceStub.GetHotelOrderListByIdResponseE();
		NorthBoundAPIServiceStub.GetHotelOrderListByIdE getHotelOrderListByIdE = new NorthBoundAPIServiceStub.GetHotelOrderListByIdE();

		request.setOrderIdList(OrderList);
		request.setRequestHead(ElongRequestHead.getRequestHead(""));

		getHotelOrderListByIdE.setGetHotelOrderListByIdRequest(request);
		responseE = stub.getHotelOrderListById(getHotelOrderListByIdE);
		// 结果代码
		String resultcode = responseE.getGetHotelOrderListByIdResult()
				.getResponseHead().getResultCode();
		System.out.println("结果代码:" + resultcode);
		// 结果信息
		String resultmsg = responseE.getGetHotelOrderListByIdResult()
				.getResponseHead().getResultMessage();
		System.out.println("结果信息:" + resultmsg);
		result.put("resultcode", resultcode);
		result.put("msg", resultmsg);
		if (resultcode != null && "0".equals(resultcode)) {
			NorthBoundAPIServiceStub.OrderPartialInfo[] orderPartialInfos = responseE
			.getGetHotelOrderListByIdResult().getHotelOrderList().getHotelOrderList().getOrderList();
			if (orderPartialInfos != null && orderPartialInfos.length > 0) {
				for (int i = 0; i < orderPartialInfos.length; i++) {
					NorthBoundAPIServiceStub.OrderPartialInfo orderPartialInfo = orderPartialInfos[i];
					// 订单ID
					// System.out.println("订单ID:"+orderPartialInfo.getHotelOrderId());
					// // 订单状态
					// System.out.println("订单状态:"+orderPartialInfo.getOrderStatusCode());
					result.put(orderPartialInfo.getHotelOrderId() + "",
							orderPartialInfo.getOrderStatusCode());
					// 酒店ID
					// System.out.println("酒店ID:"+orderPartialInfo.getHotelId());
					// // 房型ID
					// System.out.println("房型ID:"+orderPartialInfo.getRoomTypeId());
					// // 房型名称
					// System.out.println("房型名称:"+orderPartialInfo.getRoomTypeName());
					// // 价格代码ID
					// System.out.println("价格代码ID:"+orderPartialInfo.getRatePlanID());
					// // 价格代码名称
					// System.out.println("价格代码名称:"+orderPartialInfo.getRatePlanName());
					// // 入店时间
					// System.out.println("入店时间:"+DateSwitch.SwitchString(orderPartialInfo.getCheckInDate()));
					// // 离店时间
					// System.out.println("离店时间:"+DateSwitch.SwitchString(orderPartialInfo.getCheckOutDate()));
					// // 最早到达时间
					// System.out.println("最早到达时间:"+DateSwitch.SwitchString(orderPartialInfo.getArrivalEarlyTime()));
					// // 最晚离开时间
					// System.out.println("最晚离开时间:"+DateSwitch.SwitchString(orderPartialInfo.getArrivalLateTime()));
					// // 房间数量
					// System.out.println("房间数量:"+orderPartialInfo.getRoomAmount());
					// // 客人姓名
					// System.out.println("客人姓名:"+orderPartialInfo.getCustomerName());
					// // 联系人姓名
					// System.out.println("联系人姓名:"+orderPartialInfo.getContacterName());
					// // 联系人手机
					// System.out.println("联系人手机:"+orderPartialInfo.getContacterMobile());
					// // 联系人Email
					// System.out.println("联系人Email:"+orderPartialInfo.getContacterEmail());
					// 支付方式 目前API接口只支持"前台自付"方式 0 前台支付 1 预付
					// System.out.println("支付方式:"+orderPartialInfo.getPaymentTypeCode());
					// // 是否担保
					// System.out.println("是否担保:"+orderPartialInfo.getIsHasVouchRule());
					// // 货币代码
					// System.err.println("货币代码:"+orderPartialInfo.getCurrencyCode());
					// // 总价
					// System.out.println("总价:"+orderPartialInfo.getTotalPrice());
					// // 确认订单方式
					// System.out.println("确认订单方式:"+orderPartialInfo.getConfirmTypeCode());
					// // 确认订单语言
					// System.out.println("确认订单语言:"+orderPartialInfo.getConfirmLanguageCode());
					// //给酒店备注
					// System.out.println(orderPartialInfo.getNoteToHotel());
					// //给艺龙备注
					// System.out.println(orderPartialInfo.getNoteToElong());
				}
			}
		}
		return result;
	}
}

package com.ccservice.b2b2c.atom.mangguo;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.service.IHotelService;
import com.mango.hotel.DetailOrderRequest;
import com.mango.hotel.DetailOrderResponse;
import com.mango.hotel.MGHotelService;
import com.mango.hotel.MGHotelServiceProxy;
import com.mango.hotel.MangoAuthor;
import com.mango.hotel.Order;
public class DetailMangoHotelorder {    
  
  public static void main(String[] args) throws Exception {   
	  String url = "http://localhost:8080/sj_service/service/";

		HessianProxyFactory factory = new HessianProxyFactory();
		IHotelService servier = (IHotelService) factory.create(IHotelService.class,
				url + IHotelService.class.getSimpleName());
	
	
			MGHotelService hotelService=new MGHotelServiceProxy();
		
			DetailOrderRequest detailOrderRequest = new DetailOrderRequest();
			
			MangoAuthor author=new MangoAuthor();
			author.setChannel("SXTJ");
			author.setKey("02E882DC62692E9F29D8709BFD0369D7E09C5A4F");
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
			
			detailOrderRequest.setOrdercds("");//要查看得订单编号
			
			
			
			try {
				DetailOrderResponse detailOrderResponse =hotelService.detailOrder(detailOrderRequest);
				
				
				if(detailOrderResponse.getResult().getValue()==1)
				{
					String orderStates =detailOrderResponse.getRoomOrder(0).getOrderStates();//3为提交到中台,14订单取消。。。。。
					String hotelConfirmTel =detailOrderResponse.getRoomOrder(0).getHotelConfirmTel();//1为已经确认, 0为没确认
					String hotelConfirmFax =detailOrderResponse.getRoomOrder(0).getHotelConfirmFax();//1为已经确认, 0为没确认
					String hotelConfirmFaxReturn =detailOrderResponse.getRoomOrder(0).getHotelConfirmFaxReturn();//1已经收到回传, 0为没收到回传
				    String auditStates =detailOrderResponse.getRoomOrder(0).getAuditStates();
				    Order mgOrder =detailOrderResponse.getRoomOrder(0).getMgOrder();
				
				}
				} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			
			}
		

       
  
			}
  }
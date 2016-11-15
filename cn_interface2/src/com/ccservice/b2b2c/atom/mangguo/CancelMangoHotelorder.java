package com.ccservice.b2b2c.atom.mangguo;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.service.IHotelService;
import com.mango.hotel.CancelRoomOrderRequest;
import com.mango.hotel.CancelRoomOrderResponse;
import com.mango.hotel.MGHotelService;
import com.mango.hotel.MGHotelServiceProxy;
import com.mango.hotel.MangoAuthor;
public class CancelMangoHotelorder {    
  
  public static void main(String[] args) throws Exception {   
	  String url = "http://localhost:8080/sj_service/service/";

		HessianProxyFactory factory = new HessianProxyFactory();
		IHotelService servier = (IHotelService) factory.create(IHotelService.class,
				url + IHotelService.class.getSimpleName());
	
	
			MGHotelService hotelService=new MGHotelServiceProxy();
		
			CancelRoomOrderRequest cancelRoomOrderRequest =new CancelRoomOrderRequest();
			MangoAuthor author=new MangoAuthor();
			author.setChannel("SXTJ");
			author.setKey("02E882DC62692E9F29D8709BFD0369D7E09C5A4F");
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
			cancelRoomOrderRequest.setAuthor(author);
			
			cancelRoomOrderRequest.setOrdercd("");// 要取消得订单标号...必填
			cancelRoomOrderRequest.setCancelReason("");//取消原因   非必填
			
			
		
			
			
			
			try {
				CancelRoomOrderResponse cancelRoomOrderResponse =hotelService.cancelRoomOrder(cancelRoomOrderRequest);
				
				
				if(cancelRoomOrderResponse.getResult().getValue()==1)
				{
					String messa=cancelRoomOrderResponse.getResult().getMessage();
				}
				} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			
			}
		

       
  
			}
  }
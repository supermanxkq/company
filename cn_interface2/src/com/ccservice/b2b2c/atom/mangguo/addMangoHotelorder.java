package com.ccservice.b2b2c.atom.mangguo;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.service.IHotelService;
import com.mango.hotel.AddRoomOrderRequest;
import com.mango.hotel.AddRoomOrderResponse;
import com.mango.hotel.ContactorInfo;
import com.mango.hotel.GuestProfile;
import com.mango.hotel.MGHotelService;
import com.mango.hotel.MGHotelServiceProxy;
import com.mango.hotel.MangoAuthor;
public class addMangoHotelorder {    
  
  public static void main(String[] args) throws Exception {   
	  String url = "http://localhost:8080/sj_service/service/";

		HessianProxyFactory factory = new HessianProxyFactory();
		IHotelService servier = (IHotelService) factory.create(IHotelService.class,
				url + IHotelService.class.getSimpleName());
	
	
			MGHotelService hotelService=new MGHotelServiceProxy();
		
			AddRoomOrderRequest addRoomOrderRequest =new AddRoomOrderRequest();
			MangoAuthor author=new MangoAuthor();
			author.setChannel("SXTJ");
			author.setKey("02E882DC62692E9F29D8709BFD0369D7E09C5A4F");
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
			addRoomOrderRequest.setAuthor(author);
			addRoomOrderRequest.setHotelCode("");
			addRoomOrderRequest.setHotelName("");
			addRoomOrderRequest.setRoomTypeCode("");
			addRoomOrderRequest.setRoomTypeName("");
			addRoomOrderRequest.setCheckInDate("");
			addRoomOrderRequest.setCheckOutDate("");
			addRoomOrderRequest.setBedType("");//1为大床，2为双床，3为单床
			addRoomOrderRequest.setCurrency("");
			addRoomOrderRequest.setTotalAmount("");
			addRoomOrderRequest.setArriveEarlyTime("");
			addRoomOrderRequest.setLastArriveTime("");
			
			GuestProfile[]  guestProfiles = new GuestProfile[1];
			
			GuestProfile guestProfile=new GuestProfile();//入住人数组
			
			guestProfile.setName("");
			guestProfile.setMobile("");//.非必填
			guestProfile.setGuestType("");//客人类型(内宾/外宾) //.非必填
			guestProfile.setGender("");//先生/女士
			guestProfiles[0]=guestProfile;
			addRoomOrderRequest.setGuests(guestProfiles);
			
			
			ContactorInfo[]  contactorInfos = new ContactorInfo[1];
			
			ContactorInfo contactorInfo =new ContactorInfo();//联系人数组
			contactorInfo.setContactor("");
			contactorInfo.setMobile("");
			contactorInfo.setTelephone("");//.非必填
			contactorInfo.setFax("");//.非必填
			contactorInfo.setEmail("");//.非必填
			contactorInfos[0]=contactorInfo;
			
			
			addRoomOrderRequest.setContactors(contactorInfos);
			
			
			addRoomOrderRequest.setRoomquantity(1);//房间数量
			
			addRoomOrderRequest.setGuestCount(1);//客人数量
			
			addRoomOrderRequest.setSpecialRequest("无");//特殊要求
			
			addRoomOrderRequest.setHotelNote("给酒店备注");
			addRoomOrderRequest.setPayMethod("");//支付方式 pay表示面付，pre_pay表示预付
			
			addRoomOrderRequest.setGuarantee("");//是否担保...非必填
			
			
			
			try {
				AddRoomOrderResponse addRoomOrderResponse =hotelService.addRoomOrder(addRoomOrderRequest);
				
				
				if(addRoomOrderResponse.getResult().getValue()==1)
				{
					String ordercode=addRoomOrderResponse.getOrdercode();
				}
				} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			
			}
		

       
  
			}
  }
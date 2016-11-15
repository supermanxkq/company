package com.ccservice.b2b2c.atom.hotel;



import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hotelprice.Hotelprice;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.mango.hotel.AddRoomOrderRequest;
import com.mango.hotel.AddRoomOrderResponse;
import com.mango.hotel.CancelRoomOrderRequest;
import com.mango.hotel.CancelRoomOrderResponse;
import com.mango.hotel.ContactorInfo;
import com.mango.hotel.DetailOrderRequest;
import com.mango.hotel.DetailOrderResponse;
import com.mango.hotel.GuestProfile;
import com.mango.hotel.MGHotelService;
import com.mango.hotel.MGHotelServiceProxy;
import com.mango.hotel.MangoAuthor;
import com.mango.hotel.RatePlan;
import com.mango.hotel.SingleHotelRequest;
import com.mango.hotel.SingleHotelResponse;

import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.HotelSortMethod;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.HotelSortOrder;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.RoomTypeEnum;

public class HotelBook implements IHotelBook {
	private String username;
	private String password;
	
	@Override
	public String book(int hotelid, String roomcode, String starttime,
			int SponsionType, int DayLength, String Contactor, String Mobile,
			String Remark,int RoomCount, String listuser,String listprice,String qidaynumber){
	
		return "wei";
		
		
	    
	}
	public String cancelorder(String ordercode)throws Exception {
	System.out.println("取消订单=="+ordercode+"username=="+username+"  password=="+password);
	
	MGHotelService hotelService=new MGHotelServiceProxy();
	
	CancelRoomOrderRequest cancelRoomOrderRequest =new CancelRoomOrderRequest();
	MangoAuthor author=new MangoAuthor();
	author.setChannel("HBBJ3675");
	author.setKey("123456");
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
	cancelRoomOrderRequest.setAuthor(author);
	
	cancelRoomOrderRequest.setOrdercd(ordercode);// 要取消得订单标号...必填
	cancelRoomOrderRequest.setChannelCode("");
	cancelRoomOrderRequest.setCancelReason("wu");//取消原因   非必填
	
	

	
	
	
	try {
		CancelRoomOrderResponse cancelRoomOrderResponse =hotelService.cancelRoomOrder(cancelRoomOrderRequest);
		
		
		if(cancelRoomOrderResponse.getResult().getValue()==1)
		{
			System.out.println("成功取消");
			String messa=cancelRoomOrderResponse.getResult().getMessage();
			
			 return "OK";
		}else{
			
			
			System.out.println("失败原因=="+cancelRoomOrderResponse.getResult().getMessage());
		}
		} catch (Exception e) {
		// TODO Auto-generated catch block
	//	e.printStackTrace();
		
		  return "NOK";
	}
	
	
	
	
			  return "NOK";
		
	}
	

	
	public String addorder(Hotelorder hotelorder) throws Exception{
		/*if(peopleNameList.trim().indexOf(",")!=-1){
			peopleNameList=peopleNameList.replaceAll(",", " ");
		}*/
		String priceJHid="";
		
		List<Guest>listGuest=Server.getInstance().getHotelService().findAllGuest(" where 1=1 and "+Guest.COL_orderid+" ="+hotelorder.getId(), "", -1, 0);
		Hotel hotel =Server.getInstance().getHotelService().findHotel(hotelorder.getHotelid()) ;
		Roomtype roomtype =Server.getInstance().getHotelService().findRoomtype(hotelorder.getRoomid());
		String datenumber=hotelorder.getComedate().toString().substring(0, 7);
		List<Hotelprice>listprice=Server.getInstance().getHotelService().findAllHotelprice(" where 1=1 and "+Hotelprice.COL_hotelid+" ="+hotel.getId()+" and "+Hotelprice.COL_roomid+" ="+roomtype.getId()+" and "+Hotelprice.COL_datenumber+"='"+datenumber+"'", "", -1, 0);
		if(listprice.size()>0){
			
			priceJHid=listprice.get(0).getRateplancode();
		}
		
		MGHotelService hotelService=new MGHotelServiceProxy();
		
		AddRoomOrderRequest addRoomOrderRequest =new AddRoomOrderRequest();
		MangoAuthor author=new MangoAuthor();
		author.setChannel("HBBJ3675");
		author.setKey("123456");
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
		addRoomOrderRequest.setAuthor(author);
		addRoomOrderRequest.setHotelCode(hotel.getHotelcode());
		addRoomOrderRequest.setHotelName(hotel.getName());
		addRoomOrderRequest.setRoomTypeCode(roomtype.getRoomcode());
		addRoomOrderRequest.setRoomTypeName(roomtype.getName());
		addRoomOrderRequest.setCheckInDate(hotelorder.getComedate()+"");//入住时间
		addRoomOrderRequest.setCheckOutDate(hotelorder.getLeavedate()+"");//离店时间
		if(roomtype.getBed()==1){// 单床
			addRoomOrderRequest.setBedType("3");//1为大床，2为双床，3为单床
		}else
		if(roomtype.getBed()==2){// 双床
			addRoomOrderRequest.setBedType("2");//1为大床，2为双床，3为单床
		}else
		if(roomtype.getBed()==3){// 大床
			addRoomOrderRequest.setBedType("1");//1为大床，2为双床，3为单床
		}else{
			addRoomOrderRequest.setBedType("3");//1为大床，2为双床，3为单床
		}
		//addRoomOrderRequest.setRatePlanCode(listprice.get(0).getRateplancode());//价格计划ID
		addRoomOrderRequest.setRatePlanCode(hotelorder.getPricecodeid());//价格计划ID
		addRoomOrderRequest.setRatePlanName(listprice.get(0).getRateplanname());// 价格计划name
		addRoomOrderRequest.setCurrency(listprice.get(0).getMoytype());//币种
		addRoomOrderRequest.setTotalAmount(hotelorder.getPrice());//总价格
		addRoomOrderRequest.setArriveEarlyTime(hotelorder.getReservstart());//最早到点时间
		addRoomOrderRequest.setLastArriveTime(hotelorder.getReservend());//最晚到点时间
		
		GuestProfile[]  guestProfiles = new GuestProfile[listGuest.size()];
		
		for(int a=0;a<listGuest.size();a++){
			GuestProfile guestProfile=new GuestProfile();//入住人数组
			
			guestProfile.setName(listGuest.get(a).getName());
			guestProfile.setMobile(listGuest.get(a).getMobile());//.非必填
			guestProfile.setGuestType("内宾");//客人类型(内宾/外宾) //.非必填
			if(listGuest.get(a).getSex()==1){//1男2女
				guestProfile.setGender("先生");//先生/女士
			}
			if(listGuest.get(a).getSex()==2){//1男2女
				guestProfile.setGender("女士");//先生/女士
			}else{
				guestProfile.setGender("先生");//先生/女士
			}
			
			guestProfiles[a]=guestProfile;
		}
		
		addRoomOrderRequest.setGuests(guestProfiles);
		
		
		ContactorInfo[]  contactorInfos = new ContactorInfo[1];
		
		ContactorInfo contactorInfo =new ContactorInfo();//联系人数组
		
		contactorInfo.setContactor(hotelorder.getLinkname());//联系人名字
		
		contactorInfo.setMobile(hotelorder.getLinkmobile());//联系人电话
		//contactorInfo.setTelephone("");//.非必填
		//contactorInfo.setFax("");//.非必填
		//contactorInfo.setEmail("");//.非必填
		contactorInfos[0]=contactorInfo;
		
		
		addRoomOrderRequest.setContactors(contactorInfos);
		
		
		addRoomOrderRequest.setRoomquantity(hotelorder.getPrerooms());//房间数量
		
		addRoomOrderRequest.setGuestCount(hotelorder.getOrderpeaple());//客人数量
		
		addRoomOrderRequest.setSpecialRequest("wu");//特殊要求
		
		addRoomOrderRequest.setHotelNote("");
		addRoomOrderRequest.setPayMethod("pay");//支付方式 pay表示面付，pre_pay表示预付
		
		addRoomOrderRequest.setGuarantee("N");//是否担保...非必填
		
		
		
		try {
			AddRoomOrderResponse addRoomOrderResponse =hotelService.addRoomOrder(addRoomOrderRequest);
			
			
			if(addRoomOrderResponse.getResult().getValue()==1)
			{
				String ordercode=addRoomOrderResponse.getOrdercode();
				return ordercode;
			}else{
				System.out.println("错误=="+addRoomOrderResponse.getResult().getMessage());
				return "NOCODE";
			}
			} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		
		}

		return "NOCODE";
	}
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public List findhotelprice(String citycode, String hotelcode,
			String startDate, String endDate, String roomcode) throws Exception {
		List Listprice= new ArrayList();
		MGHotelService hotelService=new MGHotelServiceProxy();
		SingleHotelRequest hotelRequest=new SingleHotelRequest();
		MangoAuthor author=new MangoAuthor();
		author.setChannel("HBBJ3675");
		author.setKey("123456");
		
		
		
		//author.setChannel("SXTJ");
		//author.setKey("02E882DC62692E9F29D8709BFD0369D7E09C5A4F");
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
		hotelRequest.setAuthor(author);
		

		
		hotelRequest.setCityCode(citycode);
		hotelRequest.setHotelCode(hotelcode);
		hotelRequest.setCheckInDate(startDate);
		hotelRequest.setCheckOutDate(endDate);
		//Listprice=null;
		String pricecode="";
		SingleHotelResponse hotelResponse= hotelService.singleHotel(hotelRequest);
		if(hotelResponse.getResult().getValue()==1)
		{
			com.mango.hotel.Hotel[] list=hotelResponse.getHotelsRes();
			if(list!=null&&list.length>0)
			{
				for(int i=0;i<list.length;i++)
				{
					com.mango.hotel.Hotel hotel2=list[i];
					
					com.mango.hotel.RoomType[] listroom=hotel2.getRoomTypeList();
					if(listroom!=null)
					{
						for(int ix=0;ix<listroom.length;ix++)
						{
							com.mango.hotel.RoomType roomType2=listroom[ix];
						if(roomType2.getRoomTypeCode().equals(roomcode)){//如果前台传得房型CODE和返回的相同才取值
								
								RatePlan[] ratePlan=roomType2.getRatePlanList();
								if(ratePlan!=null&&ratePlan.length>0)
								{
									
									for(int iii=0;iii<ratePlan.length;iii++)
									{
											
											String[] datearray=ratePlan[iii].getAbleSaleDate().split("-");
											String day="";
											if(datearray[2].substring(0,1).equals("0"))
											{
												day=datearray[2].substring(1);
											}else
											{
												day=datearray[2];
											}
											//Hotelprice.class.getMethod("setNo"+day,Double.class).invoke(hotelprice,Double.parseDouble(ratePlan[iii].getSale_price()));
										 		
												if(ratePlan[iii].getRateplanName().equals("标准")){
														System.out.println("--"+day+"的价格=="+ratePlan[iii].getSale_price()+"---价格计划id是=="+ratePlan[iii].getRateplanCode()+"---价格计划name=="+ratePlan[iii].getRateplanName());
														//
														float money = 0f;
														try {
															money = Float.valueOf(ratePlan[iii].getSale_price()).floatValue();
														} catch (Exception e) {

														}
														pricecode=ratePlan[iii].getRateplanCode();
														DecimalFormat format = null;
														format = (DecimalFormat) NumberFormat.getInstance();
														format.applyPattern("###0");
														try {
															String result = format.format(money);
															Listprice.add(result);//添加价格
															
														} catch (Exception e) {
															 ;
															 Listprice.add(Float.toString(money));//添加价格
														}
														//
														
														//Listprice.add(ratePlan[iii].getSale_price());//添加价格
														
														
														
												}
											}
									
									Listprice.add(pricecode);
									
								}
							
							}
						}
					}
				}
			}
		}
		
		
		
		return Listprice;
	}
	@Override
	public String findhotelorderstate(String ordercode) throws Exception {
		// TODO Auto-generated method stub
		
		
		
		MGHotelService hotelService=new MGHotelServiceProxy();
		
		DetailOrderRequest detailOrderRequest =new DetailOrderRequest();
		MangoAuthor author=new MangoAuthor();
		author.setChannel("HBBJ3675");
		author.setKey("123456");
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
		detailOrderRequest.setAuthor(author);
		
		detailOrderRequest.setOrdercds(ordercode);
		try {
			DetailOrderResponse detailOrderResponse =hotelService.detailOrder(detailOrderRequest);
			
			
			if(detailOrderResponse.getResult().getValue()==1)
			{
				String orderstate=detailOrderResponse.getRoomOrder(0).getOrderStates();
				
				//1.前台订单未提交  2.已提交订单  3.已提交中台  4.已入住  5.提前退房  6，,正常退房 7.延住  8.已付款  9.已创建退款单 10.退款单已审批 11.财务已经退款  
				//12.退款成功  13.NOSHOW  14 已取消
				
				
				return orderstate;
			}
			} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		
		}
			
		
		return "NO";
	}
	public Hotelorder findhotelorder(String ordercode) throws Exception {
		// TODO Auto-generated method stub
		
		
		Hotelorder hotelorder=new Hotelorder();
		
		MGHotelService hotelService=new MGHotelServiceProxy();
		
		DetailOrderRequest detailOrderRequest =new DetailOrderRequest();
		MangoAuthor author=new MangoAuthor();
		author.setChannel("HBBJ3675");
		author.setKey("123456");
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
		detailOrderRequest.setAuthor(author);
		
		detailOrderRequest.setOrdercds(ordercode);
		try {
			DetailOrderResponse detailOrderResponse =hotelService.detailOrder(detailOrderRequest);
			
			
			if(detailOrderResponse.getResult().getValue()==1)
			{
				String orderstate=detailOrderResponse.getRoomOrder(0).getOrderStates();
				
				hotelorder.setOrderStates(orderstate);
				hotelorder.setHotelConfirmTel(detailOrderResponse.getRoomOrder(0).getHotelConfirmTel());
				hotelorder.setHotelConfirmFax(detailOrderResponse.getRoomOrder(0).getHotelConfirmFax());
				hotelorder.setHotelConfirmFaxReturn(detailOrderResponse.getRoomOrder(0).getHotelConfirmFaxReturn());
				hotelorder.setAuditStates(detailOrderResponse.getRoomOrder(0).getAuditStates());
				
				return hotelorder;
			}
			} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		
		}
			
		
		return hotelorder;
	}
	@Override
	public String insetrebateuser(String ordererbate, String hotelorderid,
			String type) throws Exception {
		
		return "";
	}
	//一下为国际酒店用
	@Override
	public String findInterHotelPriceByIdAndRoomType(Hotel hotel,
			String roomtype, String rommnum, String startDate, String endDate)
			throws Exception {
		
		//开始
		
		
		
		
		

		FreeHotelSearchWebServiceStub stub = new FreeHotelSearchWebServiceStub("http://publicwebservices.hotel.de/V2_8/FreeHotelSearchWebService.svc");
		
		FreeHotelSearchWebServiceStub.GetMultiAvailability a = new FreeHotelSearchWebServiceStub.GetMultiAvailability();
		FreeHotelSearchWebServiceStub.MultiAvailabilitySelectedPropertiesRequest req = new FreeHotelSearchWebServiceStub.MultiAvailabilitySelectedPropertiesRequest();
	
		
		req.setToken(""+System.currentTimeMillis());
		req.setAffiliateNumber(4833757);
		
		FreeHotelSearchWebServiceStub.Date sda =new FreeHotelSearchWebServiceStub.Date();
		
		String[] Stime=startDate.split("-");
		
		if(Stime[1].toString().substring(0,1).equals("0")){
			
			Stime[1]=Stime[1].substring(1);
			
		}
		if(Stime[2].toString().substring(0,1).equals("0")){
			
			Stime[2]=Stime[2].substring(1);
			
		}	
		sda.setYear(Integer.parseInt(Stime[0]));
		sda.setMonth(Integer.parseInt(Stime[1]));
		sda.setDay(Integer.parseInt(Stime[2]));
		
		req.setArrival(sda);//开始年月日
		
		FreeHotelSearchWebServiceStub.Date eda=new FreeHotelSearchWebServiceStub.Date();
		
		String[] Etime=endDate.split("-");
		if(Etime[1].toString().substring(0,1).equals("0")){
			
			Etime[1]=Etime[1].substring(1);
			
		}
		if(Etime[2].toString().substring(0,1).equals("0")){
			
			Etime[2]=Etime[2].substring(1);
			
		}	
		
		eda.setYear(Integer.parseInt(Etime[0]));
		eda.setMonth(Integer.parseInt(Etime[1]));
		eda.setDay(Integer.parseInt(Etime[2]));
		
		req.setDeparture(eda);//结束年月日
		
		
		req.setLanguage("EN");
		
		//FreeHotelSearchWebServiceStub.ArrayOfInt = new FreeHotelSearchWebServiceStub.ArrayOfInt();
		FreeHotelSearchWebServiceStub.ArrayOfInt arrayOfInt =  new FreeHotelSearchWebServiceStub.ArrayOfInt();
	
		
		int hid= Integer.parseInt(hotel.getHotelcode());
		arrayOfInt.set_int(new int[]{hid});
		req.setPropertyNumbers(arrayOfInt);
	
		
		
		req.setNumberOfRooms(Integer.parseInt(rommnum));//间数
		
		//房型类型  1单人房 2双人房 3三人房 4多人房
		if(roomtype.equals("1")){//单人房
		FreeHotelSearchWebServiceStub.RoomTypeEnum roomTypeEnum = RoomTypeEnum.SingleRoom;
		req.setRoomType(roomTypeEnum);
		}
		if(roomtype.equals("2")){//双人房
		FreeHotelSearchWebServiceStub.RoomTypeEnum roomTypeEnum = RoomTypeEnum.DoubleRoom;
		req.setRoomType(roomTypeEnum);
		}
		if(roomtype.equals("3")){//三人房
		FreeHotelSearchWebServiceStub.RoomTypeEnum roomTypeEnum = RoomTypeEnum.TripleRoom;
		req.setRoomType(roomTypeEnum);
		}
		if(roomtype.equals("4")){//多人房
		FreeHotelSearchWebServiceStub.RoomTypeEnum roomTypeEnum = RoomTypeEnum.FamilyRoom;
		req.setRoomType(roomTypeEnum);
		}
		
		FreeHotelSearchWebServiceStub.HotelSortMethod sortMethod = HotelSortMethod.Default;
		req.setSortMethod(sortMethod);
		
		FreeHotelSearchWebServiceStub.HotelSortOrder hotelSortOrder =HotelSortOrder.Ascending;
		req.setSortOrder(hotelSortOrder);
		
	
		a.setObjSelectedPropertiesRequest(req);
	
		
		FreeHotelSearchWebServiceStub.GetMultiAvailabilityResponse res = stub.getMultiAvailability(a);
		
		
		FreeHotelSearchWebServiceStub.ArrayOfAvailabilityListHotel los = res.getGetMultiAvailabilityResult().getAvailableHotelList();
		FreeHotelSearchWebServiceStub.AvailabilityListHotel [] lll= los.getAvailabilityListHotel();
		if(lll!=null){
		for(FreeHotelSearchWebServiceStub.AvailabilityListHotel l:lll){
			
			//图片
			FreeHotelSearchWebServiceStub.PictureReference[] aa = l.getMedia().getPictureReference();
			//
			System.out.println("名字:"+l.getName()+",,价格=="+l.getPrice().getAmountAfterTax()+",币种:"
					+l.getPrice().getCurrency()+",城市:"+l.getHotelAddress().getCity()+",图片:"+aa[0].getLink()
					+",星级:"+l.getRatingHotelDe()+",ID:"+l.getPropertyNumber());
			
			
			System.out.println("返回结果:"+l.getPrice().getAmountAfterTax()+"-"+l.getBreakfastPrice()+"-"+l.getPrice().getCurrency());
			
			return l.getPrice().getAmountAfterTax()+"-"+l.getBreakfastPrice()+"-"+l.getPrice().getCurrency();
			
		}
		
		
		
		}
		
		
		
		
		
		
		//结束
		
		return "NO";
	}


}


package com.ccservice.b2b2c.atom.mangguo;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelprice.Hotelprice;
import com.ccservice.b2b2c.base.service.IHotelService;
import com.mango.hotel.MGHotelService;
import com.mango.hotel.MGHotelServiceProxy;
import com.mango.hotel.MangoAuthor;
import com.mango.hotel.RatePlan;
import com.mango.hotel.SingleHotelRequest;
import com.mango.hotel.SingleHotelResponse;
public class TestMangoHotelRoomPrice {    
  
  public static void main(String[] args) throws Exception {   
	  String url = "http://localhost:8080/lthk_service/service/";
	  HessianProxyFactory factory = new HessianProxyFactory();
		IHotelService servier = (IHotelService) factory.create(IHotelService.class,
				url + IHotelService.class.getSimpleName());
		MGHotelService hotelService=new MGHotelServiceProxy();
		SingleHotelRequest hotelRequest=new SingleHotelRequest();
		MangoAuthor author=new MangoAuthor();
		author.setChannel("SXTJ");
		author.setKey("02E882DC62692E9F29D8709BFD0369D7E09C5A4F");
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
		hotelRequest.setAuthor(author);
		
		Hotel hotel =servier.findHotel(16283);//
		City city =servier.findCity(hotel.getCityid());
		
		hotelRequest.setCityCode(city.getAreacode());
		hotelRequest.setHotelCode(hotel.getHotelcode());
		hotelRequest.setCheckInDate("2011-05-25");
		hotelRequest.setCheckOutDate("2011-05-28");
		
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
							//if(roomType2.getRoomTypeCode().equals("25647")){//如果前台传得房型CODE和返回的相同才取值
								Hotelprice hotelprice=new Hotelprice();
								RatePlan[] ratePlan=roomType2.getRatePlanList();
								if(ratePlan!=null&&ratePlan.length>0)
								{
									hotelprice.setDeptprice(ratePlan[0].getMarkert_price());
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
												}
											}
									
								}
							
							//}
						}
					}
				}
			}
		}
		

       
  
			}
  }
package com.ccservice.b2b2c.atom.mangguo;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.service.IHotelService;
import com.mango.hotel.HotelSummary;
import com.mango.hotel.MGHotelService;
import com.mango.hotel.MGHotelServiceProxy;
import com.mango.hotel.MangoAuthor;
import com.mango.hotel.MutilHotelRequest;
import com.mango.hotel.MutilHotelResponse;
public class readMangoHotel_city {    
  
  public static void main(String[] args) throws Exception {   
	  String url = "http://localhost:8080/sj_service/service/";

		HessianProxyFactory factory = new HessianProxyFactory();
		IHotelService servier = (IHotelService) factory.create(IHotelService.class,
				url + IHotelService.class.getSimpleName());
	
		List<City> listcity=servier.findAllCity(" where C_AREACODE is not NULL ", "", -1, 0);
		Hotel hotel=null;
		for(City city:listcity)
		{
			MGHotelService hotelService=new MGHotelServiceProxy();
			MutilHotelRequest hotelRequest=new MutilHotelRequest();
			MangoAuthor author=new MangoAuthor();
			author.setChannel("SXTJ");
			author.setKey("02E882DC62692E9F29D8709BFD0369D7E09C5A4F");
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
			hotelRequest.setAuthor(author);
			hotelRequest.setCityCode(city.getAreacode());
			hotelRequest.setCheckInDate("2010-11-12");
			hotelRequest.setCheckOutDate("2010-11-13");
			try {
				MutilHotelResponse hotelResponse= hotelService.mutilHotel(hotelRequest);
				if(hotelResponse.getResult().getValue()==1)
				{
				HotelSummary[] list=hotelResponse.getHotelList();
				if(list!=null&&list.length>0)
				{
					for(int i=0;i<list.length;i++)
					{
						HotelSummary hotelSummary=list[i];
						List<Hotel> listhotel=servier.findAllHotel(" where "+Hotel.COL_hotelcode+" = '"+hotelSummary.getHotelCode()+"'", "", -1, 0);
						if(listhotel!=null&&listhotel.size()>0)
						{
							
						}else
						{
						hotel=new Hotel();
						hotel.setAddress(hotelSummary.getChnAddress());
						hotel.setHotelcode(hotelSummary.getHotelCode());
						hotel.setName(hotelSummary.getChn_name());
						hotel.setEnname(hotelSummary.getEng_name());
						hotel.setCityid(city.getId());
						hotel=servier.createHotel(hotel);
						System.out.println(hotel);
						}
					}
				}
				}else
				{
					System.out.println(hotelResponse.getResult().getMessage()+"--"+city.getName());
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			
			}
		}

       
  }
}  
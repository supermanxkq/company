package com.ccservice.b2b2c.atom.service;

import java.util.List;

import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;





public interface IManGoHotelService {
	
	public String book(int hotelid,String roomcode ,String starttime ,int SponsionType, int DayLength, String Contactor,String Mobile,String Remark,int RoomCount,String listuser,String listprice,String qidaynumber) throws Exception;
	
	
	public String cancelorder(String ordercode) throws Exception;
	

		
	public String addorder(Hotelorder hotelorder) throws Exception;
	
	public List findhotelprice(String citycode,String hotelcode,String startDate,String endDate,String roomcode) throws Exception;


	public String findhotelorderstate(String ordercode) throws Exception;
	
	public Hotelorder findhotelorder(String ordercode) throws Exception;

	public String insetrebateuser(String ordererbate,String hotelorderid,String type) throws Exception;
	
	//以下为国际酒店用
	
	public String findInterHotelPriceByIdAndRoomType(Hotel hotel,String roomtype,String rommnum,String startDate,String endDate) throws Exception;
	
	

}

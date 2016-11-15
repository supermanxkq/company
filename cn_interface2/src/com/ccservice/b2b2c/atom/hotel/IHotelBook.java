package com.ccservice.b2b2c.atom.hotel;

import java.util.List;

import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;




public interface IHotelBook {
	
	/**
	 * 根据预订信息,调用接口,判断是否可以预订
	 * @param 
	 * @return  
	 * @throws Exception 
	 * @throws Exception 
	 */
	
	public String book(int hotelid,String roomcode ,String starttime ,int SponsionType, int DayLength, String Contactor,String Mobile,String Remark,int RoomCount,String listuser,String listprice,String qidaynumber);
	

	/**
	 * 调用接口,取消订单
	 * @param 
	 * @return  
	 * @throws Exception 
	 * @throws Exception 
	 */
	
	public String cancelorder(String ordercode) throws Exception;
	
	public String addorder(Hotelorder hotelorder) throws Exception;
	
	public List findhotelprice(String citycode,String hotelcode,String startDate,String endDate,String roomcode) throws Exception;
	
	public String findhotelorderstate(String ordercode) throws Exception;
	
	public Hotelorder findhotelorder(String ordercode) throws Exception;
	
	public String insetrebateuser(String ordererbate,String hotelorderid,String type) throws Exception;
	//一下为国际酒店用
	
	public String findInterHotelPriceByIdAndRoomType(Hotel hotel,String roomtype,String rommnum,String startDate,String endDate) throws Exception;
}

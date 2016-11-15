package com.ccservice.b2b2c.atom.service;

import java.util.List;

import com.ccservice.b2b2c.atom.hotel.IHotelBook;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;

public class ManGoHotelService implements IManGoHotelService {
	private IHotelBook hotelBook;
	
	@Override
	public String book(int hotelid, String roomcode, String starttime,
			int SponsionType, int DayLength, String Contactor, String Mobile,
			String Remark,int RoomCount, String listuser,String listprice,String qidaynumber) throws Exception {
		// TODO Auto-generated method stub
		return hotelBook.book(hotelid, roomcode, starttime, SponsionType, DayLength, Contactor, Mobile, Remark,RoomCount, listuser, listprice,qidaynumber);
	}
	public String cancelorder(String ordercode) throws Exception{
		
		return hotelBook.cancelorder(ordercode);
	}
	public String findhotelorderstate(String ordercode) throws Exception {
		// TODO Auto-generated method stub
		return hotelBook.findhotelorderstate(ordercode);
	}
	public Hotelorder findhotelorder(String ordercode) throws Exception {
		// TODO Auto-generated method stub
		return hotelBook.findhotelorder(ordercode);
	}
	
	public String addorder(Hotelorder hotelorder) throws Exception{
		
		return hotelBook.addorder(hotelorder);
	}
	public List findhotelprice(String citycode, String hotelcode,
			String startDate, String endDate, String roomcode) throws Exception {
		// TODO Auto-generated method stub
		return hotelBook.findhotelprice(citycode, hotelcode, startDate, endDate, roomcode);
	}
	

	
	
	public IHotelBook getHotelBook() {
		return hotelBook;
	}
	public void setHotelBook(IHotelBook hotelBook) {
		this.hotelBook = hotelBook;
	}
	@Override
	public String insetrebateuser(String ordererbate, String hotelorderid,
			String type) throws Exception {
		// TODO Auto-generated method stub
		return hotelBook.insetrebateuser(ordererbate, hotelorderid, type);
	}


	//一下为国际酒店用
	
	public String findInterHotelPriceByIdAndRoomType(Hotel hotel,String roomtype,String rommnum,String startDate,String endDate) throws Exception{
		
		return hotelBook.findInterHotelPriceByIdAndRoomType(hotel, roomtype, rommnum, startDate, endDate);
	}

	
	

}

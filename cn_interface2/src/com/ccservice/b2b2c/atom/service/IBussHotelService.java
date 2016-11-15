package com.ccservice.b2b2c.atom.service;

/**
 * 生意人接口
 */

public interface IBussHotelService {
	/**
	 * 所有城市、区域接口
	 */
	public void getCityAndRegion() throws Exception;
	
	/**
	 * 所有酒店、房型、价格
	 */
	public void getHotelRoomPrice() throws Exception;
	
	/**
	 * 单酒店信息查询
	 * @param hotelCode 生意人酒店ID
	 * @param roomCode 生意人房型ID
	 * @param checkInDate 入住日期
	 * @param checkOutDate 离店日期
	 * @return 返回的JSON字符串
	 */
	public String getSingleHotel(String hotelCode,String roomCode,String checkInDate,String checkOutDate)throws Exception;
}

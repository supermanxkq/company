package com.ccservice.b2b2c.atom.service;

import java.util.Map;
import java.util.List;
import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.b2b2c.base.guest.Contacter;
import com.ccservice.b2b2c.base.hotel.HotelsResult;
import com.ccservice.b2b2c.base.hotel.IsVouchResult;
import com.ccservice.b2b2c.base.hotelorder.OrderResult;
import com.ccservice.b2b2c.base.creditcard.Creditcard;

/**
 * 艺龙酒店接口
 */
public interface IELongHotelService {
	/**创建订单*/
	public String createElongHotelOrder(String HotelId, String RoomTypeID, int RoomAmount, String RatePlanID, String checkindate, String checkoutdate,
										String arrivalearlytime, String arrivalatetime, String GuestTypeCode, int GuestAmount, String PaymentTypeCode,
										String CurrencyCode, double TotalPrice, String ConfirmTypeCode, String ConfirmLanguageCode, Contacter Contacters, 
										Creditcard creditCard, List<Guest> Guests);

	/** 取消订单*/
	public String cancelElongHotelOrder(int HotelOrderId);

	/**订单信息确认*/
	public String confirmInfo(int OrderId);

	/**
	 * 获取酒店价格
	 * @param CityId 城市ID 暂无效
	 * @param HotelName 酒店名字 暂无效
	 * @param HotelId 酒店ID 多个用“,”号隔开，最多10个
	 * @param checkInDate 入住日期
	 * @param checkOutDate 离店日期
	 */
	public HotelsResult getHotelPrice(String CityId, String HotelName, String HotelId, String checkInDate, String checkOutDate);

	/**
	 * 酒店是否需要担保
	 * @param HotelId 酒店ID
	 * @param RoomTypeId  房型ID
	 * @param RatePlanId 产品ID
	 * @param checkindate 入住时间 yyyy-MM-dd HH:mm:ss
	 * @param checkoutdate 离店时间 yyyy-MM-dd HH:mm:ss
	 * @param arrivalearlytime 最早到达时间 yyyy-MM-dd HH:mm:ss
	 * @param arrivalatetime 最晚到达时间 yyyy-MM-dd HH:mm:ss
	 * @param RoomNum 房间数量
	 */
	public IsVouchResult isVouch(String HotelId, String RoomTypeId, int RatePlanId, String checkindate, 
								 String checkoutdate, String arrivalearlytime, String arrivalatetime, int RoomNum);

	/**
	 * 获取价格数据接口
	 * @param StartDate 入住时间
	 * @param EndDate 离店时间
	 * @param HotelID 酒店ID
	 */
	public HotelsResult getPriceByHotelId(String StartDate, String EndDate, String HotelID);

	/**获取房态*/
	public HotelsResult getHotelInventory(String startDate, String endDate, String HotelId) throws Exception;

	/**根据订单ID查询订单信息*/
	public OrderResult getHotelOrderDetailByOrderId(long HotelOrderId);
	
	/**根据多个订单号(英文逗号隔开)获得多个订单信息，返回<订单ID,订单状态>*/
	public Map<String, String> GetHotelOrderListById(String orderidstr);
	
	/**
	 * 验证信用卡是否可用、是否需要CVV码
	 * @param CreditCardNo 信用卡号 
	 */
	public String creditcardValidate(String CreditCardNo);
}
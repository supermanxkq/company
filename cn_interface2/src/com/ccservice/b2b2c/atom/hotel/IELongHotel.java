package com.ccservice.b2b2c.atom.hotel;

import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.base.creditcard.Creditcard;
import com.ccservice.b2b2c.base.guest.Contacter;
import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.b2b2c.base.hotel.HotelsResult;
import com.ccservice.b2b2c.base.hotel.IsVouchResult;
import com.ccservice.b2b2c.base.hotelorder.OrderResult;

/**
 * 艺龙酒店接口
 * 
 * @author 师卫林
 * 
 */
public interface IELongHotel {

	/**
	 * 根据订单号ID获得多个订单信息GetHotelOrderListById
	 */
	public Map<String, String> GetHotelOrderListById(String orderidstr);

	/**
	 * 创建订单
	 * 
	 * @param HotelId
	 * @param RoomTypeID
	 * @param RoomAmount
	 * @param RatePlanID
	 * @param checkindate
	 * @param checkoutdate
	 * @param arrivalearlytime
	 * @param arrivalatetime
	 * @param GuestTypeCode
	 * @param GuestAmount
	 * @param PaymentTypeCode
	 * @param CurrencyCode
	 * @param TotalPrice
	 * @param ConfirmTypeCode
	 * @param ConfirmLanguageCode
	 * @param Contacters
	 * @param creditCard
	 * @param Guests
	 * @return
	 */
	public String createElongHotelOrder(String HotelId, String RoomTypeID,
			int RoomAmount, String RatePlanID, String checkindate,
			String checkoutdate, String arrivalearlytime,
			String arrivalatetime, String GuestTypeCode, int GuestAmount,
			String PaymentTypeCode, String CurrencyCode, double TotalPrice,
			String ConfirmTypeCode, String ConfirmLanguageCode,
			Contacter Contacters, Creditcard creditCard, List<Guest> Guests);

	/**
	 * 取消订单
	 * 
	 * @param hotelOrderId
	 *            订单ID
	 * @return
	 */
	public String cancelElongHotelOrder(int hotelOrderId);

	/**
	 * 订单确认信息
	 * 
	 * @param OrderId
	 *            订单ID
	 * @return
	 */
	public String confirmInfo(int orderId);

	/**
	 * 获取酒店价格数据
	 * 
	 * @param CityId
	 *            城市ID
	 * @param HotelName
	 *            酒店名字
	 * @param HotelId
	 *            酒店ID
	 * @param checkInDate
	 *            入住日期
	 * @param checkOutDate
	 *            离店日期
	 */
	public HotelsResult getHotelPrice(String cityId, String hotelName,
			String hotelId, String checkInDate, String checkOutDate);

	/**
	 * 酒店是否需要担保
	 * 
	 * @param hotelId
	 *            酒店ID
	 * @param roomTypeId
	 *            房型ID
	 * @param ratePlanId
	 *            RPID
	 * @param checkindate
	 *            入住时间
	 * @param checkoutdate
	 *            离店时间
	 * @param arrivalatetime
	 *            最新更新时间
	 * @param roomNum
	 *            房间数量
	 * @return
	 */
	public IsVouchResult isVouch(String hotelId, String roomTypeId,
			int ratePlanId, String checkindate, String checkoutdate,
			String arrivalearlytime, String arrivalatetime, int roomNum);

	/**
	 * 获取价格数据接口
	 * 
	 * @param StartDate
	 *            入住时间
	 * @param EndDate
	 *            离店时间
	 * @param HotelID
	 *            酒店ID
	 * @return
	 */
	public HotelsResult getPriceByHotelId(String StartDate, String EndDate,
			String HotelID);

	/**
	 * 获取房态
	 * 
	 * @param startDate
	 *            入住时间
	 * @param endDate
	 *            离店时间
	 * @param HotelId
	 *            酒店ID
	 * @return
	 * @throws Exception
	 */
	public HotelsResult getHotelInventory(String startDate, String endDate,
			String hotelId);

	/**
	 * 根据订单号查询订单详细信息
	 * 
	 * @param hotelOrderId
	 * @return
	 */
	public OrderResult getHotelOrderDetailByOrderId(int hotelOrderId);

}

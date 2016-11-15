package com.ccservice.b2b2c.atom.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.jdom.JDOMException;

import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.b2b2c.base.hmhotelprice.AllotResult;
import com.ccservice.b2b2c.base.hmhotelprice.PriceResult;
import com.ccservice.b2b2c.base.hotelgooddata.HotelGoodData;
import com.ccservice.b2b2c.base.hotelorder.HMOrderResult;
import com.ccservice.b2b2c.base.hotelorder.OrderStatusResult;
import com.ccservice.huamin.CancelResultBean;

/**
 * 华闽酒店接口
 */
public interface IHMHotelService {

	/**
	 * 华闽下订单接口
	 * 
	 * @param contract
	 *            酒店合同號碼
	 * @param ver
	 *            酒店合同版本, 0 是最新版本
	 * @param checkin
	 *            日-月-年 例如: 31-OCT-11
	 * @param checkout
	 *            日-月-年 例如: 31-OCT-11
	 * @param prod
	 *            產品代碼
	 * @param cat
	 *            房型代碼
	 * @param type
	 *            床型代碼
	 * @param serv
	 *            服務組合
	 * @param bf
	 *            早餐數量
	 * @param flightinfo
	 *            航班資料
	 * @param guest
	 *            客人名稱 用符號 , 來定義姓氏和名字 的分隔 例如: 陳,大文
	 * @param servcode
	 *            服務代碼 -----可选
	 * @param qty
	 *            服務數量 -------可选
	 * @param night
	 *            服務晚數 ----可选
	 * @param sr
	 *            特別要求代碼----- 可选
	 * @return
	 * @throws Exception
	 */
	public HMOrderResult qbooking(String contract, String ver, String checkin,
			String checkout, int preroom, long prod, String cat, String type,
			String serv, long bf, String flightinfo, List<Guest> guest,
			String servcode, int qty, int night, String sr) throws Exception;

	/**
	 * 
	 * @param ordercode
	 *            华闽订单号 查询订单状态接口
	 * @throws Exception
	 */
	public OrderStatusResult qbookingstatus(String ordercode) throws Exception;

	/**
	 * 实时查询价格接口
	 * 
	 * @param hotelid
	 *            酒店id
	 * @param hotelname
	 *            ----- 酒店名字
	 * @param country-----选其一
	 *            国家代码
	 * @param citycode----
	 *            城市代码
	 * @param roomtypecode
	 *            房型代码
	 * @param bedcode
	 *            床行代码
	 * @param grade
	 *            星级 可选
	 * @param checkin
	 *            入住日期
	 * @param checkout
	 *            离店日期
	 * @param instance
	 * 			Y 只限及时确认
	 */
	public PriceResult getQrate(String hotelid, Date checkin, Date checkout,
			String hotelname, String country, String citycode,
			String roomtypecode, String bedcode, String grade,String instance) throws Exception;
	/**
	 * 实时查询价格接口
	 * 
	 * @param hotelid
	 *            酒店id
	 * @param hotelname
	 *            ----- 酒店名字
	 * @param country-----选其一
	 *            国家代码
	 * @param citycode----
	 *            城市代码
	 * @param roomtypecode
	 *            房型代码
	 * @param bedcode
	 *            床行代码
	 * @param grade
	 *            星级 可选
	 * @param checkin
	 *            入住日期
	 * @param checkout
	 *            离店日期
	 * @param instance
	 * 			Y 只限及时确认
	 */
	public List<HotelGoodData> getQrateHotelData(String hotelid, Date checkin, Date checkout,
			String hotelname, String country, String citycode,
			String roomtypecode, String bedcode, String grade,String instance) throws Exception;
	/**
	 * 
	 * @param hotelid
	 * 酒店id
	 * @param productid
	 * 产品代码
	 * @param roomtype
	 * 房型
	 * @param bedtype
	 * 床型
	 * @param checkin
	 * 入住日期
	 * @param checkout
	 * 退房日期
	 * @return
	 */
	public AllotResult getQallot(String contractid,String productid,
			String roomtype,String bedtype,Date checkin,Date checkout) throws Exception;	
	
	/**
	 * 取消订单接口
	 */
	public CancelResultBean cancelorder(String waiorderid)  throws  IOException;
	
}

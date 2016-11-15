package com.ccservice.b2b2c.atom.service;

import java.util.List;

import com.ccservice.b2b2c.base.hmhotelprice.HTCPriceResult;
import com.ccservice.b2b2c.base.hmhotelprice.PriceResult;
import com.ccservice.b2b2c.base.hotel.BookedRates;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.roomtype.Roomtype;

public interface IHTCHotelServiceHY {

	/**
	 * 获取酒店列表
	 * @param date
	 */
	public List<Hotel> getproplistHY(String date);
	/**
	 * 获取酒店基本信息
	 * @param hotelid
	 */
	public void getPropertyHY(long hotelid);
	
	/**
	 * 获取酒店详细信息
	 * @param id
	 */
	//public void getDescHY(int id);
	
	/**
	 * 获取指定类型的酒店详细信息
	 * @param type
	 * @param id
	 */
	//public String getDescByTypeHY(String type,int id);
	
	/**
	 * 获取酒店所有房间代码与详细信息
	 * @param id
	 * @return
	 */
	public List<Roomtype> getRoomObjHY(long id);
	
	/**
	 * 根据房间类型代码查询房间详细信息
	 * @param type
	 * @param id
	 * @return
	 */
	public void getRoomObjByTypeHY(String type,int id);
	
	/**
	 * 获取指定酒店所有价格代码的详细信息
	 * @param id
	 * @return
	 */
	public void getRateObjHY(int id,String date);
	
	/**
	 * 获取指定酒店指定价格代码详细信息
	 * @param type
	 * @param id
	 * @return
	 */
	public void getRateObjByTypeHY(String type,int id);
	
	/**
	 * 获取酒店所有计划代码的详细信息
	 * @param id
	 * @return
	 */
	public String getPlanObj(long id);
	
	/**
	 * 获取酒店指定计划代码的详细信息
	 * @param planid
	 * @param id
	 * @return
	 */
	public String getPlanObjByPlanid(String planid,long id);
	
	/**
	 * 获取指定酒店图片
	 * @param id
	 * @return
	 */
	public String getImage(long id);
	
	/**
	 * 获取指定酒店订单服务
	 * @param channel
	 * @param confnum
	 * @param iata
	 * @return
	 */
	public String getPropresv(String channel,String confnum,String iata);
	
	/**
	 * 查询订单审核状态
	 * @param cnfnum
	 * @param iata
	 * @return
	 */
	public String getResvaudit(String cnfnum,String iata);
	
	/**
	 * 搜索酒店列表的可用性信息
	 * @param 日期
	 * @param 入住晚数
	 * @param 价格代码分类
	 * @param 酒店id
	 * @param 酒店级别
	 * @param 城市
	 * @param 酒店所在区域
	 * @param 酒店所在商圈
	 * @param 客人位置（经纬）
	 * @param 关键字
	 * @param 起始页
	 * @param 每页条数
	 * @return
	 */
	//public String hotelSearch(String date,String nights,String ratestyle,String prop,String proplv,String city,String district,String tradearea,String guestposition,String keywords,String pageindex,String pagesize);
	
	/**
	 * 搜索单个酒店计划价格的可用性信息
	  * @param 日期
	 * @param 入住晚数
	 * @param 价格代码分类
	 * @param 酒店id
	 * @param 酒店级别
	 * @param 城市
	 * @param 酒店所在区域
	 * @param 酒店所在商圈
	 * @param 客人位置（经纬）
	 * @param 关键字
	 * @param 起始页
	 * @param 每页条数
	 * @return
	 */
	//public String hotelSerchById(String date,String nights,String ratestyle,String prop,String proplv,String city,String district,String tradearea,String guestposition,String keywords,String pageindex,String pagesize);
	
	/**
	 * 批量搜索酒店信息及计划价格的可用性信息
	  * @param 日期
	 * @param 入住晚数
	 * @param 价格代码分类
	 * @param 酒店id
	 * @param 酒店级别
	 * @param 城市
	 * @param 酒店所在区域
	 * @param 酒店所在商圈
	 * @param 客人位置（经纬）
	 * @param 关键字
	 * @param 起始页
	 * @param 每页条数
	 * @return
	 */
	//public String hotelSerchAll(String date,String nights,String ratestyle,String prop,String proplv,String city,String district,String tradearea,String guestposition,String keywords,String pageindex,String pagesize);

	
	/**
	 * 获取酒店所有房型所有价格计划的可用性信息
	 * @param 日期
	 * @param 入住晚数
	 * @param 房间类型
	 * @param 房间数
	 * @param 成人数
	 * @param 小孩数
	 * @return
	 * 该接口是获取我司系统缓存内价格，
	 * 该缓存价格与实时价格会存在不同，
	 * 此价格仅用作系统对客人在酒店列表页面里的展示，
	 * 不能直接用来提交订单，建议将此接口设置在后台轮循执行 。
	 */
	public List<HTCPriceResult> getCrateMap(int id,String date,int night);
	
	/**
	 * 获取酒店指定房型指定价格计划可用性信息
	 * @param 日期
	 * @param 入住晚数
	 * @param 房间类型
	 * @param 房间数
	 * @param 成人数
	 * @param 小孩数
	 * @return
	 * 该接口是获取我司系统缓存内价格，
	 * 该缓存价格与实时价格会存在不同，
	 * 此价格仅用作系统对客人在酒店列表页面里的展示，
	 * 不能直接用来提交订单，建议将此接口设置在后台轮循执行 。
	 */
	public String getCrateMapByType(int id,String date, String roomtype,String rateclass,int night);
	
	/**
	 * 获取酒店所有房型所有价格计划的可用性信息
	 * @param 日期
	 * @param 入住晚数
	 * @param 房间类型
	 * @param 房间数
	 * @param 成人数
	 * @param 小孩数
	 * @return
	 * 该接口是获取我司系统内的实时价格，
	 * 客人提交订单内的价格必须是以此接口返回的价格提交，
	 * 建议将此接口设置在客人进入订单填写页面时执行调用，
	 * 为了保证提交订单中的价格准确，建议在客人点击“提交订单”时再次调用该接口读取一次价格，
	 * 避免客人在填写订单内容时，价格被缓存接口更改 。
	 */
	public List<HTCPriceResult> getOnlineRateMap(long id,String date,int night);
	
	/**
	 * 获取酒店指定房型指定价格计划可用性信息
	 * @param 日期
	 * @param 入住晚数
	 * @param 房间类型
	 * @param 房间数
	 * @param 成人数
	 * @param 小孩数
	 * @return
	 * 该接口是获取我司系统内的实时价格，
	 * 客人提交订单内的价格必须是以此接口返回的价格提交，
	 * 建议将此接口设置在客人进入订单填写页面时执行调用，
	 * 为了保证提交订单中的价格准确，建议在客人点击“提交订单”时再次调用该接口读取一次价格，
	 * 避免客人在填写订单内容时，价格被缓存接口更改 。
	 */
	public String getOnlineRateMapByType(int id,String date, String roomtype,String rateclass,int night);
	

	/**
	  * 预订指定酒店订单,内部
	 * 
	 * @param 担保，传3
	 * @param 发送模式
	 *            0:无， 1:发送短信，2:发送EMAIL，3:发送短信和EMAIL
	 * @param 每日价格
	 * @param 入住日期
	 * @param 入住晚数
	 * @param 房型
	 * @param 付费方式
	 * @param 房间数
	 * @param 成人数
	 * @param 小孩数
	 * @param 姓
	 * @param 名，中文名只传这里
	 * @param 所在街道
	 * @param 客人最晚到店时间
	 * @param 电话
	 * @param 手机
	 * @param 邮箱
	 * @param 备注
	 * @param 授权号
	 * @return
	 */
	public String newResvHY(int id,String isassure, String deliverymode,String outconfnum,
			List<BookedRates> bookedrate, String date, String nights,
			String roomtype, String rateclass, String rooms, String adults,
			String children, String firstname, String lastname,String street1,
			String holdTime,String phone,String mobile,String email,String remark, String IATA);
}

package com.ccservice.b2b2c.atom.service;

import java.util.List;

import com.ccservice.b2b2c.base.hotel.BookedRates;

public interface IHTCHotelService {

	/**
	 * 获取酒店列表
	 * 
	 * @param 日期
	 */
	public String getproplist(String date);

	/**
	 * 获取酒店基本信息
	 * 
	 * @param 酒店id
	 */
	public String getProperty(long hotelid);

	/**
	 * 获取酒店详细信息
	 * 
	 * @param 酒店id
	 */
	public String getDesc(long id);

	/**
	 * 获取指定类型的酒店详细信息
	 * 
	 * @param 类型
	 * @param 酒店id
	 */
	public String getDescByType(String type, long id);

	/**
	 * 获取酒店所有房间代码与详细信息
	 * 
	 * @param 酒店id
	 * @return
	 */
	public String getRoomObj(long id);

	/**
	 * 根据房间类型代码查询房间详细信息
	 * 
	 * @param 房间类型代码
	 * @param 酒店id
	 * @return
	 */
	public String getRoomObjByType(String type, long id);

	/**
	 * 获取指定酒店所有价格代码的详细信息
	 * 
	 * @param 酒店id
	 * @return
	 */
	public String getRateObj(long id,String date);

	/**
	 * 获取指定酒店指定价格代码详细信息
	 * 
	 * @param 代码
	 * @param 酒店id
	 * @return
	 */
	public String getRateObjByType(String type, long id);

	/**
	 * 获取酒店所有计划代码的详细信息
	 * 
	 * @param 酒店id
	 * @return
	 */
	public String getPlanObj(long id);

	/**
	 * 获取酒店指定计划代码的详细信息
	 * 
	 * @param 计划id
	 * @param 酒店id
	 * @return
	 */
	public String getPlanObjByPlanid(String planid, long id);

	/**
	 * 获取指定酒店图片
	 * 
	 * @param 酒店id
	 * @return
	 */
	public String getImage(long id);

	/**
	 * 获取指定酒店订单服务
	 * 
	 * @param 渠道
	 * @param 订单号
	 * @param iata号
	 * @return
	 */
	public String getPropresv(String channel, String confnum, String iata);

	/**
	 * 查询订单审核状态
	 * 
	 * @param 订单号
	 * @param iata号
	 * @return
	 */
	public String getResvaudit(String cnfnum, String iata);

	/**
	 * 搜索酒店列表的可用性信息
	 * 
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
	public String hotelSearch(String date, String nights, String ratestyle,
			long hotelid, String proplv, String city, String district,
			String tradearea, String guestposition, String keywords,
			String pageindex, String pagesize);

	/**
	 * 搜索单个酒店计划价格的可用性信息
	 * 
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
	public String hotelSerchById(String date, String nights, String ratestyle,
			long hotelid, String proplv, String city, String district,
			String tradearea, String guestposition, String keywords,
			String pageindex, String pagesize);

	/**
	 * 批量搜索酒店信息及计划价格的可用性信息
	 * 
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
	public String hotelSerchAll(String date, String nights, String ratestyle,
			long hotelid, String proplv, String city, String district,
			String tradearea, String guestposition, String keywords,
			String pageindex, String pagesize);

	/**
	 * 获取酒店所有房型所有价格计划的可用性信息
	 * 
	 * @param 日期
	 * @param 入住晚数
	 * @param 房间类型
	 * @param 房间数
	 * @param 成人数
	 * @param 小孩数
	 * @return 该接口是获取我司系统缓存内价格， 该缓存价格与实时价格会存在不同， 此价格仅用作系统对客人在酒店列表页面里的展示，
	 *         不能直接用来提交订单，建议将此接口设置在后台轮循执行 。
	 */
	public String getCrateMap(long id, String date,int night);

	/**
	 * 获取酒店指定房型指定价格计划可用性信息
	 * 
	 * @param 日期
	 * @param 入住晚数
	 * @param 房间类型
	 * @param 房间数
	 * @param 成人数
	 * @param 小孩数
	 * @return 该接口是获取我司系统缓存内价格， 该缓存价格与实时价格会存在不同， 此价格仅用作系统对客人在酒店列表页面里的展示，
	 *         不能直接用来提交订单，建议将此接口设置在后台轮循执行 。
	 */
	public String getCrateMapByType(long id, String date, String roomtype,
			String rateclass,int night);

	/**
	 * 获取酒店所有房型所有价格计划的可用性信息
	 * 
	 * @param 日期
	 * @param 入住晚数
	 * @param 房间类型
	 * @param 房间数
	 * @param 成人数
	 * @param 小孩数
	 * @return 该接口是获取我司系统内的实时价格， 客人提交订单内的价格必须是以此接口返回的价格提交，
	 *         建议将此接口设置在客人进入订单填写页面时执行调用，
	 *         为了保证提交订单中的价格准确，建议在客人点击“提交订单”时再次调用该接口读取一次价格，
	 *         避免客人在填写订单内容时，价格被缓存接口更改 。
	 */
	public String getOnlineRateMap(long id, String date,int night);

	/**
	 * 获取酒店指定房型指定价格计划可用性信息
	 * 
	 * @param 日期
	 * @param 入住晚数
	 * @param 房间类型
	 * @param 房间数
	 * @param 成人数
	 * @param 小孩数
	 * @return 该接口是获取我司系统内的实时价格， 客人提交订单内的价格必须是以此接口返回的价格提交，
	 *         建议将此接口设置在客人进入订单填写页面时执行调用，
	 *         为了保证提交订单中的价格准确，建议在客人点击“提交订单”时再次调用该接口读取一次价格，
	 *         避免客人在填写订单内容时，价格被缓存接口更改 。
	 */
	public String getOnlineRateMapByType(long id, String date, String roomtype,
			String rateclass,int night);

	/**
	  * 预订指定酒店订单，外部
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
	public String newResv(long id,String isassure, String deliverymode,String outconfnum,
			String bookedrate, String date, String nights,
			String roomtype, String rateclass, String rooms, String adults,
			String children, String firstname, String lastname,String street1,
			String holdTime,String phone,String mobile,String email,String remark, String IATA);
	
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
	public String newResvHY(long id,String isassure, String deliverymode,String outconfnum,
			List<BookedRates> bookedrate, String date, String nights,
			String roomtype, String rateclass, String rooms, String adults,
			String children, String firstname, String lastname,String street1,
			String holdTime,String phone,String mobile,String email,String remark, String IATA);

	/**
	 * 修改指定酒店订单
	 * 
	 * @param 酒店id
	 * @param 订单号
	 * @param 客户姓
	 * @param 客户名
	 * @param 所在街道
	 * @param 电话
	 * @param 电子邮件
	 * @return
	 */
	public String modResv(long id, String confnum, String firstname,
			String lastname, String street1, String phone, String email);

	/**
	 * 取消指定酒店订单
	 * 
	 * @param 酒店id
	 * @param 订单号
	 * @return
	 */
	public String cancelresv(long id, String confnum);
}

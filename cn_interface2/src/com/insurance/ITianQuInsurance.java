package com.insurance;

/**
 * 天衢保险接口
 * 
 * @author 贾建磊
 * 
 */
public interface ITianQuInsurance {

	/**
	 * 创建订单
	 * 
	 * @param ainame
	 *            投保人姓名
	 * @param aimobile
	 *            投保人电话
	 * @param aiaddress
	 *            投保人地址 可以为空
	 * @param aiidcard
	 *            证件类型:1 身份证 2 护照 3 军人证 4 港台同胞证 5 其它证件
	 * @param aiidnumber
	 *            证件号
	 * @param aieffectivedate
	 *            生效日期
	 * @param aiemail
	 *            电子邮件
	 * @param aisex
	 *            投保人性别 -1保密 1 男 0 女
	 * @param aiflightnumber
	 *            航班号
	 * @return
	 */
	public String createOrder(String ainame, String aimobile, String aiaddress,
			String aiidcard, String aiidnumber, String aieffectivedate,
			String aiemail, String aisex, String aiflightnumber);

	/**
	 * 查询订单详细
	 * 
	 * @param orderid
	 *            订单号
	 * @return
	 */
	public String orderInfo(String orderid);

	/**
	 * 取消订单
	 * 
	 * @param orderid
	 *            订单号
	 * @return
	 */
	public String cancleOrder(String orderid);

	/**
	 * 第一次投保
	 * 
	 * @param orderid
	 *            订单号
	 * @return
	 */
	public String payOrder(String orderid);

	/**
	 * 退保
	 * 
	 * @param orderNumber
	 *            保单号
	 * @return
	 */
	public String backOrder(String orderNumber);

	/**
	 * 投保失败时再次投保
	 * 
	 * @param orderid
	 *            订单号
	 * @param ainame
	 *            投保人姓名
	 * @param aisex
	 *            投保人性别 -1保密 1 男 0 女
	 * @param aiidnumber
	 *            证件号
	 * @param aimobile
	 *            投保人电话
	 * @param aiemail
	 *            电子邮件
	 * @param aiaddress
	 *            投保人地址 可以为空
	 * @param aiflightnumber
	 *            航班号
	 * @return
	 */
	public String payOrderAgain(String orderid, String ainame, String aisex,
			String aiidnumber, String aimobile, String aiemail,
			String aiaddress, String aiflightnumber);
}

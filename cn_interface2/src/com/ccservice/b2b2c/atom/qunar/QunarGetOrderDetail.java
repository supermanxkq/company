package com.ccservice.b2b2c.atom.qunar;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.interticket.HttpClient;
import com.tenpay.util.MD5Util;

/**
 * 
 * @author wzc 获取订单信息接口
 *         http://y8dn.trade.qunar.com/ota/#/ota/order/toOtaOrderList
 *         接口访问url:http://y8dn.trade.qunar.com/api/ota/otaQueryOrder
 *         测试接口url：http://y8dn.trade.test.qunar.com/api/ota/otaQueryOrder
 *         测试：signKey:E9AF29EEB47B4966AB91CD30F746F5EE 
 *         正式：signKey:758DF6BB4DA8467FBA4BC479AB72E887
 *         接口访问频率说明：最快支持代理商每10秒种查询一次
 */
public class QunarGetOrderDetail extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// http://localhost:8080/cn_interface/QunarGetOrderDetail?type=otaQueryOrder&fromDate=20121218000000&toDate=20121221000000
		// http://localhost:8080/cn_interface/QunarGetOrderDetail?type=otaOpt&orderNum=y2dw121220182940706&opt=2&money=2000&remark=www
		// 设置编码方式
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String type = req.getParameter("type");
		PrintWriter out = resp.getWriter();
		String result="";
		if (type != null && !"".equals(type)) {
			if ("otaQueryOrder".equals(type)) {
				String fromDate = req.getParameter("fromDate");// yyyyMMddHHmmss
				String toDate = req.getParameter("toDate");// yyyyMMddHHmmss
				System.out.println(fromDate + "----" + toDate);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				try {
					result=this.otaQueryOrder(sdf.parse(fromDate), sdf.parse(toDate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if ("otaOpt".equals(type)) {
				Map<String, String> optMap = new HashMap<String, String>();
				optMap.put("1", "CONFIRM_ROOM_SUCCESS");// 确认有房
				optMap.put("2", "CONFIRM_ROOM_FAILURE");// 确认无房
				optMap.put("3", "ARRANGE_ROOM");// 安排房间
				optMap.put("4", "AGREE_UNSUBSCRBE");// 同意退订
				optMap.put("5", "REFUSE_UNSUBSCRIBE");// 拒绝退订
				optMap.put("6", "ADD_REMARKS");// 添加备注
				String orderNum = req.getParameter("orderNum");
				String opt = req.getParameter("opt");
				String money = req.getParameter("money");
				String remark = req.getParameter("remark");
				this.otaOpt(orderNum, optMap.get(opt), money, remark);
			} else {
				System.out.println("传入的type值不合法,请核对后再次请求....");
			}
		} else {
			System.out.println("请传入type的值!!!");
		}
		out.write(result);
		out.flush();
		out.close();

	}

	/**
	 * 统一接口代理商获取订单信息接口 huc
	 * 
	 * @param fromDate
	 * @param toDate
	 */
	public String otaQueryOrder(Date fromDate, Date toDate) {
		System.out.println(fromDate + "----" + toDate);
		String signKey = "758DF6BB4DA8467FBA4BC479AB72E887";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String startDate = "";
		String endDate = "";
		String str = "";
		str += signKey;
		String url = "http://223.4.155.3:8034/cn_interface/QunarOrder.jsp?api=otaQueryOrder&";
		if (fromDate != null) {
			startDate = sdf.format(fromDate);
			str += startDate;
			url += "fromDate=" + startDate;
		}
		if (toDate != null) {
			if (fromDate != null) {
				url += "&";
			}
			endDate = sdf.format(toDate);
			str += endDate;
			url += "toDate=" + endDate;
		}
		System.out.println("[统一接口代理商获取订单信息接口]MD5未加密前:" + str);
		String hmac = MD5Util.MD5Encode(str, "utf-8");

		if (fromDate != null || toDate != null) {
			url += "&";
		}
		url += "hmac=" + hmac;
		System.out.println("[统一接口代理商获取订单信息接口]请求路径:" + url);
		String responseStr = HttpClient.httpget(url, "utf-8");
		return responseStr;
		/**
		if (responseStr != null) {
			System.out.println("[统一接口代理商获取订单信息接口]接收json格式数据:"
					+ responseStr.trim());

			JSONObject js = JSONObject.fromObject(responseStr.trim());
			String ret = js.getString("ret");
			if ("false".equals(ret)) {
				String errMsg = js.getString("errMsg");
				System.out.println("[统一接口代理商获取订单信息接口]请求失败,失败原因:" + errMsg);
			} else if ("true".equals(ret)) {
				String totalSize = js.getString("totalSize");
				System.out.println("[统一接口代理商获取订单信息接口]返回个数:" + totalSize);
				JSONArray array = js.getJSONArray("data");
				for (int i = 0; i < array.size(); i++) {
					JSONObject jsonObject = array.getJSONObject(i);
					String orderNum = jsonObject.getString("orderNum");// 订单号
					String statusCode = jsonObject.getString("statusCode");// 去哪儿网系统订单状态码
					String statusMsg = jsonObject.getString("statusMsg");// 订单状态名称
					String payTypeCode = jsonObject.getString("payTypeCode");// 去哪儿网系统支付方式代码
					String payTypeMsg = jsonObject.getString("payTypeMsg");// 支付方式（0-预付、1-前台现付）
					String roomNum = jsonObject.getString("roomNum");// 预定房间数
					String payMoney = jsonObject.getString("payMoney");// 支付金额
					String cityName = jsonObject.getString("cityName");// 城市名称
					String hotelId = jsonObject.getString("hotelId");// 代理商酒店id
					String roomId = jsonObject.getString("roomId");// 代理商房型id
					String orderdate = jsonObject.getString("orderDate");// 订单创建时间，格式为YYYYMMDDHHMMSS，比如20120316111508
					String hotelName = jsonObject.getString("hotelName");// 酒店名称
					String roomName = jsonObject.getString("roomName");// 房型名称
					String checkInDate = jsonObject.getString("checkInDate");// 入住日期，格式为YYYYMMDD，比如20120316
					String checkOutDate = jsonObject.getString("checkOutDate");// 离店日期，格式为YYYYMMDD，比如20120316
					String arriveTime = jsonObject.getString("arriveTime");// 最晚到店时间，格式为：”14:00”，或者”次日00:00”、”次日06:00”
					String customerName = jsonObject.getString("customerName");// 入住人
					String contactName = jsonObject.getString("contactName");// 联系人姓名
					String contactPhone = jsonObject.getString("contactPhone");// 联系人手机
					String contactEmail = jsonObject.getString("contactEmail");// 联系人email
					String request = jsonObject.getString("request");// 特殊要求

					try {
						JSONArray remarkArray = jsonObject.getJSONArray("remark");// TODO
						for (int j = 0; j < remarkArray.size(); j++) {
							remarkArray.get(j);// 包括备注添加时间和备注内容。具体格式为：“备注时间
												// 备注内容”。备注时间格式为YYYY-MM-DD
												// HH：MM：SS，比如：2012-03-16 11:15:08
						}
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
					
					String customerIp = jsonObject.getString("customerIp");// 客人ip
					JSONArray logsArray = jsonObject.getJSONArray("logs");// TODO
																			// 日志
					for (int j = 0; j < logsArray.size(); j++) {
						JSONObject logsJsonObject = logsArray.getJSONObject(j);
						String operator = logsJsonObject.getString("operator");// 操作员
						String opTime = logsJsonObject.getString("opTime");// 操作时间，格式为YYYYMMDDHHMMSS，比如20120316111508
						String content = logsJsonObject.getString("content");// 操作描述

					}
					String everyDayPrice = jsonObject
							.getString("everyDayPrice");// 每日价格 "everyDayPrice":
														// "[{\"date\":\"2012-06-23\",\"price\":\"815\",\"roomStatus\":\"0\"}]"
					JSONArray everyDayPriceArr = JSONArray
							.fromObject(everyDayPrice);
					JSONObject everyDayPriceObj = (JSONObject) everyDayPriceArr
							.get(0);
					String date = everyDayPriceObj.getString("date");// 日期，格式为YYYY-MM-DD，比如：2012-07-12
					String price = everyDayPriceObj.getString("price");// 当日价格
					String roomStatus = everyDayPriceObj
							.getString("roomStatus");// 1为关房，0为开房

				}
			} else {
				System.out.println("[统一接口代理商获取订单信息接口]返回结果信息格式有误!!!!!"
						+ responseStr);
			}
		} else {
			System.out.println("[统一接口代理商获取订单信息接口]没有返回数据!!!!!");

		}
	*/
	}

	/**
	 * 向qunar同步订单状态接口 huc
	 * 
	 * @param orderNum
	 *            String 是 订单编号
	 * @param opt
	 *            String 是 订单操作 opt参数 备注 CONFIRM_ROOM_SUCCESS 确认有房
	 *            CONFIRM_ROOM_FAILURE 确认无房 ARRANGE_ROOM 安排房间 AGREE_UNSUBSCRBE
	 *            同意退订 REFUSE_UNSUBSCRIBE 拒绝退订 ADD_REMARKS 添加备注
	 * 
	 * @param hmac
	 *            String 是 对参数利用一个signKey进行md5编码并转换为一个十六进制的字符串
	 * @param money
	 *            String 否 退款金额，只有在同意退款并且退款金额与订单总价不同时才填写
	 * @param remark
	 *            String 否 备注信息，同意退订时可填写备注信息，添加备注时需要填写备注信息
	 */
	public void otaOpt(String orderNum, String opt, String money, String remark) {
		System.out.println("orderNum:" + orderNum + " opt:" + opt + " money:"
				+ money + " remark:" + remark);
		String signKey = "E9AF29EEB47B4966AB91CD30F746F5EE";
		String str = "";
		String url = "http://123.196.114.122:8034/cn_interface/QunarOrder.jsp?api=otaOpt&";

		if (orderNum == null || "".equals(orderNum) || opt == null
				|| "".equals(opt)) {
			System.out.println("[向qunar同步订单状态接口]必须参数不应传空!!!!!");
			return;
		}
		str += signKey + orderNum + opt;

		if (money != null && !"".equals(money)) {
			str += money;
		}
		System.out.println("[向qunar同步订单状态接口]MD5未加密前:" + str);
		String hmac = MD5Util.MD5Encode(str, "utf-8");

		url += "hmac=" + hmac + "&orderNum=" + orderNum + "&opt=" + opt;
		if (money != null && !"".equals(money)) {
			url += "&money=" + money;
		}
		if (remark != null && !"".equals(remark)) {
			url += "&remark=" + remark;
		}
		System.out.println("[向qunar同步订单状态接口]请求路径:" + url);
		String responseStr = HttpClient.httpget(url, "utf-8");
		if (responseStr != null) {
			System.out.println("[向qunar同步订单状态接口]接收json格式数据:"
					+ responseStr.trim());
			JSONObject js = JSONObject.fromObject(responseStr.trim());
			String ret = js.getString("ret");
			if ("false".equals(ret)) {
				String errMsg = "";
				JSONArray errMsgArray = js.getJSONArray("errorMsg");//
				for (int j = 0; j < errMsgArray.size(); j++) {
					errMsg += errMsgArray.get(j);//
				}
				System.out.println("[向qunar同步订单状态接口]请求失败,失败原因:" + errMsg);
			} else if ("true".equals(ret)) {

				String statusCode = js.getString("statusCode");
				String statusDesc = js.getString("statusDesc");
				System.out.println("[向qunar同步订单状态接口]statusCode:" + statusCode
						+ " statusDesc:" + statusDesc);
			} else {
				System.out.println("[向qunar同步订单状态接口]返回结果信息格式有误!!!!!"
						+ responseStr);
			}
		} else {
			System.out.println("[向qunar同步订单状态接口]没有返回数据!!!!!");

		}

	}

	public static void main(String[] args) {

		// ------------------------huc-----测试qunarGetOrderDetail--------------Start-------
		Calendar calendar = Calendar.getInstance();
		Date toDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, -1); // 昨天
		Date fromDate = calendar.getTime();
		// new QunarGetOrderDetail().otaQueryOrder(fromDate, toDate);

		// ------------------------huc-----测试qunarGetOrderDetail--------------end-------

		// ------------------------huc-----测试otaOpt--------------Start-------
		// new QunarGetOrderDetail().otaOpt("y2dw121220182940706"
		// ,"ARRANGE_ROOM","","");
		// ------------------------huc-----测试otaOpt--------------end-------

	}

}

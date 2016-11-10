package com.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.utils.HttpUtils;
import com.test.utils.PropertyUtil;
import com.test.utils.Utils;

/**
 * Servlet implementation class AnalysisServlet
 */

/**
 * @ClassName: AnalysisServlet
 * @Description: 阿里内存重构解析
 * @author xukaiqiang
 * @date 2016年11月7日 上午10:11:29
 * @modifier
 * @modify-date 2016年11月7日 上午10:11:29
 * @version 1.0
 */

public class KPITrainOrderBeBespeakServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public KPITrainOrderBeBespeakServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 数据传输数组
		JSONArray array = new JSONArray();
		String param = request.getParameter("type");
		// alibaba meiTuan
		if (param.equals("302")) {
			array = alibabaAndMeiTuan();
		}
		// 过期扫描订单
		if (param.equals("303")) {
			array = invalidOrder();
		}
		// 抢票内存统计消费者
		if (param.equals("304")) {
			array = ticketMemory();
		}
		// 淘宝抢票查询列队
		if (param.equals("305")) {
			array = taoBao();
		}
		// 抢票内存
		if (param.equals("306")) {
			array = Memory();
		}
		// 下单消费者
		if (param.equals("307")) {
			array = placeOrderCustomerResult();
		}
		// rep
		if (param.equals("308")) {
			array = rep();
		}
		if (param.equals("309")) {
			array = serverStatus();
		}
		// 打印json数据到页面
		Utils.printInfo(array, response);
	}

	/**
	 * @Description: 服务器状态
	 * @author 徐凯强
	 * @date 2016年11月8日 上午11:28:01
	 * @param ips
	 * @return
	 */
	private JSONArray serverStatus() {
		JSONArray arrayList = new JSONArray();
		JSONArray array = new JSONArray();
		StringBuffer ret = HttpUtils.submitPost(PropertyUtil.getValue("server_status_url", "Train.properties"), "",
				"utf-8");
		// 获取url字符数组
		String[] ulrs = (ret.toString() + ",http://0.0.0.0:0/trainorder_bespeak/isNormal.jsp")
				.split(",");
		String oldIP = "";
		for (int i = 0; i < ulrs.length; i++) {
			// 替换url
			String url = ulrs[i].trim().replace("iSearch", "isNormal.jsp");
			String newIp = getIpAndPort(url)[0];
			// 根据IP进行分组，IP相同的分到一组
			if (oldIP == "") {
				oldIP = newIp;
			}
			if (!oldIP.equals(newIp)) {
				JSONObject json1 = new JSONObject();
				json1.put("IP", oldIP);
				json1.put("urls", array);
				array = new JSONArray();
				arrayList.add(json1);
				oldIP = newIp;
			}
			// 封装每台服务器的状态
			JSONObject json = new JSONObject();
			json.put("ip", newIp);
			json.put("port", getIpAndPort(url)[1]);
			String aaaaString = HttpUtils.submitGet(url);
			if (null != aaaaString) {
				json.put("value", aaaaString);
			} else {
				json.put("value", "异常");
			}
			array.add(json);
		}
		return arrayList;
	}

	/**
	 * @Description:抢票内存
	 * @author 徐凯强
	 * @date 2016年11月8日 上午10:04:02
	 * @return
	 */
	private JSONArray Memory() {
		JSONArray jsonArray = new JSONArray();
		String[] ips = PropertyUtil.getValue("memory_url", "Train.properties").split(",");
		for (int i = 0; i < ips.length; i++) {
			String[] ipAndPort = getIpAndPort(ips[i]);
			JSONObject jsonObject = new JSONObject();
			if (i < 5) {
				jsonObject.put("name", (i + 1) + "号");
			}
			if (i == 5) {
				jsonObject.put("name", "VIP");
			}
			if (i == 6) {
				jsonObject.put("name", "探针");
			}
			jsonObject.put("ip", ipAndPort[0]);
			jsonObject.put("port", ipAndPort[1]);
			String result = "";
			try {
				result = HttpUtils.submitGet(ips[i]);
				if (null != result) {
					jsonObject.put("result", result);
				} else {
					jsonObject.put("result", "异常");
				}
			} catch (Exception e) {
				jsonObject.put("result", "异常");
				e.printStackTrace();
			}
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}

	/**
	 * @Description: 下单消费者
	 * @author 徐凯强
	 * @date 2016年11月8日 上午9:29:05
	 * @return
	 */
	private JSONArray placeOrderCustomerResult() {
		JSONArray jsonArray = new JSONArray();
		String[] ips =PropertyUtil.getValue("placeOrderCustomerResult_url", "Train.properties").split(",");

		for (int i = 0; i < ips.length; i++) {
			String[] ipAndPort = getIpAndPort(ips[i]);
			JSONObject json = new JSONObject();
			if (i == 0 || i == 1) {
				json.put("name", "普通下单消费者");
			} else {
				json.put("name", "VIP下单消费者");
			}
			json.put("ip", ipAndPort[0]);
			json.put("port", ipAndPort[1]);
			String result = "";
			try {
				result = Utils.filterNum(HttpUtils.submitGet(ips[i]));
				if (null != result) {
					json.put("result", result);
				} else {
					json.put("result", "异常");
				}
			} catch (Exception e) {
				e.printStackTrace();
				json.put("result", "异常");
			}
			jsonArray.add(json);
		}
		return jsonArray;
	}

	/**
	 * @Description: REP
	 * @author 徐凯强
	 * @date 2016年11月8日 上午9:15:26
	 * @return
	 */
	private JSONArray rep() {
		String url = PropertyUtil.getValue("rep_url", "Train.properties");
		JSONArray jsonArray = new JSONArray();
		JSONObject json = new JSONObject();
		String[] ipAndPort = getIpAndPort(url);
		json.put("ip", ipAndPort[0]);
		json.put("port", ipAndPort[1]);
		String result = "";
		try {
			result = HttpUtils.submitGet(url);
			if (result != null) {
				json.put("result", result);
			} else {
				json.put("result", "异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (result == null) {
				json.put("result", "异常");
			}
		}
		jsonArray.add(json);
		return jsonArray;
	}

	/**
	 * @Description: 抢票内存统计消费者
	 * @author 徐凯强
	 * @date 2016年11月8日 上午9:03:09
	 * @return
	 */
	private JSONArray ticketMemory() {
		String url = PropertyUtil.getValue("ticket_memory_url","Train.properties");
		// 抢票内存统计消费者
		String[] ipAndPort = getIpAndPort(url);
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		json.put("ip", ipAndPort[0]);
		json.put("port", ipAndPort[1]);
		String result = "";
		try {
			result = HttpUtils.submitGet(url);
			if (null != result) {
				json.put("result", result);
			} else {
				json.put("result", "异常");
			}
		} catch (Exception e) {
			json.put("result", "异常");
			e.printStackTrace();
		}

		array.add(json);
		return array;
	}

	/**
	 * @Description: alibabaMeituan
	 * @author 徐凯强
	 * @date 2016年11月7日 下午5:44:45
	 * @param ips
	 * @return返回Data对象，可能是真实数据，也有可能是异常信息
	 */
	private JSONArray alibabaAndMeiTuan() {
		String[] ips =PropertyUtil.getValue("alibaba_meituan_url","Train.properties").split(",");
		JSONArray array = new JSONArray();
		for (int i = 0; i < ips.length; i++) {
			String[] ipAndPort = getIpAndPort(ips[i]);
			String result = "";
			JSONObject json = new JSONObject();
			json.put("ip", ipAndPort[0]);
			json.put("port", ipAndPort[1]);
			try {
				result = HttpUtils.submitGet(ips[i]);
				if (null != result) {
					if (i == 0) {
						json.put("name", "阿里内存重构");
					}
					if (i == 1) {
						json.put("name", "美团内存重构");
					}
					json.put("result", result);
				} else {
					json.put("result", "异常");
				}
			} catch (Exception e) {
				json.put("result", "异常");
			}
			array.add(json);
		}
		return array;
	}

	/**
	 * @Description: 过期扫描服务
	 * @author 徐凯强
	 * @date 2016年11月8日 上午8:35:37
	 */

	private JSONArray invalidOrder() {
		JSONArray jsonArray = new JSONArray();
		String[] ips =PropertyUtil.getValue("invalid_order_url","Train.properties").split(",");
		for (int i = 0; i < ips.length; i++) {
			String[] ipAndPort = getIpAndPort(ips[i]);
			JSONObject json = new JSONObject();
			String result = "";
			try {
				result = HttpUtils.submitGet(ips[i]);
				if (null != result) {
					json.put("result", result);
				} else {
					json.put("result", "异常");
				}
			} catch (Exception e) {
				json.put("result", "异常");
				e.printStackTrace();
			}

			json.put("ip", ipAndPort[0]);
			json.put("port", ipAndPort[1]);
			jsonArray.add(json);
		}
		return jsonArray;
	}

	/**
	 * @Description: 淘宝抢票查询列队
	 * @author 徐凯强
	 * @date 2016年11月8日 上午8:55:36
	 */
	private JSONArray taoBao() {
		JSONArray array = new JSONArray();
		String url = PropertyUtil.getValue("taobao_url","Train.properties");
		JSONObject json = new JSONObject();
		String[] ipAndPort = getIpAndPort(url);
		json.put("port", ipAndPort[1]);
		json.put("ip", ipAndPort[0]);
		String taoBaoResult = "";
		try {
			taoBaoResult = HttpUtils.submitGet(url);
			if (null != taoBaoResult) {
				json.put("result", taoBaoResult);
			} else {
				json.put("result", "异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", "异常");
		}
		array.add(json);
		return array;
	}

	/**
	 * @Description: 根据url获取ip
	 * @author 徐凯强
	 * @date 2016年11月7日 下午6:58:17
	 * @param url
	 */
	private static String[] getIpAndPort(String url) {
		String ip = "";
		String port = "";
		String regex = "//(.*?):([0-9]+)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(url);
		while (m.find()) {
			ip = m.group(1);
			port = m.group(2);
		}
		String[] data = { ip, port };
		return data;
	}
}

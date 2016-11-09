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

public class AnalysisServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AnalysisServlet() {
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
	 * Class Name: AnalysisServlet.java
	 * 
	 * @Description: 服务器状态
	 * @author xukaiqiang
	 * @date 2016年11月8日 上午11:28:01
	 * @modifier
	 * @modify-date 2016年11月8日 上午11:28:01
	 * @version 1.0
	 * @param ips
	 * @return
	 */
	private JSONArray serverStatus() {
		JSONArray arrayList = new JSONArray();
		JSONArray array = new JSONArray();
		StringBuffer ret = HttpUtils.submitPost(
				"http://tu.hangtian123.net/WebSearch/queryCustomer.jsp", "",
				"utf-8");
		String[] ulrs = ret.toString().split(",");
		String IP = "";
		for (int i = 0; i < ulrs.length; i++) {
			String url = ulrs[i].trim().replace("iSearch", "isNormal.jsp");
			String ip = getIpAndPort(url)[0];
			if (IP == "") {
				IP = ip;
			}
			if (!IP.equals(ip)) {
				JSONObject json = new JSONObject();
				json.put("IP", IP);
				json.put("urls", array);
				array = new JSONArray();
				arrayList.add(json);
				IP = ip;
			}
			JSONObject json = new JSONObject();
			json.put("ip", ip);
			json.put("port", getIpAndPort(url)[1]);
			String  aaaaString=HttpUtils.submitGet(url);
			if(null!=aaaaString){
				json.put("value", aaaaString);
			}else{
				json.put("value", "异常");
			}
			array.add(json);
		}
		return arrayList;
	}

	/**
	 * Class Name: AnalysisServlet.java
	 * 
	 * @Description:抢票内存
	 * @author xukaiqiang
	 * @date 2016年11月8日 上午10:04:02
	 * @modifier
	 * @modify-date 2016年11月8日 上午10:04:02
	 * @version 1.0
	 * @return
	 */
	private JSONArray Memory() {
		JSONArray jsonArray = new JSONArray();
		String[] ips = {
				"http://121.199.1.76:2001/trainorder_bespeak/view_data.jsp",
				"http://120.26.81.198:2101/trainorder_bespeak/view_data.jsp",
				"http://121.199.51.62:2201/trainorder_bespeak/view_data.jsp",
				"http://121.40.130.195:2301/trainorder_bespeak/view_data.jsp",
				"http://43.241.234.88:2001/trainorder_bespeak/view_data.jsp",
				"http://120.26.81.198:2401/trainorder_bespeak/view_data.jsp",
				"http://121.199.51.62:4199/trainorder_bespeak/view_data.jsp" };
		for (int i = 0; i < ips.length; i++) {
			String[] ipAndPort = getIpAndPort(ips[i]);
			JSONObject jsonObject = new JSONObject();
			if (i < 5) {
				jsonObject.put("name", (i + 1) + "号内存");
			}
			if (i == 5) {
				jsonObject.put("name", "VIP内存");
			}
			if (i == 6) {
				jsonObject.put("name", "探针内存");
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
	 * Class Name: AnalysisServlet.java
	 * 
	 * @Description: 下单消费者
	 * @author xukaiqiang
	 * @date 2016年11月8日 上午9:29:05
	 * @modifier
	 * @modify-date 2016年11月8日 上午9:29:05
	 * @version 1.0
	 * @return
	 */
	private JSONArray placeOrderCustomerResult() {
		JSONArray jsonArray = new JSONArray();
		String[] ips = {
				"http://121.41.51.7:29134/trainorder_bespeak/mq/MqTrainCreateOrder.jsp?type=0",
				"http://121.41.51.7:29234/trainorder_bespeak/mq/MqTrainCreateOrder.jsp?type=0",
				"http://121.41.51.7:49034/trainorder_bespeak/mq/MqTrainCreateOrder.jsp?type=0",
				"http://121.41.51.7:49134/trainorder_bespeak/mq/MqTrainCreateOrder.jsp?type=0",
				"http://121.41.51.7:49234/trainorder_bespeak/mq/MqTrainCreateOrder.jsp?type=0" };

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
	 * Class Name: AnalysisServlet.java
	 * 
	 * @Description: REP
	 * @author xukaiqiang
	 * @date 2016年11月8日 上午9:15:26
	 * @modifier
	 * @modify-date 2016年11月8日 上午9:15:26
	 * @version 1.0
	 * @return
	 */
	private JSONArray rep() {
		String url = "http://121.199.51.62:2201/trainorder_bespeak/isNormal.jsp";
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
	 * Class Name: AnalysisServlet.java
	 * 
	 * @Description: 抢票内存统计消费者
	 * @author xukaiqiang
	 * @date 2016年11月8日 上午9:03:09
	 * @modifier
	 * @modify-date 2016年11月8日 上午9:03:09
	 * @version 1.0
	 * @return
	 */
	private JSONArray ticketMemory() {
		String url = "http://121.40.130.195:19078/trainorder_bespeak/isNormal.jsp";
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
	 * Class Name: AnalysisServlet.java
	 * 
	 * @Description: alibabaMeituan
	 * @author xukaiqiang
	 * @date 2016年11月7日 下午5:44:45
	 * @modifier
	 * @modify-date 2016年11月7日 下午5:44:45
	 * @version 1.0
	 * @param ips
	 * @return返回Data对象，可能是真实数据，也有可能是异常信息
	 */
	private JSONArray alibabaAndMeiTuan() {
		String[] ips = {
				"http://121.199.1.76:3001/trainorder_bespeak/isNormal.jsp",
				"http://43.241.234.88:3001/trainorder_bespeak/isNormal.jsp" };
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
	 * Class Name: AnalysisServlet.java
	 * 
	 * @Description: 过期扫描服务
	 * @author xukaiqiang
	 * @date 2016年11月8日 上午8:35:37
	 * @modifier
	 * @modify-date 2016年11月8日 上午8:35:37
	 * @version 1.0
	 * @return
	 */

	private JSONArray invalidOrder() {
		JSONArray jsonArray = new JSONArray();
		String[] ips = {
				"http://120.26.81.198:9077/trainorder_bespeak/isNormal.jsp",
				"http://120.26.81.198:9066/trainorder_bespeak/isNormal.jsp" };
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
	 * Class Name: AnalysisServlet.java
	 * 
	 * @Description: 淘宝抢票查询列队
	 * @author xukaiqiang
	 * @date 2016年11月8日 上午8:55:36
	 * @modifier
	 * @modify-date 2016年11月8日 上午8:55:36
	 * @version 1.0
	 * @return
	 */
	private JSONArray taoBao() {
		JSONArray array = new JSONArray();
		String url = "http://121.40.130.195:19044/trainorder_bespeak/isNormal.jsp";
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
	 * Class Name: AnalysisServlet.java
	 * 
	 * @Description: 根据url获取ip
	 * @author xukaiqiang
	 * @date 2016年11月7日 下午6:58:17
	 * @modifier
	 * @modify-date 2016年11月7日 下午6:58:17
	 * @version 1.0
	 * @param url
	 * @return
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

	public static void main(String[] args) {
		JSONArray arrayList = new JSONArray();
		JSONArray array = new JSONArray();
		StringBuffer ret = HttpUtils.submitPost(
				"http://tu.hangtian123.net/WebSearch/queryCustomer.jsp", "",
				"utf-8");
		String[] ulrs = ret.toString().split(",");
		String IP = "";
		for (int i = 0; i < ulrs.length; i++) {
			String url = ulrs[i].trim().replace("iSearch", "isNormal.jsp");
			String ip = getIpAndPort(url)[0];
			if (IP == "") {
				IP = ip;
			}
			if (!IP.equals(ip)) {
				JSONObject json = new JSONObject();
				json.put("IP", IP);
				json.put("urls", array);
				array = new JSONArray();
				arrayList.add(json);
				IP = ip;
			}
			JSONObject json = new JSONObject();
			json.put("ip", ip);
			json.put("port", getIpAndPort(url)[1]);
//			String  aaaaString=HttpUtils.submitGet(url);
//			if(null!=aaaaString){
//				json.put("value", aaaaString);
//			}else{
				json.put("value", "异常");
//			}
			
			array.add(json);
		}
		System.out.println(arrayList.toJSONString());
	}
}

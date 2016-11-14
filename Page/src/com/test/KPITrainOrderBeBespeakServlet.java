package com.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		String type = request.getParameter("type");
		// alibaba meiTuan
		if ("302".equals(type)) {
			array = checkServerStatus("alibaba_meituan_url", type);
		} else if ("303".equals(type)) {
			// 过期扫描订单
			array = checkServerStatus("invalid_order_url", type);
		} else if ("304".equals(type)) {
			// 抢票内存统计消费者
			array = checkServerStatus("ticket_memory_url", type);
		} else if ("305".equals(type)) {
			// 淘宝抢票查询列队
			array = checkServerStatus("taobao_url", type);
		} else if ("306".equals(type)) {
			// 抢票内存
			array = checkServerStatus("memory_url", type);
		} else if ("307".equals(type)) {
			// 下单消费者
			array = checkServerStatus("placeOrderCustomerResult_url", type);
		} else if ("308".equals(type)) {
			// rep
			array = checkServerStatus("rep_url", type);
		} else if ("309".equals(type)) {
			// 服务器状态
			array = checkServerStatus("server_status_url", type);
		}
		// 打印json数据到页面
		Utils.printInfo(array, response);
	}

	/**
	 * @Description: 传入配置文件的key值和请求的类型type获取要返回的服务器状态jsonArray
	 * @author 徐凯强
	 * @date 2016年11月11日 下午5:38:41
	 * @param property_key配置文件键值
	 * @param type请求的类型
	 *            ，例如：点击抢票内存统计消费者，点击淘宝抢票查询列队
	 * @return
	 */
	private static JSONArray checkServerStatus(String property_key, String type) {
		// 要返回的对象
		JSONArray serverStatusArray = new JSONArray();
		// 从Train.properties获取serverUrls数组
		String[] serverUrls = {};
		// 进行分组的map
		Map<String, List<JSONObject>> resultMap = new HashMap<String, List<JSONObject>>();
		// 所有查询消费者
		if ("309".equals(type)) {
			StringBuffer ret = HttpUtils.submitPost(PropertyUtil.getValue(
					"server_status_url", "Train.properties"), "", "utf-8");
			// 获取url字符数组
			serverUrls = ret.toString().split(",");
		} else {
			serverUrls = PropertyUtil
					.getValue(property_key, "Train.properties").split("\\,");
		}

		// 遍历serverUrls
		for (int i = 0; i < serverUrls.length; i++) {
			JSONObject serverStatusJsonObj = new JSONObject();
			String serverUrl = "";

			// 如果type为309,将所有的url中iSearch替换成isNormal.jsp
			if ("309".equals(type)) {
				// 替换url
				serverUrl = serverUrls[i].trim().replace("iSearch",
						"isNormal.jsp");
			} else {
				serverUrl = serverUrls[i];
			}

			// 如果是302,307,306，获取其中的name
			if ("302".equals(type) || "307".equals(type) || "306".equals(type)) {
				String[] serverUrlAndServerName = serverUrls[i].split("\\|");
				// 获取名称
				serverStatusJsonObj.put("name", serverUrlAndServerName[1]);
				// 获取状态
				String serverStatus = "";
				if ("307".equals(type)) {
					serverStatus = Utils.filterNum(HttpUtils
							.submitGet(serverUrlAndServerName[0]));
				} else {
					serverStatus = HttpUtils
							.submitGet(serverUrlAndServerName[0]);
				}
				if (null != serverStatus && !"异常".equals(serverStatus)) {
					serverStatusJsonObj.put("serverStatus", serverStatus);
				} else {
					serverStatusJsonObj.put("serverStatus", "异常");
				}
			}

			if ("303".equals(type) || "305".equals(type) || "308".equals(type)
					|| "304".equals(type) || "309".equals(type)) {
				// 获取状态
				String serverStatus = "";
				serverStatus = HttpUtils.submitGet(serverUrl);
				if (null != serverStatus && !"异常".equals(serverStatus)) {
					serverStatusJsonObj.put("serverStatus", serverStatus);
				} else {
					serverStatusJsonObj.put("serverStatus", "异常");
				}
			}
			// 获取端口,获取IP
			String[] ipAndPort = getIpAndPort(serverUrl);
			// 实例化jsonObject
			serverStatusJsonObj.put("ip", ipAndPort[0]);
			serverStatusJsonObj.put("port", ipAndPort[1]);
			// ip分组
			if ("309".equals(type)) {
				if (resultMap.containsKey(ipAndPort[0])) {
					resultMap.get(ipAndPort[0]).add(serverStatusJsonObj);
				} else {
					List<JSONObject> l = new ArrayList<JSONObject>();
					l.add(serverStatusJsonObj);
					resultMap.put(ipAndPort[0], l);
				}
				serverStatusArray.add(resultMap);
			} else {
				serverStatusArray.add(serverStatusJsonObj);
			}

		}
		return serverStatusArray;
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

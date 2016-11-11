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
		String type = request.getParameter("type");
		// alibaba meiTuan
		if ("302".equals(type)) {
			array = checkServerStatus("alibaba_meituan_url", type);
		} else if ("303".equals(type)) {
			// 过期扫描订单
			array=checkServerStatus("invalid_order_url",type);
		} else if ("304".equals(type)) {
			// 抢票内存统计消费者
			array=checkServerStatus("ticket_memory_url",type);
		} else if ("305".equals(type)) {
			// 淘宝抢票查询列队
			array=checkServerStatus("taobao_url",type);
		} else if ("306".equals(type)) {
			// 抢票内存
			array=checkServerStatus("memory_url",type);
		} else if ("307".equals(type)) {
			// 下单消费者
			array=checkServerStatus("placeOrderCustomerResult_url",type);
		} else if ("308".equals(type)) {
			// rep
			array=checkServerStatus("rep_url",type);
		} else if ("309".equals(type)) {
			// 服务器状态
			array=serverStatus();
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
		StringBuffer ret = HttpUtils.submitPost(PropertyUtil.getValue("server_status_url", "Train.properties"),
				"", "utf-8");
		// 获取url字符数组
		String[] serverUrls = ret.toString().split(",");
		String oldIP = "";
		
		Map<String,JSONObject> list=new HashMap<String,JSONObject>();
		for (int i = 0; i < serverUrls.length; i++) {
			// 替换url
			String newServerUrl = serverUrls[i].trim().replace("iSearch", "isNormal.jsp");
			String [] ipAndPort = getIpAndPort(newServerUrl);
			// 根据IP进行分组，IP相同的分到一组
//			if (oldIP == "") {
//				oldIP = ipAndPort[0];
//			}
//			if (!oldIP.equals(ipAndPort[0])) {
//				JSONObject json1 = new JSONObject();
//				json1.put("IP", oldIP);
//				json1.put("urls", array);
//				array = new JSONArray();
//				arrayList.add(json1);
//				oldIP = ipAndPort[0];
//			}
			// 封装每台服务器的状态
			JSONObject json = new JSONObject();
			json.put("ip", ipAndPort[0]);
			json.put("port", getIpAndPort(newServerUrl)[1]);
			String serverStatus = HttpUtils.submitGet(newServerUrl);
			if (null != serverStatus) {
				json.put("serverStatus", serverStatus);
			} else {
				json.put("serverStatus", "异常");
			}
			array.add(json);
		}
		return arrayList;
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
		JSONArray serverStatusArray = new JSONArray();
		// 从Train.properties获取serverUrls数组
		String[] serverUrls = PropertyUtil.getValue(property_key,
				"Train.properties").split("\\,");
		//遍历serverUrls
		for (int i = 0; i < serverUrls.length; i++) {
			JSONObject serverStatusJsonObj = new JSONObject();
			if ("302".equals(type)||"307".equals(type)||"306".equals(type)) {
				String [] serverUrlAndServerName = serverUrls[i].split("\\|");
				// 获取名称
				serverStatusJsonObj.put("name", serverUrlAndServerName[1]);
				// 获取状态
				try {
					String serverStatus="";
					if("307".equals(type)){
						serverStatus = Utils.filterNum(HttpUtils.submitGet(serverUrlAndServerName[0]));	
					}else{
						serverStatus = HttpUtils.submitGet(serverUrlAndServerName[0]);
					}
					if(null!=serverStatus){
						serverStatusJsonObj.put("serverStatus", serverStatus);
					}else{
						serverStatusJsonObj.put("serverStatus", "异常");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 获取端口,获取IP
			String[] ipAndPort = getIpAndPort(serverUrls[i]);
			// 实例化jsonObject
			serverStatusJsonObj.put("ip", ipAndPort[0]);
			serverStatusJsonObj.put("port", ipAndPort[1]);
			if("303".equals(type)||"305".equals(type)||"308".equals(type)||"304".equals(type)){
				// 获取状态
				try {
					String serverStatus="";
						serverStatus = HttpUtils.submitGet(serverUrls[i]);
					if(null!=serverStatus){
						serverStatusJsonObj.put("serverStatus", serverStatus);
					}else{
						serverStatusJsonObj.put("serverStatus", "异常");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			serverStatusArray.add(serverStatusJsonObj);
		}
		return serverStatusArray;
	}

}

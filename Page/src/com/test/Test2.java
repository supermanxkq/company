package com.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;
import com.test.utils.HttpUtils;
import com.test.utils.PropertyUtil;

public class Test2 {
	public static void main(String[] args) {
		
		JSONArray    array=new JSONArray();
		StringBuffer ret = HttpUtils.submitPost(
				PropertyUtil.getValue("server_status_url", "Train.properties"),
				"", "utf-8");
		// 获取url字符数组
		String[] serverUrls = ret.toString().split(",");
		Map<String, List<JSONObject>> map = new HashMap<String, List<JSONObject>>();
		for (int i = 0; i < serverUrls.length; i++) {
			// 替换url
			String newServerUrl = serverUrls[i].trim().replace("iSearch",
					"isNormal.jsp");
			String[] ipAndPort = getIpAndPort(newServerUrl);

			JSONObject json = new JSONObject();
			json.put("ip", ipAndPort[0]);
			json.put("port", ipAndPort[1]);
			try {
				String serverStatus = HttpUtils.submitGet(newServerUrl);
				if (null != serverStatus) {
					json.put("serverStatus", serverStatus);
				} else {
					json.put("serverStatus", "异常");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (map.containsKey(ipAndPort[0])) {
				map.get(ipAndPort[0]).add(json);
			} else {
				List<JSONObject> l = new  ArrayList<JSONObject>();
				l.add(json);
				map.put(ipAndPort[0], l);
			}
			array.add(map);
			System.out.println(map);
		}
		System.out.println(array);
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

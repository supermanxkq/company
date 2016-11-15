package com.ccservice.b2b2c.atom.servlet.tuniu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.callback.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;

public class TuniuTestSearchStatus {
	public static void main(String[] args) {
		 String url =
		 "http://localhost:9016/cn_interface/trainTicket/searchStatusFrom12306";
//		String url = "http://trainorder.test.hangtian123.net/cn_interface/trainTicket/searchStatusFrom12306";

		JSONObject param = new JSONObject();
		JSONObject ticketNo = new JSONObject();
		JSONArray ticket = new JSONArray();
		JSONArray data = new JSONArray();
		JSONObject dataone = new JSONObject();

		ticketNo.put("ticketNo", "E108927674101001D");
		ticket.add(ticketNo);
		dataone.put("orderId", "33168");
		dataone.put("orderNumber", "E108927674");
		dataone.put("vendorOrderId", "T1606131447348801281");
		dataone.put("ticket", ticket);
		// dataone.put("userName", "zoushano5gkd5s5t");
		// dataone.put("password", "asd123456");
		data.add(dataone);
		String datas = "";
		try {
			datas = TuNiuDesUtil.encrypt(data.toJSONString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		param.put("account", "testAccount");
		param.put("sign", "testsign");
		param.put("timestamp", "2016-06-08 14:39:00");
		param.put("data", datas);
		String jsonFromTuniu = param.toJSONString();
		System.out.println(SendPostandGet.submitPostTimeOut(url, jsonFromTuniu,
				"utf-8", 5 * 60 * 1000));
	}
}

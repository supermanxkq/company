package com.ccservice.b2b2c.atom.servlet.tuniu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.callback.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;

public class TestRefundNotice {
	public static void main(String[] args) {
		String url = "http://localhost:9016/cn_interface/train/refundNotice";
		JSONObject param = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray tickets = new JSONArray();
		tickets.add("E0104002221053132");
		tickets.add("E5449442891120094");
		data.put("vendorOrderId", "000");
		data.put("orderId", "00");
		data.put("orderNumber", "00");
		data.put("tickets", tickets);
		String datas = "";
		try {
			datas = TuNiuDesUtil.encrypt(data.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		param.put("account", "testAccount");
		param.put("sign", "testsign");
		param.put("timestamp", "2016-06-08 14:39:00");
		param.put("data", datas);
		String jsonFromTuniu = param.toString();
		String s = (SendPostandGet.submitPostTimeOut(url, jsonFromTuniu,
				"utf-8", 5 * 60 * 1000)).toString();
		JSONObject sj = JSONObject.parseObject(s);
		try {
			String ssss = TuNiuDesUtil.decrypt(sj.get("data").toString());
			System.out.println(ssss);
		} catch (Exception e) {
		}
	}
}

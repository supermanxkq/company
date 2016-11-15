package com.ccservice.b2b2c.atom.servlet.tuniu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.callback.SendPostandGet;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;

public class TestTuNiuQueryRefund {
	public static void main(String[] args) {
		String url = "http://localhost:9016/cn_interface/train/refund/query";
		JSONObject param = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("vendorOrderId", "T160606021AEC330A24204468088DC0FC86BC80D213");
		data.put("orderId", "20160606112448933_cd_1465183488933");
		//T160603D6247867021700443D08DF1052C6BB017907
		//20160603165637591_cd_1464944197591
		
		//T160603F3D5B95F0581D040E1093D4002477E3AB7EA
		//20160603153837712_cd_1464939517712
		
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

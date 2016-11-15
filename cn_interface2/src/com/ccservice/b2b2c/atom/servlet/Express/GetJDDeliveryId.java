package com.ccservice.b2b2c.atom.servlet.Express;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.SendPostandGet;

public class GetJDDeliveryId {
	
	/**
	 * 获取京东订单号
	 * @param num	获取几个
	 */
	public static void getDeliveryId(int num){
		for (int i = 0; i < num; i++) {
			String url="http://120.26.100.206:12345/JD/jdgetwaybill";//正式
            String param="number=1";
            String resultjson;
			try {
				resultjson = SendPostandGet.submitPost(url, param, "UTF-8").toString();
				JSONObject json = JSONObject.parseObject(resultjson);
				int code = json.getJSONObject("jingdong_etms_waybillcode_get_responce").getJSONObject("resultInfo").getIntValue("code");
				String deliveryId = json.getJSONObject("jingdong_etms_waybillcode_get_responce").getJSONObject("resultInfo").getString("deliveryIdList");
				deliveryId = deliveryId.replace("[", "").replace("]", "").replace("\"", "");
				if (code == 100 && deliveryId != null && !"".equals(deliveryId)) {
					insertJD(deliveryId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void insertJD(String deliveryId){
		String sql = "INSERT INTO JDEXPRESSNUM(EXPRESSNUM) VALUES('" + deliveryId + "')";
		Server.getInstance().getSystemService().findMapResultBySql(sql, null);
	}
	
	public static void main(String[] args) {
		GetJDDeliveryId.getDeliveryId(1);
	}
}

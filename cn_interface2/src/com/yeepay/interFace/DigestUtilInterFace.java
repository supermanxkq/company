package com.yeepay.interFace;

import java.util.Map;

import com.yeepay.util.DigestUtil;


public class DigestUtilInterFace {
	private static LogUtilInterFace logUtil = LogUtilInterFace.createLog(DigestUtilInterFace.class);
	// ��map�����hmac
	public static Map addHmac(String[] HmacOrder, Map map, String keyValue){
		map = formatMap(HmacOrder, map);
		String sbold = DigestUtil.getHmacSBOld(HmacOrder, map);
		logUtil.log("���������"+sbold);
		String hmac = DigestUtil.hmacSign(sbold, keyValue);
		map.put("hmac", hmac);
		logUtil.log("[KeyValue]" + keyValue + ",[sbold]" + sbold + ",[hmac]" + hmac);
		return map;
	}
	// ��ʽ������Map����HmacOrder���еĲ�������Map��û�д˼�ֵ�ԵĻ�����Map����Ӽ�ֵ��
	public static Map formatMap(String[] HmacOrder, Map map){
		String key = "";
		String value = "";
		for(int i = 0; i < HmacOrder.length; i++){
			key = HmacOrder[i];
			value = (String)map.get(key);
			if(value == null){
				map.put(key, "");
			}
		}
		return map;
	}
	// ���map�е�hmac����map�е���HmacOrderΪ����ֵ����ɵ�hmac�Ƿ�һ��
	public static boolean checkHmac(String[] HmacOrder, Map map, String keyValue){
		boolean returnBoolean = false;
		Object hmacObj = map.get("hmac");
		String hmac = (hmacObj == null) ? "" : (String)hmacObj ;
		String sbold = DigestUtil.getHmacSBOld(HmacOrder, map);
		String newHmac = "";
		newHmac = DigestUtil.hmacSign(sbold, keyValue);
		if(hmac.equals(newHmac)){
			returnBoolean = true;
		}
		logUtil.log("[hmac]" + hmac + ",[keyvalue]" + keyValue + ",[sbold]" + sbold + ",[newHmac]" + newHmac);
		return returnBoolean; 
	}
}

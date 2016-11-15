package com.yeepay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// 处理对象的类
public class ProcessUtil {
	private static Log log = LogFactory.getLog(ProcessUtil.class);
	// 从request.getParameterMap取出的Map传入获得所有参数与参数值Map的方法
	public static Map processParameterMap(Map parameterMap){
		Map returnMap = new UpgradeMap();
		java.util.Set set = parameterMap.keySet();
		Object[] objs = set.toArray();
		int index = objs.length;
		String key = "";
		String value = "";
		for(int i = 0; i < index; i++ )
		{
			key = objs[i].toString();
			value = ((String[])parameterMap.get(key))[0]; 
			returnMap.put(key, value);
		}
		return returnMap;
	}
	// 将HttpServletRequest传入获得所有参数与参数值Map的方法
	public static Map processParameterMap(HttpServletRequest request){
		final Map returnMap = new UpgradeMap();
		String key = "";
		String value = "";
		Enumeration names = request.getParameterNames();
		while(names.hasMoreElements()){
			key = (String)names.nextElement();
			value = request.getParameter(key);
			returnMap.put(key, value);
		  }
		return returnMap;
	}
	// 格式化String将null转化为""
	public static String formatString(String str){
		if(str == null){
			str = "";
		}
		return str;
	}

	public static String formatString(Object str){
		String returnString = (String)str;
		if(returnString == null){
			returnString = "";
		}
		return returnString;
	}
	// 中文转码
	public static String chagneStringCode(String str, String beforeChangeCode, String afterChangeCode) throws UnsupportedEncodingException{
		return new String(str.getBytes(beforeChangeCode), afterChangeCode); 
	}
	public static String changeStringCodeDefault(String string, String beforeChangeCode, String afterChangeCode){
		try {
			string = new String(string.getBytes(beforeChangeCode), afterChangeCode);
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}
		return string;
	}
	public static Map changeMapCode(Map parameterMap, String beforeChangeCode, String afterChangeCode){
		Map returnMap = new UpgradeMap();
		Set set = parameterMap.keySet();
		Iterator iterator  = set.iterator();
		String key = "";
		String value = "";
		while(iterator.hasNext()){
			key = (String)iterator.next();
			value = (String)parameterMap.get(key);
			value = changeStringCodeDefault(value, beforeChangeCode, afterChangeCode);
			returnMap.put(key, value);
		}
		return returnMap;
	}
	public static Map urlDecodeMap(Map parameterMap){
		Map returnMap = new UpgradeMap();
		Set set = parameterMap.keySet();
		Iterator iterator  = set.iterator();
		String key = "";
		String value = "";
		while(iterator.hasNext()){
			key = (String)iterator.next();
			value = (String)parameterMap.get(key);
			value = URLDecoder.decode(value);
			returnMap.put(key, value);
		}
		return returnMap;
		
	}
	// 设置固定值的方法
	public static Map setFixParameterValue(Map parameterMap, Map fixParameterMap){
		Object[] keys = fixParameterMap.keySet().toArray();
		int index = keys.length;
		String key = "";
		String value = "";
		for(int i = 0; i < index; i++){
			key = keys[i].toString();
			value = fixParameterMap.get(key).toString();
			parameterMap.put(key, value);
		}
		return parameterMap;
	}
	// 格式化http通讯返回文本流的方法
	public static Map formatReqReturnString(String str, String groupFlag, String keyValueFlag ){
		Map returnMap = new UpgradeMap();
		String[] groups = str.split(groupFlag);
		String[] group = new String[2];
		String key = "";
		String value = "";
		int index = groups.length;

		for(int i = 0; i < index; i++){
			group = groups[i].split(keyValueFlag);
			if(group.length >= 1 ){
				key = group[0];
			}
			if(group.length >= 2 ){
				value = group[1];
			}else{
				value = "";
			}
			returnMap.put(key, value);
		}
		return returnMap; 
	}
	public static long getTime(){
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return currentTime.getTime();
		//return formatter.format(currentTime);
	}
	public static Map changeMapCharset(Map map, String beforeChangeCharsetName, String afterChangeCharsetName){
		if(beforeChangeCharsetName.equals(afterChangeCharsetName)){
			return map;
		}
		Set keySet = map.keySet();
		Object[] objs = keySet.toArray();
		for(int i = 0; i < objs.length; i++){
			String key = objs[i].toString();
			String value = (String) map.get(key);
			if(value == null){
				value = "";
			}else{
				try {
					value = new String(value.getBytes(beforeChangeCharsetName), afterChangeCharsetName);
				} catch (UnsupportedEncodingException uee) {
					uee.printStackTrace();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			System.out.println(key+"="+value);
			
			map.put(key, value);
		}
		return map;
	}
}

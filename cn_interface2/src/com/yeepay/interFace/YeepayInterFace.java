package com.yeepay.interFace;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.yeepay.server.ServerInfo;
import com.yeepay.util.ProcessUtil;
import com.yeepay.util.UpgradeMap;

public class YeepayInterFace {
	public static final String CHECKHMAC = "checkHmac";
	public static final String ResultString = "resultString";
	public static final String Parameter = "parameter";
	public static final String HTTPConnection = "httpConnection";

	public static final String GroupFlag = "\n";
	public static final String KeyValueFlag = "=";

	private static LogUtilInterFace logUtil = LogUtilInterFace.createLog(YeepayInterFace.class);
	
	// 获得通讯的返回文本流
	public static String getRequestBackString(HttpServletRequest request, String keyValue, String url, Map fixParameter, String[] hmacOrder){
		Map parameterMap = ProcessUtil.processParameterMap(request);
		return getRequestBackString(parameterMap, keyValue, url, fixParameter, hmacOrder );
	}
	public static String getRequestBackString(Map parameterMap, String keyValue, String url, Map fixParameter, String[] hmacOrder){
		parameterMap = ProcessUtil.setFixParameterValue(parameterMap, fixParameter);
		parameterMap = DigestUtilInterFace.addHmac(hmacOrder, parameterMap, keyValue);
		String returnString = HttpUtilInterFace.sendRequestDefault(url, parameterMap);
		return returnString;
	}
	
	// 获得通讯的提交返回参数
	public static Map getRequestBackMap(HttpServletRequest request, String keyValue, String url, Map fixParameter, String[] hmacOrder, String[] backHmacOrder){
		logUtil.log("[method]" + request.getMethod() + ",[queryString]" + request.getQueryString() + ",[requestURL]" + request.getRequestURL());
		Map parameterMap = ProcessUtil.processParameterMap(request);
		return getRequestBackMap(parameterMap, keyValue, url, fixParameter, hmacOrder, backHmacOrder);
	}
	//
	public static Map getRequestBackMap(Map parameterMap, String keyValue, String url, Map fixParameter, String[] hmacOrder, String[] backHmacOrder){
		try {
			logUtil.log("[parameterMap]" + new String(parameterMap.toString().getBytes("iso-8859-1"), "gbk"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block

			logUtil.log("[parameterMap]" + parameterMap);
			e.printStackTrace();
		}
		parameterMap = ProcessUtil.changeMapCharset(parameterMap, ServerInfo.getServerCharsetName(), ServerInfo.getYeepayCharsetName());
		String reqResult = getRequestBackString(parameterMap, keyValue, url, fixParameter, hmacOrder);
		logUtil.log(reqResult);
		System.out.println("请求返回结果："+reqResult);
		return getRequestBackMap(reqResult, keyValue, url, fixParameter, hmacOrder, backHmacOrder);
	}	
	
	
	public static Map getRequestBackMap(String reqResult, String keyValue, String url, Map fixParameter, String[] hmacOrder, String[] backHmacOrder){
		Map returnMap = new UpgradeMap();
		if(reqResult == null){
			returnMap.put(HTTPConnection, Boolean.valueOf(false));
		}else{
			returnMap.put(HTTPConnection, Boolean.valueOf(true));
			Map parameterMap = ProcessUtil.formatReqReturnString(reqResult, GroupFlag, KeyValueFlag);
			parameterMap = ProcessUtil.urlDecodeMap(parameterMap);
			returnMap.put(CHECKHMAC, Boolean.valueOf(DigestUtilInterFace.checkHmac(backHmacOrder, parameterMap,  keyValue)));
			returnMap.put(Parameter, parameterMap);
			//returnMap.put(resultString, reqResult);
		}
		return returnMap;
	}
	// 获得提交的url
	public static String getRequestUrl(HttpServletRequest request, String keyValue, String url, Map fixParameter, String[] hmacOrder){
		logUtil.log("[method]" + request.getMethod() + ",[queryString]" + request.getQueryString() + ",[requestURL]" + request.getRequestURL());
		Map parameterMap = ProcessUtil.processParameterMap(request);
		return getRequestUrl(parameterMap, keyValue, url, fixParameter, hmacOrder);
	}
	public static String getRequestUrl(Map parameterMap, String keyValue, String url, Map fixParameter, String[] hmacOrder){
		logUtil.log("[parameterMap]" + parameterMap);
		parameterMap = ProcessUtil.changeMapCharset(parameterMap, ServerInfo.getServerCharsetName(), ServerInfo.getYeepayCharsetName());
		parameterMap = ProcessUtil.setFixParameterValue(parameterMap, fixParameter);
		parameterMap = DigestUtilInterFace.addHmac(hmacOrder, parameterMap, keyValue);
		return UrlUtilInterFace.createURL(url, parameterMap);
	}
	
	
	// 获得提交的form表单
	public static String getRequestForm(HttpServletRequest request, String keyValue, String url, Map fixParameter, String[] hmacOrder, String formName, String submitValue){
		logUtil.log("[method]" + request.getMethod() + ",[queryString]" + request.getQueryString() + ",[requestURL]" + request.getRequestURL());
		Map parameterMap = ProcessUtil.processParameterMap(request);
		return getRequestForm(parameterMap, keyValue, url, fixParameter, hmacOrder, formName, submitValue);
	}
	public static String getRequestForm(Map parameterMap, String keyValue, String url, Map fixParameter, String[] hmacOrder, String formName, String submitValue){
		logUtil.log("[parameterMap]" + parameterMap);
		parameterMap = ProcessUtil.changeMapCharset(parameterMap, ServerInfo.getServerCharsetName(), ServerInfo.getYeepayCharsetName());
		parameterMap = ProcessUtil.setFixParameterValue(parameterMap, fixParameter);
		parameterMap = DigestUtilInterFace.addHmac(hmacOrder, parameterMap, keyValue);
		return FormUtilInterFace.createFormDefault(url, ProcessUtil.changeMapCode(parameterMap, ServerInfo.getYeepayCharsetName(), ServerInfo.getServerCharsetName()), formName, submitValue);
	}
	
	
	// 获得callback接收到的返回参数
	public static Map getCallbackMap(HttpServletRequest request, String keyValue , String[] callbackHmacOrder){
		logUtil.log("[method]" + request.getMethod() + ",[queryString]" + request.getQueryString() + ",[requestURL]" + request.getRequestURL());
		Map parameterMap = ProcessUtil.processParameterMap(request);
		return getCallbackMap(parameterMap, keyValue , callbackHmacOrder);
	}
	public static Map getCallbackMap(Map parameterMap, String keyValue , String[] callbackHmacOrder){
		logUtil.log("[parameterMap]" + parameterMap);
		parameterMap = ProcessUtil.changeMapCharset(parameterMap, ServerInfo.getServerCharsetName(), ServerInfo.getYeepayCharsetName());
		Map returnMap = new UpgradeMap();
		returnMap.put(CHECKHMAC, Boolean.valueOf(DigestUtilInterFace.checkHmac(callbackHmacOrder, parameterMap, keyValue)));
		returnMap.put(Parameter, parameterMap);
		returnMap.put("r1_Code", parameterMap.get("r1_Code"));
		return returnMap;
	}
}

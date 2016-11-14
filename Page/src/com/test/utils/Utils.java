package com.test.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;

/**
 * @ClassName: Utils
 * @Description: 打印json数据到页面中
 * @author xukaiqiang
 * @date 2016年11月7日 上午10:46:11
 * @modifier
 * @modify-date 2016年11月7日 上午10:46:11
 * @version 1.0
 */

public class Utils {

	/**
	 * Class Name: Utils.java
	 * 
	 * @Description: 使用I/O流输出 json格式的数据
	 * @author xukaiqiang
	 * @date 2016年11月7日 上午10:46:42
	 * @modifier
	 * @modify-date 2016年11月7日 上午10:46:42
	 * @version 1.0
	 * @param object打印对象
	 * @param response
	 * @return
	 */

	public static String printInfo(JSONArray array, HttpServletResponse response) {
		response.setContentType("text/json; charset=utf-8");
		response.setHeader("Cache-Control", "no-cache"); // 取消浏览器缓存
		PrintWriter out;
		try {
			out = response.getWriter();
			out.print(array);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Class Name: Utils.java
	 * 
	 * @Description: 过滤字符串中的数字
	 * @author xukaiqiang
	 * @date 2016年11月7日 下午2:09:44
	 * @modifier
	 * @modify-date 2016年11月7日 下午2:09:44
	 * @version 1.0
	 * @param str
	 * @return
	 */
	public static String filterNum(String str) {
		String[] aStrings = str.split(">");
		String a = aStrings[2].replace("<br /", "");
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(a);
		return m.replaceAll("").trim();
	}

}
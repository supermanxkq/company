package com.test.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @ClassName: HttpUtils
 * @Description: http请求工具类,通过get或者是post方式请求数据
 * @author xukaiqiang
 * @date 2016年11月7日 上午10:08:06
 * @modifier
 * @modify-date 2016年11月7日 上午10:08:06
 * @version 1.0
 */

public class HttpUtils {
	/**
	 * java.net实现 HTTP或HTTPs GET方法提交
	 * 
	 * @param strUrl
	 *            提交的地址及参数
	 * @return 返回的response信息
	 */
	public static String submitGet(String strUrl) {
		URLConnection connection = null;
		BufferedReader reader = null;
		String str = null;
		try {
			URL url = new URL(strUrl);
			connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setConnectTimeout(100);
            connection.setReadTimeout(100);
			// 取得输入流，并使用Reader读取
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String lines;
			StringBuffer linebuff = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				linebuff.append(lines);
			}
			str = linebuff.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	/**
	 * java.net实现 HTTP POST方法提交
	 * 
	 * @param url
	 * @param paramContent
	 * @return
	 */
	public static StringBuffer submitPost(String url, String paramContent,
			String codetype) {
		StringBuffer responseMessage = null;
		java.net.URLConnection connection = null;
		java.net.URL reqUrl = null;
		OutputStreamWriter reqOut = null;
		InputStream in = null;
		BufferedReader br = null;
		try {
			String param = paramContent;
			// System.out.println("url=" + url + "?" + paramContent + "\n");
			// System.out.println("===========post method start=========");
			responseMessage = new StringBuffer();
			reqUrl = new java.net.URL(url);
			connection = reqUrl.openConnection();
			connection.setConnectTimeout(100);
            connection.setReadTimeout(100);
			connection.setDoOutput(true);
			reqOut = new OutputStreamWriter(connection.getOutputStream());
			reqOut.write(param);
			reqOut.flush();
			int charCount = -1;
			in = connection.getInputStream();

			br = new BufferedReader(new InputStreamReader(in, codetype));
			while ((charCount = br.read()) != -1) {
				responseMessage.append((char) charCount);
			}
			// System.out.println(responseMessage);
			// System.out.println("===========post method end=========");
		} catch (Exception ex) {
			//responseMessage.append("ERROR");
		} finally {
			try {
				br.close();
				in.close();
				reqOut.close();
			} catch (Exception e) {
				System.out
						.println("paramContent=" + paramContent + "|err=" + e);
			}
		}
		return responseMessage;
	}

	/**
	 * Class Name: HttpUtils.java
	 * 
	 * @Description: 测试
	 * @author xukaiqiang
	 * @date 2016年11月7日 上午10:09:59
	 * @modifier
	 * @modify-date 2016年11月7日 上午10:09:59
	 * @version 1.0
	 * @param args
	 */

	public static void main(String[] args) {
//		StringBuffer html = submitPost(
//				"http://121.199.1.76:2001/trainorder_bespeak/view_data.jsp",
//				"", "utf-8");
//		System.out.println(html);
//
//		System.out
//				.println(submitGet("http://121.199.1.76:2001/trainorder_bespeak/view_data.jsp"));
		System.out
		.println(submitGet("http://121.41.51.7:29134/trainorder_bespeak/mq/MqTrainCreateOrder.jsp?type=0"));
		
	}
}

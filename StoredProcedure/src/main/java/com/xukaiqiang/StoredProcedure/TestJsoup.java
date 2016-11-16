package com.xukaiqiang.StoredProcedure;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


/**
 * @Description: 获取一个天气网站上面的今日天气
 * @author xukaiqiang
 * @date 2016年11月16日 下午2:10:41
*/
	
public class TestJsoup {

	
	/**
	 * @Description: 获取html资源
	 * @author xukaiqiang
	 * @date 2016年11月16日 下午2:11:07
	 * @param url
	 * @return
	*/
	public Document getDocument(String url) {
		try {
			return Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		TestJsoup t = new TestJsoup();
		Document doc = t.getDocument("http://www.weather.com.cn/html/weather/101280101.shtml");
		// 获取目标HTML代码
		Elements todayWeatherElement = doc.select("li[class=sky skyid lv2 on]");//使用属性选择器进行选择
		
		//获取日期
		Elements  dayElement=todayWeatherElement.select("h1:first-child");
		String today=dayElement.get(0).text();
		System.out.println(today);
		
		//获取天气情况
		Elements  weatherElement=todayWeatherElement.select(".wea");
		String weather=weatherElement.get(0).text();
		System.out.println(weather);
		
		//获取温度情况
		Elements  temperatureElement=todayWeatherElement.select(".tem");
		String temperature=temperatureElement.get(0).text();
		System.out.println(temperature);
		
		//风
		Elements   windElement=todayWeatherElement.select("i");
		String wind=windElement.get(1).text();
		System.out.println(wind);
	}
}
package com.xukaiqiang.StoredProcedure;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * @Description: 获取一个网站的所有的超链接
 * @author 徐凯强
 * @date 2016年11月11日 上午9:23:18
*/
	
public class JsoupA {
	public static void main(String[] args) {

		Document doc;
		try {
			doc = Jsoup.connect("http://123.57.73.59:8080/blog-front-web/article/queryArticleList.html").post();
			String title = doc.title();
			// Elements hrefs = doc.select("a[href]");
			Elements hrefs = doc.select("a");
			for (Element link : hrefs) {
				String linkHref = link.attr("abs:href");
				String linkText = link.text();
				System.out.println(title + ":" + linkHref + ":" + linkText);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

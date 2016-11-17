package com.xukaiqiang.StoredProcedure;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FetchFromCsdn {
	public static void main(String[] args) {
	    try {
			Element element=Jsoup.connect("http://blog.csdn.net/shuangrenyu1234/article/details/50011895").userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").get();
			Elements articleElements=element.select("#article_content");
			String articleString=articleElements.get(0).html();
			System.out.println(articleString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

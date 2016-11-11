package com.xukaiqiang.StoredProcedure;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @Description: 获取网站的所有的图片
 * @author 徐凯强
 * @date 2016年11月11日 上午9:23:53
 */

public class JsoubB {
	public static void main(String[] args) {
		Document doc;
		try {

			// get all images
			doc = Jsoup.connect("http://image.baidu.com/search/index?tn=baiduimage&ps=1&ct=201326592&lm=-1&cl=2&nc=1&ie=utf-8&word=%E9%A3%8E%E6%99%AF").get();
			Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
			for (Element image : images) {

				System.out.println("\nsrc : " + image.attr("abs:src"));
				System.out.println("height : " + image.attr("height"));
				System.out.println("width : " + image.attr("width"));
				System.out.println("alt : " + image.attr("alt"));

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

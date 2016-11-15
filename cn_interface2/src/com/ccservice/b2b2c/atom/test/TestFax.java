package com.ccservice.b2b2c.atom.test;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.service.IAtomService;

public class TestFax {

	public static void main(String[] args) throws MalformedURLException {
		String url = "http://localhost:8080/sj_interface/service/";

		HessianProxyFactory factory = new HessianProxyFactory();
		IAtomService servier = (IAtomService) factory.create(IAtomService.class,
				url + IAtomService.class.getSimpleName());
		Map<String, String> map=new HashMap<String, String>();
		map.put("newfax", "D://hotelfax/hotelfaxH21023.html");//创建新传真的路径
		map.put("faxtemple", "D://hotelfax/hotel.html");//传真模板的路径
		map.put("newfaxpdf", "D://hotelfax/hotelfaxH21023.pdf");//创建新传真的pdf
		map.put("rname", "北京国际大酒店");//收件人
		map.put("rphone", "01012345678");//收件人电话
		map.put("rfax", "01012345678");//收件人传真
		map.put("sname", "东航商旅");//发件人
		map.put("sphone", "01087654321");//发件人电话
		map.put("sfax", "01087654321");//发件人传真
		map.put("senddate", "2010-12-12");//日期
		map.put("hotelname", "北京国际大酒店");//入住酒店名称
		map.put("countty", "中国");//客人国籍
		map.put("peoplenum", "3人");//人数
		map.put("order", "H21023");//单号
		map.put("name", "韩方圆");//客人姓名/团号
		map.put("begindate", "2010-12-12");//入住日期
		map.put("enddate", "2010-12-14");//离店日期
		map.put("roommun", "4间大床房");//房型房数
		map.put("breakfast", "无");//早餐
		map.put("price", "￥121含单早");//房价单价
		map.put("content", "请保密单价");//特殊要求
		map.put("paymoney", "前台先付");//付款方式
		map.put("makename", "王晓竹");//制单人
		
		String filename=servier.getHotelTemple(map);
		int i = servier.sendFax("01084977053",filename);
		System.out.println(i);
	}
}

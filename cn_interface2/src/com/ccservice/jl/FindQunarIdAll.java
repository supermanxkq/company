package com.ccservice.jl;

import java.io.File;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotelall.Hotelall;

/**
 * 
 * @author wzc
 * 直接读取没有去哪id的酒店 HotelAll
 *
 */
public class FindQunarIdAll {
	public static String findcity(long cityid){
		City city=Server.getInstance().getHotelService().findCity(cityid);
		if(city!=null){
			return city.getName();
		}
		return "";
	}
public static void main(String[] args) throws Exception {
	WritableWorkbook wwb;
	String fos = "D:\\查询酒店last" + ".xls";
	wwb = Workbook.createWorkbook(new File(fos));
	WritableSheet ws = wwb.createSheet("酒店", 11);
	ws.addCell(new Label(0, 0, "酒店id"));
	ws.addCell(new Label(1, 0, "酒店名称"));
	ws.addCell(new Label(2, 0, "去哪id"));
	ws.addCell(new Label(3, 0, "城市"));
	ws.addCell(new Label(4, 0, "电话"));
	ws.addCell(new Label(5, 0, "传真"));
	ws.addCell(new Label(6, 0, "地址"));
	List<Hotelall> hotels=Server.getInstance().getHotelService().findAllHotelall("where c_qunarid is null or c_qunarid=''", "order by c_cityid", -1, 0);
	for (int i = 0; i < hotels.size(); i++) {
		Hotelall hotel = hotels.get(i);
		System.out.println("酒店名称："+hotel.getName()+"--------------"+i);
		ws.addCell(new Label(0, i + 1, hotel.getId() + ""));
		ws.addCell(new Label(1, i + 1, hotel.getName()));
		ws.addCell(new Label(2, i + 1, ""));
		if(hotel.getCityid()!=null){
			ws.addCell(new Label(3, i + 1, findcity(hotel.getCityid())));
		}
		if(hotel.getMarkettell()!=null){
			ws.addCell(new Label(4, i + 1,hotel.getMarkettell()));
		}
		if(hotel.getFax1()!=null){
			ws.addCell(new Label(5, i + 1,hotel.getFax1()));
		}
		if(hotel.getAddress()!=null){
			ws.addCell(new Label(6, i + 1,hotel.getAddress()));
		}
	}
	wwb.write();
	wwb.close();
	System.out.println("over.......");
}
}

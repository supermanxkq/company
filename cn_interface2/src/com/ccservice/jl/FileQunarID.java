package com.ccservice.jl;

import java.io.FileInputStream;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelall.Hotelall;
/**
 * 
 * @author wzc
 * 根据文件更新数据库的去哪id
 *
 */
public class FileQunarID {
	public static void main(String[] args) throws Exception {
		FileInputStream fileInputStream1 = new FileInputStream(
				"D:\\查询酒店last.xls");
		readExcel(fileInputStream1);
	}

	public static String readExcel(InputStream fInputStream1) throws Exception {
		Workbook hotelbook1 = Workbook.getWorkbook(fInputStream1);
		Sheet sheet1 = hotelbook1.getSheet(0);
		for (int i = 2; i < sheet1.getRows(); i++) {
			Hotelall hotel = new Hotelall();
			System.out.println("酒店名称："+sheet1.getCell(1, i).getContents());
			long hotelid=Long.valueOf(sheet1.getCell(0, i).getContents());
			hotel.setId(hotelid);
			String qunarid=sheet1.getCell(2, i).getContents();
			if(!"".equals(qunarid)){
				hotel.setQunarId(qunarid);
				System.out.println("更新："+qunarid);
				Server.getInstance().getHotelService().updateHotelallIgnoreNull(hotel);
			}
		}
		return "";
	}
}

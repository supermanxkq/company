package com.ccservice.jl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotel.Hotel;

import jxl.Sheet;
import jxl.Workbook;
/**
 * 
 * @author wzc
 * 读取捷旅不可上酒店（线下处理）
 *
 */
public class ReadExcelHotelNoable {


	public static void main(String[] args) throws Exception {
		FileInputStream fileInputStream = new FileInputStream("D:\\需下线酒店清单0819致同行.xls");
		readExcel(fileInputStream);
	}

	@SuppressWarnings("unchecked")
	public static void readExcel(InputStream fileinputstream) throws Exception,
			IOException {
		Workbook citybookk = Workbook.getWorkbook(fileinputstream);
		Sheet sheet = citybookk.getSheet(0);
		for (int i = 1; i < sheet.getRows(); i++) {
			String hotelname = sheet.getCell(1, i).getContents();
			String hotelid=sheet.getCell(2, i).getContents();
			List<Hotel> hoteles=Server.getInstance().getHotelService().findAllHotel("where c_sourcetype=6 and c_hotelcode='"+hotelid+"'", "", -1, 0);
			if(hoteles.size()>0){
				Hotel hotel=hoteles.get(0);
				hotel.setState(4);
				hotel.setHcontrol(1l);
				Server.getInstance().getHotelService().updateHotelIgnoreNull(hotel);
				System.out.println(hotelname+"------被禁用");
			}
		}
	}

}

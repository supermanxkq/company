package com.ccservice.b2b2c.atom.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.sellreport.Sellreport;
import com.ccservice.b2b2c.base.service.IAirService;
import com.ccservice.b2b2c.base.tuipiao.Tuipiao;


public class Import51booktuipiao {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws BiffException 
	 * @throws ParseException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws BiffException, IOException, ParseException, SQLException {
		
		
		// TODO Auto-generated method stub
		
		String url = "http://localhost:8080/lthk_service/service/" ;

		HessianProxyFactory factory = new HessianProxyFactory();
		IAirService servier = (IAirService) factory.create(IAirService.class,
				url + IAirService.class.getSimpleName());
		
		
		
		
		DateFormat format = new SimpleDateFormat("M/d/y HH:mm");
		DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");				
		FileInputStream fileInputStream = new FileInputStream("D://51booktuipiao.xls");
		Workbook hotelbook = Workbook.getWorkbook(fileInputStream);
		
		Sheet sheetHotel = hotelbook.getSheet(0);
		Sellreport sellreport2 = new Sellreport();
		
		List<Sellreport> list = new ArrayList<Sellreport>();
		
		for(int d=1;d<18;d++){
			
		Tuipiao tuipiao = new Tuipiao();
		
		Cell ordertime = sheetHotel.getCell(1, d);
		
		Date aaaa = format2.parse(ordertime.getContents());
		tuipiao.setRttime(new java.sql.Timestamp(aaaa.getTime()));
		
	
		
		
		Cell bianhao = sheetHotel.getCell(2, d);
		
		tuipiao.setOrdercode(bianhao.getContents());
		
		
		
		
		
		
		
		Cell pnr = sheetHotel.getCell(3, d);
	
		tuipiao.setPnr(pnr.getContents());
		
	
		
		Cell number = sheetHotel.getCell(4, d);
	
		String nu =number.getContents();
		long n = Long.parseLong(nu);
		tuipiao.setNumber(n);
	
	
		
		Cell tuipiaoren = sheetHotel.getCell(5, d);
	
		String num =tuipiaoren.getContents();
		long nua = Long.parseLong(num);
		tuipiao.setRnumber(nua);
	
	
	
		Cell feituitime = sheetHotel.getCell(6, d);
		
		Date aaa = format2.parse(feituitime.getContents());
		tuipiao.setApplytime(new java.sql.Timestamp(aaa.getTime()));
	
		
		Cell zhuangtai = sheetHotel.getCell(7, d);
		tuipiao.setState(zhuangtai.getContents());
		
		
		//offine 8
			
		Cell tuikuanjia = sheetHotel.getCell(9, d);
		if(tuikuanjia.getContents().length()>1){
		tuipiao.setTuiprice(Double.parseDouble(tuikuanjia.getContents()));	
		}
		
		Cell tuikuantime = sheetHotel.getCell(10, d);
		if(tuikuantime.getContents()!=""&&tuikuantime.getContents().length()>1){
		Date tui = format2.parse(tuikuantime.getContents());
		tuipiao.setTuitime(new java.sql.Timestamp(tui.getTime()));
		}
		
		Cell piaohao = sheetHotel.getCell(11, d);
		tuipiao.setTicketno(piaohao.getContents());
		
		Cell chengjiren = sheetHotel.getCell(12, d);
		tuipiao.setPassenger(chengjiren.getContents());
		
		Cell chengjitype = sheetHotel.getCell(13, d);
		tuipiao.setPtype(chengjitype.getContents());
		
		Cell chufa = sheetHotel.getCell(14, d);
		tuipiao.setStartcity(chufa.getContents());
		
		Cell daoda = sheetHotel.getCell(15, d);
		tuipiao.setEndcity(daoda.getContents());
		
		Cell hangcheng = sheetHotel.getCell(16, d);
		tuipiao.setJourneytype(hangcheng.getContents());
		
		Cell gongsi = sheetHotel.getCell(17, d);
		tuipiao.setAircompany(gongsi.getContents());
		
		Cell hangban = sheetHotel.getCell(18, d);
		tuipiao.setCabin(hangban.getContents());
		
		Cell hangbantime = sheetHotel.getCell(19, d);
		Date han = format3.parse(hangbantime.getContents());
		tuipiao.setFlighttime(new java.sql.Timestamp(han.getTime()));
		
		
		Cell cangwei = sheetHotel.getCell(20, d);
		tuipiao.setCabin(cangwei.getContents());
		
		
		Cell piaomian = sheetHotel.getCell(21, d);
		tuipiao.setPrice(Double.parseDouble(piaomian.getContents()));
		
		Cell jijian = sheetHotel.getCell(22, d);
		tuipiao.setJijian(Double.parseDouble(piaomian.getContents()));
		
		Cell yuanyou = sheetHotel.getCell(23, d);
		tuipiao.setRanyou(Double.parseDouble(piaomian.getContents()));
		
		Cell danzhong = sheetHotel.getCell(24, d);
		tuipiao.setTalfee(Double.parseDouble(piaomian.getContents()));
		
		Cell xingcheng = sheetHotel.getCell(25, d);
		tuipiao.setJourneytype(xingcheng.getContents());
		

		Cell jiashoufuwu = sheetHotel.getCell(26, d);
		tuipiao.setFuwufei(Double.parseDouble(jiashoufuwu.getContents()));
		
		servier.createTuipiao(tuipiao);
		
		
		
		
	
		}
		
	}

	}


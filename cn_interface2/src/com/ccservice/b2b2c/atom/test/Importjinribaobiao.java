package com.ccservice.b2b2c.atom.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.jinribaobiao.Jinribaobiao;
import com.ccservice.b2b2c.base.service.IAirService;


public class Importjinribaobiao {

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
								
		FileInputStream fileInputStream = new FileInputStream("D://jinribaobiao2.xls");
		Workbook hotelbook = Workbook.getWorkbook(fileInputStream);
		
		Sheet sheetHotel = hotelbook.getSheet(0);
		for(int d=1;d<330;d++){
			
		Jinribaobiao jinribaobiao = new Jinribaobiao();
		
		Cell ordertime = sheetHotel.getCell(1, d);
		Date aaaa = format.parse(ordertime.getContents());
		jinribaobiao.setOrdertime(new java.sql.Timestamp(aaaa.getTime()));
		
		Cell pnr = sheetHotel.getCell(2, d);
		jinribaobiao.setPnr(pnr.getContents());
		
		Cell piaohao = sheetHotel.getCell(3, d);
		jinribaobiao.setTicketcode(piaohao.getContents());
		
		Cell bianhao = sheetHotel.getCell(4, d);
		jinribaobiao.setOrdercode(bianhao.getContents());
		
		Cell username = sheetHotel.getCell(5, d);
		jinribaobiao.setUsername(username.getContents());
		
		Cell shifa = sheetHotel.getCell(6, d);
		jinribaobiao.setStartcity(shifa.getContents());
		
		Cell mudi = sheetHotel.getCell(7, d);
		jinribaobiao.setEndcity(mudi.getContents());
		
		Cell shifaszm = sheetHotel.getCell(8, d);
		jinribaobiao.setStartcityszm(shifaszm.getContents());
		
		Cell mudiszm = sheetHotel.getCell(9, d);
		jinribaobiao.setEndcityszm(mudiszm.getContents());
		
		Cell hangbanhao = sheetHotel.getCell(10, d);
		jinribaobiao.setFlightnumber(hangbanhao.getContents());
		
		Cell cangwei = sheetHotel.getCell(11, d);
		jinribaobiao.setCabincode(cangwei.getContents());
		
		Cell qifei = sheetHotel.getCell(12, d);
		Date qitime = format.parse(qifei.getContents());
		jinribaobiao.setFlightdate(new java.sql.Timestamp(qitime.getTime()));
		
		Cell piaomian = sheetHotel.getCell(13, d);
		jinribaobiao.setPrice(Double.parseDouble(piaomian.getContents()));
		
		Cell tonghang = sheetHotel.getCell(14, d);
		String[] fandian = tonghang.getContents().trim().split("%");
		String fan =fandian[0];
		jinribaobiao.setFandian(Double.parseDouble(fan));
		
		Cell jijian = sheetHotel.getCell(15, d);
		jinribaobiao.setJijianranyou(Double.parseDouble(jijian.getContents()));
		
		Cell number = sheetHotel.getCell(16, d);
		String numb = number.getContents();
		long nu = Long.parseLong(numb);
		jinribaobiao.setNumber(nu);
		
		Cell shoukuan = sheetHotel.getCell(17, d);
		jinribaobiao.setSubmoney(Double.parseDouble(shoukuan.getContents()));
		
		
		
		Cell shouxufei = sheetHotel.getCell(18, d);
		jinribaobiao.setShouxufei(Double.parseDouble(shouxufei.getContents()));
		
		Cell tuikuan = sheetHotel.getCell(19, d);
		jinribaobiao.setTuikuan(Double.parseDouble(tuikuan.getContents()));
		
		Cell yingshou = sheetHotel.getCell(20, d);
		jinribaobiao.setYingshou(Double.parseDouble(yingshou.getContents()));
		
		Cell lirun = sheetHotel.getCell(21, d);
		jinribaobiao.setLirun(Double.parseDouble(lirun.getContents()));
		
		Cell zhifu = sheetHotel.getCell(22, d);
		jinribaobiao.setPaymethod(zhifu.getContents());
		
		Cell zhifutime = sheetHotel.getCell(23, d);
		Date zhitime = format.parse(zhifutime.getContents());
		jinribaobiao.setPaytime(new java.sql.Timestamp(zhitime.getTime()));
		
		Cell feitui = sheetHotel.getCell(24, d);
		if(feitui.getContents()!=null && feitui.getContents().length()>0){
		Date feitime = format.parse(feitui.getContents());
		jinribaobiao.setFeitime(new java.sql.Timestamp(feitime.getTime()));
		}
		Cell chupiao = sheetHotel.getCell(25, d);
		Date chupiaotime = format.parse(chupiao.getContents());
		jinribaobiao.setRttime(new java.sql.Timestamp(chupiaotime.getTime()));
		
		Cell states = sheetHotel.getCell(26, d);
		String st = states.getContents();
		//long state = Long.parseLong(st);
		jinribaobiao.setState(st);
		
		servier.createJinribaobiao(jinribaobiao);
		}
		
	}

}

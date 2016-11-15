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


public class Import51book {

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
		
		
		Date ordtime=null;
		String ordercode="";
		String xiao="";
		long ren=0;
		String pn="";
		String churen="";
		String zhitype="";
		String ddtype="";
		String hao="";
		String cheng="";
		Double piaoji=0.00;
		Double jing=0.00;
		Double jiyou=0.00;
		Double shouxu=0.00;
		Double shiji=0.00;
		
		DateFormat format = new SimpleDateFormat("M/d/y HH:mm");
		DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");				
		FileInputStream fileInputStream = new FileInputStream("D://51bookxiaoshou.xls");
		Workbook hotelbook = Workbook.getWorkbook(fileInputStream);
		
		Sheet sheetHotel = hotelbook.getSheet(0);
		Sellreport sellreport2 = new Sellreport();
		
		List<Sellreport> list = new ArrayList<Sellreport>();
		
		for(int d=1;d<54;d++){
			
		Sellreport sellreport = new Sellreport();
		

		Cell number = sheetHotel.getCell(6, d);
		if(number.getContents().length()==0){
			sellreport.setNumber(ren);
		}else{
		String nu =number.getContents();
		long n = Long.parseLong(nu);
		sellreport.setNumber(n);
		ren=n;
		}
		
		
		
	/*	Cell ordertime = sheetHotel.getCell(1, d);
		if(ordertime.getContents()==""){
			sellreport.setRttime(new java.sql.Timestamp(ordtime.getTime()));
		}else{
		Date aaaa = format.parse(ordertime.getContents());
		sellreport.setRttime(new java.sql.Timestamp(aaaa.getTime()));
		ordtime =aaaa;
		}*/
		
		
		Cell bianhao = sheetHotel.getCell(2, d);
		System.out.println("ordercode=="+ordercode);
		if(bianhao.getContents().length()==0){
		sellreport.setOrdercode(ordercode);
		}else{
		sellreport.setOrdercode(bianhao.getContents());
		ordercode=bianhao.getContents();
		
		}
		
		
		
		Cell xiaolv = sheetHotel.getCell(3, d);
		if(xiaolv.getContents().length()==0){
			sellreport.setXiaolv(xiao);
		}else{
		sellreport.setXiaolv(xiaolv.getContents());
		xiao=xiaolv.getContents();
		}
		
		
		Cell chupiaoren = sheetHotel.getCell(4, d);
		if(chupiaoren.getContents().length()==0){
			sellreport.setUsername(churen);
		}else{
		sellreport.setUsername(chupiaoren.getContents());
		churen=chupiaoren.getContents();
		}
		
		
		Cell pnr = sheetHotel.getCell(5, d);
		if(pnr.getContents().length()==0){
			sellreport.setPnr(pn);
		}else{
		sellreport.setPnr(pnr.getContents());
		pn=pnr.getContents();
		}
		
		//保证金空着 7
		
		Cell piaomian = sheetHotel.getCell(8, d);
		if(piaomian.getContents().length()==0){
			sellreport.setPrice(piaoji);
		}else{
		sellreport.setPrice(Double.parseDouble(piaomian.getContents()));
		piaoji=Double.parseDouble(piaomian.getContents());
		}
		
		
		Cell jingjia = sheetHotel.getCell(9, d);
		if(jingjia.getContents().length()==0){
			sellreport.setJingjia(jing);
		}else{
		sellreport.setJingjia(Double.parseDouble(jingjia.getContents()));
		jing=Double.parseDouble(jingjia.getContents());
		}
		
		
		Cell jijianyuanyou = sheetHotel.getCell(10, d);
		if(jijianyuanyou.getContents().length()==0){
			sellreport.setJijianranyou(jiyou);
		}else{
		sellreport.setJijianranyou(Double.parseDouble(jijianyuanyou.getContents()));
		jiyou=Double.parseDouble(jijianyuanyou.getContents());
		}
		
		
		
		Cell zongshuxufei = sheetHotel.getCell(11, d);
		if(zongshuxufei.getContents().length()==0){
			sellreport.setPoundage(shouxu);
		}else{
		sellreport.setPoundage(Double.parseDouble(zongshuxufei.getContents()));
		shouxu=Double.parseDouble(zongshuxufei.getContents());
		}
		
		Cell shishouzongji = sheetHotel.getCell(12, d);
		if(shishouzongji.getContents().length()==0){
			sellreport.setPoundage(shiji);
		}else{
		sellreport.setPoundage(Double.parseDouble(shishouzongji.getContents()));
		shiji=Double.parseDouble(shishouzongji.getContents());
		}
		
		Cell zhifufangshi = sheetHotel.getCell(13, d);
		sellreport.setPaytype(zhifufangshi.getContents());
		zhitype=zhifufangshi.getContents();
		
		
		Cell dingdantype = sheetHotel.getCell(14, d);
		sellreport.setOrdertype(dingdantype.getContents());
		ddtype=dingdantype.getContents();
		
		
		Cell jipiaotype = sheetHotel.getCell(15, d);
		sellreport.setTickettype(jipiaotype.getContents());
		
		Cell xingchengtype = sheetHotel.getCell(16, d);
		sellreport.setJourneytype(xingchengtype.getContents());
		
		Cell chupiaotype = sheetHotel.getCell(17, d);
		sellreport.setChupiaotype(chupiaotype.getContents());
		//office 18  备注19
		Cell piaohao = sheetHotel.getCell(20, d);
		sellreport.setPiaohao(piaohao.getContents());
		hao=piaohao.getContents();
		
		
		Cell chengjiren = sheetHotel.getCell(21, d);
		sellreport.setPiaohao(chengjiren.getContents());
		cheng=chengjiren.getContents();
		
		Cell chengren = sheetHotel.getCell(22, d);
		sellreport.setPiaohao(chengren.getContents());
		
		Cell chufa = sheetHotel.getCell(23, d);
		sellreport.setStartcity(chufa.getContents());
		
		Cell daoda = sheetHotel.getCell(24, d);
		sellreport.setEndcity(daoda.getContents());
		
		Cell hangcheng = sheetHotel.getCell(25, d);
		sellreport.setSail(hangcheng.getContents());
		
		Cell gongsi = sheetHotel.getCell(26, d);
		sellreport.setAircompany(gongsi.getContents());
		
		Cell hangban = sheetHotel.getCell(27, d);
		sellreport.setFlightnumber(hangban.getContents());
		
		Cell hangbantime = sheetHotel.getCell(28, d);
		Date zhitime = format3.parse(hangbantime.getContents());
		sellreport.setFlighttime(new java.sql.Timestamp(zhitime.getTime()));
		
		Cell cangwei = sheetHotel.getCell(29, d);
		sellreport.setCabin(cangwei.getContents());
		
		Cell zce = sheetHotel.getCell(30, d);
		sellreport.setPolicy(zce.getContents());
		
		Cell piaomianjia = sheetHotel.getCell(31, d);
		sellreport.setPrice(Double.parseDouble(piaomianjia.getContents()));
		
		Cell danzhangjingjia = sheetHotel.getCell(32, d);
		sellreport.setLeafletsnet(Double.parseDouble(danzhangjingjia.getContents()));
		
		Cell jijian = sheetHotel.getCell(33, d);
		sellreport.setJijian(Double.parseDouble(jijian.getContents()));
		
		Cell ruanyou = sheetHotel.getCell(34, d);
		sellreport.setRanyou(Double.parseDouble(ruanyou.getContents()));
		
		Cell danzhangjiesuan = sheetHotel.getCell(35, d);
		sellreport.setJiesuanprice(Double.parseDouble(danzhangjiesuan.getContents()));
		
		
		servier.createSellreport(sellreport);
		
		
		
		
	
		}
		
	}

}

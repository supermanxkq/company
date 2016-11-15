package com.ccservice.b2b2c.atom.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.service.IAirService;
import com.ccservice.b2b2c.base.zrate.Zrate;

public class ImportZrate {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 * @throws IOException 
	 * @throws BiffException 
	 */
	public static void main(String[] args) throws MalformedURLException{
		// TODO Auto-generated method stub
		String url = "http://localhost:8080/lthk_service/service/" ;

		HessianProxyFactory factory = new HessianProxyFactory();
		IAirService servier = (IAirService) factory.create(IAirService.class,
				url + IAirService.class.getSimpleName());
		Zrate zrate=servier.findZrate(316782l);
		Workbook workbook;
		try {
			workbook = Workbook.getWorkbook(new File("D://2.xls"));
			Sheet sheet = workbook.getSheet(0);
			
			for(int i=0;i< sheet.getRows();i++)
			{
				zrate.setId(-1);
				zrate.setAircompanycode("MU");
				String a=sheet.getCell(1, i).getContents().toString();
				if(a.length()>2)
				{
				zrate.setFlightnumber(a);
				}
				zrate.setDepartureport(sheet.getCell(4, i).getContents().toString());
				zrate.setArrivalport(sheet.getCell(6, i).getContents().toString());
				zrate.setCabincode(sheet.getCell(7, i).getContents().toString());
				String s=sheet.getCell(8, i).getContents().toString();
				if(s.indexOf('.')>0)
				{
					s=s.replace(".", "");
				}
				zrate.setRatevalue(Float.parseFloat(s));
				zrate=servier.createZrate(zrate);
				System.out.println(zrate);
			}
			System.out.println("导入成功！");
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}

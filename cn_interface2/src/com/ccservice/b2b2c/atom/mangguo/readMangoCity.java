package com.ccservice.b2b2c.atom.mangguo;
import java.io.File;
import java.util.List;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.service.IHotelService;

import jxl.Sheet;
import jxl.Workbook;
public class readMangoCity {    
  
  public static void main(String[] args) throws Exception {   
	  String url = "http://localhost:8080/cn_service/service/";

		HessianProxyFactory factory = new HessianProxyFactory();
		IHotelService servier = (IHotelService) factory.create(IHotelService.class,
				url + IHotelService.class.getSimpleName());
	Workbook workbook = Workbook.getWorkbook(new File("d://a.xls")); 
	Sheet sheet = workbook.getSheet(0); 
	for(int i=0;i<sheet.getRows();i++)
	{
		String city=sheet.getCell(4, i).getContents().toString().trim();
		String citycode=sheet.getCell(5, i).getContents().toString().trim();
		List<City> list=servier.findAllCity(" where "+City.COL_name+" = '"+city+"'", "", -1, 0);
		if(list!=null&&list.size()>0)
		{
			City city3=list.get(0);
			city3.setAreacode(citycode);
			servier.updateCity(city3);
			System.out.println(city3);
			for(int j=1;j<list.size();j++)
			{
				System.out.println("shangchu"+list.get(j));
				servier.deleteCity(list.get(j).getId());
			}
		}else
		{
			City city2=new City();
			city2.setName(city);
			city2.setAreacode(citycode);
			city2=servier.createCity(city2);
			System.out.println(city2);
			
		}
	} 
     workbook.close(); 
	}
  }  
 
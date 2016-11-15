package com.ccservice.b2b2c.atom.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.province.Province;
import com.ccservice.b2b2c.base.region.Region;
import jxl.Sheet;
import jxl.Workbook;

public class ExcleUtil {
	public static void main(String[] args) throws FileNotFoundException,
			Exception {
		readExcel(new FileInputStream("D:\\省市区\\城市.xls"));
	}

	public static void readExcel(InputStream fInputStream) throws Exception {
		Workbook hotelbook = Workbook.getWorkbook(fInputStream);
		Sheet sheet = hotelbook.getSheet(0);
		
		//System.out.println("行数："+sheet.getRows());
		//i=行,更新省份
		
		/*for (int i = 1; i < sheet.getRows(); i++) {
				String sheng = sheet.getCell(0, i).getContents();
				String pin=sheet.getCell(1, i).getContents();
				System.out.println(pin);
				Province province=new Province();
					String where="where C_NAME='"+sheng+"'";
					List<Province> cc = Server.getInstance().getHotelService().findAllProvince(where, "", -1, 0);
					System.out.println(cc.get(0).getId());
					province.setId(cc.get(0).getId());
					province.setHtcode(pin);
					int pr = Server.getInstance().getHotelService().updateProvinceIgnoreNull(province);
		}*/
		//更新城市
		for (int i = 1; i < sheet.getRows(); i++) {
			String shi = sheet.getCell(0, i).getContents();
			String shipin=sheet.getCell(1, i).getContents();
			String sheng=sheet.getCell(2, i).getContents();
			String shengpin=sheet.getCell(3, i).getContents();
			City city=new City();
			String where="where C_NAME='"+sheng+"'";
			List<Province> cc = Server.getInstance().getHotelService().findAllProvince(where, "", -1, 0);
			String wheres="where C_NAME='"+shi+"'";
			List<City> c=Server.getInstance().getHotelService().findAllCity(wheres, "", -1, 0);
			System.out.println(c);
			if(c.size()==0){
				city.setName(shi);
				city.setProvinceid(cc.get(0).getId());
				city.setCountryid(168l);
				city.setHtcode(shipin);
				Server.getInstance().getHotelService().createCity(city);
				System.out.println("创建市");
			}else{
					System.out.println(c.get(0).getId());
					city.setId(c.get(0).getId());
					city.setHtcode(shipin);
					city.setProvinceid(cc.get(0).getId());
					Server.getInstance().getHotelService().updateCityIgnoreNull(city);
					System.out.println("更新市");
			}
		}
		
		//商圈入库
		/*int xx=0;
		int cc=0;
		for (int i = 1; i < sheet.getRows(); i++) {
			
			String qu = sheet.getCell(0, i).getContents();
			String shi=sheet.getCell(1, i).getContents();
			String qupin=sheet.getCell(2, i).getContents();
			
			String wheres=" where C_HTCODE='"+shi+"'";
			List<City> city = Server.getInstance().getHotelService().findAllCity(wheres, "", -1, 0);
			
			if(city.size()>0){
				
				cc++;
				String where = " where C_NAME='"+qu+"' and C_CITYID='"+city.get(0).getId()+"'";
				System.out.println(where);
				List<Region> region = Server.getInstance().getHotelService().findAllRegion(where, "", -1, 0);
				
				
				System.out.println(region.size());
				
				//System.out.println(city.size());
				Region regions=new Region();
				//System.out.println(region);
				if(region.size()!=0){
					label:for (int j = 0; j < region.size(); j++) {
						if(region.get(j).getCityid()==city.get(0).getId()){
							regions.setId(region.get(0).getId());
							regions.setType("1");
							regions.setHtcode(qupin);
							Server.getInstance().getHotelService().updateRegionIgnoreNull(regions);
							xx++;
							System.out.println("更新biao");
							continue label;
						}else{
							regions.setName(qu);
							regions.setCityid(city.get(0).getId());
							regions.setHtcode(qupin);
							regions.setCountryid(168l);
							regions.setType("1");
							xx++;
							Server.getInstance().getHotelService().createRegion(regions);
							System.out.println("新增");
							continue label;
						}
					}
					
				}else{
					regions.setName(qu);
					regions.setCityid(city.get(0).getId());
					regions.setHtcode(qupin);
					regions.setCountryid(168l);
					regions.setType("1");
					xx++;
					Server.getInstance().getHotelService().createRegion(regions);
					System.out.println("新增");
				}
			}
			System.out.println(cc);
		}*/
		//行政区入库
		/*for (int i = 1; i < sheet.getRows()+1; i++) {
			String qu = sheet.getCell(0, i).getContents();
			String qupin=sheet.getCell(1, i).getContents();
			String shi=sheet.getCell(2, i).getContents();
			
			
			String wheres = "where C_NAME='"+shi+"'";
			List<City> city=Server.getInstance().getHotelService().findAllCity(wheres, "", -1, 0);
			
			if(city.size()>0){
				String where = " where C_NAME='"+qu+"' and C_CITYID='"+city.get(0).getId()+"'";
				List<Region> region = Server.getInstance().getHotelService().findAllRegion(where, "", -1, 0);
				
				
				Region regions=new Region();
			
				//System.out.println(region);
				if(region.size()!=0){
					label:for (int j = 0; j < region.size()+1; j++) {
						if((region.get(j).getCityid()==city.get(0).getId())){
							regions.setId(region.get(0).getId());
							regions.setType("2");
							regions.setHtcode(qupin);
							Server.getInstance().getHotelService().updateRegionIgnoreNull(regions);
							System.out.println("更新biao");
							continue label;
						}else{
							regions.setName(qu);
							regions.setCityid(city.get(0).getId());
							regions.setHtcode(qupin);
							regions.setCountryid(168l);
							regions.setType("2");
							Server.getInstance().getHotelService().createRegion(regions);
							System.out.println("新增");
							//System.out.println("没录入");
							continue label;
						}
						
					}
					
					
				}else{
					regions.setName(qu);
					regions.setCityid(city.get(0).getId());
					regions.setHtcode(qupin);
					regions.setCountryid(168l);
					regions.setType("2");
					Server.getInstance().getHotelService().createRegion(regions);
					System.out.println("新增");
					
				}
			}
		
		}*/
		//酒店品牌入库
		/*for (int i = 1; i < sheet.getRows()+1; i++) {
			
			String htcode=sheet.getCell(0, i).getContents();
			String pinpai = sheet.getCell(1, i).getContents();
			//System.out.println(pin);
			Chaininfo ci=new Chaininfo();
			String where="where C_NAME='"+pinpai+"'";
			List<Chaininfo> cif = Server.getInstance().getHotelService().findAllChaininfo(where, "", -1, 0);
			String wheres="";
		
			System.out.println(cif.size());
			if(cif.size()==0){
				ci.setName(pinpai);
				ci.setHtcode(htcode);
				Server.getInstance().getHotelService().createChaininfo(ci);
				System.out.println("新增");
			}else{
				ci.setId(cif.get(0).getId());
				ci.setHtcode(htcode);
				Server.getInstance().getHotelService().updateChaininfoIgnoreNull(ci);
				System.out.println("更新");
			}
		}*/
			//ci.setHtcode(htcode);
		
	}
}

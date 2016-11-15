package com.ccservice.bussinessmancode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.province.Province;
import com.ccservice.b2b2c.base.region.Region;

public class LoadBussCityData {

	public static void main(String[] args) throws Exception {
		FileInputStream fileInputStream = new FileInputStream("D:\\城市清单.xls");
		readExcel(fileInputStream);
		//citycount(fileInputStream);
	}

	public static void citycount(InputStream fileinputstream) throws Exception {
		Workbook citybookk = Workbook.getWorkbook(fileinputstream);
		Sheet sheet = citybookk.getSheet(0);
		Set<String> set = new HashSet<String>();
		for (int i = 1; i < sheet.getRows(); i++) {
			String cityid = sheet.getCell(2, i).getContents();
			if(!set.contains(cityid)){
				set.add(cityid);
			}
		}
		System.out.println(set.size());
	}

	@SuppressWarnings("unchecked")
	public static void readExcel(InputStream fileinputstream) throws Exception,
			IOException {
		Workbook citybookk = Workbook.getWorkbook(fileinputstream);
		Sheet sheet = citybookk.getSheet(0);
		for (int i = 1; i < sheet.getRows(); i++) {
			String provicename = sheet.getCell(0, i).getContents();
			if(provicename.contains("省")){
				provicename=provicename.replaceAll("省", "");
			}
			List<Province> p=Server.getInstance().getHotelService().findAllProvince("where c_name='"+provicename+"'", "", -1, 0);
			if(p.size()==1){
				if (provicename != null && !"".equals(provicename)) {
					String cityname = sheet.getCell(1, i).getContents();
					String cityid = sheet.getCell(2, i).getContents();
					City city = new City();
					if(cityname.contains("市")){
						cityname=cityname.replaceAll("市", "");
					}
					city.setName(cityname);
					city.setBusscode(cityid);
					city.setProvinceid(p.get(0).getId());
					city.setLanguage(0);
					city.setCountryid(168l);
					List<City> citys = Server.getInstance().getHotelService()
					.findAllCity("where c_name='" + cityname+"'", "", -1, 0);
					if (citys.size() > 0) {
						city.setId(citys.get(0).getId());
						Server.getInstance().getHotelService().updateCityIgnoreNull(city);
						city = citys.get(0);
						System.out.println("更新城市……");
					} else {
						city = Server.getInstance().getHotelService().createCity(city);
						System.out.println("创建城市……");
					}
					
					
					Region region = new Region();
					String regionName = sheet.getCell(3, i).getContents();
					String regionId = sheet.getCell(4, i).getContents();
					region.setBusscode(regionId);
					region.setCityid(city.getId());
					region.setCountryid(168l);
					region.setName(regionName);
					List<Region> regions = Server.getInstance().getHotelService()
					.findAllRegion(" where c_name='" + regionName+"' and c_cityid="+city.getId(), "order by c_regionid desc", -1,
							0);
					if (regions.size() > 0) {
						if(regions.size()>1){
							//Server.getInstance().getSystemService().findMapResultBySql("delete from t_region where c_regionid is null and  c_name='" + regionName+"' and c_cityid="+city.getId(), null);
							System.out.println("删除"+regionName);
						}
						regions = Server.getInstance().getHotelService().findAllRegion(" where c_name='" + regionName+"' and c_cityid="+city.getId(), "", -1,
								0);
						region.setId(regions.get(0).getId());
						Server.getInstance().getHotelService()
						.updateRegionIgnoreNull(region);
						region = regions.get(0);
						System.out.println("更新区域……");
					} else {
						region = Server.getInstance().getHotelService()
						.createRegion(region);
						System.out.println("创建区域……");
					}
					System.out.println(cityname);
				}
			}else{
				
			}
		}
	}
}

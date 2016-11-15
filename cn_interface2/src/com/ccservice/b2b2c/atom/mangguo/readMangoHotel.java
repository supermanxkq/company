package com.ccservice.b2b2c.atom.mangguo;
import java.io.File;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.service.IHotelService;
import com.mango.hotel.HotelSummary;
import com.mango.hotel.MGHotelService;
import com.mango.hotel.MGHotelServiceProxy;
import com.mango.hotel.MangoAuthor;
import com.mango.hotel.MutilHotelRequest;
import com.mango.hotel.MutilHotelResponse;
public class readMangoHotel {    
  
  public static void main(String[] args) throws Exception {   
	  String url = "http://localhost:8080/cn_service/service/";

		HessianProxyFactory factory = new HessianProxyFactory();
		IHotelService servier = (IHotelService) factory.create(IHotelService.class,
				url + IHotelService.class.getSimpleName());
		File file=new File("d://e.xls");
		WritableWorkbook workbook=Workbook.createWorkbook(file); 
		WritableSheet sheet = workbook.createSheet("e", 0); 
		Label label0 = new Label(0, 0, "id");    
		sheet.addCell(label0); 
		Label label1 = new Label(1, 0, "name");    
		sheet.addCell(label1);
		Label label2 = new Label(2, 0, "e");    
		sheet.addCell(label2);
		int sheetindex=1;
		List<City> listcity=servier.findAllCity(" where C_AREACODE is not NULL ", "", -1, 0);
		Hotel hotel=null;
		for(City city:listcity)
		{
			MGHotelService hotelService=new MGHotelServiceProxy();
			MutilHotelRequest hotelRequest=new MutilHotelRequest();
			MangoAuthor author=new MangoAuthor();
			author.setChannel("SXTJ");
			author.setKey("02E882DC62692E9F29D8709BFD0369D7E09C5A4F");
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
			hotelRequest.setAuthor(author);
			hotelRequest.setCityCode(city.getAreacode());
			hotelRequest.setCheckInDate("2011-06-07");
			hotelRequest.setCheckOutDate("2011-06-08");
			try {
				MutilHotelResponse hotelResponse= hotelService.mutilHotel(hotelRequest);
				if(hotelResponse.getResult().getValue()==1)
				{
				HotelSummary[] list=hotelResponse.getHotelList();
				if(list!=null&&list.length>0)
				{
					for(int i=0;i<list.length;i++)
					{
						HotelSummary hotelSummary=list[i];
						List<Hotel> listhotel=servier.findAllHotel(" where "+Hotel.COL_hotelcode+" = '"+hotelSummary.getHotelCode()+"'", "", -1, 0);
						if(listhotel!=null&&listhotel.size()>0)
						{
							System.out.println("hotelSummary=="+hotelSummary);
						}else
						{
						hotel=new Hotel();
						
						hotel.setAddress(hotelSummary.getChnAddress());
						hotel.setHotelcode(hotelSummary.getHotelCode());
						hotel.setName(hotelSummary.getChn_name());
						hotel.setLanguage(0);
						hotel.setEnname(hotelSummary.getEng_name());
						hotel.setCityid(city.getId());
						//
						/*Region region = new Region();
						region.setCityid(city.getId());
						region.setName(hotelSummary.getZone());
						region.setType("商业区");
						region.setLanguage(0);
						region=servier.createRegion(region);*/
						//
						//hotel.setRegionid2(region.getId());
						
						//hotel=servier.createHotel(hotel);
					
						System.out.println(hotel);
						}
					}
				}
				}else
				{
					System.out.println(hotelResponse.getResult().getMessage()+"--"+city.getName());
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				Label labelid = new Label(0, sheetindex, city.getId()+"");    
		        sheet.addCell(labelid); 
		        Label labelcity = new Label(1, sheetindex, city.getName()+"/"+city.getAreacode());    
		        sheet.addCell(labelcity);
		        Label labelpic = new Label(2, sheetindex, e.toString());    
		        sheet.addCell(labelpic); 
		        sheetindex++;
			}
		}

        workbook.write();    
        workbook.close(); 
  }
}  
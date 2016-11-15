package test_zhuna;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.region.Region;
import com.ccservice.b2b2c.base.service.IHotelService;




public class readRegion {

	

	public static void main(String[] args) throws Exception{
		
		//download("http://p.zhuna.cn/Hotel_Images/160x120_201012416444628140.jpg", "F:\\Hotel_Images\\abcd.jpg");
		
	
		int xlsindex =1;
		//File filesss=new File("d://zhuna/hotel.xls");
		//WritableWorkbook workbook=Workbook.createWorkbook(filesss); 
		
		
		
		//WritableSheet sheet = workbook.createSheet("Hotel", 0); 
		 	
	//	Label label0 = new Label(0, 0, "酒店ID");    
	//	sheet.addCell(label0); 
		 String url = "http://121.37.59.209:88/jx_service/service/";
    	
	    	HessianProxyFactory factory = new HessianProxyFactory();
			IHotelService servier = (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName()) ;
			String response="";		
			//21800
			//4201 ---4221
		for(int a =21369;a<21780;a++){
			try{
			 response=getDateString(a);
		
		//System.out.println("response=="+response);
		Document document = DocumentHelper.parseText(response);			
		Element root = document.getRootElement();
		//List<Element> list = root.elements("hotelinfo");
		Hotel hotel=new Hotel();
		if(root.attributeValue("HotelName")!=""){
			//商业区	
			List<Element> listshangyequ = root.elements("shangyequ");
			//	hotel.setr(listshangyequ.get(0).attributeValue("v"));
					
			List<Element> listshangyequid = root.elements("shangyequid");
			//hotel.setRegionid2(Long.parseLong(listshangyequ.get(0).attributeValue("v")));		
			//
			
			//创建商业区
			Region region =new Region();
			if(listshangyequ.get(0).attributeValue("v")!=""){
			region.setCityid(Long.parseLong(root.attributeValue("cityid")));
			region.setLanguage(1);
			region.setType("商业区");
			region.setName(listshangyequ.get(0).attributeValue("v"));
			
			region.setRegionid(listshangyequid.get(0).attributeValue("v"));
			region=servier.createRegion(region);
			}
			//
			hotel=servier.findHotel(a);
			
			
			hotel.setRegionid2(region.getId());	
			
			hotel.setState(3);
				
		   servier.updateHotelIgnoreNull(hotel);
				
				//System.out.println("hotel===="+hotel);
			
		}
			} catch (Exception e) {
				// TODO: handle exception
				
				
				
		
			
		      
		      
		        
					 


				
			}
		}
		System.out.println("OKokOKOKOKOK");
		
		
	}
	public static String getDateString(int hid)
	{
		System.out.println("id=="+hid);
		//String urltemp="http://un.zhuna.cn/api/gbk/city.asp?u=6&m=ed314f60ea53fb5e";导入城市
		String urltemp="http://un.zhuna.cn/api/gbk/hotelinfo.asp?u=399124&m=be0ed39211a5b4e6&hid="+hid;//导入酒店
		URL url;
		try {
			url = new URL(urltemp);
			URLConnection connection = url.openConnection();  
	
			String sCurrentLine;  
		    String sTotalString;  
		    sCurrentLine = "";  
		    sTotalString = "";  
		    InputStream l_urlStream;  
		    l_urlStream = connection.getInputStream();  
		    BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));  
		    while ((sCurrentLine = l_reader.readLine()) != null) {  
		    sTotalString += sCurrentLine + "\r\n";  
		    }
		    return sTotalString;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return "";
	}
	 
	 



}


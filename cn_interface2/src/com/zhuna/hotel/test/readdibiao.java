package com.zhuna.hotel.test;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotellandmark.Hotellandmark;
import com.ccservice.b2b2c.base.service.IHotelService;




public class readdibiao {


	public static void main(String[] args) throws Exception{
			String url = "http://localhost:8080/sj_service/service/";
    	
	    	HessianProxyFactory factory = new HessianProxyFactory();
			IHotelService servier = (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName()) ;
			String response="";		
			
		//
			List<Hotel> listHotel = servier.findAllHotel("where 1=1 ", "ORDER BY ID", -1, 0);
			List<City> listcity = servier.findAllCity("where 1=1 ", "ORDER BY ID", -1, 0);
			
			System.out.println("listHotel=="+listHotel.size());
			
			for(Hotel h:listHotel){
				
				for(City c:listcity){
						try{
							
							response=getDateString(h.getId(),c.getId());
						
							Document document = DocumentHelper.parseText(response);
							
							Element root = document.getRootElement();
							try{
							List<Element> listnear = root.elements("near");
						
							} catch (Exception e) {
								break;
					
								}
							Hotellandmark hotellandmark = new Hotellandmark();
							hotellandmark.setHotelid(h.getId());
							//hotellandmark.set
						 
					} catch (Exception e) {
					// TODO: handle exception
		
					}	
				}
			}
		
			
			System.out.println("����ѭ������,,,����");
			
			
			
			
			
			
	
		
	}
	public static String getDateString(long hid,long cid)
	{
		
		String urltemp="http://un.zhuna.cn/api/gbk/around.asp?u=6&m=ed314f60ea53fb5e&hid="+hid+"&cityid=+"+cid;
		URL url;
		try {
			url = new URL(urltemp);
			URLConnection connection = url.openConnection();  
			connection.setDoOutput(true);  
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.flush();  
		    out.close();  
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
	 public static void download(String urlString, String filename) throws Exception {    
		    // ����URL    
		    URL url = new URL(urlString);    
		    // ������    
		    URLConnection con = url.openConnection();   
		    // ������    
		    InputStream is = con.getInputStream();   
		  
		    // 1K�����ݻ���    
		    byte[] bs = new byte[1024];    
		    // ��ȡ�������ݳ���    
		    int len;    
		    // ������ļ���    
		    OutputStream os = new FileOutputStream(filename);    
		    // ��ʼ��ȡ    
		    while ((len = is.read(bs)) != -1) {    
		      os.write(bs, 0, len);    
		   }   
		    // ��ϣ��ر���������    
		    os.close();   
		    is.close();   
		  }  


}


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
import com.ccservice.b2b2c.base.service.IHotelService;




public class readcity {


	public static void main(String[] args) throws Exception{
			String url = "http://localhost:8080/cn_service/service/";
    	
	    	HessianProxyFactory factory = new HessianProxyFactory();
			IHotelService servier = (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName()) ;
			String response="";		
			
		
			
			
		
			
			
				
	
						try{
							
							response=getDateString();
							
						} catch (Exception e) {
							// TODO: handle exception
				
							}
						
						    System.out.println("response=="+response);
							Document document = DocumentHelper.parseText(response);
							
							Element root = document.getRootElement();
						
							List<Element> list = root.elements("city");
							for(int a=0;a<list.size();a++){
								City city = new City();
								System.out.println("����name=="+list.get(a).attributeValue("name"));
								List<City> listcity = servier.findAllCity("where 1=1 and "+City.COL_name+" ='"+list.get(a).attributeValue("name")+"'", "ORDER BY ID ", -1, 0);
								if(listcity.size()>0){
									
									city=listcity.get(0);
									city.setName(list.get(a).attributeValue("name"));
									city.setCarcode(list.get(a).attributeValue("ID"));// ���б���
									city.setEnname(list.get(a).attributeValue("pinyin"));//Ӣ��ȫƴ
									city.setSname(list.get(a).attributeValue("suoxie"));//Ӣ�ļ���
									city.setIscode(list.get(a).attributeValue("HotelNum"));//�Ƶ�����
									servier.updateCityIgnoreNull(city);
									System.out.println("�����˳���");
								}else{
									city.setName(list.get(a).attributeValue("name"));
									city.setCarcode(list.get(a).attributeValue("ID"));// ���б���
									city.setEnname(list.get(a).attributeValue("pinyin"));//Ӣ��ȫƴ
									city.setSname(list.get(a).attributeValue("suoxie"));//Ӣ�ļ���
									city.setIscode(list.get(a).attributeValue("HotelNum"));//�Ƶ�����
									servier.createCity(city);
									
									System.out.println("�����˳���");
								}
								
							}
							
							
						 
						
				
			
		
			
			System.out.println("����ѭ������,");
			
			
			
			
			
			
	
		
	}
	public static String getDateString()
	{
		
		String urltemp="http://un.zhuna.cn/api/gbk/city.asp?u=&m=";
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


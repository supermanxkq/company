package com.zhuna.hotel.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import com.ccservice.b2b2c.base.hotelimage.Hotelimage;
import com.ccservice.b2b2c.base.service.IHotelService;




public class readHotelImagesByHotel {


	public static void main(String[] args) throws Exception{
		
		 String url = "http://localhost:8080/cn_service/service/";
    	
	    	HessianProxyFactory factory = new HessianProxyFactory();
			IHotelService servier = (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName()) ;
			
			List<Hotel>listh=servier.findAllHotel(" where 1=1 and "+Hotel.COL_type+" =1 and "+Hotel.COL_hotelcode+" is not null ", "", -1, 0);
			
			String response="";		
		for(int a=0;a<listh.size();a++){
			try{
			City cit=servier.findCity(listh.get(a).getCityid());
			 response=getDateString(listh.get(a).getHotelcode(),cit.getCarcode());
		
		
		Document document = DocumentHelper.parseText(response);			
		Element root = document.getRootElement();
		
		Hotel hotel=new Hotel();
		if(root.attributeValue("HotelName")!=""){
			
			List<Element> listPicture  = root.elements("Picture");
			String [] Picture = listPicture.get(0).attributeValue("hpicmin").split(",");
			
			
			String [] hpictxt = listPicture.get(0).attributeValue("hpictxt").split(",");
			
				int s =0;
				if(listPicture.get(0).attributeValue("hpicnum")!=""){
						s =Integer.parseInt(listPicture.get(0).attributeValue("hpicnum"));
				}
				servier.createHotel(hotel);
				if(s>0){
				for(int d=0;d<s;d++){
				Hotelimage hotelimage =new Hotelimage();
				hotelimage.setHotelid(hotel.getId());
				hotelimage.setLanguage(0);
				hotelimage.setType(1);
				hotelimage.setDescription(hpictxt[d]);
				String sss=Picture[d];
				String aaa=	sss.substring(sss.lastIndexOf('/'));
				hotelimage.setPath("2011-08-16/"+hotel.getId()+"/"+aaa);
				servier.createHotelimage(hotelimage);
				File file=new File("F://Hotel_Images/"+hotel.getId());
				file.mkdirs();
				download(sss,"F://Hotel_Images/"+hotel.getId()+hotelimage.getPath());
				}
		}
		}
			} catch (Exception e) {
				// TODO: handle exception
				

				
			}
		}
		System.out.println("OKokOKOKOKOK");
		
	
		
	}
	public static String getDateString(String hid,String cid)
	{
		//String urltemp="http://un.zhuna.cn/api/gbk/city.asp?u=6&m=ed314f60ea53fb5e";导入城市
		String urltemp="http://un.zhuna.cn/api/gbk/hotelinfo.asp?u=6&m=ed314f60ea53fb5e&hid="+hid+"&cityid="+cid;//导入酒店
		
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
	 public static void download(String urlString, String filename) throws Exception {    
		    // 构造URL    
		    URL url = new URL(urlString);    
		    // 打开连接    
		    URLConnection con = url.openConnection();   
		    // 输入流    
		    InputStream is = con.getInputStream();   
		  
		    // 1K的数据缓冲    
		    byte[] bs = new byte[1024];    
		    // 读取到的数据长度    
		    int len;    
		    // 输出的文件流    
		    OutputStream os = new FileOutputStream(filename);    
		    // 开始读取    
		    while ((len = is.read(bs)) != -1) {    
		      os.write(bs, 0, len);    
		   }   
		    // 完毕，关闭所有链接    
		    os.close();   
		    is.close();   
		  }
	 



}


package com.zhuna.hotel.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelimage.Hotelimage;
import com.ccservice.b2b2c.base.service.IHotelService;




public class readHotelImagesById {


	public static void main(String[] args) throws Exception{
		
		 String url = "http://localhost:8080/cn_service/service/";
    	
	    	HessianProxyFactory factory = new HessianProxyFactory();
			IHotelService servier = (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName()) ;
			
			List<Hotel>listh=servier.findAllHotel(" where 1=1 and "+Hotel.COL_type+" =1 and "+Hotel.COL_hotelcode+" is not null ", "", 1, 0);
			
			String response="";		
			Hotel hotel=new Hotel();
		for(int a=0;a<listh.size();a++){
			try{
			City cit=servier.findCity(listh.get(a).getCityid());
			 response=getDateString(listh.get(a).getHotelcode(),cit.getCarcode());
			 hotel=listh.get(a);
		System.out.println("�Ƶ�code=="+hotel.getHotelcode());
		Document document = DocumentHelper.parseText(response);			
		Element root = document.getRootElement();
		//List<Element> list = root.elements("hotelinfo");
		
		if(root.attributeValue("HotelName")!=""){
			
			List<Element> listliansuo = root.elements("liansuo");
			
			if(listliansuo.get(0).attributeValue("v")!=null&&listliansuo.get(0).attributeValue("v").length()>0){
				System.out.println("l��-=="+listliansuo.get(0).attributeValue("v"));
			hotel.setFullname(listliansuo.get(0).attributeValue("v"));
			}
			
			List<Element> listPicture  = root.elements("Picture");
			String [] Picture = listPicture.get(0).attributeValue("hpicmin").split(",");
			
			
			String [] hpictxt = listPicture.get(0).attributeValue("hpictxt").split(",");
			
				int s =0;
				if(listPicture.get(0).attributeValue("hpicnum")!=""){
						s =Integer.parseInt(listPicture.get(0).attributeValue("hpicnum"));
				}
				System.out.println("s=="+s);
				
				//servier.updateHotelIgnoreNull(hotel);
				
				
				if(s>0){
				for(int d=0;d<s;d++){
				Hotelimage hotelimage =new Hotelimage();
				hotelimage.setHotelid(hotel.getId());
				hotelimage.setLanguage(0);
				hotelimage.setType(1);
				hotelimage.setDescription(hpictxt[d]);
				String sss=Picture[d];
				System.out.println("sss="+sss);
				String aaa=	sss.substring(sss.lastIndexOf('/'));
				hotelimage.setPath("2010-08-30/"+hotel.getId()+"/"+aaa);
				servier.createHotelimage(hotelimage);
				File file=new File("E:\\HotelImage\\2010-08-30\\"+hotelimage.getHotelid());
				
				
					file.mkdirs();
				
					download(sss,"E:\\HotelImage\\"+hotelimage.getPath());
				}
		}
		}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("ee=="+e);
				
				
				
				

		         
			
		      
		      
		        
					 


				
			}
		}
		System.out.println("OKokOKOKOKOK");
		
	/*	for(Element e:list)
		{
			City city=new City();
			city.setId(Long.parseLong(e.attributeValue("ID")));
			city.setName(e.attributeValue("name"));
			city.setEnname(e.attributeValue("pinyin"));
			servier.createCity(city);
		}*/
		
	}
	public static String getDateString(String hid,String cid)
	{
		//String urltemp="http://un.zhuna.cn/api/gbk/city.asp?u=6&m=ed314f60ea53fb5e";�������
		String urltemp="http://un.zhuna.cn/api/gbk/hotelinfo.asp?u=6&m=ed314f60ea53fb5e&hid="+hid+"&cityid="+cid;//����Ƶ�
		
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
		 System.out.println("urlString=="+urlString);
		 System.out.println("filename=="+filename);
		 
		    URL url = new URL(urlString);    
		    // ��l��    
		    URLConnection con = url.openConnection();   
		    // ������    
		    java.io.InputStream is = con.getInputStream();   
		  
		    // 1K����ݻ���    
		    byte[] bs = new byte[1024];    
		    // ��ȡ������ݳ���    
		    int len;    
		    // �����ļ���    
		    java.io.OutputStream os = new FileOutputStream(filename);    
		    // ��ʼ��ȡ    
		    while ((len = is.read(bs)) != -1) {    
		      os.write(bs, 0, len);    
		   }   
		    // ��ϣ��ر�����t��    
		    os.close();   
		    is.close();   
		    
		/*    // ����URL    
		    URL url = new URL(urlString);    
		    // ��l��    
		    URLConnection con = url.openConnection();   
		    // ������    
		    InputStream is = con.getInputStream();   
		  
		    // 1K����ݻ���    
		    byte[] bs = new byte[1024];    
		    // ��ȡ������ݳ���    
		    int len;    
		    // �����ļ���    
		    OutputStream os = new FileOutputStream(filename);    
		    // ��ʼ��ȡ    
		    while ((len = is.read(bs)) != -1) {    
		      os.write(bs, 0, len);    
		   }   
		    // ��ϣ��ر�����t��    
		    os.close();   
		    is.close();   */
		  }
	 



}


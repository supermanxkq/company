package com.zhuna.hotel.test;


import java.io.BufferedReader;
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
import com.ccservice.b2b2c.base.region.Region;
import com.ccservice.b2b2c.base.service.IHotelService;




public class readHotelByCityIDAndPg {


	public static void main(String[] args) throws Exception{
		
		 String url = "http://localhost:8080/cn_service/service/";
    	
	    	HessianProxyFactory factory = new HessianProxyFactory();
			IHotelService servier = (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName()) ;
			String response="";		
			int pg=1;
			List<City> listcity= servier.findAllCity(" where 1=1 and "+City.COL_carcode+" is not null  AND "+City.COL_id+" NOT IN ( SELECT distinct "+Hotel.COL_cityid+" FROM "+Hotel.TABLE+" where  "+Hotel.COL_type+" =1)", " order by id ", -1, 20);
			System.out.println("还有"+listcity.size()+"家城市没有导入酒店");
			
			for(int a =0;a<listcity.size();a++){
				System.out.println("当前城市=="+listcity.get(a).getName()+",,,ID=="+listcity.get(a).getId());
				try{
			//计算分页
		    response=getDateString(Integer.parseInt(listcity.get(a).getCarcode()),1);
		   
		    if(response.toString().indexOf("<?xml")!=-1){
			Document document2 = DocumentHelper.parseText(response);			
			Element root2 = document2.getRootElement();
			if(root2.attributeValue("totalput")!=""&&Integer.parseInt(root2.attributeValue("totalput"))>0){
			String totalput=root2.attributeValue("totalput");
			String PageSize=root2.attributeValue("PageSize");
			int pags = Integer.parseInt(totalput)/Integer.parseInt(PageSize);
				pg=pags+1;
			}
			System.out.println("城市=="+listcity.get(a).getName()+",,酒店数量=="+root2.attributeValue("totalput")+",,页数=="+pg);
			//解析数据
			for(int p=0;p<pg;p++){
					
				
				 response=getDateString(Integer.parseInt(listcity.get(a).getCarcode()),p+1);
				 //System.out.println("response="+response);
				// System.out.println(response.toString().indexOf("<?xml"));
				
				 if(response.toString().indexOf("<?xml")!=-1){
					Document document = DocumentHelper.parseText(response);			
					Element root = document.getRootElement();
					
					 //System.out.println(root.attributeValue("totalput"));
					 
					if(root.attributeValue("totalput")!=""&&Integer.parseInt(root.attributeValue("totalput"))>0){
						List<Element> listhotel = root.elements("hotel");
						System.out.println("listhotel=="+listhotel.size());
						for(int h=0;h<listhotel.size();h++){
							
						Hotel hotel = new Hotel();
						List<Hotel>listh=servier.findAllHotel(" where 1=1 and "+Hotel.COL_name+"='"+listhotel.get(h).attributeValue("Name")+"'", "", -1, 0);	
						if(listh.size()>0){
							hotel=listh.get(0);
						}	
							
						//商业区	
						List<Element> listshangyequ = listhotel.get(h).elements("shangyequ");
						List<Element> listshangyequid = listhotel.get(h).elements("shangyequid");
						//创建商业区
						Region region =new Region();
						if(listshangyequ.get(0).attributeValue("v")!=""){
						region.setCityid(listcity.get(a).getId());
						region.setLanguage(1);
						
						region.setName(listshangyequ.get(0).attributeValue("v"));
						region.setType("商业区");
						region.setRegionid(listshangyequid.get(0).attributeValue("v"));
						region=servier.createRegion(region);
						}
						
						hotel.setHotelcode(listhotel.get(h).attributeValue("ID"));
						hotel.setRegionid2(region.getId());	
						hotel.setLanguage(1);
						hotel.setName(listhotel.get(h).attributeValue("Name"));
						List<Element> listPicture  = listhotel.get(h).elements("Picture");
						hotel.setCheckdesc(listPicture.get(0).attributeValue("v"));//图片路径
						hotel.setType(1);//1,国内   2,国际
						hotel.setStar(Integer.parseInt(listhotel.get(0).attributeValue("xingji")));
						hotel.setStartprice(Double.parseDouble(listhotel.get(0).attributeValue("mjiage")));
						List<Element> listContent = listhotel.get(h).elements("Content");
						hotel.setDescription(listContent.get(0).getStringValue());
						List<Element> list = listhotel.get(h).elements("Address");
						hotel.setAddress(list.get(0).attributeValue("v"));
						
						List<Element> listmapbz = listhotel.get(h).elements("mapbz");
						if(listmapbz.size()>0){
						hotel.setLat(Double.parseDouble(listmapbz.get(0).attributeValue("y")));
						hotel.setLng(Double.parseDouble(listmapbz.get(0).attributeValue("x")));
						}
						List<Element> listService = listhotel.get(h).elements("Service");
						hotel.setServiceitem(listService.get(0).getStringValue());
						
						List<Element> listCanyin = listhotel.get(h).elements("Canyin");
						hotel.setFootitem(listCanyin.get(0).getStringValue());
						
						List<Element> listCard = listhotel.get(h).elements("Card");
						hotel.setCarttype(listCard.get(0).getStringValue());
						hotel.setCityid(listcity.get(a).getId());
						if(listh.size()>0){
							servier.updateHotelIgnoreNull(hotel);
							System.out.println("uupdate--hotel=="+hotel);
						}else{
						servier.createHotel(hotel);
						System.out.println("add----hotel=="+hotel);
						}
						
						
						
						
							
						
						}
						
					}	
					
				 }
			
			}
			
		    }
				} catch (Exception e) {
					// TODO: handle exception
					
					
					
					
					
					
					
				
			      
			      
			        
						 


					
				}
			}
		System.out.println("完了");
		
		 
	
		
	}
	public static String getDateString(int cid,int pg)
	{
		//String urltemp="http://un.zhuna.cn/api/gbk/city.asp?u=6&m=ed314f60ea53fb5e";导入城市
		String urltemp="http://un.zhuna.cn/api/gbk/search.asp?u=831713&m=54fee3e2184ce526&cityid="+cid+"&pg="+pg;
		
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


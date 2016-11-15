package test_zhuna;


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
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelimage.Hotelimage;
import com.ccservice.b2b2c.base.region.Region;
import com.ccservice.b2b2c.base.service.IHotelService;




public class readCity {

	public static void aa(String s){
		s="";
	}

	public static void main(String[] args) throws Exception{
		String str="424234";
		aa(str);
		System.out.println(str);
		
		//download("http://p.zhuna.cn/Hotel_Images/160x120_201012416444628140.jpg", "F:\\Hotel_Images\\abcd.jpg");
		
	
		int xlsindex =1;
		//File filesss=new File("d://zhuna/hotel.xls");
		//WritableWorkbook workbook=Workbook.createWorkbook(filesss); 
		
		
		
		//WritableSheet sheet = workbook.createSheet("Hotel", 0); 
		 	
	//	Label label0 = new Label(0, 0, "酒店ID");    
	//	sheet.addCell(label0); 
		 String url = "http://localhost:8080/jx_service/service/";
    	
	    	HessianProxyFactory factory = new HessianProxyFactory();
			IHotelService servier = (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName()) ;
			String response="";		
			//21800
			//7273 7441  9793
		for(int a =8888;a<8889;a++){
			try{
			 response=getDateString(a);
		
		System.out.println("response=="+response);
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
			
			region.setName(listshangyequ.get(0).attributeValue("v"));
			region.setType("商业区");
			region.setRegionid(listshangyequid.get(0).attributeValue("v"));
			region=servier.createRegion(region);
			}
			//
			
			
			hotel.setId(Long.parseLong(root.attributeValue("id")));
			hotel.setRegionid2(region.getId());	
			
			hotel.setLanguage(1);
			hotel.setCountryid(168l);
			String hname=root.attributeValue("HotelName");
			hotel.setName(root.attributeValue("HotelName"));
			hotel.setStar(Integer.parseInt(root.attributeValue("xingji")));
			hotel.setCityid(Long.parseLong(root.attributeValue("cityid")));
			
			
			
			List<Element> list = root.elements("Address");
			hotel.setAddress(list.get(0).attributeValue("v"));
			
			List<Element> listmapbz = root.elements("mapbz");
			if(listmapbz.size()>0){
			hotel.setLat(Double.parseDouble(listmapbz.get(0).attributeValue("y")));
			hotel.setLng(Double.parseDouble(listmapbz.get(0).attributeValue("x")));
			}
			
			List<Element> listyule = root.elements("Yulejianshen");
			hotel.setPlayitem(listyule.get(0).getStringValue());
			
			List<Element> listService = root.elements("Service");
			hotel.setServiceitem(listService.get(0).getStringValue());
			
			List<Element> listCanyin = root.elements("Canyin");
			hotel.setFootitem(listCanyin.get(0).getStringValue());
			
			List<Element> listCard = root.elements("Card");
			hotel.setCarttype(listCard.get(0).getStringValue());
			
			List<Element> listContent = root.elements("Content");
			hotel.setDescription(listContent.get(0).getStringValue());
			
			List<Element> listjianshu = root.elements("jianshu");
			hotel.setSellpoint(listjianshu.get(0).getStringValue());
			
			List<Element> listPicture  = root.elements("Picture");
			
			
			String [] Picture = listPicture.get(0).attributeValue("hpicmin").split(",");
			
			
			String [] hpictxt = listPicture.get(0).attributeValue("hpictxt").split(",");
			
				int s =0;
				if(listPicture.get(0).attributeValue("hpicnum")!=""){
						s =Integer.parseInt(listPicture.get(0).attributeValue("hpicnum"));
						//System.out.println("s="+s);
				}
				//System.out.println("hotel===="+hotel);
				servier.createHotel(hotel);
				
				System.out.println("hotel===="+hotel);
						if(s>0){
						for(int d=0;d<s;d++){
						Hotelimage hotelimage =new Hotelimage();
						hotelimage.setHotelid(hotel.getId());
						hotelimage.setLanguage(0);
						hotelimage.setType(1);
						hotelimage.setDescription(hpictxt[d]);
						String sss=Picture[d];
						String aaa=	sss.substring(sss.lastIndexOf('/'));
						hotelimage.setPath("2011-01-10/"+hotel.getId()+aaa);
						servier.createHotelimage(hotelimage);
						File file=new File("F://Hotel_Images/2011-01-10/"+hotel.getId());
						file.mkdirs();
						try{
						download(sss,"F://Hotel_Images/"+hotelimage.getPath());
						}catch (Exception e) {
							e.printStackTrace();
						}
						}
				}
		}
			} catch (Exception e) {
				// TODO: handle exception
				
				
				
				
				
				
			/*	 Label label00 = new Label(0, xlsindex, a+"");    
				 xlsindex++;
		         sheet.addCell(label00); */
		         
			
		      
		      
		        
					 


				
			}
		}
		System.out.println("OKokOKOKOKOK");
		/*  workbook.write();    
	      workbook.close(); */
	/*	for(Element e:list)
		{
			City city=new City();
			city.setId(Long.parseLong(e.attributeValue("ID")));
			city.setName(e.attributeValue("name"));
			city.setEnname(e.attributeValue("pinyin"));
			servier.createCity(city);
		}*/
		
	}
	public static String getDateString(int hid)
	{
		System.out.println("进来了...............酒店id=="+hid);
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
	 public static void download(String urlString, String filename) throws Exception {    
		 //System.out.println("图片路径=="+urlString);
		// System.out.println("图片路径22=="+filename);
		    // 构造URL    
		    URL url = new URL(urlString);    
		    // 打开连接    
		    URLConnection con = url.openConnection();  
		    con.setDoInput(true);
		    // 输入流    
		    InputStream is = con.getInputStream();   
		  
		    // 1K的数据缓冲    
		    byte[] bs = new byte[1024];    
		    // 读取到的数据长度    
		    int len=0;    
		    // 输出的文件流    
		    OutputStream os = new FileOutputStream(filename);    
		    // 开始读取    
		    while ((len = is.read(bs)) >0) {    
		      os.write(bs, 0, len);    
		   }   
		    // 完毕，关闭所有链接    
		    os.close();   
		    is.close();   
		  }
	 



}


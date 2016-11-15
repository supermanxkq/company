package test_zhuna;


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
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelprice.Hotelprice;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.base.service.IHotelService;




public class readRoom {


	public static void main(String[] args) throws Exception{
			//String url = "http://121.37.59.209:88/jx_service/service/";
		String url = "http://localhost:8080/jx_service/service/";
	    	HessianProxyFactory factory = new HessianProxyFactory();
			IHotelService servier = (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName()) ;
			String response="";		
			
		//21799  16760
			List<Hotel> listHotel = servier.findAllHotel("where 1=1 and "+Hotel.COL_id+" >20014 and "+Hotel.COL_id+"<=21799 ", "ORDER BY ID", -1, 0);
			System.out.println("listHotel=="+listHotel.size());
			
			for(Hotel h:listHotel){
				try{
					System.out.println("ID=="+h.getId());
					response=getDateString(h.getId());
					//System.out.println(response.indexOf("?xml")==1);
					if(response!=""&&response.indexOf("?xml")==1){
						//System.out.println("OK");
					//System.out.println(response);
					Document document = DocumentHelper.parseText(response);
					
					Element root = document.getRootElement();
				//	List<Element> listroom = root.elements("shangyequ");
				Roomtype roomtype = new Roomtype();
				
				if(root.elements("room").size()>0){
				List<Element> listroom = root.elements("room");
				for(int a=0;a<listroom.size();a++){
				
				roomtype.setId(Long.parseLong(listroom.get(a).attributeValue("rid")));
				
				roomtype.setName(listroom.get(a).attributeValue("title"));
				
				String zao = listroom.get(a).attributeValue("zaocan");
				if(zao.equals("含早")){
					roomtype.setBreakfast(2);
				}
				if(zao.equals("无")){
					roomtype.setBreakfast(1);
				}else{
					roomtype.setBreakfast(2);
				}
				String kuan = listroom.get(a).attributeValue("adsl");
				if(kuan.equals("有(免费)")){
					roomtype.setWideband(1);
				}
				if(kuan.equals("无")){
					roomtype.setWideband(0);
				}
				if(kuan.equals("有")){
					roomtype.setWideband(2);
				}else{
					roomtype.setWideband(1);
				}
				String chuan = listroom.get(a).attributeValue("Bed");
				
				if(chuan.equals("大床/双床")){
					
					roomtype.setBed(4);
				}
				if(chuan.equals("大床")){
					
					roomtype.setBed(2);
				}
				if(chuan.equals("双床")){
					
					roomtype.setBed(3);
				}
				
				if(chuan.equals("单人床")){
					
					roomtype.setBed(1);
				}else{
					roomtype.setBed(5);
				}
				
				roomtype.setAreadesc(listroom.get(a).attributeValue("jiangjin"));
				
				roomtype.setHotelid(h.getId());
				roomtype.setLanguage(0);
				roomtype.setState(1);
				//System.out.println("roomtype=="+roomtype);
				roomtype=servier.createRoomtype(roomtype);
				
				Hotelprice hotelprice = new Hotelprice();
				String startDate = root.attributeValue("tm1");
				String endDate = root.attributeValue("tm2");
				String snian=startDate.trim().substring(0,7);//开始yue
				
				String sday=startDate.trim().substring(8,10);//开始日
				String endday=endDate.trim().substring(8,10);//开始日
				
				hotelprice.setHotelid(h.getId());
				hotelprice.setDatenumber(snian);
				String men = listroom.get(a).attributeValue("Menshi");
				hotelprice.setDeptprice(men);
				hotelprice.setLanguage(0);
				hotelprice.setRoomid(roomtype.getId());
				String jia = listroom.get(a).getStringValue();
			//	String jia = listjia.get(0).getStringValue();
				String []j = jia.split(",");
				
				//hotelprice.setNo1(Double.parseDouble(j[0]));
				int inde=0;
				for(int i=Integer.parseInt(sday);i<Integer.parseInt(endday);i++)
				{
					String ii =i+"";
					if(ii.substring(0,1).equals("0")){
						ii=ii.substring(1);
					}
						if(j[inde].trim().equals("×")){//没有价格的日期
							
						Hotelprice.class.getMethod("setNo"+ii,Double.class).invoke(hotelprice,0.00);
								
						}else{
						Hotelprice.class.getMethod("setNo"+ii,Double.class).invoke(hotelprice,Double.parseDouble(j[inde].trim()));
						}
					inde++;
//					System.out.println(ratePlan[iii].getMarkert_price()+"---"+ratePlan[iii].getSale_price()+"---"+ratePlan[iii].getRateplanCode()+"----"+ratePlan[iii].getRateplanName()+"---"+ratePlan[iii].getAbleSaleDate()+"----"+ratePlan[iii].getNeed_assure()+"---"+ratePlan[iii].getAssure_type()+"---"+ratePlan[iii].getAssure_money()+"---"+ratePlan[iii].getAssure_con());
				}
				
				
				
				System.out.println("hotelprice=="+hotelprice);
				servier.createHotelprice(hotelprice);
				}
				
				
					}
					}
				 
			} catch (Exception e) {
			// TODO: handle exception
				continue;
			}	
					
			}
		
			
			System.out.println("总算循环完了");
			
			
	
		
	}
	public static String getDateString(long hid)
	{
		
		String urltemp="http://un.zhuna.cn/api/gbk/room.asp?u=399124&m=be0ed39211a5b4e6&hid="+hid+"&tm1=2011-02-01&tm2=2011-02-28";//导入房型
		URL url;
		try {
			System.out.println("url="+urltemp);
			url = new URL(urltemp);
			URLConnection connection = url.openConnection();  
			connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

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


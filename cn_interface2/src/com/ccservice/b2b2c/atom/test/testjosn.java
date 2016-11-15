package com.ccservice.b2b2c.atom.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelprice.Hotelprice;




public class testjosn {

	public static void main(String[] args) throws MalformedURLException, JSONException, SQLException {
	String josbs="var _Data=" +
			"[" +
			"{'zid':10604,'eid':'22001021','tm1':'2011-08-25','tm2':'2011-08-28','status':'0','rooms':" +
			"[" +
			"{'rid':46971,'title':'\u8c6a\u534e\u5355\u4eba\u623f','adsl':'\u514d\u8d39','bed':'\u5927\u5e8a','area':'','floor':'','status':'0','notes':'','plans':[{'planid':22945,'planname':'\u4e0d\u542b\u65e9','totalprice':525,'jiangjin':17,'date':[{'day':'2011-08-25','week':'4','menshi':268,'price':175},{'day':'2011-08-26','week':'5','menshi':268,'price':175},{'day':'2011-08-27','week':'6','menshi':268,'price':175}],'description':{'AddValues':null,'Promotion':'','GaranteeRule':null},'menshi':268,'status':0}]}," +
			"{'rid':46969,'title':'\u8c6a\u534e\u53cc\u4eba\u623f','adsl':'\u514d\u8d39','bed':'\u53cc\u5e8a','area':'28','floor':'','status':'0','notes':'','plans':[{'planid':22945,'planname':'\u4e0d\u542b\u65e9','totalprice':525,'jiangjin':17,'date':[{'day':'2011-08-25','week':'4','menshi':268,'price':175},{'day':'2011-08-26','week':'5','menshi':268,'price':175},{'day':'2011-08-27','week':'6','menshi':268,'price':175}],'description':{'AddValues':null,'Promotion':'','GaranteeRule':null},'menshi':268,'status':0}]}" +
			"]" +
			"}" +
			"];" +
			"if(callback){callback(_Data)}else{alert('Err:callback')}";
	
	String josbs2=getDateString(4281);
	
	josbs=josbs.replace("var _Data=", "");
	//josbs=josbs.replace("];if(callback){callback(_Data)}else{alert('Err:callback')}", "%;if(callback){callback(_Data)}else{alert('Err:callback')}");//
	josbs=josbs.replace(";if(callback){callback(_Data)}else{alert('Err:callback')}", "");
	System.out.println("josbs="+josbs);
	JSONArray jsonObject = new JSONArray(josbs); 
	
	
	JSONObject josnobj = (JSONObject) jsonObject.get(0);
	
	
	String hid=josnobj.getString("zid");//酒店ID
	System.out.println("hid=="+hid);
    String stime=josnobj.getString("tm1");//入住时间
    String etime=josnobj.getString("tm2"); //离店时间  
    String state=josnobj.getString("status"); //状态  0是正常
    String[] stingroom = josbs.split("'rooms':");
    System.out.println("stingroom=="+stingroom);
    //解析房型
    String josnroom=stingroom[1];
  System.out.println(josnroom.toString());
    /* josnroom="[" +
	"{'rid':46971,'title':'\u8c6a\u534e\u5355\u4eba\u623f','adsl':'\u514d\u8d39','bed':'\u5927\u5e8a','area':'','floor':'','status':'0','notes':'','plans':[{'planid':22945,'planname':'\u4e0d\u542b\u65e9','totalprice':525,'jiangjin':17,'date':[{'day':'2011-08-25','week':'4','menshi':268,'price':175},{'day':'2011-08-26','week':'5','menshi':268,'price':175},{'day':'2011-08-27','week':'6','menshi':268,'price':175}],'description':{'AddValues':null,'Promotion':'','GaranteeRule':null},'menshi':268,'status':0}]}," +
	"{'rid':46969,'title':'\u8c6a\u534e\u53cc\u4eba\u623f','adsl':'\u514d\u8d39','bed':'\u53cc\u5e8a','area':'28','floor':'','status':'0','notes':'','plans':[{'planid':22945,'planname':'\u4e0d\u542b\u65e9','totalprice':525,'jiangjin':17,'date':[{'day':'2011-08-25','week':'4','menshi':268,'price':175},{'day':'2011-08-26','week':'5','menshi':268,'price':175},{'day':'2011-08-27','week':'6','menshi':268,'price':175}],'description':{'AddValues':null,'Promotion':'','GaranteeRule':null},'menshi':268,'status':0}]}" +
	"]";*/
    josnroom=josnroom.replace("}%", "");
    JSONArray jsonObjectroom = new JSONArray(josnroom); 
    System.out.println("数组大小=="+jsonObjectroom.length());
	    for(int a=0;a<jsonObjectroom.length();a++){
		    JSONObject josnobjroom = (JSONObject) jsonObjectroom.get(a);
		  
		    String pricestring =jsonObjectroom.get(a).toString();
		   // System.out.println("pricestring=="+pricestring);
		    String test="{'plans':[{'planid':22945,'planname':'不含早','status':0,'totalprice':525,'description':{'Promotion':'','GaranteeRule':null,'AddValues':null},'date':[{'price':175,'day':'2011-08-25','menshi':268,'week':'4'},{'price':175,'day':'2011-08-26','menshi':268,'week':'5'},{'price':175,'day':'2011-08-27','menshi':268,'week':'6'}],'jiangjin':17,'menshi':268}],'title':'豪华单人房','area':'','floor':'','status':'0','rid':46971,'bed':'大床','notes':'','adsl':'免费'}";
		    //
		    pricestring=pricestring.replace("{\"plans\":", "");
		  //  System.out.println("pricestring1=="+pricestring);
		    String[] stingroompr = pricestring.split(",\"title\"");
		    pricestring=stingroompr[0];
		    System.out.println("pricestring2=="+pricestring);
		    
		    JSONArray jsonObjectprice = new JSONArray(pricestring); 
		    JSONObject josnobjpr = (JSONObject) jsonObjectprice.get(0);
		    System.out.println("jsonObjectprice=="+jsonObjectprice.length());
		    
		    System.out.println("房型ID="+josnobjroom.get("rid"));
		    System.out.println("房型="+josnobjroom.get("title"));
		    System.out.println("宽带="+josnobjroom.get("adsl"));
		    System.out.println("床型="+josnobjroom.get("bed"));
		    System.out.println("planid="+josnobjpr.get("planid"));
		    System.out.println("早餐="+josnobjpr.get("planname"));
		    
		    String pricrsr=pricestring.toString();
		    
		    String[] dateprice = pricrsr.split(",\"date\":");
		    pricrsr=dateprice[1];
		    String[] datepricedeletemenshi = pricrsr.split(",\"jiangjin\"");
		    pricrsr=datepricedeletemenshi[0];
		    System.out.println("pricrsr=="+pricrsr);
		    pricrsr="[{'price':175,'day':'2011-08-25','menshi':268,'week':'4'},{'price':176,'day':'2011-08-26','menshi':269,'week':'5'},{'price':177,'day':'2011-08-27','menshi':270,'week':'6'}]";
		    //解析每天的价格
		    JSONArray jsonObjectdataprice = new JSONArray(pricrsr); 
		    JSONObject josnobjprdate = (JSONObject) jsonObjectdataprice.get(0);
		    System.out.println("jsonObjectdataprice的大小=="+jsonObjectdataprice.length());
		    System.out.println("价格=="+josnobjprdate.get("price"));
		    for(int p=0;p<jsonObjectdataprice.length();p++){
		    	
		    	
		    	  JSONObject josnobjroomdayprice = (JSONObject) jsonObjectdataprice.get(p);
		    	  
		    	  
		    	  
		    	  
		    	  System.out.println("day=="+josnobjroomdayprice.getString("day"));
		    	  System.out.println("price=="+josnobjroomdayprice.getString("price"));
		    	  System.out.println("menshi=="+josnobjroomdayprice.getString("menshi"));
		    	  String pr=josnobjroomdayprice.getString("price");
		    	  String datenum=josnobjroomdayprice.getString("day");
		    	  String datenumber=datenum.substring(0, 7);
		    	 
		    	  
		    	  String[] datearray=datenum.trim().split("-");
		    	  String day="";
					if(datearray[2].substring(0,1).equals("0"))
					{
						day=datearray[2].substring(1);
					}else
					{
						day=datearray[2];
					}
					
					
					
					
					long hotelid=2841l;
					long roomid=2841;
					 Hotelprice hotelprice = new Hotelprice();
					List<Hotelprice>listhotelprice=Server.getInstance().getHotelService().findAllHotelprice(" where 1=1 and "+Hotelprice.COL_hotelid+" ="+hotelid+" and "+Hotelprice.COL_datenumber+" ='"+datenumber+"' and "+Hotelprice.COL_roomid+" ="+roomid, "", -1, 0);
					if(listhotelprice.size()>0){
					hotelprice=listhotelprice.get(0);
					
					}
					hotelprice.setDeptprice(josnobjroomdayprice.getString("menshi"));
					hotelprice.setDatenumber(datenumber);
					hotelprice.setRoomid(roomid);
					hotelprice.setHotelid(hotelid);
					hotelprice.setLanguage(1);
					
					 try {
							Hotelprice.class.getMethod("setNo"+day,Double.class).invoke(hotelprice,Double.parseDouble(pr));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
					if(listhotelprice.size()>0){
						Server.getInstance().getHotelService().updateHotelpriceIgnoreNull(hotelprice);
						
						
					}else{
						
						Server.getInstance().getHotelService().createHotelprice(hotelprice);
					}
		    	 
		    	  
		    	  
		    	  
		    	
		    }
		    
		    
	    }
	}
	public static String getDateString(long hid)
	{
		
		String urltemp="http://www.api.zhuna.cn/e/json.php?hid="+hid+"&tm1=2011-09-05&tm2=2011-09-15&orderfrom=0&call=callback";
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
}

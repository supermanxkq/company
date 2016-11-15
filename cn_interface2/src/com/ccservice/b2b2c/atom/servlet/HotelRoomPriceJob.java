package com.ccservice.b2b2c.atom.servlet;

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
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelprice.Hotelprice;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.base.service.IHotelService;

public class HotelRoomPriceJob implements Job{


    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("--------------------------------------");
        try {

            String url = "http://localhost:8080/cn_service/service/";

            HessianProxyFactory factory = new HessianProxyFactory();
            IHotelService servier = (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName());
            String response = "";
            List<City> listcity = servier.findAllCity(" where 1=1 and " + City.COL_language + " =1   AND " + City.COL_id + " NOT IN ( SELECT distinct " + Hotel.COL_cityid + " FROM " + Hotel.TABLE
                    + " where  " + Hotel.COL_type + " =1)", " order by id ", -1, 0);
            for (int c = 0; c < listcity.size(); c++) {
                WriteLog.write("city", c + "");
                List<Hotel> listHotel = servier.findAllHotel(" where 1=1 and " + Hotel.COL_type + " =1 and " + Hotel.COL_hotelcode + " is not null and " + Hotel.COL_id + " not in ( SELECT "
                        + Roomtype.COL_hotelid + " FROM " + Roomtype.TABLE + " )", "ORDER BY ID", -1, 0);
                System.out.println("listHotel==" + listHotel.size());

                for (Hotel h : listHotel) {
                    try {
                        if (!h.getHotelcode().equals("10583")) {
                            response = getDateString(h.getHotelcode());
                        }

                        System.out.println(response != "" && response.indexOf("?xml") == 1 && response.indexOf("<hotel") != -1 && response.indexOf("</hotel>") != -1);
                        if (response != "" && response.indexOf("?xml") == 1 && response.indexOf("<hotel") != -1 && response.indexOf("</hotel>") != -1) {
                            System.out.println("OK");
                            System.out.println(response);
                            Document document = DocumentHelper.parseText(response);

                            Element root = document.getRootElement();

                            if (root.elements("room").size() > 0) {
                                List<Element> listroom = root.elements("room");
                                for (int a = 0; a < listroom.size(); a++) {

                                    Roomtype roomtype = new Roomtype();

                                    List<Roomtype> listroomtype = servier.findAllRoomtype(" where 1=1 and " + Roomtype.COL_name + " ='" + listroom.get(a).attributeValue("title") + "' and "
                                            + Roomtype.COL_hotelid + " =" + h.getId(), "", -1, 0);
                                    if (listroomtype.size() > 0) {
                                        roomtype = listroomtype.get(0);

                                    }

                                    roomtype.setRoomcode(listroom.get(a).attributeValue("rid"));
                                    roomtype.setName(listroom.get(a).attributeValue("title"));

                                    String zao = listroom.get(a).attributeValue("zaocan");
                                    if (zao.equals("含单早")) {
                                        roomtype.setBreakfast(2);
                                    }
                                    if (zao.equals("含双早")) {
                                        roomtype.setBreakfast(3);
                                    }
                                    if (zao.equals("无")) {
                                        roomtype.setBreakfast(1);
                                    }
                                    else {
                                        roomtype.setBreakfast(2);
                                    }
                                    String kuan = listroom.get(a).attributeValue("adsl");
                                    if (kuan.equals("免费")) {
                                        roomtype.setWideband(1);
                                    }
                                    if (kuan.equals("无")) {
                                        roomtype.setWideband(0);
                                    }
                                    if (kuan.equals("有")) {
                                        roomtype.setWideband(2);
                                    }
                                    else {
                                        roomtype.setWideband(1);
                                    }

                                    String chuan = listroom.get(a).attributeValue("Bed");

                                    if (chuan.equals("大床/双床")) {

                                        roomtype.setBed(4);
                                    }
                                    if (chuan.equals("大床")) {

                                        roomtype.setBed(2);
                                    }
                                    if (chuan.equals("双床")) {

                                        roomtype.setBed(3);
                                    }

                                    if (chuan.equals("单人床")) {

                                        roomtype.setBed(1);
                                    }
                                    else {
                                        roomtype.setBed(5);
                                    }

                                    roomtype.setAreadesc(listroom.get(a).attributeValue("jiangjin"));

                                    roomtype.setHotelid(h.getId());
                                    roomtype.setLanguage(0);
                                    roomtype.setState(1);

                                    if (listroomtype.size() > 0) {
                                        servier.updateRoomtypeIgnoreNull(roomtype);

                                    }
                                    else {
                                        roomtype = servier.createRoomtype(roomtype);
                                    }

                                    Hotelprice hotelprice = new Hotelprice();
                                    String startDate = root.attributeValue("tm1");
                                    String snian = startDate.trim().substring(0, 7);//开始yue

                                    List<Hotelprice> listprice = servier.findAllHotelprice(" where 1=1 and " + Hotelprice.COL_hotelid + " =" + h.getId() + " and " + Hotelprice.COL_roomid + " ="
                                            + roomtype.getId() + " and " + Hotelprice.COL_datenumber + " ='" + startDate.trim().substring(0, 7) + "'", "", -1, 0);
                                    if (listprice.size() > 0) {
                                        hotelprice = listprice.get(0);

                                    }

                                    hotelprice.setHotelid(h.getId());
                                    hotelprice.setDatenumber(snian);
                                    String men = listroom.get(a).attributeValue("Menshi");
                                    hotelprice.setDeptprice(men);
                                    hotelprice.setLanguage(0);
                                    hotelprice.setRoomid(roomtype.getId());
                                    String jia = listroom.get(a).getStringValue();
                                    //	String jia = listjia.get(0).getStringValue();
                                    String[] j = jia.split(",");
                                    Double price = 0.0;

                                    for (int p = 0; p < j.length; p++) {
                                        if (!j[p].equals("×")) {
                                            price = Double.parseDouble(j[0]);

                                        }

                                    }

                                    hotelprice.setNo1(price);
                                    hotelprice.setNo2(price);
                                    hotelprice.setNo3(price);
                                    hotelprice.setNo4(price);
                                    hotelprice.setNo5(price);
                                    hotelprice.setNo6(price);
                                    hotelprice.setNo7(price);
                                    hotelprice.setNo8(price);
                                    hotelprice.setNo9(price);
                                    hotelprice.setNo10(price);
                                    hotelprice.setNo11(price);
                                    hotelprice.setNo12(price);
                                    hotelprice.setNo13(price);
                                    hotelprice.setNo14(price);
                                    hotelprice.setNo15(price);
                                    hotelprice.setNo16(price);
                                    hotelprice.setNo17(price);
                                    hotelprice.setNo18(price);
                                    hotelprice.setNo19(price);
                                    hotelprice.setNo20(price);
                                    hotelprice.setNo21(price);
                                    hotelprice.setNo22(price);
                                    hotelprice.setNo23(price);
                                    hotelprice.setNo24(price);
                                    hotelprice.setNo25(price);
                                    hotelprice.setNo26(price);
                                    hotelprice.setNo27(price);
                                    hotelprice.setNo28(price);
                                    hotelprice.setNo29(price);
                                    hotelprice.setNo30(price);
                                    hotelprice.setNo31(price);

                                    if (listprice.size() > 0) {
                                        servier.updateHotelpriceIgnoreNull(hotelprice);

                                    }
                                    else {
                                        servier.createHotelprice(hotelprice);
                                    }
                                }

                            }
                        }

                    }
                    catch (Exception e) {
                        // TODO: handle exception
                        continue;
                    }

                }

                System.out.println("完了");

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    } 
	public static String getDateString(String hid)
	{
		
		String urltemp="http://un.zhuna.cn/api/gbk/room.asp?u=831713&m=54fee3e2184ce526&hid="+hid+"&tm1=2011-12-12&tm2=2011-12-20";//导入房型
		System.out.println("urltemp=="+urltemp);
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
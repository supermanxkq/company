package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelprice.Hotelprice;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.base.service.IHotelService;

public class NewHotelRoomPriceJob implements Job {

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
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

                //List<Hotel> listHotel = servier.findAllHotel(" where 1=1 and "+Hotel.COL_type+" =1 and "+Hotel.COL_hotelcode+" is not null and "+Hotel.COL_id+" not in ( SELECT "+Roomtype.COL_hotelid+" FROM "+Roomtype.TABLE+" )", "ORDER BY ID", -1, 0);
                System.out.println("listHotel==" + listHotel.size());
                if (listHotel.size() > 0) {
                    for (int h = 0; h < listHotel.size(); h++) {

                        response = getDateString(listHotel.get(h).getHotelcode());
                        System.out.println("response=" + response);
                        if (response.indexOf("zid") != -1 && response.indexOf("rid") != -1 && response.indexOf("day") != -1 && response.indexOf("price") != -1 && response.indexOf("title") != -1) {

                            response = response.replace("var _Data=", "");
                            //josbs=josbs.replace("];if(callback){callback(_Data)}else{alert('Err:callback')}", "%;if(callback){callback(_Data)}else{alert('Err:callback')}");//
                            response = response.replace(";if(callback){callback(_Data)}else{alert('Err:callback')}", "");
                            System.out.println("josbs=" + response);
                            JSONArray jsonObject = new JSONArray(response);

                            JSONObject josnobj = (JSONObject) jsonObject.get(0);

                            String hid = josnobj.getString("zid");//酒店ID
                            System.out.println("hid==" + hid);
                            String stime = josnobj.getString("tm1");//入住时间
                            String etime = josnobj.getString("tm2"); //离店时间  
                            String state = josnobj.getString("status"); //状态  0是正常
                            String[] stingroom = response.split("\"rooms\":");
                            //解析房型
                            String josnroom = stingroom[1];
                            System.out.println(josnroom.toString());
                            /* josnroom="[" +
                            "{'rid':46971,'title':'\u8c6a\u534e\u5355\u4eba\u623f','adsl':'\u514d\u8d39','bed':'\u5927\u5e8a','area':'','floor':'','status':'0','notes':'','plans':[{'planid':22945,'planname':'\u4e0d\u542b\u65e9','totalprice':525,'jiangjin':17,'date':[{'day':'2011-08-25','week':'4','menshi':268,'price':175},{'day':'2011-08-26','week':'5','menshi':268,'price':175},{'day':'2011-08-27','week':'6','menshi':268,'price':175}],'description':{'AddValues':null,'Promotion':'','GaranteeRule':null},'menshi':268,'status':0}]}," +
                            "{'rid':46969,'title':'\u8c6a\u534e\u53cc\u4eba\u623f','adsl':'\u514d\u8d39','bed':'\u53cc\u5e8a','area':'28','floor':'','status':'0','notes':'','plans':[{'planid':22945,'planname':'\u4e0d\u542b\u65e9','totalprice':525,'jiangjin':17,'date':[{'day':'2011-08-25','week':'4','menshi':268,'price':175},{'day':'2011-08-26','week':'5','menshi':268,'price':175},{'day':'2011-08-27','week':'6','menshi':268,'price':175}],'description':{'AddValues':null,'Promotion':'','GaranteeRule':null},'menshi':268,'status':0}]}" +
                            "]";*/
                            josnroom = josnroom.replace("}%", "");
                            JSONArray jsonObjectroom = new JSONArray(josnroom);
                            System.out.println("数组大小==" + jsonObjectroom.length());
                            for (int a = 0; a < jsonObjectroom.length(); a++) {
                                JSONObject josnobjroom = (JSONObject) jsonObjectroom.get(a);

                                String pricestring = jsonObjectroom.get(a).toString();
                                // System.out.println("pricestring=="+pricestring);
                                String test = "{'plans':[{'planid':22945,'planname':'不含早','status':0,'totalprice':525,'description':{'Promotion':'','GaranteeRule':null,'AddValues':null},'date':[{'price':175,'day':'2011-08-25','menshi':268,'week':'4'},{'price':175,'day':'2011-08-26','menshi':268,'week':'5'},{'price':175,'day':'2011-08-27','menshi':268,'week':'6'}],'jiangjin':17,'menshi':268}],'title':'豪华单人房','area':'','floor':'','status':'0','rid':46971,'bed':'大床','notes':'','adsl':'免费'}";
                                //
                                pricestring = pricestring.replace("{\"plans\":", "");
                                //  System.out.println("pricestring1=="+pricestring);
                                String[] stingroompr = pricestring.split(",\"title\"");
                                pricestring = stingroompr[0];
                                System.out.println("pricestring2==" + pricestring);

                                JSONArray jsonObjectprice = new JSONArray(pricestring);
                                JSONObject josnobjpr = (JSONObject) jsonObjectprice.get(0);
                                System.out.println("jsonObjectprice==" + jsonObjectprice.length());

                                System.out.println("房型ID=" + josnobjroom.get("rid"));
                                System.out.println("房型=" + josnobjroom.get("title"));
                                System.out.println("宽带=" + josnobjroom.get("adsl"));
                                System.out.println("床型=" + josnobjroom.get("bed"));
                                System.out.println("planid=" + josnobjpr.get("planid"));
                                System.out.println("早餐=" + josnobjpr.get("planname"));

                                String pricrsr = pricestring.toString();

                                String[] dateprice = pricrsr.split(",\"date\":");
                                pricrsr = dateprice[1];
                                String[] datepricedeletemenshi = pricrsr.split(",\"jiangjin\"");
                                pricrsr = datepricedeletemenshi[0];
                                System.out.println("pricrsr==" + pricrsr);

                                //解析每天的价格
                                JSONArray jsonObjectdataprice = new JSONArray(pricrsr);
                                JSONObject josnobjprdate = (JSONObject) jsonObjectdataprice.get(0);
                                System.out.println("jsonObjectdataprice的大小==" + jsonObjectdataprice.length());

                                //房型开始
                                Roomtype roomtype = new Roomtype();
                                String whereroom = " where 1=1 and " + Roomtype.COL_name + " ='" + josnobjroom.get("title") + "' and " + Roomtype.COL_hotelid + " =" + listHotel.get(h).getId()
                                        + " and " + Roomtype.COL_roomcode + " ='" + josnobjroom.get("rid") + "'";
                                List<Roomtype> listroomtype = servier.findAllRoomtype(whereroom, " ORDER BY ID ", -1, 0);
                                if (listroomtype.size() > 0) {

                                    roomtype = listroomtype.get(0);
                                }

                                roomtype.setName(josnobjroom.get("title").toString().trim());
                                roomtype.setHotelid(listHotel.get(h).getId());
                                roomtype.setRoomcode(josnobjroom.get("rid").toString().trim());

                                String chuan = josnobjroom.get("bed").toString();
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
                                String kuan = josnobjroom.get("adsl").toString();
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
                                String zao = josnobjpr.get("planname").toString();
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

                                roomtype.setLanguage(0);
                                roomtype.setState(1);

                                if (listroomtype.size() > 0) {

                                    servier.updateRoomtypeIgnoreNull(roomtype);
                                }
                                else {
                                    roomtype = servier.createRoomtype(roomtype);

                                }

                                //房型结束
                                for (int p = 0; p < jsonObjectdataprice.length(); p++) {

                                    JSONObject josnobjroomdayprice = (JSONObject) jsonObjectdataprice.get(p);

                                    System.out.println("day==" + josnobjroomdayprice.getString("day"));
                                    System.out.println("price==" + josnobjroomdayprice.getString("price"));
                                    System.out.println("menshi==" + josnobjroomdayprice.getString("menshi"));
                                    String pr = josnobjroomdayprice.getString("price");
                                    String menshi = josnobjroomdayprice.getString("menshi");
                                    String datenum = josnobjroomdayprice.getString("day");
                                    String datenumber = datenum.substring(0, 7);

                                    String[] datearray = datenum.trim().split("-");
                                    String day = "";
                                    if (datearray[2].substring(0, 1).equals("0")) {
                                        day = datearray[2].substring(1);
                                    }
                                    else {
                                        day = datearray[2];
                                    }
                                    //
                                    Double price = 0.0;
                                    if (pr.trim().equals("×")) {
                                        if (menshi.toString().trim().equals("×")) {

                                            pr = "0.0";
                                        }
                                        else {
                                            pr = menshi.toString().trim();
                                        }
                                    }
                                    else {

                                        pr = pr.toString().trim();
                                    }
                                    //

                                    long hotelid = listHotel.get(h).getId();
                                    long roomid = roomtype.getId();

                                    Hotelprice hotelprice = new Hotelprice();
                                    List<Hotelprice> listhotelprice = Server
                                            .getInstance()
                                            .getHotelService()
                                            .findAllHotelprice(
                                                    " where 1=1 and " + Hotelprice.COL_hotelid + " =" + hotelid + " and " + Hotelprice.COL_datenumber + " ='" + datenumber + "' and "
                                                            + Hotelprice.COL_roomid + " =" + roomid, "", -1, 0);
                                    if (listhotelprice.size() > 0) {
                                        hotelprice = listhotelprice.get(0);

                                    }
                                    hotelprice.setDeptprice(menshi);
                                    hotelprice.setDatenumber(datenumber);
                                    hotelprice.setRoomid(roomid);
                                    hotelprice.setHotelid(hotelid);
                                    hotelprice.setLanguage(0);

                                    try {
                                        Hotelprice.class.getMethod("setNo" + day, Double.class).invoke(hotelprice, Double.parseDouble(pr));
                                    }
                                    catch (NumberFormatException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    catch (IllegalArgumentException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    catch (SecurityException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    catch (IllegalAccessException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    catch (InvocationTargetException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    catch (NoSuchMethodException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    if (listhotelprice.size() > 0) {
                                        Server.getInstance().getHotelService().updateHotelpriceIgnoreNull(hotelprice);

                                    }
                                    else {

                                        Server.getInstance().getHotelService().createHotelprice(hotelprice);
                                    }
                                }
                                //

                                //System.out.println("价格=="+josnobjprdate.get("price"));
                                // System.out.println("门市价格=="+josnobjprdate.get("menshi"));

                                /*Hotelprice hotelprice = new Hotelprice();
                                String snian=stime.trim().substring(0,7);//开始yue
                                List<Hotelprice>listprice=servier.findAllHotelprice(" where 1=1 and "+Hotelprice.COL_hotelid+" ="+listHotel.get(h).getId()+" and "+Hotelprice.COL_roomid+" ="+roomtype.getId()+" and "+Hotelprice.COL_datenumber+" ='"+stime.trim().substring(0,7)+"'", "", -1, 0);
                                if(listprice.size()>0){
                                	hotelprice=listprice.get(0);
                                }
                                hotelprice.setHotelid(listHotel.get(h).getId());
                                hotelprice.setDatenumber(snian);
                                String men = josnobjprdate.get("menshi").toString().trim();
                                hotelprice.setDeptprice(men);
                                hotelprice.setLanguage(0);
                                hotelprice.setRoomid(roomtype.getId());
                                Double price=0.0;
                                if(josnobjprdate.get("price").toString().trim().equals("×")){
                                	if(josnobjprdate.get("menshi").toString().trim().equals("×")){
                                		
                                		price=0.0;
                                	}else{
                                	price=Double.parseDouble(josnobjprdate.get("menshi").toString().trim());
                                	}
                                }else{
                                	
                                	price=Double.parseDouble(josnobjprdate.get("price").toString().trim());
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
                                
                                if(listprice.size()>0){
                                servier.updateHotelpriceIgnoreNull(hotelprice);
                                	
                                }else{
                                servier.createHotelprice(hotelprice);
                                }*/

                            }

                        }

                    }

                }
                System.out.println("完了!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDateString(String hid) {
        String startDate = "";
        String endDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        startDate = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, 7);
        endDate = sdf.format(calendar.getTime());

        String urltemp = "http://www.api.zhuna.cn/e/json.php?hid=" + hid + "&tm1=" + startDate + "&tm2=" + endDate + "&orderfrom=0&call=callback";
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
        }
        catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

}
package com.ccservice.b2b2c.atom.robticket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.elong.inter.PropertyUtil;

public class RobticketSupportMethod {

    /**
     * 判断订单是不是抢票订单
     * @param trainorderid
     */
    public boolean SendRobticket(long orderid){
        boolean isrobticket = false;
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorderPayinfo(orderid);
        if(trainorder.getOrderstatus()==Trainorder.WAITISSUE){
            String  sql = "SELECT * FROM TrainOrderBespeak WITH(NOLOCK)WHERE OldDBOrderId ="+orderid;
            try {
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                if (list.size() > 0) {
                    isrobticket = true;
                    Map map = (Map) list.get(0);
                    long pkid = Long.valueOf(map.get("PKId").toString());
                    if(pkid>0){
                        String  spassengerSQL ="SELECT * FROM TrainTicketBespeak WITH(NOLOCK) WHERE OrderId = "+pkid;
                        List plist = Server.getInstance().getSystemService().findMapResultBySql(spassengerSQL, null);
                        if(plist.size()>0){
                            JSONArray passengers = fromPList(plist);  
                            GetOrderBeaskParam(map,passengers,pkid);
                        }
                    }
                   
                }
                return isrobticket ;
            }
            catch (Exception e) {
                e.printStackTrace();
            } 
        }
      
        return isrobticket ;
        
    }
    /**
     * 转换乘客
     * @param plist
     * @return
     */
    private JSONArray fromPList(List<Map> plist) {
        JSONArray passengers = new JSONArray();
        for (int i = 0; i < plist.size(); i++) {
             JSONObject passenger = new JSONObject();
             Map map = plist.get(i);
             String pkid = map.get("PKId").toString();
             String passengersename =  map.get("Name").toString();
             String passporttypeid = map.get("IdType").toString();
             String passportseno = map.get("IdNumber").toString();
             String piaotype  = map.get("TicketType").toString();
             passenger.put("passengersename", passengersename);
             passenger.put("passportseno", passportseno);
             passenger.put("piaotype", piaotype);
             passenger.put("passengerid", pkid);
             passenger.put("passporttypeseid", getStringStr(Integer.valueOf(passporttypeid)));
             if("1".equals(piaotype)){
                 passenger.put("piaotypename", "成人票");
             }else {
                 passenger.put("piaotypename", "儿童票");
             }
             passenger.put("passporttypeidname", getIdtypestr(Integer.valueOf(passporttypeid)));
             passengers.add(passenger);
        }
        return passengers;
    }
    
    public String getStringStr(int idtype){
        switch (idtype) {
        case 1:
            return "1";
        case 2:
            return "1";
        case 3:
            return "B";
        case 4:
            return "C";
        case 5:
            return "G";
        }
        return "1";
    }
    
    public  String getMapValue(Map map, String key) {
        return getMapValue(map, key, "");
    }

    public  String getMapValue(Map map,String key ,String defaultValue){
        if(map!=null&&map.containsKey(key)){
            defaultValue = map.get(key).toString();
        }
        return defaultValue;
    }
    
    public void GetOrderBeaskParam(Map map,JSONArray passengers,long pkid){
     try {
         long orderid = Long.valueOf( map.get("OldDBOrderId").toString());
         String BeginTime = getMapValue(map, "BeginTime"); //map.get("BeginTime").toString();
         String EndTime = getMapValue(map, "EndTime");//map.get("EndTime").toString();
         String BespeakDateList = getMapValue(map, "BespeakDateList"); //map.get("BespeakDateList").toString();
         String BeginBespeakDate = getMapValue(map, "BeginBespeakDate"); //map.get("BeginBespeakDate").toString();
         String EndBespeakDate = getMapValue(map, "EndBespeakDate"); //map.get("EndBespeakDate").toString();
         String WantedTrainType = getMapValue(map, "WantedTrainType"); //map.get("WantedTrainType").toString();
         String WantedSeatType =getMapValue(map, "WantedSeatType"); // map.get("WantedSeatType").toString();
         String FromCity =getMapValue(map, "FromCity"); // map.get("FromCity").toString();
         String ToCity = getMapValue(map, "ToCity"); //map.get("ToCity").toString();
         String InterfaceOrderNumber = getMapValue(map, "InterfaceOrderNumber"); //map.get("InterfaceOrderNumber").toString();
         String MaxPrice =getMapValue(map, "MaxPrice"); // map.get("MaxPrice").toString();
         String LoginName12306 =getMapValue(map, "LoginName12306"); // map.containsKey(key) map.get("LoginName12306").toString();
         String LoginPassword12306 = getMapValue(map, "LoginPassword12306"); //map.get("LoginPassword12306").toString();
         String AllNeedTrainCode =getMapValue(map, "AllNeedTrainCode"); // map.get("AllNeedTrainCode").toString();
         String NeedStandingSeat = getMapValue(map, "NeedStandingSeat"); //map.get("NeedStandingSeat").toString();
         String Priority = getMapValue(map, "Priority"); //map.get("Priority").toString();
         JSONObject robticketJSON = new JSONObject();
         String reqtime =new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
         robticketJSON.put("from_station_name", FromCity);
         robticketJSON.put("start_end_time", EndBespeakDate);
         robticketJSON.put("train_codes", AllNeedTrainCode);
         robticketJSON.put("seat_type", WantedSeatType);
         robticketJSON.put("passengers", passengers);
         robticketJSON.put("method", "qiang_piao_order");
         robticketJSON.put("reqtime", reqtime);
         if("0".equals(NeedStandingSeat)){
             robticketJSON.put("hasseat", true);
         }else{
             robticketJSON.put("hasseat", false);
         }
         String partnerid = PropertyUtil.getValue("Robticket_partnerid", "Train.properties");
         String key =PropertyUtil.getValue("Robticket_key", "Train.properties");
         String callback_url = PropertyUtil.getValue("Robticket_callback_url", "Train.properties");
         String signflag = partnerid + "qiang_piao_order" + reqtime + ElongHotelInterfaceUtil.MD5(key);
         try {
             signflag = ElongHotelInterfaceUtil.MD5(signflag);
         }
         catch (Exception e) {
             e.printStackTrace();
         }
         robticketJSON.put("sign", signflag);
         robticketJSON.put("qpriority", Integer.valueOf(Priority));
         robticketJSON.put("qorderid", InterfaceOrderNumber);
         robticketJSON.put("from_station_code", "");
         robticketJSON.put("callback_url", callback_url);
         robticketJSON.put("max_price", Float.valueOf(MaxPrice));
         robticketJSON.put("to_station_code", "");
         robticketJSON.put("qorder_type", 100);
         robticketJSON.put("qorder_start_time", BeginTime);
         robticketJSON.put("train_type", WantedTrainType);
         robticketJSON.put("partnerid", partnerid);
         robticketJSON.put("to_station_name", ToCity);
         robticketJSON.put("start_begin_time", BeginBespeakDate);
         robticketJSON.put("qorder_end_time", EndTime);
         robticketJSON.put("start_date", changeTime(BespeakDateList));
         Long l1 = System.currentTimeMillis();
         String robticketUrl =PropertyUtil.getValue("robticketUrl", "Train.properties");
         String paramContent = "jsonStr=" + URLEncoder.encode(robticketJSON.toString(),"utf-8");
         WriteLog.write("RobticketSupportMethod_train_confirm", l1 + ":cn_home:" + orderid + ":" + paramContent + "-"
                 + robticketUrl);
         String resultString = submitPost(robticketUrl, paramContent, "UTF-8").toString();
         WriteLog.write("RobticketSupportMethod_train_confirm", l1 + ":cn_home:" + resultString);
         
         JSONObject res = new JSONObject();
         try {
               res = JSONObject.parseObject(resultString);
          } catch (Exception e) {
              
          }
         if(res==null||res.isEmpty()){
             String update = "UPDATE TrainOrderBespeak  SET CreateStatus  = 3  WHERE PKId = "+pkid;  //修改抢票状态为下单中
             Server.getInstance().getSystemService().excuteAdvertisementBySql(update);
             createTrainorderrc(1, orderid, "提交约票接口异常", "请求出票", 1, 0);
             String updateOrderId =" update T_TRAINORDER set C_ORDERSTATUS = "+Trainorder.REFUSENOREFUND+" where ID ="+orderid;
             Server.getInstance().getSystemService().excuteAdvertisementBySql(updateOrderId);
             createTrainorderrc(1, orderid, "提交约票接口拒单等待退款", "请求出票", 1, 0);
             
             Customeruser user = new Customeruser();
             user.setMembername("退款接口");
             user.setId(1002l);
             String serverinfo = PropertyUtil.getValue("RobticketServerinfo", "train.properties");
             Server.getInstance().getTrainService().refuseTrain(orderid, user, serverinfo);
             return;
         }
         boolean success =res.containsKey("success")?res.getBooleanValue("success"):false;
         String msg = res.containsKey("msg")?res.getString("msg"):"";
         if(success){
        	 if(msg.equals("当前时间不提供服务")){
                 String update = "UPDATE TrainOrderBespeak  SET CreateStatus  = 3  WHERE PKId = " + pkid; //修改抢票状态为下单中
                 Server.getInstance().getSystemService().excuteAdvertisementBySql(update);
                 createTrainorderrc(1, orderid, "提交约票接口:"+msg, "请求出票", 1, 0);
                 String updateOrderId = " update T_TRAINORDER set C_ORDERSTATUS = " + Trainorder.REFUSENOREFUND
                         + " where ID =" + orderid;
                 Server.getInstance().getSystemService().excuteAdvertisementBySql(updateOrderId);
                 createTrainorderrc(1, orderid, "提交约票接口拒单等待退款", "请求出票", 1, 0);
                 Customeruser user = new Customeruser();
                 user.setMembername("退款接口");
                 user.setId(1002l);
                 String serverinfo = PropertyUtil.getValue("RobticketServerinfo", "train.properties");
                 Server.getInstance().getTrainService().refuseTrain(orderid, user, serverinfo); //退款
             }else{
                 String update = "UPDATE TrainOrderBespeak  SET CreateStatus  = 1  WHERE PKId = " + pkid; //修改抢票状态为下单中
                 Server.getInstance().getSystemService().excuteAdvertisementBySql(update);
                 createTrainorderrc(1, orderid, "提交约票接口:" + msg, "请求出票", 1, 0);
             }
             return;
         }else{
             String update = "UPDATE TrainOrderBespeak  SET CreateStatus  = 3 WHERE PKId = "+pkid;  //修改抢票状态为下单中
             Server.getInstance().getSystemService().excuteAdvertisementBySql(update);
             createTrainorderrc(1, orderid, "提交约票接口:"+msg, "请求出票", 1, 0);
             String updateOrderId =" update T_TRAINORDER set C_ORDERSTATUS = "+Trainorder.REFUSENOREFUND+" where ID ="+orderid;
             Server.getInstance().getSystemService().excuteAdvertisementBySql(updateOrderId);
             createTrainorderrc(1, orderid, "提交约票接口拒单等待退款", "请求出票", 1, 0);
             
             Customeruser user = new Customeruser();
             user.setMembername("退款接口");
             user.setId(1002l);
             String serverinfo = PropertyUtil.getValue("RobticketServerinfo", "train.properties");
             Server.getInstance().getTrainService().refuseTrain(orderid, user, serverinfo);
             return;
         }
        }
        catch (Exception e) {
            WriteLog.write("RobticketSupportMethod_train_confirm", "异常" + e.getMessage());
        }   
    
    
    }
    
   
    
    
    public String changeTime(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        try {
               return  df2.format( df.parse(time));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        
        return time;
    }
    
    
    /**
     * 书写操作记录
     * @param yewutype
     * @param trainorderid
     * @param content
     * @param createurser
     * @param status
     * @param ticketid
     */
    public void createTrainorderrc(int yewutype, long trainorderid, String content, String createurser, int status,
            long ticketid) {
        try {
            Trainorderrc rc = new Trainorderrc();
            rc.setOrderid(trainorderid);
            rc.setContent(content);
            rc.setStatus(status);// Trainticket.ISSUED
            rc.setCreateuser(createurser);// "12306"
            rc.setTicketid(ticketid);
            rc.setYwtype(yewutype);
            Server.getInstance().getTrainService().createTrainorderrc(rc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
//    
//    /**
//     * 证件类型
//     * @param idtype
//     * @return
//     */
//    public String changePassengerIdType(String idtype) {
//        if ("B".equals(idtype)) {
//            return "护照";
//        }
//        if ("C".equals(idtype)) {
//            return "港澳通行证";
//        }
//        if ("G".equals(idtype)) {
//            return "台胞证";
//        }
//        return "二代身份证";
//    }
    
    /**
     * 转换证件类型
     * @param idtype
     * @return
     */
    public String getIdtypestr(int idtype) {
        switch (idtype) {
        case 1:
            return "二代身份证";
        case 2:
            return "一代身份证";
        case 3:
            return "护照";
        case 4:
            return "港澳通行证";
        case 5:
            return "台湾通行证";
        }
        return "";
    }
    
    /**
     * java.net实现 HTTP POST方法提交
     * 
     * @param url
     * @param paramContent
     * @return
     */
    public static StringBuffer submitPost(String url, String paramContent, String codetype) {
        StringBuffer responseMessage = null;
        java.net.URLConnection connection = null;
        java.net.URL reqUrl = null;
        OutputStreamWriter reqOut = null;
        InputStream in = null;
        BufferedReader br = null;
        try {
            String param = paramContent;
            responseMessage = new StringBuffer();
            reqUrl = new java.net.URL(url);
            connection = reqUrl.openConnection();
            connection.setDoOutput(true);
            reqOut = new OutputStreamWriter(connection.getOutputStream());
            reqOut.write(param);
            reqOut.flush();
            int charCount = -1;
            in = connection.getInputStream();

            br = new BufferedReader(new InputStreamReader(in, codetype));
            while ((charCount = br.read()) != -1) {
                responseMessage.append((char) charCount);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("url=" + url + "?" + paramContent + "\n e=" + ex);
        }
        finally {
            try {
                br.close();
                in.close();
                reqOut.close();
            }
            catch (Exception e) {
                System.out.println("paramContent=" + paramContent + "|err=" + e);
            }
        }
        return responseMessage;
    }

}

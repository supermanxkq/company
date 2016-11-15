package com.ccservice.b2b2c.atom.taobao.thread;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.elong.inter.PropertyUtil;

public class MyThreadTaoBaoOrderOfflineAdd extends Thread {

    private JSONObject jb;

    private long l1;

    public MyThreadTaoBaoOrderOfflineAdd(JSONObject json) {
        this.jb = json;
    }

    @Override
    public void run() {
        l1 = System.currentTimeMillis();
        getorder(this.jb);
    }

    public void getorder(JSONObject info) {
        WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":getorder:" + "info=" + info);
        String ispaper = info.getString("is_success");// 是否成功.
        String order_status = info.getString("order_status");//1-已付款，2-关闭，3-成功
        String order_type = info.getString("order_type");//2：线下邮寄票
        String ttp_order_id = info.getString("ttp_order_id");//淘宝线下交易号
        WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", this.l1 + ":ispaper:" + ispaper + ":order_status:" + order_status + ":order_type:" + order_type);
        if ("true".equals(ispaper) && "1".equals(order_status) && "2".equals(order_type)) {
            try {
                String latest_issue_time = info.getString("latest_issue_time");//最晚出票时间
                String main_order_id = info.getString("main_order_id");//主订单id
                Thread.sleep((long) (1000 * Math.random()));
                String sql = "SELECT * FROM TrainOrderOffline with(nolock) WHERE OrderNumberOnline='" + main_order_id
                        + "'";
                List list = getSystemServiceOldDB().findMapResultBySql(sql, null);
                WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":getorder:" + "main_order_id=" + main_order_id
                        + ";防止订单重复list=" + list.size());
                if (list.size() == 0) {
                    String paperBackup = info.getString("paper_backup");//当下铺/靠窗/连坐无票时,是否支持非下铺/非靠窗/非连坐(0不接受,1接受)
                    String paperLowSeatCount = info.getString("paper_low_seat_count");//至少接受下铺/靠窗/连坐数量
                    String paperType = info.getString("paper_type");//纸质票类型: 1 靠窗,2 连坐,3 上铺,4中铺,5 下铺
                    float total_price = info.getFloat("total_price");
                    Trainorder trainorder = new Trainorder();// 创建订单
                    trainorder.setTaobaosendid("无");//备选席别
                    trainorder.setQunarOrdernumber(main_order_id);//淘宝订单号
                    trainorder.setOrdertimeout(Timestamp.valueOf(latest_issue_time));//最晚出票时间
                    trainorder.setTradeno(ttp_order_id);
                    String tickets1 = info.getString("tickets");
                    JSONObject info1 = JSONObject.parseObject(tickets1);
                    JSONArray tickets = info1.getJSONArray("to_agent_ticket_info");
                    List<Trainpassenger> trainplist = new ArrayList<Trainpassenger>();
                    for (int i = 0; i < tickets.size(); i++) {
                        List<Trainticket> traintickets = new ArrayList<Trainticket>();
                        JSONObject infotickets = tickets.getJSONObject(i);
                        String birthday = infotickets.getString("birthday");//乘客生日
                        String certNoValue = infotickets.getString("certificate_num");//乘车人证件号码
                        String certificate_type = infotickets.getString("certificate_type");//证件类型，0:身份证 1:护照 4:港澳通行证 5:台湾通行证
                        String dptStationValue = infotickets.getString("from_station");//出发站
                        String trainStartTimeValue = infotickets.getString("from_time");//出发时间
                        String insurance_price = infotickets.getString("insurance_price");//保险价格，精确到分，例如10元，输入1000
                        String insurance_unit_price = infotickets.getString("insurance_unit_price");//保险的单一价格
                        String nameValue = infotickets.getString("passenger_name");//乘客姓名
                        int passenger_type;
                        String seat = infotickets.getString("seat");//坐席
                        String seatValue_Key = getSeat(Integer.parseInt(seat));
                        String sub_order_id = infotickets.getString("sub_order_id");//淘宝火车票子订单id
                        String tag = infotickets.getString("tag");//1:单程票
                        float seatValue_Value = infotickets.getFloat("ticket_price");
                        String arrStationValue = infotickets.getString("to_station");//到达站
                        String trainEndTimeValue = infotickets.getString("to_time");//到站时间
                        String trainNoValue = infotickets.getString("train_num");//车次
                        Date date_trainEndTime = new Date();
                        DateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm");
                        try {
                            date_trainEndTime = df.parse(trainEndTimeValue);
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date_trainStartTime = new Date();
                        try {
                            date_trainStartTime = df.parse(trainStartTimeValue);
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long endMilliSeconds = date_trainEndTime.getTime();
                        long startMilliSeconds = date_trainStartTime.getTime();
                        int costtime = (int) ((endMilliSeconds - startMilliSeconds) / 1000);
                        String costTime = String.valueOf(costtime / (60 * 60)) + ":"
                                + String.valueOf((costtime % (60 * 60)) / 60);// 历时

                        if ("1".equals(certificate_type)) {
                            certificate_type = "3";//护照
                        }
                        else if ("0".equals(certificate_type)) {
                            certificate_type = "1";//身份证
                        }
                        if (infotickets.containsKey("passenger_type")) {// 票种类型，0:成人 1:儿童 2：学生
                            passenger_type = Integer.parseInt(infotickets.getString("passenger_type"));
                            if (passenger_type == 0) {
                                passenger_type = 1;
                            }
                            else if (passenger_type == 1) {
                                passenger_type = 0;
                            }
                        }
                        else {
                            passenger_type = 1;
                        }
                        Trainticket trainticket = new Trainticket();
                        Trainpassenger trainpassenger = new Trainpassenger();
                        trainticket.setArrival(arrStationValue);// 存入终点站
                        trainticket.setDeparture(dptStationValue);// 存入始发站
                        trainticket.setStatus(Trainticket.WAITISSUE);
                        trainticket.setTicketno(sub_order_id);
                        // 无法存入剩余票
                        trainticket.setSeq(0);// 存入订单类型
                        trainticket.setTickettype(passenger_type);// 存入票种类型
                        trainticket.setPayprice(seatValue_Value / 100);
                        trainticket.setPrice(seatValue_Value / 100);// 应付火车票价格
                        trainticket.setArrivaltime(trainEndTimeValue);// 火车到达时间
                        trainticket.setTrainno(trainNoValue); // 火车编号
                        trainticket.setDeparttime(trainStartTimeValue);// 火车出发时间
                        trainticket.setIsapplyticket(1);
                        trainticket.setRefundfailreason(0);
                        trainticket.setCosttime(costTime);// 历时
                        trainticket.setSeattype(seatValue_Key);// 车票坐席
                        trainticket.setInsurenum(0);
                        trainticket.setInsurprice(0f);// 采购支付价
                        trainticket.setInsurorigprice(0f);// 保险原价
                        traintickets.add(trainticket);
                        trainpassenger.setIdnumber(certNoValue);// 存入乘车人证件号码
                        trainpassenger.setIdtype(Integer.parseInt(certificate_type));// 存入乘车人证件种类
                        trainpassenger.setName(nameValue);// 存入乘车人姓名
                        trainpassenger.setBirthday(birthday);
                        trainpassenger.setChangeid(0);
                        trainpassenger.setAduitstatus(1);
                        trainpassenger.setTraintickets(traintickets);
                        trainplist.add(trainpassenger);
                    }

                    //            //纸质票类型(0普通,1团体,2下铺,3靠窗,4连坐)
                    trainorder.setPaymethod(Integer.parseInt(paperType));
                    //            //当下铺/靠窗/连坐无票时,是否支持非下铺/非靠窗/非连坐(0不接受,1接受)
                    trainorder.setSupplypayway(Integer.parseInt(paperBackup));
                    //            //至少接受下铺/靠窗/连坐数量
                    trainorder.setRefuseaffirm(Integer.parseInt(paperLowSeatCount));
                    trainorder.setContactuser(info.getString("transport_name"));
                    trainorder.setContacttel(info.getString("transport_phone"));//线下票收件电话
                    trainorder.setControlname(info.getString("telephone"));//联系电话
                    trainorder.setTicketcount(trainplist.size());
                    trainorder.setCreateuid(56l);
                    trainorder.setInsureadreess(info.getString("transport_address"));
                    //
                    trainorder.setOrderprice(total_price / 100);
                    trainorder.setIsjointtrip(0);
                    trainorder.setAgentid(0);
                    trainorder.setState12306(Trainorder.WAITORDER);
                    trainorder.setPassengers(trainplist);
                    trainorder.setOrderstatus(Trainorder.WAITISSUE);
                    trainorder.setAgentprofit(0f);
                    trainorder.setCommission(0f);
                    trainorder.setTcprocedure(0f);
                    trainorder.setInterfacetype(2);
                    trainorder.setCreateuser("淘宝");
                    WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":getorder:" + trainorder.getQunarOrdernumber() + "(:)"
                            + trainorder.getId());
                    trainorderofflineadd(trainorder);
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("TAOBAO_Add_Thread线程异常", e);
                WriteLog.write("TAOBAO_Add_Thread线程异常", "e:" + e);
                e.printStackTrace();
            }
        }

    }

    public void trainorderofflineadd(Trainorder trainorder) {
        // 创建火车票线下订单TrainOrderOffline
        String addresstemp = trainorder.getInsureadreess();
        String agentidtemp = distribution2(addresstemp);
        WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":trainorderofflineadd:" + "线下火车票分单log记录:地址:" + addresstemp + "----->agentId:"
                + agentidtemp);
        //getCreateTrainorderOfficeProcedureSql
        String sp_TrainOrderOffline_insert = getCreateTrainorderOfficeProcedureSql(trainorder, agentidtemp);
        WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":trainorderofflineadd:" + "TrainOrderOffline_insert_TaoBao保存订单存储过程"
                + sp_TrainOrderOffline_insert);
        String sql = "SELECT * FROM TrainOrderOffline with(nolock) WHERE OrderNumberOnline='"
                + trainorder.getQunarOrdernumber() + "'";
        List listtemp = getSystemServiceOldDB().findMapResultBySql(sql, null);
        WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":trainorderofflineadd:" + "main_order_id=" + trainorder.getQunarOrdernumber()
                + ";防止订单重复list=" + listtemp.size());
        if (listtemp.size() == 0) {
            List list = getSystemServiceOldDB().findMapResultByProcedure(sp_TrainOrderOffline_insert);
            Map map = (Map) list.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String orderid = map.get("id").toString();
            //通过出票点和邮寄地址获取预计到达时间results
            String delieveStr = "暂无快递信息!";
            try {
                delieveStr = getDelieveStr(agentidtemp, trainorder.getInsureadreess());//
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            String updatesql = "UPDATE TrainOrderOffline SET telephone='" + trainorder.getControlname()
                    + "',expressDeliver ='" + delieveStr + "' WHERE ID=" + orderid;
            WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":trainorderofflineadd:" + "TrainOrderOfflineOffline_保存快递时效信息" + updatesql);
            getSystemServiceOldDB().excuteAdvertisementBySql(updatesql);
            // [TrainOrderOfflineRecord]表插入数据
            String procedureRecord = "sp_TrainOrderOfflineRecord_insert @FKTrainOrderOfflineId=" + orderid
                    + ",@ProviderAgentid=" + agentidtemp + ",@DistributionTime='" + sdf.format(new Date())
                    + "',@ResponseTime='',@DealResult=0,@RefundReason=0,@RefundReasonStr=''";
            WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":trainorderofflineadd:" + "sp_TrainOrderOfflineRecord_insert_记录表存储过程"
                    + procedureRecord);
            getSystemServiceOldDB().findMapResultByProcedure(procedureRecord);
            // 火车票线下订单邮寄信息
            String sp_MailAddress_insert = getMailAddressProcedureSql(trainorder, orderid);
            WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":trainorderofflineadd:"
                    + "TrainOrderOfflineMailAddress_insert_TaoBao保存邮寄地址存储过程" + sp_MailAddress_insert);
            getSystemServiceOldDB().findMapResultByProcedure(sp_MailAddress_insert);
            for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
                // 火车票乘客线下TrainPassengerOffline
                String sp_TrainPassengerOffline_insert = getCreateTrainpassengerOfficeProcedureSql(trainpassenger,
                        orderid);
                WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":trainorderofflineadd:"
                        + "TrainOrderOfflinePassengerOffline_insert_TaoBao保存乘客存储过程" + sp_TrainPassengerOffline_insert);
                List listP = getSystemServiceOldDB().findMapResultByProcedure(sp_TrainPassengerOffline_insert);
                Map map2 = (Map) listP.get(0);
                String trainPid = map2.get("id").toString();
                for (Trainticket ticket : trainpassenger.getTraintickets()) {
                    // 线下火车票TrainTicketOffline
                    String sp_TrainTicketOffline_insert = getCreateTrainticketOfficeProcedureSql(ticket, trainPid,
                            orderid);
                    WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", l1 + ":"
                            + "TrainOrderOfflineTicketOffline_insert_TaoBao保存车票存储过程" + sp_TrainTicketOffline_insert);
                    getSystemServiceOldDB().findMapResultByProcedure(sp_TrainTicketOffline_insert);
                }
            }
        }
    }

    private ISystemService getSystemServiceOldDB() {
        String systemdburlString = PropertyUtil.getValue("offlineservice", "Train.properties");
        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            return (ISystemService) factory.create(ISystemService.class,
                    systemdburlString + ISystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //	  public static void main(String[] args) {
    //	      JSONObject json=JSONObject.parseObject("{\"mailing\":false,\"service_price\":1000,\"relation_name\":\"易兵根\",\"transport_price\":2000,\"transport_phone\":\"15058715667\",\"transport_name\":\"易兵根\",\"transport_address\":\"浙江省-温州市-瓯海区-南白象街道 上蔡村南安盛锦园\",\"total_price\":45800,\"order_status\":1,\"paper_type\":5,\"ttp_order_id\":160928089079,\"is_success\":true,\"request_id\":\"10fdepnbxpoac\",\"paper_backup\":0,\"address\":\"no address\",\"main_order_id\":2193934234107930,\"paper_low_seat_count\":1,\"telephone\":\"13736997982\",\"tickets\":{\"to_agent_ticket_info\":[{\"birthday\":\"1955-06-12\",\"train_num\":\"K325/K328\",\"tag\":1,\"certificate_num\":\"362201195506125227\",\"passenger_type\":0,\"to_station\":\"宜春\",\"insurance_price\":0,\"certificate_type\":\"0\",\"insurance_unit_price\":0,\"seat\":4,\"ticket_price\":21400,\"from_time\":\"2016-08-22 17:20:00\",\"sub_order_id\":\"204198778\",\"from_station\":\"温州\",\"passenger_name\":\"谢国连\",\"to_time\":\"2016-08-23 06:24:00\"},{\"birthday\":\"1955-06-12\",\"train_num\":\"K325/K328\",\"tag\":1,\"certificate_num\":\"362201195506125227\",\"passenger_type\":1,\"to_station\":\"宜春\",\"insurance_price\":0,\"certificate_type\":\"0\",\"insurance_unit_price\":0,\"seat\":4,\"ticket_price\":21400,\"from_time\":\"2016-08-22 17:20:00\",\"sub_order_id\":\"204198779\",\"from_station\":\"温州\",\"passenger_name\":\"谢国连\",\"to_time\":\"2016-08-23 06:24:00\"}]},\"latest_issue_time\":\"2016-08-14 13:59:52\",\"order_type\":2}");
    //	      getorder(json);
    //	  }

    public String getDelieveStr(String agengId, String address) {
        String results = "";
        String fromcode = "010";
        String tocode = getExpressCodes(address);
        String time1 = "10:00:00";
        String time2 = "18:00:00";

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String dates = sdf.format(new Date());
        String sql1 = "SELECT fromcode,time1,time2 from TrainOrderAgentTimes with(nolock) where agentId=" + agengId;
        List list = getSystemServiceOldDB().findMapResultBySql(sql1, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            fromcode = map.get("fromcode").toString();
            time1 = map.get("time1").toString();
            time2 = map.get("time2").toString();
        }
        String realTime = getRealTimes(dates, time1, time2);
        String urlString = PropertyUtil.getValue("expressDeliverUrl", "Train.properties");
        String param = "times=" + realTime + "&fromcode=" + fromcode + "&tocode=" + tocode;
        WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", this.l1 + ":TrainOrderOfflineOffline_保存快递时效信息" + "agengId=" + agengId + "------->" + "address=" + address + 
                "---------->" + urlString + "?" + param);
        String result = SendPostandGet.submitPost(urlString, param, "UTF-8").toString();
        WriteLog.write("MyThreadTaoBaoOrderOfflineAdd", this.l1 + ":result" + "agengId=" + result);
        if (result.contains("OK") && result.contains("deliver_time")) {
            try {
                Document document = DocumentHelper.parseText(result);
                Element root = document.getRootElement();
                if(root != null){
                    Element head = root.element("Head");
                    Element body = root.element("Body");
                    if ("OK".equals(root.elementText("Head"))) {
                        Element deliverTmResponse = body.element("DeliverTmResponse");
                        Element deliverTm = deliverTmResponse.element("DeliverTm");
                        String business_type_desc = deliverTm.attributeValue("business_type_desc");
                        String deliver_time = deliverTm.attributeValue("deliver_time");
                        String business_type = deliverTm.attributeValue("business_type");
                        results = "如果" + realTime + "正常发件。快递类型为:" + business_type_desc + "。快递预计到达时间:" + deliver_time
                                + "。以上时效为预计到达时间，仅供参考，精准时效以运单追踪结果中的“预计到达时间”为准。";
                    }
                }
            }
            catch (DocumentException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            results = "暂无快递信息!";
        }

        return results;
    }

    /**
     * 通过存储过程获取乘客地址的citycode
     * @param address
     * @return
     */
    public String getExpressCodes(String address) {
        String procedure = "sp_TrainOfflineExpress_getCode @address=\"" + address + "\"";
        List list = getSystemServiceOldDB().findMapResultByProcedure(procedure);
        String cityCode = "010";
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            cityCode = map.get("CityCode").toString();
        }
        return cityCode;
    }

    /**
     * 获取取快递时间
     * @param dates
     * @param time1
     * @param time2
     * @return
     */
    public String getRealTimes(String dates, String time1, String time2) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String realDates = sdf1.format(new Date());
        try {
            Date date0 = sdf.parse(dates);
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            if (date0.before(date1)) {
                result = (realDates.substring(0, 10) + " " + sdf.format(date1));
            }
            else if (date0.after(date1) && date0.before(date2)) {
                result = (realDates.substring(0, 10) + " " + sdf.format(date2));
            }
            else if (date0.after(date2)) {
                Date ds = getDate(new Date());
                String nextd = sdf1.format(ds);
                result = (nextd.substring(0, 10) + " " + sdf.format(date1));
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Date getDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date date1 = new Date(calendar.getTimeInMillis());
        return date1;
    }

    //    public static  String distribution2(String address1) {
    //    	boolean flag=false;
    //		//默认出票点
    //    	String sql1="SELECT agentId FROM TrainOfflineMatchAgent WHERE status=2 AND createUid="+"56";
    //    	List list1=getSystemServiceOldDB().findMapResultBySql(sql1, null);
    //    	String agentId="378";
    //    	if(list1.size()>0){
    //    		Map map=(Map)list1.get(0);
    //    		agentId=map.get("agentId").toString();
    //    	}
    //    	//程序自动分配出票点
    //    	String sql2="SELECT * FROM TrainOfflineMatchAgent WHERE status=1 AND createUid="+"56";
    //    	List list2=getSystemServiceOldDB().findMapResultBySql(sql2, null);
    //    	for(int i=0;i<list2.size();i++){
    //    		Map mapp=(Map)list2.get(i);
    //    		String provinces=mapp.get("provinces").toString();
    //    		String agentid=mapp.get("agentId").toString();
    //    		String[] add=provinces.split(",");
    //    		for (int j = 0; j < add.length; j++) {
    //				if (address1.startsWith(add[j])) {
    //					agentId=agentid;
    //					flag=true;
    //				}
    //				if(flag){
    //					break;
    //				}
    //			}
    //			if(flag){
    //				break;
    //			}
    //    	}
    //    	WriteLog.write("淘宝线下新版分配订单", "agentId="+agentId+";address1="+address1);
    //    	return agentId;
    //	}
    //新版分单
    public String distribution2(String address1) {
        //默认出票点
        String sql1 = "SELECT agentId FROM TrainOfflineMatchAgent WHERE status=2 AND createUid=" + "56";
        List list1 = getSystemServiceOldDB().findMapResultBySql(sql1, null);
        String agentId = "378";
        if (list1.size() > 0) {
            Map map = (Map) list1.get(0);
            agentId = map.get("agentId").toString();
        }
        //程序自动分配出票点
        String sql2 = "SELECT * FROM TrainOfflineMatchAgent WHERE status=1 AND createUid=" + "56";
        List list2 = getSystemServiceOldDB().findMapResultBySql(sql2, null);
        List listAgents = new ArrayList();
        for (int i = 0; i < list2.size(); i++) {
            Map mapp = (Map) list2.get(i);
            String provinces = mapp.get("provinces").toString();
            String agentid = mapp.get("agentId").toString();
            String[] add = provinces.split(",");
            for (int j = 0; j < add.length; j++) {
                boolean flag = false;
                if (address1.startsWith(add[j])) {
                    listAgents.add(agentid);
                    flag = true;
                }
                if (flag) {
                    break;
                }
            }
        }
        if (listAgents.size() > 0) {
            int max = listAgents.size();
            int min = 1;
            Random random = new Random();
            int s = random.nextInt(max) % (max - min + 1) + min;
            if (s > 0 && s <= listAgents.size()) {
                agentId = listAgents.get(s - 1) + "";
            }
        }
        WriteLog.write("淘宝线下新版分配订单", "agentId=" + agentId + ";address1=" + address1);
        return agentId;
    }

    public String distribution1(String address1) {
        String resultp = "";
        String agentp = "415";
        int num = Integer.parseInt(PropertyUtil.getValue("allAgentNum", "Train.properties"));
        boolean flag = false;
        for (int i = 1; i <= num; i++) {
            String agents = PropertyUtil.getValue("AgentId" + i, "Train.properties");
            String provinces = PropertyUtil.getValue("MailProvince" + i, "Train.properties");
            String resultNames = "";
            try {
                resultNames = new String(provinces.getBytes("ISO-8859-1"), "utf-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String[] add = resultNames.split(",");
            for (int j = 0; j < add.length; j++) {
                //				if (address1.contains(add[j])) {
                //					resultp=add[j];
                //					agentp=agents;
                //					flag=true;
                //				}
                if (address1.startsWith(add[j])) {
                    resultp = add[j];
                    agentp = agents;
                    flag = true;
                }
                if (flag) {
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        return agentp;
    }

    //1：硬座；2：硬卧上；3：硬卧中；4：硬卧下；5:软座；6:软卧上;7:软卧中;8:软卧下;9：商务座；10:观光座;11:一等包座;12:特等座;13:一等座;14:二等座;15:高级软卧上;16:高级软卧上;19:动卧;
    public String getSeat(int i_seat) {
        String str = "";
        switch (i_seat) {
        case 1:
            str = "硬座";
            break;
        case 2:
            str = "硬卧上";
            break;
        case 3:
            str = "硬卧中";
            break;
        case 4:
            str = "硬卧下";
            break;
        case 5:
            str = "软座";
            break;
        case 6:
            str = "软卧上";
            break;
        case 7:
            str = "软卧中";
            break;
        case 8:
            str = "软卧下";
            break;
        case 9:
            str = "商务座";
            break;
        case 10:
            str = "观光座";
            break;
        case 11:
            str = "一等包座";
            break;
        case 12:
            str = "特等座";
            break;
        case 13:
            str = "一等座";
            break;
        case 14:
            str = "二等座";
            break;
        case 15:
            str = "高级软卧上";
            break;
        case 16:
            str = "高级软卧下";
            break;
        case 19:
            str = "动卧";
            break;    
        default:
            break;
        }
        return str;
    }

    public String getCreateTrainorderOfficeProcedureSql(Trainorder trainorder, String agentidtemp) {
        StringBuffer procedureSql = new StringBuffer();
        procedureSql.append("sp_TrainOrderOffline_insert1 ");
        procedureSql.append("@OrderNumber = N'" + trainorder.getQunarOrdernumber() + "',");
        procedureSql.append("@AgentId = " + agentidtemp + ",");
        String CreateUser = trainorder.getCreateuser();
        String ContactUser = trainorder.getContactuser();
        String ContactTel = trainorder.getContacttel();
        Float OrderPrice = trainorder.getOrderprice();
        Float AgentProfit = trainorder.getAgentprofit();
        Integer TicketCount = trainorder.getTicketcount();
        Timestamp outtime = trainorder.getOrdertimeout();
        String tradeno = trainorder.getTradeno();
        procedureSql.append("@CreateUId = " + trainorder.getCreateuid() + ",");
        procedureSql.append("@CreateUser = N'" + CreateUser + "',");
        procedureSql.append("@ContactUser = N'" + ContactUser + "',");
        procedureSql.append("@ContactTel = N'" + ContactTel + "',");
        procedureSql.append("@OrderPrice = " + OrderPrice + ",");
        procedureSql.append("@AgentProfit = " + AgentProfit + ",");
        procedureSql.append("@TicketCount = " + TicketCount + ",");
        procedureSql.append("@Paystatus = " + 1 + ",");
        procedureSql.append("@OrderNumberOnline = '" + trainorder.getQunarOrdernumber() + "',");
        procedureSql.append("@PaperType = " + trainorder.getPaymethod() + ",");
        procedureSql.append("@PaperBackup = " + trainorder.getSupplypayway() + ",");
        procedureSql.append("@paperLowSeatCount = " + trainorder.getRefuseaffirm() + ",");
        procedureSql.append("@extSeat = '" + trainorder.getTaobaosendid() + "',");
        procedureSql.append("@TradeNo = '" + tradeno + "',");
        if (outtime == null || "".equals(outtime)) {
            procedureSql.append("@OrderTimeout = '无'");
        }
        else {
            procedureSql.append("@OrderTimeout = '" + outtime + "'");
        }

        return procedureSql.toString();
    }

    public String getMailAddressProcedureSql(Trainorder trainorder, String orderid) {
        StringBuffer procedureSql = new StringBuffer();
        procedureSql.append("sp_MailAddress1_insert ");
        String ProvinceName = "";
        String CityName = "";
        String RegionName = "";
        String TownName = "";
        String Mail = "";
        String Note = "";
        String Busstype = "0";
        procedureSql.append("@MailName= '" + trainorder.getContactuser() + "',");
        procedureSql.append("@MailTel= '" + trainorder.getContacttel() + "',");
        procedureSql.append("@Postcode= '100000',");
        procedureSql.append("@Address =\"" + trainorder.getInsureadreess() + "\",");
        procedureSql.append("@ProvinceName ='" + ProvinceName + "',");
        procedureSql.append("@CityName ='" + CityName + "',");
        procedureSql.append("@RegionName ='" + RegionName + "',");
        procedureSql.append("@TownName ='" + TownName + "',");
        procedureSql.append("@Orderid ='" + orderid + "',");
        procedureSql.append("@Mail='" + Mail + "',");
        procedureSql.append("@Note='" + Note + "',");
        procedureSql.append("@Busstype=" + Busstype);
        return procedureSql.toString();
    }

    public String getCreateTrainpassengerOfficeProcedureSql(Trainpassenger trainpassenger, String orderid) {
        StringBuffer procedureSql = new StringBuffer();
        procedureSql.append("sp_TrainPassengerOffline_insert ");
        //		Long OrderId = trainPassengerOffline.getorderid();
        procedureSql.append("@OrderId = " + orderid + ",");
        String Name = trainpassenger.getName();
        procedureSql.append("@Name = '" + Name + "',");
        Integer IdType = trainpassenger.getIdtype();
        procedureSql.append("@IdType = " + IdType + ",");
        String IdNumber = trainpassenger.getIdnumber();
        procedureSql.append("@IdNumber = '" + IdNumber + "',");
        String Birthday = trainpassenger.getBirthday() == null ? TimeUtil.gettodaydate(4) : trainpassenger
                .getBirthday();
        procedureSql.append("@Birthday = '" + Birthday + "'");
        return procedureSql.toString();
    }

    public String getCreateTrainticketOfficeProcedureSql(Trainticket ticket, String trainPid, String orderid) {
        StringBuffer procedureSql = new StringBuffer();
        procedureSql.append("sp_TrainTicketOffline_insert ");
        Timestamp departTime = Timestamp.valueOf(ticket.getDeparttime());
        String departure = ticket.getDeparture();
        String arrival = ticket.getArrival();
        String trainno = ticket.getTrainno();
        int tickettype = ticket.getTickettype();
        String seattype = ticket.getSeattype();
        String seatno = ticket.getSeatno();
        String coach = ticket.getCoach();
        float price = ticket.getPrice();
        String costtime = ticket.getCosttime();
        String starttime = ticket.getDeparttime().substring(11, 16);
        String arrivaltime = ticket.getArrivaltime().substring(11, 16);
        String suborderid = ticket.getTicketno();
        procedureSql.append("@TrainPid= " + trainPid + ",");
        procedureSql.append("@OrderId= " + orderid + ",");
        procedureSql.append("@DepartTime= '" + departTime + "',");
        procedureSql.append("@Departure ='" + departure + "',");
        procedureSql.append("@Arrival ='" + arrival + "',");
        procedureSql.append("@TrainNo ='" + trainno + "',");
        procedureSql.append("@TicketType =" + tickettype + ",");
        procedureSql.append("@SeatType ='" + seattype + "',");
        procedureSql.append("@SeatNo ='" + seatno + "',");
        procedureSql.append("@Coach ='" + coach + "',");
        procedureSql.append("@Price =" + price + ",");
        procedureSql.append("@CostTime ='" + costtime + "',");
        procedureSql.append("@StartTime='" + starttime + "',");
        procedureSql.append("@ArrivalTime ='" + arrivaltime + "',");
        if (suborderid == null || "".equals(suborderid)) {
            procedureSql.append("@SubOrderId ='0'");
        }
        else {
            procedureSql.append("@SubOrderId ='" + suborderid + "'");
        }
        return procedureSql.toString();
    }

    public static void main1(String[] args) {
        String json = "{\"mailing\":false,\"service_price\":500,\"relation_name\":\"胡进英\",\"transport_price\":2000,\"transport_phone\":\"13214416899\",\"transport_name\":\"叶永茂\",\"transport_address\":\"吉林省-长春市-双阳区-奢岭街道 吉林油田长春釆油厂试井队'\",\"total_price\":42200,\"order_status\":1,\"paper_type\":5,\"ttp_order_id\":195958731099,\"is_success\":true,\"request_id\":\"z2cg0oym4lio\",\"paper_backup\":1,\"address\":\"no address\",\"main_order_id\":2571210610749910,\"paper_low_seat_count\":1,\"telephone\":\"13214416899\",\"tickets\":{\"to_agent_ticket_info\":[{\"train_num\":\"T241/T244\",\"tag\":1,\"certificate_num\":\"222325196702244311\",\"passenger_type\":0,\"insurance_price\":0,\"to_station\":\"合肥\",\"certificate_type\":\"0\",\"insurance_unit_price\":0,\"seat\":4,\"ticket_price\":39700,\"from_time\":\"2016-10-26 13:42:00\",\"sub_order_id\":\"254101527\",\"from_station\":\"长春\",\"passenger_name\":\"叶永茂\",\"to_time\":\"2016-10-27 13:03:00\"}]},\"latest_issue_time\":\"2016-10-20 15:26:24\",\"order_type\":2}";
        MyThreadTaoBaoOrderOfflineAdd myThreadTaoBaoOrderOfflineAdd = new MyThreadTaoBaoOrderOfflineAdd(
                JSONObject.parseObject(json));
        myThreadTaoBaoOrderOfflineAdd.getorder(JSONObject.parseObject(json));
    }

}

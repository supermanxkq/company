package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.MemCached;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.Exception.TrainOrderException;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread.SubmittrainorderThread;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.mailaddress.MailAddress;
import com.ccservice.b2b2c.base.train.Train;
import com.ccservice.b2b2c.base.train.TrainStudentInfo;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.AirUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.InterfaceTimeRuleUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 申请分配座位席别
 * 
 * @author 路平 创建时间 2014年12月9日 下午5:30:51
 */
public class TongchengTrainOrder extends TrainSelectLoginWay {
    Logger logger = Logger.getLogger(TongchengTrainOrder.class.getSimpleName());

    @SuppressWarnings("unused")
    private static Train train = new Train();

    // TrainOrdering ordering = new TrainOrdering(); 

    @SuppressWarnings("unused")
    public String submittrainorder(JSONObject jsonObject, int r1, String key) throws TrainOrderException {

        boolean b_istongbu = true;// 是否是同步1
        boolean is_occupyingAseat_success = false;// 是否占座成功
        boolean acquiringresult = false;//是否收单
        Long starttime = System.currentTimeMillis();
        String helpinfo = "";
        String msg = "";

        String res = "false";
        String pnames = "";
        float totalprice = 0f;
        boolean ordersuccess = false;
        String code = "301";
        Trainorder trainorders = new Trainorder();
        Customeruser customeruser = new Customeruser();// 用户表
        Trainorder trainorder = new Trainorder();// 火车订单表

        JSONObject json = new JSONObject();
        JSONObject jsonstr = new JSONObject();
        String ticinfo = "";
        JSONArray jsonArray = new JSONArray();
        String orderid = jsonObject.getString("orderid");// 同程订单号
        String interfaceOrderNumber = jsonObject.getString("orderid");
        String checi = jsonObject.getString("checi").toUpperCase();// 车次
        String from_station_code = jsonObject.getString("from_station_code");// 出发站简码
        String from_station_name = jsonObject.getString("from_station_name");// 出发站名称
        String to_station_code = jsonObject.getString("to_station_code");// 到达站简码
        String to_station_name = jsonObject.getString("to_station_name");// 到达站名称
        String train_date = jsonObject.getString("train_date");// 乘车日期
        JSONArray jsons = jsonObject.getJSONArray("passengers");
        String partnerid = jsonObject.getString("partnerid");// 传过来的partnerid
        // =========异步 S
        String callbackurl = jsonObject.getString("callbackurl");// 锁票异步回调地址[选填]
        String reqtoken = jsonObject.getString("reqtoken");// 请求物证值[异步时填写]
        String WormholeAccount = jsonObject.getString("WormholeAccount");//虫洞帐号
        String hasseat = jsonObject.getString("hasseat");// 是否出无座票 true:不出无座票 False:允许出无座票
        String username = getUsername(jsonObject);// 12306用户名可以为null
        String userpassword = getUserPassword(jsonObject);// 12306用户密码可以为null
        String cookie = getCookie(jsonObject);// 12306用户密码 对应的cookie
        int ordertype = TrainAccountSrcUtil.getOrdertype(username, userpassword, cookie);
        trainorder.setOrdertype(ordertype);
        //是否需要供应商收单处理
        String waitfororder = jsonObject.getString("waitfororder");//true 收单模式  false不走收单
        String shoudan = jsonObject.getString("shoudan");//自己判断是否是自己发送的请求
        // -----------接口类型S
        Integer interfacetype = Integer.valueOf(jsonObject.getString("interfacetype"));// 接口类型
        trainorder.setInterfacetype(interfacetype);
        // -----------接口类型E
        //
        String agentid = gettongchengagentid(partnerid);

        String mobile = jsonObject.getString("mobile");//登录帐号
        String loginpassword = jsonObject.getString("loginpassword");//登录密码
        String contracttel = jsonObject.getString("contacttel");//联系人手机号
        String contactusername = jsonObject.getString("contactusername");//
        if (TrainSupplyMethodUtil.getMobileFlag(mobile, loginpassword)) {//手机端逻辑
            Customeruser currentuser = TrainSupplyMethodUtil.getLoginUser(mobile, loginpassword, Long.valueOf(agentid));
            if (currentuser == null) {
                if (mobile.equals(loginpassword)) {
                    try {
                        currentuser = createUser(mobile, loginpassword, Long.valueOf(agentid));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    jsonstr.put("code", "103");
                    jsonstr.put("success", "false");
                    jsonstr.put("msg", "当前用户不存在");
                    return jsonstr.toString();
                }
            }
            trainorder.setContacttel(contracttel);
            trainorder.setContactuser(contactusername);
            trainorder.setCreateuid(currentuser.getId());
        }
        else {
            if (agentid != null && !agentid.equals("")) {
                trainorder.setCreateuid(Long.parseLong(agentid));// 用户ID
            }
        }

        String trainorder_system_error = isTrainorderSystemError(r1, interfacetype);
        if ("SUCCESS".equalsIgnoreCase(trainorder_system_error)) {
            //如果是meituan、高铁 的夜間單，先進王戰朝收單表
            //如果是王戰朝的放單，放出來的，就不攔截
            SimpleDateFormat date = new SimpleDateFormat("HH");
            Date time = new Date();
            String datetime = date.format(time);
            String msgorder;
            if ((InterfaceTimeRuleUtil.isNightCreateOrder()) && (datetime.equals("0"))) {
              jsonObject.put("shoudan", "true");
              msgorder = jsonObject.toJSONString();
              saveOrderInfoShouDan(msgorder, orderid, agentid);
              code = "802";
              ordersuccess = true;
              acquiringresult = true;
                msg = "收单请求已接收";
              jsonstr.put("reqtoken", reqtoken);
            }
            if (InterfaceTimeRuleUtil.isNightCreateOrder() && ("67".equals(agentid) || "86".equals(agentid))
                    && !"true".equals(shoudan)) {//收单
                jsonObject.put("shoudan", "true");
                msgorder = jsonObject.toJSONString();
                saveOrderInfoShouDan(msgorder, orderid, agentid);
                code = "802";
                ordersuccess = true;
                acquiringresult = true;
                msg = "收单请求已接收";
                jsonstr.put("reqtoken", reqtoken);
            }
            else {
                try {
                    boolean acquiringByAgentid = getAcquiringflagByAgentID(agentid);
                    if (("true".equalsIgnoreCase(waitfororder) || acquiringByAgentid || InterfaceTimeRuleUtil
                            .isNightCreateOrder())) {//收单
                        jsonObject.put("shoudan", "true");
                        msgorder = jsonObject.toJSONString();
                        //                         saveOrderInfoShouDan(msgorder, orderid, agentid);
                        code = "802";
                        ordersuccess = true;
                        acquiringresult = true;
                        msg = "收单请求已接收";
                        jsonstr.put("reqtoken", reqtoken);
                    }
                    boolean checkThreeWordCode = true;
                    //海  口东、三  亚 -->中间两个空格  
                    //美团的BUG，我们这边做兼容：
                    //                        1、经核实，12306出发站没有空格，所以美团如果传有空格的就干掉
                    //                        2、经核实，12306到达站可能有空格，所以根据美团传的三字码判断站名是否需要空格
                    if ("meituan".equals(partnerid)) {
                        from_station_name = from_station_name.equals("海  口东") ? "海口东" : from_station_name
                                .equals("三  亚") ? "三亚" : from_station_name;
                        if (to_station_code.equals("KEQ") && to_station_name.equals("海口东")) {
                            to_station_name = "海  口东";
                        }
                        if (to_station_code.equals("JUQ") && to_station_name.equals("三亚")) {
                            to_station_name = "三  亚";
                        }
                    }
                    if (ElongHotelInterfaceUtil.StringIsNull(from_station_code)) {
                        from_station_code = Train12306StationInfoUtil.getThreeByName(from_station_name);
                    }
                    else {
                        if (!Train12306StationInfoUtil.getThreeByName(from_station_name).equals(from_station_code)) {
                            checkThreeWordCode = false;
                            WriteLog.write("TongchengTrainOrder_车站对应三字码有误", orderid + ":" + from_station_name + ":-->"
                                    + Train12306StationInfoUtil.getThreeByName(from_station_name) + "=="
                                    + from_station_code);
                        }
                    }
                    if (ElongHotelInterfaceUtil.StringIsNull(to_station_code)) {
                        to_station_code = Train12306StationInfoUtil.getThreeByName(to_station_name);
                    }
                    else {
                        if (!Train12306StationInfoUtil.getThreeByName(to_station_name).equals(to_station_code)) {
                            checkThreeWordCode = false;
                            WriteLog.write("TongchengTrainOrder_车站对应三字码有误", orderid + ":" + to_station_name + ":-->"
                                    + Train12306StationInfoUtil.getThreeByName(to_station_name) + "=="
                                    + to_station_code);
                        }
                    }
                    if (!checkThreeWordCode) {
                        code = "348";
                        ordersuccess = false;
                        msg = "车站对应三字码有误";
                        jsonstr.put("reqtoken", reqtoken);
                        jsonstr.put("code", code);
                        jsonstr.put("success", ordersuccess);
                        jsonstr.put("msg", msg);

                        if ("true".equals(shoudan)) {
                            new TongchengShoudanErrorCallback().callBackTongChengOrdered(agentid, msg, interfacetype,
                                    reqtoken, partnerid, callbackurl, key, orderid);
                        }
                        return jsonstr.toString();
                    }
                }
                catch (Exception e2) {
                }
                // 添加订单信息
                if (agentid != null && !agentid.equals("")) {
                    trainorder.setAgentid(Long.parseLong(agentid));// 代理ID
                }

                Train traininfo = new Train();
                try {
                    String train_time = jsonObject.getString("train_time");// 乘车时间
                    if (train_time != null) {
                        traininfo.setStarttime(train_time);
                    }
                }
                catch (Exception e) {
                }
                List<Trainpassenger> passengers = gettrainpassenger(jsons, traininfo, checi, to_station_name,
                        train_date, from_station_name);
                totalprice = gettotalpricebytrainpassenger(passengers);
                addInsurPrice(passengers, jsonObject);
                // 返回size>0则需要添加乘客信息
                // if (jsons.size() > 0) {
                //
                // }

                trainorder.setPassengers(passengers);
                trainorder.setOrderstatus(Trainorder.WAITPAY);
                trainorder.setQunarOrdernumber(orderid);
                trainorder.setOrderprice(totalprice);
                trainorder.setAgentprofit(0f);// 采购利润
                trainorder.setCommission(0f);
                trainorder.setSupplyprice(totalprice);
                trainorder.setCreateuser("接口");
                trainorder.setPaymethod(4);
                // ============================开始处理同步还是异步
                boolean useTongbu = false;
                if ((callbackurl == null || "".equals(callbackurl)) && useTongbu) {//同步接口,废弃2015年12月4日16:46:20 chendong
                    //同程的同步接口
                    reqtoken = System.currentTimeMillis() + "_" + getRandomNum(3, 3) + "_" + getRandomNum(3, 3) + "_"
                            + getRandomNum(3, 3);
                    trainorder.setContactuser(reqtoken);//设置一个请求物证如果同步转异步的时候用
                    Long temp1 = System.currentTimeMillis();
                    try {
                        trainorder = tongbuxianchengmethod(trainorder, jsonstr, r1);
                    }
                    catch (InterruptedException e1) {
                        logger.error("InterruptedException", e1.fillInStackTrace());
                    }
                    catch (Exception e1) {
                        logger.error("submittrainorder_Exception", e1.fillInStackTrace());
                    }
                    res = trainorder.getChangesupplytradeno() == null ? "" : trainorder.getChangesupplytradeno();//把原来返回的信息放到这里来
                    String extnumber = trainorder.getExtnumber();//12306电子单号
                    WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":返回:" + (System.currentTimeMillis() - temp1) + ":信息:"
                            + extnumber + ":res:" + res + ":" + ("".equals(res) || res == null));
                    if (extnumber != null && extnumber.length() > 5 && Character.isDigit(extnumber.charAt(5))) {
                        is_occupyingAseat_success = true;//占座成功
                        trainorder.setExtnumber(extnumber);
                        //向火车票订单表插入数据并返回本条数据
                        trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
                        createTrainOrderExtSeat(trainorder, jsonObject);
                        addMailAddress(jsonObject, trainorder);
                        createtrainorderrc(1, "提交订单成功", trainorder.getId(), 0L, trainorder.getOrderstatus(), "系统接口");
                        createtrainorderrc(1, "下单成功,电子单号:" + extnumber, trainorder.getId(), 0L,
                                trainorder.getOrderstatus(), "12306");
                        updateExtordercreatetime(trainorder.getId());
                        trainorders = Server.getInstance().getTrainService().findTrainorder(trainorder.getId());
                        String ticket_no = "";
                        String pid = "";
                        String seattype = "";
                        String seattypestr = "";
                        String piaotype = "0";
                        float price = 0f;
                        String piaotypename = "";
                        for (int i = 0; i < trainorders.getPassengers().size(); i++) {
                            for (Trainticket trainticket : trainorders.getPassengers().get(i).getTraintickets()) {
                                ticinfo = trainticket.getCoach() + "车厢," + trainticket.getSeatno();
                                ticket_no = trainticket.getTicketno();
                                seattype = trainticket.getSeattype();
                                seattypestr = getzwname(trainticket.getSeattype());
                                price = trainticket.getPrice();
                                piaotype = String.valueOf(trainticket.getTickettype());
                                piaotypename = trainticket.getTickettypestr() + "票";
                            }
                            pid = trainorders.getPassengers().get(i).getPassengerid();
                            Map<String, String> jsonper = new HashMap<String, String>();
                            jsonper.put("cxin", ticinfo);
                            String pidnumber = trainorders.getPassengers().get(i).getIdnumber();
                            jsonper.put("passengerid", pid);
                            jsonper.put("ticket_no", ticket_no);
                            jsonper.put("passengersename", trainorders.getPassengers().get(i).getName());
                            jsonper.put("passportseno", pidnumber);
                            jsonper.put("passporttypeseid", trainorders.getPassengers().get(i).getIdtype() + "");
                            jsonper.put("passporttypeseidname", trainorders.getPassengers().get(i).getIdtypestr());
                            jsonper.put("piaotype", piaotype);
                            jsonper.put("piaotypename", piaotypename);
                            jsonper.put("zwcode", seattypestr);
                            jsonper.put("zwname", seattype);
                            jsonper.put("price", String.valueOf(price));
                            jsonArray.add(jsonper);
                            //                        try {
                            //                            new TrainpayMqMSGUtil(MQMethod.ORDERGETURL_NAME).sendGetUrlMQmsg(trainorders.getId());
                            //                            WriteLog.write("12306_TongchengTrainOrder_MQ_GetUrl", " ：回调同程：" + trainorders.getId());
                            //                        }
                            //                        catch (Exception e) {
                            //                            e.printStackTrace();
                            //                        }
                        }
                        ordersuccess = true;
                        code = "100";
                        msg = "创建订单成功";

                    }
                    else {
                        res = res == null ? "" : res;
                        /**
                         * res 里的信息包含12306返回的信息
                         */
                        if (res.indexOf("没有余票") > -1 || res.indexOf("此车次无票") > -1 || res.indexOf("已无余票") > -1) {
                            code = "301";
                            msg = "没有余票";
                            helpinfo = res;//helpinfo可以直接返回
                        }
                        //                    elSE IF (RES != NULL && !"".EQUALS(RES) && RES.INDEXOF("未完成") > -1) {
                        //                        CODE = "304";
                        //                        HELPINFO = "已经超过未完成订单的授权的数量";
                        //                    }
                        else if (res.indexOf("已订") > -1) {
                            code = "305";
                            msg = "乘客已经预订过该车次";
                            helpinfo = res;
                        }
                        else if (res.indexOf("身份") > -1) {
                            code = "308";
                            msg = "乘客身份信息未通过验证订票失败";
                            helpinfo = res;
                        }
                        else if (res.indexOf("其他订单行") > -1 || res.indexOf("本次购票行程冲突") > -1) {
                            code = "310";
                            msg = "本次购票与其他订单行程冲突";
                            helpinfo = res;
                        }
                        else if (res.indexOf("距离开车时间太近") > -1) {
                            code = "700";
                            msg = "距离开车时间太近";
                            helpinfo = msg;
                        }
                        else {//同步转异步
                            code = "950";
                            msg = "同步订单已经转为异步";
                            helpinfo = "";//helpinfo可以直接返回
                            boolean iswaitorder = false;//同步转异步后是否处于下单中
                            try {
                                if ("".equals(res) || res == null) {
                                    trainorder.setState12306(2);//12306状态--(同步转异步)下单中
                                }
                                else {//res.indexOf("多次打码失败") >= 0
                                    trainorder.setState12306(1);//12306状态--(同步转异步)等待下单
                                    iswaitorder = true;
                                }
                                trainorder.setChangesupplytradeno("");
                                trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
                                orderid = getordernumberbyid(trainorder.getId());
                                addMailAddress(jsonObject, trainorder);
                                createtrainorderrc(1, "(同步转异步)提交订单成功", trainorder.getId(), 0L,
                                        trainorder.getOrderstatus(), customeruser.getMembername());
                                WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":订单id:" + trainorder.getId()
                                        + ":iswaitorder:" + iswaitorder);
                                if (iswaitorder) {
                                    newyibuchulidingdan(acquiringresult, trainorder);//异步或者同步转异步需要的操作
                                    diaoyongTrainAccountSrcUtildeinsertData(trainorder.getId(), username, userpassword,
                                            partnerid, cookie);
                                }
                                ordersuccess = false;
                            }
                            catch (Exception e) {
                                //TODO createtrainorder
                                logger.error("tongchengTrainorder_createtrainorder", e.fillInStackTrace());
                                code = "113";
                                jsonstr.put("tooltip", "当前时间不提供服务");
                            }
                            WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":同步转异步了:trainorderid:" + trainorder.getId());
                            String ticket_no = "";
                            String pid = "";
                            String seattype = "";
                            String seattypestr = "";
                            String piaotype = "0";
                            float price = 0f;
                            String piaotypename = "";
                            for (int i = 0; i < passengers.size(); i++) {
                                for (Trainticket trainticket : passengers.get(i).getTraintickets()) {
                                    //                                if (ordersuccess) {
                                    //                                    ticinfo = trainticket.getCoach() + "车厢," + trainticket.getSeatno();
                                    //                                }
                                    ticket_no = trainticket.getTicketno();
                                    seattype = trainticket.getSeattype();
                                    seattypestr = getzwname(trainticket.getSeattype());
                                    price = trainticket.getPrice();
                                    piaotype = String.valueOf(trainticket.getTickettype());
                                    piaotypename = trainticket.getTickettypestr() + "票";
                                }
                                pid = passengers.get(i).getPassengerid();
                                Map<String, String> jsonper = new HashMap<String, String>();
                                jsonper.put("cxin", ticinfo);
                                String pidnumber = passengers.get(i).getIdnumber();
                                jsonper.put("passengerid", pid);
                                jsonper.put("ticket_no", ticket_no);
                                jsonper.put("passengersename", passengers.get(i).getName());
                                jsonper.put("passportseno", pidnumber);
                                jsonper.put("passporttypeseid", passengers.get(i).getIdtype() + "");
                                jsonper.put("passporttypeseidname", passengers.get(i).getIdtypestr());
                                jsonper.put("piaotype", piaotype);
                                jsonper.put("piaotypename", piaotypename);
                                jsonper.put("zwcode", seattypestr);
                                jsonper.put("zwname", seattype);
                                jsonper.put("price", String.valueOf(price));
                                jsonArray.add(jsonper);
                            }
                        }
                        //                    else {
                        //                        code = "999";
                        //                        msg = "订单失败";
                        //                        helpinfo = msg;
                        //                    }
                    }
                    jsonstr.put("transactionid", trainorders.getOrdernumber());
                    jsonstr.put("ordersuccess", ordersuccess);
                    jsonstr.put("orderamount", trainorders.getOrderprice());
                    jsonstr.put("checi", checi);
                    jsonstr.put("from_station_code", from_station_code);
                    jsonstr.put("from_station_name", from_station_name);
                    jsonstr.put("to_station_code", to_station_code);
                    jsonstr.put("to_station_name", to_station_name);
                    jsonstr.put("train_date", train_date);
                    Trainticket trainticket = trainorder.getPassengers().get(0).getTraintickets().get(0);
                    jsonstr.put("start_time", trainticket.getDeparttime().split(" ")[1]);
                    jsonstr.put("arrive_time", trainticket.getArrivaltime());
                    jsonstr.put("runtime", trainticket.getCosttime());
                    jsonstr.put("passengers", jsonArray);
                    jsonstr.put("help_info", helpinfo);
                }
                else {// 同程的异步接口
                      // orderid 1~32 string 合作伙伴方订单号
                      // code 1~32 string 消息代码 802：操作请求已接受 112：订单状态不正确 111：处理失败
                      // success 1~8 bool 订票是否成功
                      // msg 1~255 string 代码描述
                      // reqtoken 1~64 string API用户请求时传入的特征
                      // tooltip 1~255 string 提示内容，失败时，此处会显示原因

                    b_istongbu = false;
                    try {
                        trainorder.setState12306(Trainorder.WAITORDER);// 12306状态--等待下单
                        boolean isTimeoutOrder = false;
                        try {
                            String sql = "SELECT C_ORDERNUMBER FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='"
                                    + orderid + "' AND C_AGENTID=" + agentid;
                            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                            if (list.size() > 0) {
                                Map map = (Map) list.get(0);
                                String ordernumberString = map.get("C_ORDERNUMBER").toString();
                                orderid = ordernumberString;
                                isTimeoutOrder = true;
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        boolean createSuccess = false;//插入数据库异常标识     默认为false    当数据库插入订单发生异常时为   true
                        if (!isTimeoutOrder) {//当前订单不存在时    创建订单
                            trainorder.setContactuser(reqtoken);// 回调时候使用
                            try {
                                trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
                                createSuccess = true;
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (createSuccess) {//判断之后   下面的情况是创建订单成功
                                createTrainOrderExtSeat(trainorder, jsonObject);
                                orderid = getordernumberbyid(trainorder.getId());
                                addWormholedata(trainorder.getId(), WormholeAccount);//有订单是添加到TrainOrderIsWormhole里面
                                addMailAddress(jsonObject, trainorder);
                                createtrainorderrc(1, "提交订单成功", trainorder.getId(), 0L, trainorder.getOrderstatus(),
                                        customeruser.getMembername());
                                newyibuchulidingdan(acquiringresult, trainorder);
                                diaoyongTrainAccountSrcUtildeinsertData(trainorder.getId(), username, userpassword,
                                        partnerid, cookie);
                                code = "802";
                                ordersuccess = true;
                                msg = "创建订单成功";
                            }
                            else {
                                code = "999";
                                ordersuccess = false;
                                msg = "订单创建失败";
                                WriteLog.write("闩锁问题订单", "使用方订单号 " + orderid);
                            }
                        }
                        else if (partnerid.contains("gaotie")) {
                            code = "100802";
                            ordersuccess = false;
                            msg = "重复下单";
                        }
                        else {//当订单已存在 
                            code = "802";
                            ordersuccess = true;
                            msg = "创建订单成功";
                        }
                        //                        if (createSuccess) {//数据库插入失败
                        //                            code = "999";
                        //                            ordersuccess = false;
                        //                            msg = "订单创建失败";
                        //                        }else if (!isTimeoutOrder){
                        //                                createTrainOrderExtSeat(trainorder, jsonObject);
                        //                                orderid = getordernumberbyid(trainorder.getId());
                        //                                addWormholedata(trainorder.getId(), WormholeAccount);//有订单是添加到TrainOrderIsWormhole里面
                        //                                addMailAddress(jsonObject, trainorder);
                        //                                createtrainorderrc(1, "提交订单成功", trainorder.getId(), 0L, trainorder.getOrderstatus(),
                        //                                        customeruser.getMembername());
                        //                                newyibuchulidingdan(acquiringresult, trainorder);
                        //                                diaoyongTrainAccountSrcUtildeinsertData(trainorder.getId(), username, userpassword,
                        //                                        partnerid, cookie);
                        //                            code = "802";
                        //                            ordersuccess = true;
                        //                            msg = "创建订单成功";
                        //                        }
                        jsonstr.put("reqtoken", reqtoken);
                    }
                    catch (Exception e) {
                        logger.error("tongchengTrainorder_createtrainorder", e.fillInStackTrace());
                        code = "113";
                        ordersuccess = false;
                        jsonstr.put("tooltip", "当前时间不提供服务");
                    }
                }
            }
            //                else {
            //                    code = "999";
            //                    msg = "无此车次";
            //                    helpinfo = msg;
            //                }
            //        }
        }
        else {
            code = "506";
            msg = trainorder_system_error;
            helpinfo = msg;
        }
        jsonstr.put("code", code);
        jsonstr.put("orderid", orderid);
        if (giveUpOrder(agentid)) {
            jsonstr.put("transactionid", interfaceOrderNumber);
        }
        jsonstr.put("success", ordersuccess);
        jsonstr.put("msg", msg);
        /*
         * if (b_istongbu) { Long outtime = getOuttime(); Long time_interval =
         * System.currentTimeMillis() - starttime;//同步的时间差 if (outtime <=
         * time_interval) {// callBackTongChengOrdered(trainorder.getId(),
         * jsonstr, is_occupyingAseat_success, r1);//执行同程回调占座结果的方法 } }
         */

        Long endtime = System.currentTimeMillis();
        return jsonstr.toString();
    }

    private void addWormholedata(long id, String wormholeaccount) {
        if (!ElongHotelInterfaceUtil.StringIsNull(wormholeaccount)) {
            String sql = "EXEC [dbo].[sp_TrainOrderIsWormhole_insert]@orderid=" + id + ",@WormholeAccount='"
                    + wormholeaccount + "'";
            List list = new ArrayList();
            try {
                list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            }
            catch (Exception e) {
                WriteLog.write("添加订单异常_Error", sql);
                ExceptionUtil.writelogByException("添加订单异常_Error", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 同步和同步转异步的公共方法 如果指定的时间内没有返回结果，订单自动转为异步订单
     * 
     * @param trainorder
     * @param jsonstr
     * @param r1
     * @return
     * @time 2015年1月13日 下午1:38:45
     * @author chendong
     * @throws InterruptedException
     */
    private Trainorder tongbuxianchengmethod(Trainorder trainorder, JSONObject jsonstr, int r1)
            throws InterruptedException {
        int countthread = 1;
        ExecutorService threadPool2 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Trainorder>> futures = new ArrayList<Future<Trainorder>>(countthread);
        CompletionService<Trainorder> completionService = new ExecutorCompletionService<Trainorder>(threadPool2);
        futures.add(completionService.submit(new SubmittrainorderThread(trainorder, jsonstr, r1)));
        Long timeouttime = getOuttime();
        try {
            for (int i = 0; i < futures.size(); i++) {
                Future<Trainorder> result = completionService.poll(timeouttime, TimeUnit.MILLISECONDS);
                if (result == null) {
                    for (Future future : futures) {
                        if (future.isDone()) {
                            continue;
                        }
                        else {
                            future.cancel(true);
                            break;
                        }
                    }
                }
                else {
                    try {
                        if (result.isDone() && !result.isCancelled() && result.get() != null) {
                            trainorder = result.get();
                        }
                        else {
                        }
                    }
                    catch (ExecutionException ee) {
                        ee.printStackTrace(System.out);
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error(e.fillInStackTrace());
        }
        finally {
            for (Future<Trainorder> f : futures) {
                f.cancel(true);
            }
            threadPool2.shutdown();
        }
        return trainorder;
    }

    /**
     * 根据火车票订单id获取订单号
     * 
     * @param id
     * @return
     * @time 2015年1月5日 下午10:42:33
     * @author chendong
     */
    private String getordernumberbyid(long id) {
        String orderid = "";
        try {
            List list_trainorder = Server.getInstance().getSystemService()
                    .findMapResultBySql("SELECT C_ORDERNUMBER FROM T_TRAINORDER WHERE ID=" + id, null);
            if (list_trainorder.size() > 0) {
                Map maptrainnumber = (Map) list_trainorder.get(0);
                Object C_ORDERNUMBER_Object = maptrainnumber.get("C_ORDERNUMBER");
                if (C_ORDERNUMBER_Object != null) {
                    orderid = C_ORDERNUMBER_Object.toString();
                }
            }
        }
        catch (Exception e) {
            logger.error("Tongchengtrainorder_getordernumberbyid:" + id, e.fillInStackTrace());
        }
        return orderid;
    }

    /**
     * 获取同程同步的超时时间 默认 30秒
     * @time 2015年1月5日 下午7:36:04
     * @author chendong
     */
    private Long getOuttime() {
        Long outtime = 30000L;
        String outtime_string = getSystemConfig("tongcheng_tongbu_outtime");
        if ("-1".equals(outtime_string)) {
        }
        else {
            try {
                outtime = Long.parseLong(outtime_string);
            }
            catch (Exception e) {
            }
        }
        return outtime;
    }

    /**
     * 同程同步如果超时就回调占座结果
     * 
     * @param orderid
     *            我们自己的订单id
     * @param jsonstr
     *            给同程返回的信息
     * @param is_occupyingAseat_success
     *            是否占座成功
     * @return
     * @time 2014年12月12日 下午2:20:30
     * @author fiend
     * @param is_occupyingAseat_success
     */
    // TODO 同程同步如果超时就回调占座结果
    public String callBackTongChengOrdered(long orderid, JSONObject jsonstr, boolean is_occupyingAseat_success, int r1) {
        String result = "false";
        // {"from_station_name":"济南","runtime":"00:16","checi":"D6016","code":"999","msg":"订单失败",
        // "from_station_code":"JNK","to_station_name":"济南西","arrive_time":"22:01",
        // "passengers":[],"to_station_code":"JGK","help_info":"订单失败","train_date":"2015-01-19",
        // "ordersuccess":false,"start_time":"21:45","orderid":"TC_20150101_cdtest_1420181297890_965545","success":false}
        String tongchengorderid = jsonstr.getString("orderid");
        String help_info = jsonstr.getString("help_info");// 同程客户看
        String msg = jsonstr.getString("msg");// 同程看

        String tcTrainCallBack = getSysconfigString("tcTrainCallBack");
        JSONObject jso = new JSONObject();
        jso.put("trainorderid", orderid);
        jso.put("method", "train_order_callback");
        jso.put("tongchengorderid", tongchengorderid);
        jso.put("ordersuccess", is_occupyingAseat_success);
        jso.put("resultJsonstr", jsonstr);
        jso.put("returnmsg", "");
        try {
            WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":同步超时请求同步转异步接口:" + jso.toString() + ":" + tcTrainCallBack);
            result = SendPostandGet.submitPost(tcTrainCallBack, jso.toString(), "UTF-8").toString();
            WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":同步超时请求同步转异步接口返回结果:" + result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private float gettotalpricebytrainpassenger(List<Trainpassenger> passengers) {
        return 0;
    }

    /**
     * 添加保险
     */
    public void addInsurPrice(List<Trainpassenger> passengers, JSONObject json) {
        try {
            String insurtypeid = json.getString("insurtypeid");//保险类型ID
            if (insurtypeid != null && !"".equals(insurtypeid)) {
                for (Trainpassenger passenger : passengers) {
                    for (Trainticket ticket : passenger.getTraintickets()) {
                        if ("1".equals(insurtypeid)) {
                            ticket.setInsurorigprice(20f);
                        }
                        else if ("2".equals(insurtypeid)) {
                            ticket.setInsurorigprice(5f);
                        }
                    }
                }
            }
        }
        catch (Exception e) {

        }
    }

    /**
     * 把json里的对象转换成我们自己的 List<Trainpassenger>
     * 
     * @param jsons
     * @param traininfo
     * @param checi
     * @param to_station_name
     * @param train_date
     * @param from_station_name
     * @return
     * @time 2015年1月15日 上午9:35:03
     * @author chendong
     * @throws TrainOrderException 
     */
    private List<Trainpassenger> gettrainpassenger(JSONArray jsons, Train traininfo, String checi,
            String to_station_name, String train_date, String from_station_name) throws TrainOrderException {
        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();
        for (int i = 0; i < jsons.size(); i++) {
            Trainpassenger trainpassenger = new Trainpassenger();// 订单的人员信息
            Trainticket ticket = new Trainticket();// 票
            String passengerid = jsons.getJSONObject(i).getString("passengerid");// 乘客的顺序号
            String ticket_no = jsons.getJSONObject(i).getString("ticket_no");// 票号
            String passengersename = jsons.getJSONObject(i).getString("passengersename");// 乘客姓名
            String passportseno = jsons.getJSONObject(i).getString("passportseno");// 乘客证件号码
            String passporttypeseid = jsons.getJSONObject(i).getString("passporttypeseid");// 证件类型ID
            String passporttypeseidname = jsons.getJSONObject(i).getString("passporttypeseidname");// 证件类型名称
            String piaotype = jsons.getJSONObject(i).getString("piaotype");// 票种ID
            String piaotypename = jsons.getJSONObject(i).getString("piaotypename");// 票种名称
            String zwcode = jsons.getJSONObject(i).getString("zwcode");// 座位编码
            String zwname = jsons.getJSONObject(i).getString("zwname");// 座位名称
            if (zwname == null) {
                throw new TrainOrderException("103 err|zwname is null");
            }
            zwname = zwname.replace("上", "").replace("中", "").replace("下", "");
            String cxin = jsons.getJSONObject(i).getString("cxin");// 几车厢几座
            String price = jsons.getJSONObject(i).getString("price");// 票价
            try {
                String identitystatusid = jsons.getJSONObject(i).getString("identitystatusid");//
                if (identitystatusid != null) {
                    Integer i_identitystatusid = Integer.parseInt(identitystatusid);
                    trainpassenger.setIdentitystatusid(i_identitystatusid);
                }
                else {
                    trainpassenger.setIdentitystatusid(0);
                }
            }
            catch (Exception e) {
                logger.error("identitystatusid:" + e.fillInStackTrace());
            }
            // if (price != null && !"".equals(price)) {
            // totalprice += Float.valueOf(price);
            // }
            passportseno = AirUtil.ToDBC(passportseno);
            // 乘客信息
            if (passportseno.length() > 14) {
                trainpassenger.setBirthday(passportseno.substring(6, 14));
            }
            else {
                trainpassenger.setBirthday("");
            }
            trainpassenger.setName(passengersename);
            trainpassenger.setIdtype(getIdtype12306tolocal(passporttypeseid));
            trainpassenger.setIdnumber(passportseno);
            trainpassenger.setAduitstatus(0);
            trainpassenger.setChangeid(0);
            trainpassenger.setPassengerid(passengerid);
            // 票信息
            ticket.setTrainno(checi);
            ticket.setPrice(Float.valueOf(price));
            ticket.setPayprice(Float.valueOf(price));
            // ticket.setCoach(cxin);
            // ticket.setSeatno(zwcode);
            if ("无座".equals(zwname)) {
                String codeNum = "";
                if (checi.startsWith("D")) {
                    zwname = "二等座";
                }
                else if (checi.startsWith("C")) {
                    zwname = "软座";
                }
                else {
                    zwname = "硬座";
                }
                ticket.setSeattype(zwname);
            }
            else {
                ticket.setSeattype(zwname);
            }
            ticket.setSeattype(zwname);
            ticket.setArrival(to_station_name);
            ticket.setTcseatno(passengerid);
            if (traininfo != null && traininfo.getStarttime() != null) {
                ticket.setDeparttime(train_date + " " + traininfo.getStarttime());
            }
            else {
                ticket.setDeparttime(train_date + " 00:00");
            }
            if (traininfo != null && traininfo.getEndtime() != null) {
                ticket.setArrivaltime(traininfo.getEndtime());
            }
            ticket.setDeparture(from_station_name);
            if (traininfo != null && traininfo.getCosttime() != null) {
                ticket.setCosttime(traininfo.getCosttime());// 历时
            }
            ticket.setStatus(Trainticket.WAITPAY);
            ticket.setInsurprice(0f);// 采购支付
            ticket.setInsurorigprice(0f);// 保险
            ticket.setInsurenum(0);
            piaotype = (piaotype == null || "".equals(piaotype)) ? "1" : piaotype;
            ticket.setTickettype(Integer.parseInt(piaotype));
            List<Trainticket> tickets = new ArrayList<Trainticket>();
            tickets.add(ticket);
            trainpassenger.setTraintickets(tickets);
            JSONObject JSONObjecttrainpassenger = jsons.getJSONObject(i);
            int tickettype = ticket.getTickettype();// 1:成人票，2:儿童票，3:学生票，4:残军票
            trainpassenger = setStudentInfo(trainpassenger, i, JSONObjecttrainpassenger, tickettype);
            passengers.add(trainpassenger);
        }
        return passengers;
    }

    /**
     * 设置学生票信息
     * @return
     * @time 2016年1月19日 上午11:19:55
     * @author chendong
     * @param i 
     * @param trainpassenger 
     * @param jSONObjecttrainpassenger 
     * @param tickettype 
     */
    private Trainpassenger setStudentInfo(Trainpassenger trainpassenger, int i, JSONObject jSONObjecttrainpassenger,
            int tickettype) {
        try {
            //TODO 学生票
            List<TrainStudentInfo> trainStudentInfos = new ArrayList<TrainStudentInfo>();
            TrainStudentInfo trainStudentInfo = new TrainStudentInfo();
            String province_name = jSONObjecttrainpassenger.getString("province_name") == null ? ""
                    : jSONObjecttrainpassenger.getString("province_name");// 省份名称
            String province_code = jSONObjecttrainpassenger.getString("province_code") == null ? ""
                    : jSONObjecttrainpassenger.getString("province_code");// 省份编号
            String school_code = jSONObjecttrainpassenger.getString("school_code") == null ? ""
                    : jSONObjecttrainpassenger.getString("school_code");// 学校代号
            String school_name = jSONObjecttrainpassenger.getString("school_name") == null ? ""
                    : jSONObjecttrainpassenger.getString("school_name");// 学校名称
            String student_no = jSONObjecttrainpassenger.getString("student_no") == null ? ""
                    : jSONObjecttrainpassenger.getString("student_no");// 学号
            String school_system = jSONObjecttrainpassenger.getString("school_system") == null ? ""
                    : jSONObjecttrainpassenger.getString("school_system");// 学制
            String enter_year = jSONObjecttrainpassenger.getString("enter_year") == null ? ""
                    : jSONObjecttrainpassenger.getString("enter_year"); // 入学年份
            String preference_from_station_name = jSONObjecttrainpassenger.getString("preference_from_station_name") == null ? ""
                    : jSONObjecttrainpassenger.getString("preference_from_station_name");// 起始名称
            String preference_from_station_code = jSONObjecttrainpassenger.getString("preference_from_station_code") == null ? ""
                    : jSONObjecttrainpassenger.getString("preference_from_station_code");// 起始地代号
            String preference_to_station_name = jSONObjecttrainpassenger.getString("preference_to_station_name") == null ? ""
                    : jSONObjecttrainpassenger.getString("preference_to_station_name");// 到达名称
            String preference_to_station_code = jSONObjecttrainpassenger.getString("preference_to_station_code") == null ? ""
                    : jSONObjecttrainpassenger.getString("preference_to_station_code");// 到达地代号
            if (3 == tickettype) {
                trainStudentInfo.setStudentcard("");//优惠卡号
                trainStudentInfo.setClasses(""); //所在班级
                trainStudentInfo.setDepartment(""); //所在院系
                trainStudentInfo.setEductionalsystem(school_system); //学制
                trainStudentInfo.setEntranceyear(enter_year); //入学年份
                trainStudentInfo.setFromcity(preference_from_station_name); //出发城市
                trainStudentInfo.setTocity(preference_to_station_name); //到达城市
                trainStudentInfo.setSchoolprovince(province_name); //学校所在省
                trainStudentInfo.setSchoolname(school_name); //学校名称
                trainStudentInfo.setStudentno(student_no); //学生证号
                trainStudentInfo.setSchoolnamecode(school_code); //学校代号
                trainStudentInfo.setSchoolprovincecode(province_code); //学校所在省代号
                trainStudentInfo.setFromcitycode(preference_from_station_code); //出发城市代号
                trainStudentInfo.setTocitycode(preference_to_station_code); //到达城市代号
                trainStudentInfo.setArg1(0l); //备用参数1
                trainStudentInfo.setArg2(""); //备用参数2
                trainStudentInfo.setArg3(0l); //备用参数3
                trainStudentInfos.add(trainStudentInfo);
                trainpassenger.setTrainstudentinfos(trainStudentInfos);
            }
        }
        catch (Exception e) {
            logger.error(trainpassenger + ":" + i + ":" + jSONObjecttrainpassenger + ":" + tickettype, e);
        }
        return trainpassenger;
    }

    /**
     * 说明：根据12306要求，将数据库中日期转变格式
     * 
     * @param date
     * @return date
     * @time 2014年8月30日 上午11:18:41
     * @author yinshubin
     */
    private String changeDate(String date) {
        try {
            Date date_result = new Date();
            DateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm");
            date_result = df.parse(date);
            DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
            date = dfm.format(date_result);
        }
        catch (ParseException e1) {
            logger.error(e1.fillInStackTrace());
        }
        return date;
    }

    /**
     * 修改12306下单成功时间
     * 
     * @param trainorderid
     * @time 2015年1月19日 下午2:52:46
     * @author chendong
     */
    private void updateExtordercreatetime(Long trainorderid) {
        String sql1 = "UPDATE T_TRAINORDER SET C_EXTORDERCREATETIME='" + new Timestamp(System.currentTimeMillis())
                + "' WHERE ID=" + trainorderid;
        try {
            int i1 = Server.getInstance().getSystemService().excuteGiftBySql(sql1);
        }
        catch (Exception e) {
            logger.error("tongchengtrainorder_updateExtordercreatetime", e.fillInStackTrace());
        }
    }

    /**
     * 根据单个供应商判断收单
     * 
     * 
     * 
     * @param agentid
     * @return 收单返回true 不收单返回false
     * @time 2015年11月5日 下午2:35:52
     * @author chendong
     */
    public boolean getAcquiringflagByAgentID(String agentid) {
        String AcquiringflagMsg = "";
        try {
            String sysflag = getSysconfigString("sysflag");//系统标识 系统标识 1 同程 2 空铁
            String quiringOrderkey = "quiringOrderkey_" + sysflag + "_" + agentid;
            if ("".equals(MemCached.getInstance().get(quiringOrderkey))) {
                return false;
            }
            else {
                Object o_AcquiringflagMsg = MemCached.getInstance().get(quiringOrderkey);//系统整体收单标识
                AcquiringflagMsg = o_AcquiringflagMsg == null ? "" : o_AcquiringflagMsg.toString();
                if ("success".equals(AcquiringflagMsg)) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            logger.error("allAcquiringflag:" + e.fillInStackTrace());
        }
        return false;
    }

    /**
     * 整体收单
     * @return
     */
    public String getAllAcquiringflag() {
        String Acquiringflag = "";
        try {
            Object o_AcquiringflagMsg = MemCached.getInstance().get("allAcquiringflag");//系统整体收单标识
            String AcquiringflagMsg = o_AcquiringflagMsg == null ? "" : o_AcquiringflagMsg.toString();
            if ("".equals(AcquiringflagMsg)) {
                Acquiringflag = getSystemConfig("allAcquiringflag");
                MemCached.getInstance().delete("allAcquiringflag");
                MemCached.getInstance().add("allAcquiringflag", Acquiringflag, new Date(0));
                System.out.println("获取数据库Acquiringflag：" + Acquiringflag);
            }
            else {
                Acquiringflag = AcquiringflagMsg;//系统整体收单标识
            }
        }
        catch (Exception e) {
            logger.error("allAcquiringflag:" + e.fillInStackTrace());
        }
        return Acquiringflag;
    }

    /**
     * 供应商收单处理
     * 收单模式 保存数据库
     */
    public void saveOrderInfoShouDan(String ordermsg, String key, String agentid) {
        try {
            String countsql = "select count(id) from T_TRAINORDERMSG where C_KEY='" + key + "'";
            int c1 = Server.getInstance().getSystemService().countGiftBySql(countsql);
            WriteLog.write("t同程火车票接口_4.5收单", ordermsg);
            if (c1 <= 0) {
                String msgsql = "insert into T_TRAINORDERMSG(C_MSG,C_TIME,C_STATE,C_USERID,C_KEY,C_MSGTYPE,C_INTERFACETYPE,C_AGENTID) values('"
                        + ordermsg
                        + "','"
                        + new Timestamp(System.currentTimeMillis())
                        + "',1,0,'"
                        + key
                        + "',1,1,"
                        + agentid + ")";
                int i1 = Server.getInstance().getSystemService().excuteGiftBySql(msgsql);
            }
            else {
                WriteLog.write("t同程火车票接口_4.5收单", "已存在：" + key);
            }
        }
        catch (Exception e) {
            logger.error("tongchengtrainorder_updateExtordercreatetime", e.fillInStackTrace());
        }
    }

    /**
     * 注册用户
     * @param mobile
     * @param registpassword
     * @param agentid
     * @return
     */
    public Customeruser createUser(String mobile, String registpassword, long agentid) {
        Customeruser user = new Customeruser();
        user.setLoginname(mobile);
        try {
            String registpasswordtoMD5 = ElongHotelInterfaceUtil.MD5(registpassword);
            user.setLogpassword(registpasswordtoMD5);
            user.setMobile(mobile);
            user.setMembertype(3);
            // 用户的状态是否可用
            user.setState(1);
            // 是否网站会员
            user.setIsweb(1);
            // 代理商的id
            user.setAgentid(agentid);
            // set是否启用
            user.setIsenable(1);
            // 注册会员的时间
            user.setCreatetime(new Timestamp(System.currentTimeMillis()));
            user = Server.getInstance().getMemberService().createCustomeruser(user);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 添加邮寄地址
     * @param busstype  1 d订单邮寄  2 保险邮寄
     */
    public void addMailAddress(JSONObject json, Trainorder order) {
        try {
            //            System.out.println(json.toJSONString());
            //            System.out.println(order);
            boolean mailflag = json.getBooleanValue("mailflag");//火车票订单邮寄标识
            //            System.out.println(mailflag);
            if (!mailflag) {
                return;
            }
            if (mailflag) {
                WriteLog.write("h火车票订单邮寄标识", order.getId() + "jsonstr:" + json);
                String mailname = json.getString("mailname");
                String mailtel = json.getString("mailtel");
                String mailcode = json.getString("mailcode");
                String mailaddress = json.getString("mailaddress");
                MailAddress address = new MailAddress();
                address.setOrderid((int) order.getId());
                address.setBusstype(1);//火车票订单
                address.setAddress(mailaddress);
                address.setMailName(mailname);
                address.setMailTel(mailtel);
                address.setPostcode(mailcode);
                address.setAgentId(order.getAgentid());
                address.setOrdernumber(order.getOrdernumber());
                List<Trainpassenger> plist = order.getPassengers();
                try {
                    for (int i = 0; i < plist.size(); i++) {
                        if (i == 0) {
                            Trainpassenger p = plist.get(i);
                            List<Trainticket> tickets = p.getTraintickets();
                            for (int j = 0; j < tickets.size(); j++) {
                                if (j == 0) {
                                    address.setDeparttime(tickets.get(0).getDeparttime());
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                address.setPrintState(1);//带打印
                address.setState(1);
                address.setCreatetime(getCurrentTime());
                Server.getInstance().getMemberService().createMailAddress(address);
            }
            boolean insurinvoiceflag = json.containsKey("insurinvoiceflag") ? json.getBoolean("insurinvoiceflag")
                    : false;//保险发票
            if (insurinvoiceflag) {
                WriteLog.write("h保险发票", order.getId() + "jsonstr:" + json);
                String mailname = json.getString("mailname");
                String mailtel = json.getString("mailtel");
                String mailcode = json.getString("mailcode");
                String mailaddress = json.getString("mailaddress");
                MailAddress address = new MailAddress();
                address.setOrderid((int) order.getId());
                address.setBusstype(2);//保险
                address.setAddress(mailaddress);
                address.setMailName(mailname);
                address.setMailTel(mailtel);
                address.setPostcode(mailcode);
                address.setAgentId(order.getAgentid());
                address.setOrdernumber(order.getOrdernumber());
                List<Trainpassenger> plist = order.getPassengers();
                try {
                    for (int i = 0; i < plist.size(); i++) {
                        if (i == 0) {
                            Trainpassenger p = plist.get(i);
                            List<Trainticket> tickets = p.getTraintickets();
                            for (int j = 0; j < tickets.size(); j++) {
                                if (j == 0) {
                                    address.setDeparttime(tickets.get(0).getDeparttime());
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                address.setPrintState(1);//带打印
                address.setState(1);
                address.setCreatetime(getCurrentTime());
                Server.getInstance().getMemberService().createMailAddress(address);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void diaoyongTrainAccountSrcUtildeinsertData(long id, String UserName, String PassWord, String partnerid,
            String cookie) {
        if ((UserName != null && PassWord != null && UserName.length() > 0 && PassWord.length() > 0)
                || (cookie != null && cookie.length() > 0)) {
            TrainAccountSrcUtil.insertData(UserName, PassWord, partnerid, id, cookie);
        }
    }

    /**
     * 接口返回接口方订单号
     * 
     * @param agentid
     * @return
     * @time 2016年3月24日 上午10:06:39
     * @author fiend
     */
    private boolean giveUpOrder(String agentid) {
        try {
            String giveUpOrderAgentIds = PropertyUtil.getValue("giveUpOrderAgentIds", "Train.properties");
            String[] giveUpOrderAgentIdss = giveUpOrderAgentIds.split(",");
            for (String string : giveUpOrderAgentIdss) {
                if (agentid.equals(string)) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

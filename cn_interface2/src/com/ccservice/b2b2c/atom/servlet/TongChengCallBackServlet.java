package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.atom.servlet.QunarTrain.Thread.MyThreadCancelMessageAgain;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengCodeSwitch;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengQueueCallbackMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengTrainOrder;
import com.ccservice.b2b2c.atom.servlet.format.json.ValueFilterTuniuChangeValue;
import com.ccservice.b2b2c.atom.servlet.job.Train12306StationInfo;
import com.ccservice.b2b2c.atom.servlet.yl.ElongTrainOrderCallBack;
import com.ccservice.b2b2c.atom.servlet.yl.YiLongCallBackMethod;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.TrainorderTimeUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.tenpay.util.MD5Util;

@SuppressWarnings("serial")
public class TongChengCallBackServlet extends HttpServlet {

    public String partnerid;

    public String key;

    public String payCallbackUrl;//支付回调

    public String refunCallBackUrl;//退款回调

    public String zhanzuojieguoBackUrl;//占座结果回调url

    public String budanBackUrl;//补单通知回调 URL

    public String quxiaoquerenBackUrl;

    @Override
    public void init() throws ServletException {
        super.init();
        this.key = this.getInitParameter("key");
        this.partnerid = this.getInitParameter("partnerid");
        this.payCallbackUrl = this.getInitParameter("payCallbackUrl");
        this.refunCallBackUrl = this.getInitParameter("refunCallBackUrl");
        this.zhanzuojieguoBackUrl = this.getInitParameter("zhanzuojieguoBackUrl");
        this.budanBackUrl = this.getInitParameter("budanBackUrl");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String result = "";
        PrintWriter out = null;
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        boolean is_refund_online = false;//是否是拉萨有关车次
        int r1 = new Random().nextInt(10000);
        String orderid = "";
        try {
            out = res.getWriter();
            //POST请求参数
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            String param = buf.toString();
            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
                JSONObject obj = new JSONObject();
                obj.put("success", false);
                obj.put("code", "101");
                obj.put("msg", "请求参数为空");
                result = obj.toString();
            }
            else {
                JSONObject json = JSONObject.parseObject(param);
                //请求方法
                String method = json.getString("method");
                //参数
                orderid = json.getString("orderid");
                String transactionid = json.getString("transactionid");
                //出票回调
                if ("train_pay_callback".equals(method)) {
                    String isSuccess_String = json.getString("isSuccess");
                    String iskefu = json.getString("iskefu");
                    String pkid = json.get("pkid") != null ? json.getString("pkid") : "";
                    String isSuccess = "N".equals(isSuccess_String) ? "N" : "Y";
                    WriteLog.write("t同程火车票接口_4.16确认出票回调通知", orderid + ":json:" + json.toJSONString());
                    result = payCallBack(orderid, transactionid, 0, isSuccess, iskefu, pkid);
                    WriteLog.write("t同程火车票接口_4.16确认出票回调通知", orderid + ":result:" + result);
                }
                //==============================线上退票通知
                else if ("train_refund_callback".equals(method)) {
                    result = refunCallBack(json);//退票通知
                }
                //==============================异步占座回调
                else if ("train_order_callback".equals(method)) {
                    Long trainorderid = json.getLong("trainorderid");
                    String unmatchedpasslist = json.getString("unmatchedpasslist");
                    WriteLog.write("t同程火车票接口_占座回调", r1 + ":trainorderid:" + trainorderid);
                    if (trainorderid > 0) { //如果大于0说明有订单号无论是异步的还是同步转异步的目前都是进入这里
                        String returnmsg = json.getString("returnmsg");
                        String interfacetype = json.getString("interfacetype");
                        if (json.containsKey("refund_online") && "1".equals(json.getString("refund_online"))) {
                            is_refund_online = true;
                            WriteLog.write("t同程火车票接口_占座回调", r1 + ":refund_online:" + json.getString("refund_online"));
                        }
                        WriteLog.write("t同程火车票接口_占座回调", r1 + ":returnmsg:1:" + geturldecode(returnmsg));
                        result = train_order_callback(trainorderid, returnmsg, r1, is_refund_online, interfacetype,
                                unmatchedpasslist, json.getIntValue("returncode"));//占座成功后回调给同程(异步接口)
                    }
                    else {//如果等于0说明是同步的占座失败
                        boolean ordersuccess = json.getBooleanValue("ordersuccess");
                        String tongchengorderid = json.getString("tongchengorderid");
                        String resultJsonstr = json.getString("resultJsonstr");
                        String agentid = json.getString("agentid");
                        result = train_order_callback_tongbuzhuanyibu(ordersuccess, tongchengorderid, resultJsonstr,
                                r1, agentid);
                    }
                } //4.17. 补单通知  45分钟了还没支付给12306,订单自动取消,需要重新去12306下单,然后可能信息变了但是我们需要通知给同程把最新的座位信息通知过去
                else if ("train_order_budan_callback".equals(method)) {
                    Long trainorderid = json.getLong("trainorderid");
                    if (trainorderid > 0) { //如果大于0说明有订单号无论是异步的还是同步转异步的目前都是进入这里
                        String returnmsg = json.getString("returnmsg");//备注的一下信息
                        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);
                        result = train_order_budan_callback(r1, trainorder, "", returnmsg);//占座成功后回调给同程(异步接口)
                    }
                    else {
                    }
                }
                //请求改签（改签占座）异步回调
                else if ("train_request_change".equals(method)) {
                    result = changeCallBack(orderid, json, 1);
                }
                //确认改签异步回调
                else if ("train_confirm_change".equals(method)) {
                    result = changeCallBack(orderid, json, 2);
                }
                //4.22. 核验登录状态接口
                else if ("get_trainAccount_status".equals(method)) {

                }
                //重新获取账号 信息或者其他的cookie
                else if ("reGetCookieOrLoginUserAndPasswordforCookie".equals(method)) {

                }
                //4.18. 确认取消回调通知[同程]升级版
                else if ("cancelCallback".equals(method)) {
                    WriteLog.write("t同程火车票接口_确认取消回调通知", json.toJSONString());
                    String jiekouorderno = json.getString("jiekouorderno");
                    String callbackurl = "http://train.17usoft.com/trainOrder/services/confirmCancelNotify";
                    //boolean success, String code, String msg, String partnerid, String key, String jiekouorderno, int methodtype, String callbackurl, String changereqtoken
                    new MyThreadCancelMessageAgain(true, "100", "取消订单成功", partnerid, key, jiekouorderno, 1,
                            callbackurl, "").start();
                    WriteLog.write("t同程火车票接口_确认取消回调通知", jiekouorderno + "--->已进线程");
                }
                //4.18. 确认取消回调通知[同程]
                else if ("confirm_cancel".equals(method)) {
                    result = confirm_cancel(orderid, json);
                }
                else if ("query_queue".equals(method)) {
                    String agentid = json.getString("agentid");
                    //获取内存中这个商户的信息
                    Map map = getkeybyagentid(agentid);
                    result = new TongchengQueueCallbackMethod().operate(json,
                            gettrainorderinfodatabyMapkey(map, "C_USERNAME"),
                            gettrainorderinfodatabyMapkey(map, "C_KEY"), gettimeString(3),
                            gettrainorderinfodatabyMapkey(map, "C_QUEUECALLBACKURL"));
                }
                //其他未知方法
                else {
                    JSONObject obj = new JSONObject();
                    obj.put("success", false);
                    obj.put("code", "106");
                    if (ElongHotelInterfaceUtil.StringIsNull(method)) {
                        obj.put("msg", "方法名为空");
                    }
                    else {
                        obj.put("msg", "未找到方法:" + method + ".");
                    }
                    result = obj.toString();
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("TongChengCallBackServlet_未知异常", e);
            log("回调", e.fillInStackTrace());
            JSONObject obj = new JSONObject();
            obj.put("success", false);
            obj.put("code", "999");
            obj.put("msg", "未知异常");
            result = obj.toString();
        }
        finally {
            WriteLog.write("t同程火车票接口_回调", r1 + ":orderid:" + orderid + ":" + result);
            if (out != null) {
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 45分钟了还没支付给12306,订单自动取消,需要重新去12306下单,然后可能信息变了但是我们需要通知给同程把最新的座位信息通知过去
     * 
     * @param trainorder
     * @param returnmsg
     * @param remedyfailmsg 补单失败原因（失败才有值）
     * @return
     * @time 2015年1月9日 上午11:17:12
     * @author chendong
     */
    private String train_order_budan_callback(int r1, Trainorder trainorder, String returnmsg, String remedyfailmsg) {
        String time = gettimeString(2);
        String sign;
        boolean remedysuccess = false;
        JSONObject jsonstr = new JSONObject();
        if ("true".equals(remedyfailmsg)) {
            remedyfailmsg = "";
            remedysuccess = true;
        }
        else {
            remedyfailmsg = TongChengCallBackServletUtil.geturlencode(remedyfailmsg);
        }
        try {
            sign = ElongHotelInterfaceUtil.MD5(key);
            sign = partnerid + time + sign;
            sign = ElongHotelInterfaceUtil.MD5(sign);
            jsonstr.put("reqtime", time);
            jsonstr.put("sign", sign);
            jsonstr.put("orderid", trainorder.getQunarOrdernumber());
            String ordernumber_new = "T_" + gettimeString(1) + TongchengSupplyMethod.getRandomNum(4, 1);
            ordernumber_new = trainorder.getOrdernumber();
            jsonstr.put("transactionid", ordernumber_new);
            //            jsonstr.put("transactionid", trainorder.getOrdernumber());
            jsonstr.put("remedysuccess", remedysuccess);//补单是否成功

            jsonstr.put("remedyfailmsg", remedyfailmsg);//补单失败原因（失败才有值）
            jsonstr.put("orderamount", trainorder.getOrderprice() + "");
            jsonstr.put("newordernumber", trainorder.getExtnumber());
            String checi = "";
            String from_station_code = "";
            String from_station_name = "";
            String to_station_code = "";
            String to_station_name = "";
            String train_date = "";
            String start_time = "";
            String arrive_time = "";
            //            String ordernumber = trainorder.getExtnumber();
            String runtime = "";
            JSONArray passengers = new JSONArray();
            for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                Trainpassenger trainpassenger = trainorder.getPassengers().get(i);
                Trainticket trainticket = trainpassenger.getTraintickets().get(0);
                from_station_name = trainticket.getDeparture();
                to_station_name = trainticket.getArrival();
                train_date = trainticket.getDeparttime().split(" ")[0];
                start_time = trainticket.getDeparttime() + ":00";
                runtime = trainticket.getCosttime();
                try {
                    arrive_time = TrainorderTimeUtil.getArrivalTime(start_time, runtime);
                }
                catch (Exception e1) {
                }
                checi = trainticket.getTrainno();
                JSONObject passengerjson = new JSONObject();
                //            passengerid int 乘客的顺序号
                String passengerid = trainpassenger.getPassengerid() == null ? "0" : trainpassenger.getPassengerid();
                passengerjson.put("passengerid", Integer.parseInt(passengerid));
                //            ticket_no   string  票号（此票在本订单中的唯一标识，订票成功后才有值）
                passengerjson.put("ticket_no", trainticket.getTicketno());
                //            passengersename string  乘客姓名
                String name = TongChengCallBackServletUtil.geturlencode(trainpassenger.getName());
                passengerjson.put("passengersename", name);
                //            passportseno    string  乘客证件号码
                passengerjson.put("passportseno", trainpassenger.getIdnumber());
                //            passporttypeseid    string  证件类型ID
                //            与名称对应关系:
                //            1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通行证，B:护照
                passengerjson.put("passporttypeseid", TongchengTrainOrder.getIdtype12306(trainpassenger.getIdtype()));
                //            passporttypeseidname    string  证件类型名称
                String passporttypeseidname = TongChengCallBackServletUtil.geturlencode(trainpassenger.getIdtypestr());
                passengerjson.put("passporttypeseidname", passporttypeseidname);
                //            piaotype    string  票种ID。
                //            与票种名称对应关系：
                //            1:成人票，2:儿童票，3:学生票，4:残军票
                passengerjson.put("piaotype", trainticket.getTickettype() + "");
                //            piaotypename    string  票种名称
                String piaotypename = TongChengCallBackServletUtil.geturlencode(trainticket.getTickettypestr() + "票");
                passengerjson.put("piaotypename", piaotypename);
                //            zwcode  string  座位编码。
                //            与座位名称对应关系：
                //            9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，
                //            4:软卧，3:硬卧，2:软座，1:硬座
                //            注意：当最低的一种座位，无票时，购买选择该座位种类，买下的就是无座(也就说买无座的席别编码就是该车次的最低席别的编码)，另外，当最低席别的票卖完了的时候才可以卖无座的票。
                passengerjson.put("zwcode", TongchengTrainOrder.getzwname(trainticket.getSeattype()));
                //            zwname  string  座位名称
                String zwname = TongChengCallBackServletUtil.geturlencode(trainticket.getSeattype());
                passengerjson.put("zwname", zwname);
                try {
                    //            cxin    string  几车厢几座（在订票成功后才会有值）
                    String cxin = trainticket.getCoach() + "车厢," + trainticket.getSeatno().replace('号', '座');
                    cxin = TongChengCallBackServletUtil.geturlencode(cxin);
                    passengerjson.put("cxin", cxin);
                }
                catch (Exception e) {
                }
                //            price   string  票价
                passengerjson.put("price", trainticket.getPrice() + "");
                //            reason  int 身份核验状态 0：正常 1：待审核 2：未通过
                passengerjson.put("reason", trainpassenger.getAduitstatus());
                passengers.add(passengerjson);
            }
            try {
                from_station_code = Train12306StationInfoUtil.getThreeByName(from_station_name);
                to_station_code = Train12306StationInfoUtil.getThreeByName(to_station_name);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            from_station_name = TongChengCallBackServletUtil.geturlencode(from_station_name);
            to_station_name = TongChengCallBackServletUtil.geturlencode(to_station_name);
            jsonstr.put("from_station_name", from_station_name);
            jsonstr.put("from_station_code", from_station_code);
            jsonstr.put("to_station_code", to_station_code);
            jsonstr.put("to_station_name", to_station_name);
            jsonstr.put("train_date", train_date);
            jsonstr.put("start_time", start_time);
            jsonstr.put("arrive_time", arrive_time);
            //            jsonstr.put("ordernumber", ordernumber);
            jsonstr.put("runtime", runtime);
            jsonstr.put("checi", checi);
            jsonstr.put("passengers", passengers);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //请求同程
        WriteLog.write("t同程火车票接口_4.17_补单通知", r1 + ":remedyinfo=" + jsonstr.toJSONString()
                + ":train_order_budan_callback:" + budanBackUrl);
        String ret = "false";
        try {
            ret = SendPostandGet.submitPost(budanBackUrl, "remedyinfo=" + jsonstr.toString(), "utf-8").toString();
        }
        catch (Exception e) {
            WriteLog.write("t同程火车票接口_4.17_补单通知_train_order_budan_callback_Exception", trainorder.getQunarOrdernumber()
                    + ":trainorderid:" + trainorder.getId() + ":" + e.fillInStackTrace().toString());
        }
        WriteLog.write("t同程火车票接口_4.17_补单通知", r1 + ":train_order_budan_callback:同程返回:" + ret);
        return ret;
    }

    /**
     * 同步转异步接口,超时了而且是失败了就进入到这里
     * @return
     * @time 2015年1月5日 下午8:41:37
     * @author chendong
     * @param tongchengorderid  同程订单号
     * @param resultJsonstr 同步时候返回给同程的json字符串
     * //{"from_station_name":"济南","runtime":"00:16","checi":"D6016","code":"999","msg":"订单失败",
        //"from_station_code":"JNK","to_station_name":"济南西","arrive_time":"22:01",
        //"passengers":[],"to_station_code":"JGK","help_info":"订单失败","train_date":"2015-01-19",
        //"ordersuccess":false,"start_time":"21:45","orderid":"TC_20150101_cdtest_1420181297890_965545","success":false}
     * @param ordersuccess 订票是否成功
     * @param r1 
     */

    private String train_order_callback_tongbuzhuanyibu(boolean ordersuccess, String tongchengorderid,
            String returnmsg, int r1, String agentid) {
        JSONObject jsonstr = new JSONObject();
        String reqtoken = System.currentTimeMillis() + "";
        //TODO 异步回调 传真实       API用户请求时传入的特征
        //取消订单交易关闭   说明没有占座成功
        boolean success = false;
        int code = 999;
        String msg = "";
        String res = returnmsg;
        res = res == null ? "" : geturldecode(res);
        if (res.indexOf("style='color:red'") > 0) {
            res = chuliretrunmsg1(res, r1);
        }
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", tongchengorderid + ":res:" + res);
        /**
         * res 里的信息包含12306返回的信息
         */
        if (res.indexOf("ErrorType506") > -1) {
            code = 506;
            msg = res.replace("ErrorType506", "");
            msg = TongChengCallBackServletUtil.geturlencode(msg);
        }
        else if (res.indexOf("没有余票") > -1 || res.indexOf("此车次无票") > -1 || res.indexOf("已无余票") > -1
                || res.indexOf("没有足够的票") > -1 || res.indexOf("余票不足") > -1 || res.indexOf("非法的席别") > -1) {
            code = 301;
            msg = "没有余票";
            msg = TongChengCallBackServletUtil.geturlencode(msg);
        }
        else if (res.indexOf("其他订单行") > -1 || res.indexOf("本次购票行程冲突") > -1) {
            code = 310;
            msg = TongChengCallBackServletUtil.geturlencode(res);
        }
        else if (res.indexOf("已订") > -1 || res.indexOf("已购买") > -1) {
            code = 305;
            //            msg = "乘客已经预订过该车次";
            msg = TongChengCallBackServletUtil.geturlencode(res);
        }
        else if (res.indexOf("当前提交订单用户过多 ") > -1 || res.indexOf("提交订单失败：包含排队中的订单") > -1) {
            code = 307;
            msg = "当前提交订单用户过多 ";
            msg = TongChengCallBackServletUtil.geturlencode(msg);
        }
        else if (res.indexOf("冒用") > -1) {
            code = 315;
            msg = res;
            msg = TongChengCallBackServletUtil.geturlencode(msg);
        }
        else if (res.indexOf("身份") > -1) {
            code = 308;
            msg = "乘客身份信息未通过验证_订票失败  ";
            try {
                res = res.split(":")[1];
                msg += res;
            }
            catch (Exception e) {
            }
            WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", tongchengorderid + ":msg:" + msg);
            msg = TongChengCallBackServletUtil.geturlencode(msg);
        }

        else if (res.indexOf("距离开车时间太近") > -1) {
            code = 700;
            msg = "距离开车时间太近";
            msg = TongChengCallBackServletUtil.geturlencode(msg);
        }
        else if (res.indexOf("限制高消费") > -1) {
            code = 313;
            msg = TongChengCallBackServletUtil.geturlencode(res);
        }
        else if (res.indexOf("validatorMessage") > -1) {
            code = 999;
            msg = "订单失败";
            msg = TongChengCallBackServletUtil.geturlencode(msg);
        }
        else if (res.contains("可用账号使用中")) {
            code = 3151;
            msg = "该订单常旅对应的可用账号使用中，请等待";
            msg = TongChengCallBackServletUtil.geturlencode(msg);
        }
        else {
            code = 999;
            msg = TongChengCallBackServletUtil.geturlencode(res);
        }
        jsonstr.put("reqtoken", reqtoken);
        jsonstr.put("orderid", tongchengorderid);
        jsonstr.put("transactionid", "T123456");
        jsonstr.put("orderamount", "0.0");
        jsonstr.put("ordersuccess", ordersuccess);
        jsonstr.put("success", success);
        jsonstr.put("code", code);
        jsonstr.put("msg", msg);

        //        JSONArray unmatchedpasslist = new JSONArray();
        //        Map map = getkeybyagentid(agentid);
        //        String C_LOGINNAME = gettrainorderinfodatabyMapkey(map, "C_USERNAME");
        //        if (code == 315 && C_LOGINNAME.indexOf("tongcheng") > -1) {
        //            unmatchedpasslist.add(getUnmatchedpasslist(trainorderid));
        //            jsonstr.put("accountlist", accountlist);
        //            jsonstr.put("msg12306", returnmsg);
        //            jsonstr.put("unmatchedpasslist", unmatchedpasslist);
        //        }
        String zhanzuojieguoBackUrl_temp = zhanzuojieguoBackUrl;
        String zhanzuojieguoBackUrl_temp_other = TongChengCallBackServletUtil.getValueByMap(
                getcallbackurlbyAgentId(agentid), "C_ZHANZUOHUIDIAO");
        if (zhanzuojieguoBackUrl_temp_other != null && !"-1".equals(zhanzuojieguoBackUrl_temp_other)) {
            zhanzuojieguoBackUrl_temp = zhanzuojieguoBackUrl_temp_other;
        }
        //请求同程
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", r1 + ":train_order_callback_tongbuzhuanyibu:backurl:"
                + zhanzuojieguoBackUrl + ":parm:" + jsonstr.toJSONString());
        String ret = "";
        try {
            ret = SendPostandGet.submitPost(zhanzuojieguoBackUrl_temp, "data=" + jsonstr.toString(), "utf-8")
                    .toString();
        }
        catch (Exception e) {
            WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调_train_order_callback_tongbuzhuanyibu_Exception", tongchengorderid
                    + ":" + tongchengorderid + ":" + e.fillInStackTrace().toString());
        }
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", r1 + ":train_order_callback_tongbuzhuanyibu:同程返回:" + ret);
        return ret;
    }

    /**
     * 占座成功后回调给同程(异步接口或者同步超时成功回调)
     * 
     * @param trainorder
     * @time 2014年12月31日 下午8:12:37
     * @author chendong
     * @param returnmsg 
     * @param interfacetype 接口类型  参考 TrainInterfaceMethod
     */
    private String train_order_callback(long trainorderid, String returnmsg, int r1, boolean is_refund_online,
            String interfacetype, String unmatchedpasslistStr, int returncode) {
        Map traininfodataMap = getTrainorderstatus(1, trainorderid, "", "");//得到表中的一些信息
        Map dataMap = getcallbackurl(trainorderid, 1, "", "");
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", trainorderid + ":traininfodataMap:" + traininfodataMap);
        int orderstatus = 0;
        try {
            orderstatus = Integer.parseInt(gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERSTATUS"));
            //            if (interfacetype == null) {//如果interfaceType为空重新给这个赋值
            interfacetype = gettrainorderinfodatabyMapkey(traininfodataMap, "C_INTERFACETYPE");
            //            }
        }
        catch (Exception e) {
        }
        JSONObject jsonstr = new JSONObject();
        boolean success = false;//        true:成功，false:失败
        boolean ordersuccess = false;
        int code = 100;//    int 4   状态编码
        String msg = "";//   1~256   提示信息
        //        String reqtoken = System.currentTimeMillis() + "";
        // 异步回调 传真实       API用户请求时传入的特征
        //        String reqtoken = trainorder.getContactuser();
        String reqtoken = gettrainorderinfodatabyMapkey(traininfodataMap, "C_CONTACTUSER");
        jsonstr.put("reqtoken", reqtoken);
        //        String qunarordernumber = trainorder.getQunarOrdernumber();
        String qunarordernumber = gettrainorderinfodatabyMapkey(traininfodataMap, "C_QUNARORDERNUMBER");
        jsonstr.put("orderid", qunarordernumber);
        //        String Ordernumber = trainorder.getOrdernumber();
        String Ordernumber = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERNUMBER");
        jsonstr.put("transactionid", Ordernumber);
        //        String Orderprice = trainorder.getOrderprice();
        String Orderprice = gettrainorderinfodatabyMapkey(traininfodataMap, "C_TOTALPRICE");
        jsonstr.put("orderamount", Orderprice);
        String transactionid = "";
        Trainorder trainorder = null;
        if (orderstatus == 1) {//等待支付说明占座成功
            trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);//根据ID查询交易单号
            transactionid = trainorder.getOrdernumber();//交易单号
            String checi = "";
            String from_station_code = "";
            String from_station_name = "";
            String to_station_code = "";
            String to_station_name = "";
            String train_date = "";
            String start_time = "";
            String arrive_time = "";
            String ordernumber = trainorder.getExtnumber();
            String runtime = "";
            JSONArray passengers = new JSONArray();
            for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                Trainpassenger trainpassenger = trainorder.getPassengers().get(i);
                Trainticket trainticket = trainpassenger.getTraintickets().get(0);
                from_station_name = trainticket.getDeparture();
                to_station_name = trainticket.getArrival();
                train_date = trainticket.getDeparttime().split(" ")[0];
                start_time = trainticket.getDeparttime() + ":00";
                runtime = trainticket.getCosttime();
                //            int r1 = (int) (Math.random() * 10000);
                try {
                    arrive_time = TrainorderTimeUtil.getArrivalTime(start_time, runtime);//输入类车发车时间和运行时间，得到到达时间
                }
                catch (Exception e1) {
                }
                checi = trainticket.getTrainno();
                JSONObject passengerjson = new JSONObject();
                //            passengerid int 乘客的顺序号
                String passengerid = trainpassenger.getPassengerid() == null ? "0" : trainpassenger.getPassengerid();
                passengerjson.put("passengerid", passengerid);
                //            ticket_no   string  票号（此票在本订单中的唯一标识，订票成功后才有值）
                passengerjson.put("ticket_no", trainticket.getTicketno());
                //            passengersename string  乘客姓名
                String name = TongChengCallBackServletUtil.geturlencode(trainpassenger.getName());
                passengerjson.put("passengersename", name);
                //            passportseno    string  乘客证件号码
                passengerjson.put("passportseno", trainpassenger.getIdnumber());
                //            passporttypeseid    string  证件类型ID
                //            与名称对应关系:
                //            1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通行证，B:护照
                passengerjson.put("passporttypeseid", TongchengTrainOrder.getIdtype12306(trainpassenger.getIdtype()));
                //            passporttypeseidname    string  证件类型名称
                String passporttypeseidname = TongChengCallBackServletUtil.geturlencode(trainpassenger.getIdtypestr());
                passengerjson.put("passporttypeseidname", passporttypeseidname);
                //            piaotype    string  票种ID。
                //            与票种名称对应关系：
                //            1:成人票，2:儿童票，3:学生票，4:残军票
                passengerjson.put("piaotype", trainticket.getTickettype() + "");
                //            piaotypename    string  票种名称
                String piaotypename = TongChengCallBackServletUtil.geturlencode(trainticket.getTickettypestr() + "票");
                passengerjson.put("piaotypename", piaotypename);
                //            zwcode  string  座位编码。
                //            与座位名称对应关系：
                //            9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，
                //            4:软卧，3:硬卧，2:软座，1:硬座
                //            注意：当最低的一种座位，无票时，购买选择该座位种类，买下的就是无座(也就说买无座的席别编码就是该车次的最低席别的编码)，另外，当最低席别的票卖完了的时候才可以卖无座的票。
                passengerjson.put("zwcode", TongchengTrainOrder.getzwname(trainticket.getSeattype()));
                //            zwname  string  座位名称
                String zwname = TongChengCallBackServletUtil.geturlencode(trainticket.getSeattype());
                passengerjson.put("zwname", zwname);
                try {
                    //            cxin    string  几车厢几座（在订票成功后才会有值）
                    String cxin = trainticket.getCoach() + "车厢," + trainticket.getSeatno().replace('号', '座');
                    cxin = TongChengCallBackServletUtil.geturlencode(cxin);
                    passengerjson.put("cxin", cxin);
                }
                catch (Exception e) {
                }
                //            price   string  票价
                passengerjson.put("price", trainticket.getPrice() + "");
                //            reason  int 身份核验状态 0：正常 1：待审核 2：未通过
                passengerjson.put("reason", trainpassenger.getAduitstatus());
                passengers.add(passengerjson);
            }

            try {
                //                from_station_name = URLDecoder.decode(from_station_name);
                //                to_station_name = URLDecoder.decode(to_station_name);
                from_station_code = Train12306StationInfo.GetValue(from_station_name);//获取车站名称对应三字码
                to_station_code = Train12306StationInfo.GetValue(to_station_name);//获取车站名称对应三字码
            }
            catch (Exception e) {
                System.out.println("错误的三字码:" + from_station_name + ":" + to_station_name);
                System.out.println(e.fillInStackTrace());
            }
            from_station_name = TongChengCallBackServletUtil.geturlencode(from_station_name);
            to_station_name = TongChengCallBackServletUtil.geturlencode(to_station_name);
            jsonstr.put("from_station_name", from_station_name);
            jsonstr.put("from_station_code", from_station_code);
            jsonstr.put("to_station_name", to_station_name);
            jsonstr.put("to_station_code", to_station_code);
            jsonstr.put("train_date", train_date);
            jsonstr.put("start_time", start_time);
            jsonstr.put("arrive_time", arrive_time);
            jsonstr.put("ordernumber", ordernumber);
            jsonstr.put("runtime", runtime);
            jsonstr.put("checi", checi);
            jsonstr.put("passengers", passengers);
            if (is_refund_online) {
                jsonstr.put("refund_online", 1);
            }
            ordersuccess = true;
            success = true;
            msg = "处理或操作成功";
            msg = TongChengCallBackServletUtil.geturlencode(msg);
            if (Integer.parseInt(interfacetype) == TrainInterfaceMethod.TONGCHENG) {
                jsonstr.put("accountlist", getAccountlist(trainorderid));
            }
        }
        else if (orderstatus == 8) {//取消订单交易关闭   说明没有占座成功
            transactionid = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERNUMBER");
            success = false;
            code = 999;
            String res = returnmsg;
            res = res == null ? "" : geturldecode(res);
            if (res.indexOf("style='color:red'") > 0) {
                res = chuliretrunmsg1(res, r1);
            }
            WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", trainorderid + ":res:" + res);
            Map infoMap = TongChengCallBackServletUtil.getMapInfo(res, returncode, dataMap);
            code = Integer.parseInt(infoMap.get("code") == null ? "0" : infoMap.get("code").toString());
            msg = infoMap.get("msg") == null ? "" : infoMap.get("msg").toString();
        }
        jsonstr.put("ordersuccess", ordersuccess);
        jsonstr.put("success", success);
        jsonstr.put("code", code);
        jsonstr.put("msg", msg);
        WriteLog.write("返回同程未匹配有效帐号的乘客", "code:" + code);
        if (code == 315 && Integer.parseInt(interfacetype) == TrainInterfaceMethod.TONGCHENG) {
            JSONArray unmatchedpasslist = JSONArray.parseArray(unmatchedpasslistStr);
            jsonstr.put("accountlist", getAccountlist(trainorderid));
            jsonstr.put("msg12306", returnmsg);
            jsonstr.put("unmatchedpasslist", unmatchedpasslist);
            WriteLog.write("返回同程未匹配有效帐号的乘客", "code:" + code + ":unmatchedpasslist:" + unmatchedpasslist);
        }
        String zhanzuojieguoBackUrl_temp = zhanzuojieguoBackUrl;
        String zhanzuojieguoBackUrl_temp_other = TongChengCallBackServletUtil
                .getValueByMap(dataMap, "C_ZHANZUOHUIDIAO");
        if (zhanzuojieguoBackUrl_temp_other != null && !"-1".equals(zhanzuojieguoBackUrl_temp_other)) {
            zhanzuojieguoBackUrl_temp = zhanzuojieguoBackUrl_temp_other;
        }
        String parm = "data=" + jsonstr.toString();
        String ret = "-1";
        //请求同程
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", trainorderid + ":interfacetype:" + interfacetype + ":orderstatus="
                + orderstatus);
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", trainorderid + ":dataMap:" + dataMap + ":parm=" + parm);
        if (interfacetype != null
                && ((TrainInterfaceMethod.MEITUAN + "").equals(interfacetype) || (TrainInterfaceMethod.YILONG2 + "")
                        .equals(interfacetype))) {
            String partnerid = TongChengCallBackServletUtil.getValueByMap(dataMap, "C_USERNAME");
            String key = TongChengCallBackServletUtil.getValueByMap(dataMap, "C_KEY");
            if (orderstatus == 1) {//等待支付说明占座成功,美团的类型是不回调占座成功,而是自己调自己的确认出票接口
                zhanzuojieguoBackUrl_temp = PropertyUtil.getValue("zhanzuojieguoBackUrl", "train.tongcheng.properties");
                WriteLog.write("t同程火车票接口_1.1火车票确认出票_callback", trainorderid + ":zhanzuojieguoBackUrl_temp:"
                        + zhanzuojieguoBackUrl_temp + ":transactionid=" + transactionid + ":trainorderid="
                        + trainorderid);
                ret = train_confirm(zhanzuojieguoBackUrl_temp, transactionid, trainorderid, qunarordernumber,
                        partnerid, key);
            }
            else if (orderstatus == 8) {//取消订单交易关闭   说明没有占座成功,美团的类型是不回调占座失败,而是直接回调给美团出票失败
                if ((TrainInterfaceMethod.YILONG2 + "").equals(interfacetype)) {
                    trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);//根据ID查询交易单号
                    Map map_data = dataMap;
                    String payCallbackUrl_temp = TongChengCallBackServletUtil.getValueByMap(map_data,
                            "C_PAYCALLBACKURL");
                    String merchantCode = TongChengCallBackServletUtil.getValueByMap(map_data, "C_USERNAME");
                    String keys = TongChengCallBackServletUtil.getValueByMap(map_data, "C_KEY");
                    WriteLog.write("艺龙火车票接口_1.1申请分配座位席别回调", trainorderid + "|:回调返回:" + zhanzuojieguoBackUrl_temp_other
                            + "|merchantCode号" + merchantCode + "|号" + transactionid + "|result结果：" + ret + "|key的值："
                            + keys + "|trainorder:" + trainorder + "|returnmsg:" + returnmsg + "|interfacetype"
                            + interfacetype);
                    String qunarOrderid = "";
                    YiLongCallBackMethod yiLongCallBackMethod = new YiLongCallBackMethod(trainorder);
                    String msgs = returnmsg;
                    WriteLog.write("艺龙火车票接口_1.1msg", msgs);
                    ret = yiLongCallBackMethod.payCallBack(trainorder, merchantCode, payCallbackUrl_temp, keys, msgs);
                    WriteLog.write("艺龙火车票接口_1.1申请分配座位席别回调", trainorderid + ":回调返回:" + zhanzuojieguoBackUrl_temp_other
                            + "qunarOrderid号" + qunarOrderid + "号" + transactionid + "result结果：" + ret + "|miyao："
                            + this.key + "|map取值key：" + key);
                    if ("success".equalsIgnoreCase(ret)) {
                        ret = "SUCCESS";
                    }
                    else {
                        ret = "false";
                    }

                }
                else {

                    WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调_meituan_callback", trainorderid + ":meituan:" + trainorderid
                            + ":transactionid=" + transactionid);
                    Map map_data = dataMap;
                    String reqtime = gettimeString(1);
                    ret = TongChengCallBackServletMeiTuan.payCallBack_meituan_fail(trainorderid + "", transactionid, 0,
                            "N", "0", parm, map_data, reqtime, qunarordernumber);

                }
            }
        }
        //艺龙占座回调
        else if (interfacetype != null && (TrainInterfaceMethod.YILONG1 + "").equals(interfacetype)) {
            String result = "";
            String key = TongChengCallBackServletUtil.getValueByMap(dataMap, "C_KEY");
            //            String zhanzuoCallBackUrl = TongChengCallBackServletUtil.getValueByMap(dataMap, "C_ZHANZUOHUIDIAO");
            String merchantCode = TongChengCallBackServletUtil.getValueByMap(dataMap, "C_USERNAME");
            String holdingSeatSuccessTime = gettrainorderinfodatabyMapkey(traininfodataMap, "C_EXTORDERCREATETIME");
            WriteLog.write("Elong_先占座后支付模式占座回调_ElongTrainOrderCallBackServlet", r1 + ":merchantCode:" + merchantCode
                    + ":holdingSeatSuccessTime:" + holdingSeatSuccessTime + ":trainorderid:" + trainorderid);
            if (orderstatus == 1) {//等待支付说明占座成功
                result = "SUCCESS";
                ret = new ElongTrainOrderCallBack().trainOrderCallBackSuccess(merchantCode, key,
                        zhanzuojieguoBackUrl_temp, result, trainorder, holdingSeatSuccessTime);
            }
            else if (orderstatus == 8) {//取消订单交易关闭   说明没有占座成功
                trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);//根据ID查询交易单号
                result = "FAIL";
                int failReason = code;
                String failReasonDesc = ElongHotelInterfaceUtil.StringIsNull(msg) ? "" : geturldecode(msg);
                WriteLog.write("Elong_先占座后支付模式占座回调_ElongTrainOrderCallBackServlet", r1 + ":failReason:" + failReason
                        + ":failReasonDesc:" + failReasonDesc + ":trainorderid:" + trainorderid);
                ret = new ElongTrainOrderCallBack().trainOrderCallBackFail(merchantCode, key,
                        zhanzuojieguoBackUrl_temp, result, trainorder, failReasonDesc, failReason);
            }

        }
        else {
            WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", trainorderid + ":" + zhanzuojieguoBackUrl_temp + "?" + parm);
            ret = train_order_callback_sendpostandget(zhanzuojieguoBackUrl_temp, parm);
        }
        WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", trainorderid + ":回调返回:" + ret);
        int i = 0;
        if ("SUCCESS".equalsIgnoreCase(ret)) {
            ret = "success";
        }
        else {
            if (interfacetype != null
                    && ((TrainInterfaceMethod.YILONG1 + "").equals(interfacetype) || (TrainInterfaceMethod.YILONG2 + "")
                            .equals(interfacetype))) {
                return ret;
            }
            else {
                while (i < 5) {
                    try {
                        Thread.sleep(15000L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ret = train_order_callback_sendpostandget(zhanzuojieguoBackUrl_temp, "data=" + jsonstr.toString());
                    WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调", trainorderid + ":回调返回" + i + "返回:" + ret);
                    if ("SUCCESS".equalsIgnoreCase(ret)) {
                        ret = "success";
                        i = 5;
                    }
                    else {
                        i++;
                    }
                }
            }
        }
        //        sendMQUrlMethod(ret, orderstatus, trainorderid);
        return ret;
    }

    /**
     * 4.16. 确认出票回调通知
     * 
     * 支付通知，约定：
     * 1.遇到通知异常的，要间隔1分钟再尝试通知。
     * 2.连续通知异常5次的，要停止通知，程序加监控，人工介入处理。
     * 3.人工介入处理后，可以触发单次通知。
     * @param qunarOrderid 同程订单号
     * @param transactionid 交易单号 我方订单号
     * @param errorCount 错误次数，默认0
     * @param iskefu
     */
    public String payCallBack(String qunarOrderid, String transactionid, int errorCount, String isSuccess,
            String iskefu, String pkid) {
        String ret = "false";
        Map traininfodataMap = (pkid != null && !"".equals(pkid)) ? getTrainorderstatusByPkid(pkid)
                : getTrainorderstatus(2, 0L, qunarOrderid, transactionid);//得到表中的一些信息
        if (transactionid == null) {
            transactionid = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERNUMBER");
        }
        String interfacetype = gettrainorderinfodatabyMapkey(traininfodataMap, "C_INTERFACETYPE");
        String enrefundable = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ENREFUNDABLE");// 1：不可以退票 0：可以退票
        WriteLog.write("t同程火车票接口_4.16确认出票回调通知", qunarOrderid + ":interfacetype:" + interfacetype);
        if (interfacetype == null || "".equals(interfacetype)) {
            Map map_data = getcallbackurl(0L, 3, qunarOrderid, transactionid);
            interfacetype = TongChengCallBackServletUtil.getValueByMap(map_data, "C_INTERFACETYPE");
        }
        WriteLog.write("t同程火车票接口_4.16确认出票回调通知", qunarOrderid + ":traininfodataMap:" + traininfodataMap);
        if ((TrainInterfaceMethod.MEITUAN + "").equals(interfacetype)) {//这个美团类型确认出票回调的时候用到了占座回调的一些属性
            //TODO===========================================================================================================
            JSONObject jsonstr = new JSONObject();
            jsonstr.put("refund_online", enrefundable);
            boolean success = false;//        true:成功，false:失败
            boolean ordersuccess = false;
            int code = 100;//    int 4   状态编码
            String msg = "";//   1~256   提示信息
            //        String reqtoken = System.currentTimeMillis() + "";
            // 异步回调 传真实       API用户请求时传入的特征
            //        String reqtoken = trainorder.getContactuser();
            String reqtoken = gettrainorderinfodatabyMapkey(traininfodataMap, "C_CONTACTUSER");
            jsonstr.put("reqtoken", reqtoken);
            //        String qunarordernumber = trainorder.getQunarOrdernumber();
            String qunarordernumber = gettrainorderinfodatabyMapkey(traininfodataMap, "C_QUNARORDERNUMBER");
            jsonstr.put("orderid", qunarordernumber);
            //        String Ordernumber = trainorder.getOrdernumber();
            String Ordernumber = gettrainorderinfodatabyMapkey(traininfodataMap, "C_ORDERNUMBER");
            jsonstr.put("transactionid", Ordernumber);
            //        String Orderprice = trainorder.getOrderprice();
            String Orderprice = gettrainorderinfodatabyMapkey(traininfodataMap, "C_TOTALPRICE");
            jsonstr.put("orderamount", Orderprice);
            Long trainorderid = Long.parseLong(gettrainorderinfodatabyMapkey(traininfodataMap, "ID"));

            Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);//根据ID查询交易单号
            transactionid = trainorder.getOrdernumber();//交易单号
            String checi = "";
            String from_station_code = "";
            String from_station_name = "";
            String to_station_code = "";
            String to_station_name = "";
            String train_date = "";
            String start_time = "";
            String arrive_time = "";
            String ordernumber = trainorder.getExtnumber();
            String runtime = "";
            JSONArray passengers = new JSONArray();
            for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                Trainpassenger trainpassenger = trainorder.getPassengers().get(i);
                Trainticket trainticket = trainpassenger.getTraintickets().get(0);
                from_station_name = trainticket.getDeparture();
                to_station_name = trainticket.getArrival();
                train_date = trainticket.getDeparttime().split(" ")[0];
                start_time = trainticket.getDeparttime() + ":00";
                runtime = trainticket.getCosttime();
                //            int r1 = (int) (Math.random() * 10000);
                try {
                    arrive_time = TrainorderTimeUtil.getArrivalTime(start_time, runtime);//输入类车发车时间和运行时间，得到到达时间
                }
                catch (Exception e1) {
                }
                checi = trainticket.getTrainno();
                JSONObject passengerjson = new JSONObject();
                //            passengerid int 乘客的顺序号
                String passengerid = trainpassenger.getPassengerid() == null ? "0" : trainpassenger.getPassengerid();
                passengerjson.put("passengerid", passengerid);
                //            ticket_no   string  票号（此票在本订单中的唯一标识，订票成功后才有值）
                passengerjson.put("ticket_no", trainticket.getTicketno());
                //            passengersename string  乘客姓名
                String name = trainpassenger.getName();
                passengerjson.put("passengersename", name);
                //            passportseno    string  乘客证件号码
                passengerjson.put("passportseno", trainpassenger.getIdnumber());
                //            passporttypeseid    string  证件类型ID
                //            与名称对应关系:
                //            1:二代身份证，2:一代身份证，C:港澳通行证，G:台湾通行证，B:护照
                passengerjson.put("passporttypeseid", TongchengTrainOrder.getIdtype12306(trainpassenger.getIdtype()));
                //            passporttypeseidname    string  证件类型名称
                String passporttypeseidname = trainpassenger.getIdtypestr();
                passengerjson.put("passporttypeseidname", passporttypeseidname);
                //            piaotype    string  票种ID。
                //            与票种名称对应关系：
                //            1:成人票，2:儿童票，3:学生票，4:残军票
                passengerjson.put("piaotype", trainticket.getTickettype() + "");
                //            piaotypename    string  票种名称
                String piaotypename = trainticket.getTickettypestr() + "票";
                passengerjson.put("piaotypename", piaotypename);
                //            zwcode  string  座位编码。
                //            与座位名称对应关系：
                //            9:商务座，P:特等座，M:一等座，O:二等座，6:高级软卧，
                //            4:软卧，3:硬卧，2:软座，1:硬座
                //            注意：当最低的一种座位，无票时，购买选择该座位种类，买下的就是无座(也就说买无座的席别编码就是该车次的最低席别的编码)，另外，当最低席别的票卖完了的时候才可以卖无座的票。
                passengerjson.put("zwcode", TongchengTrainOrder.getzwname(trainticket.getSeattype()));
                //            zwname  string  座位名称
                String zwname = trainticket.getSeattype();
                passengerjson.put("zwname", zwname);
                try {
                    //            cxin    string  几车厢几座（在订票成功后才会有值）
                    String cxin = trainticket.getCoach() + "车厢," + trainticket.getSeatno().replace('号', '座');
                    passengerjson.put("cxin", cxin);
                }
                catch (Exception e) {
                }
                //            price   string  票价
                passengerjson.put("price", trainticket.getPrice() + "");
                //            reason  int 身份核验状态 0：正常 1：待审核 2：未通过
                passengerjson.put("reason", trainpassenger.getAduitstatus());
                passengers.add(passengerjson);
            }
            try {
                from_station_code = Train12306StationInfo.GetValue(from_station_name);//获取车站名称对应三字码
                to_station_code = Train12306StationInfo.GetValue(to_station_name);//获取车站名称对应三字码
            }
            catch (Exception e) {
                System.out.println("错误的三字码:" + from_station_name + ":" + to_station_name);
                System.out.println(e.fillInStackTrace());
            }
            jsonstr.put("from_station_name", from_station_name);
            jsonstr.put("from_station_code", from_station_code);
            jsonstr.put("to_station_name", to_station_name);
            jsonstr.put("to_station_code", to_station_code);
            jsonstr.put("train_date", train_date);
            jsonstr.put("start_time", start_time);
            jsonstr.put("arrive_time", arrive_time);
            jsonstr.put("ordernumber", ordernumber);
            jsonstr.put("runtime", runtime);
            jsonstr.put("checi", checi);
            jsonstr.put("passengers", passengers);
            ordersuccess = true;
            success = true;
            if ("N".equalsIgnoreCase(isSuccess)) {
                ordersuccess = false;
                success = false;
            }
            msg = "处理或操作成功";
            jsonstr.put("ordersuccess", ordersuccess);
            jsonstr.put("orderamount", trainorder.getOrderprice());
            jsonstr.put("success", success);
            jsonstr.put("code", code);
            jsonstr.put("msg", msg);
            Map map_data = getcallbackurl(0L, 2, qunarOrderid, transactionid);
            String zhanzuojieguoBackUrl_temp_other = TongChengCallBackServletUtil.getValueByMap(map_data,
                    "C_PAYCALLBACKURL");
            String reqtime = gettimeString(1);
            try {
                String sign = ElongHotelInterfaceUtil.MD5(this.key);
                sign = this.partnerid + reqtime + sign;
                sign = ElongHotelInterfaceUtil.MD5(sign);
                String payCallbackUrl_temp = this.payCallbackUrl;
                if (zhanzuojieguoBackUrl_temp_other != null && !"-1".equals(zhanzuojieguoBackUrl_temp_other)) {
                    String partnerid = TongChengCallBackServletUtil.getValueByMap(map_data, "C_USERNAME");
                    String key = TongChengCallBackServletUtil.getValueByMap(map_data, "C_KEY");
                    sign = ElongHotelInterfaceUtil.MD5(key);
                    sign = partnerid + reqtime + sign;
                    sign = ElongHotelInterfaceUtil.MD5(sign);
                    payCallbackUrl_temp = zhanzuojieguoBackUrl_temp_other;
                }
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知_meituan",
                        qunarOrderid + ":jsonstr.toString():" + jsonstr.toString());
                String data = "data=" + TongChengCallBackServletUtil.geturlencode(jsonstr.toString());
                String parm = data + "&reqtime=" + reqtime + "&sign=" + sign + "&orderid=" + qunarOrderid
                        + "&transactionid=" + transactionid.trim() + "&isSuccess=" + isSuccess;
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知_meituan", qunarOrderid + ":" + payCallbackUrl_temp + "?" + parm);
                try {
                    ret = SendPostandGet.submitPostMeiTuan(payCallbackUrl_temp, parm, "utf-8").toString();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    WriteLog.write("t同程火车票接口_4.16确认出票回调通知_train_order_budan_callback_Exception", "orderid:"
                            + qunarOrderid + ":" + e.fillInStackTrace().toString());
                }
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知_meituan", qunarOrderid + ":回调接口返回:" + ret);
                //成功
                if ("success".equalsIgnoreCase(ret)) {
                    WriteLog.write("t同程火车票接口_4.16确认出票回调通知_meituan", qunarOrderid + ":回调接口返回:" + ret + "成功");
                    return "success";
                }
                else {
                    //                    throw new Exception(ret);
                    WriteLog.write("t同程火车票接口_4.16确认出票回调通知_meituan", qunarOrderid + ":回调接口返回:" + ret + "失败");
                    return "false";
                }
            }
            catch (Exception e) {
            }
        }
        else if ((TrainInterfaceMethod.YILONG1 + "").equals(interfacetype)
                || (TrainInterfaceMethod.YILONG2 + "").equals(interfacetype)) {
            /**
             * 3.3出票结果 回调 
             * @time 2015年12月8日 12:11:58
             * @author Administrator
             * @param result 返回12306的信息 是否出票成功
             * yangtao
             */
            String result = "";
            String miyao = "";
            WriteLog.write("t同程火车票接口_4.16确认出票回调通知_yiLong", "isSuccess" + isSuccess + ":回调返回iskefu:" + iskefu
                    + ":qunarOrderid:" + qunarOrderid + ":transactionid:" + transactionid + ":pkid:" + pkid);
            String payCallbackUrl_temp = this.payCallbackUrl;
            Map map_data = getcallbackurl(0L, 2, qunarOrderid, transactionid);
            String zhanzuojieguoBackUrl_temp_other = TongChengCallBackServletUtil.getValueByMap(map_data,
                    "C_PAYCALLBACKURL");
            payCallbackUrl_temp = zhanzuojieguoBackUrl_temp_other;
            Long trainorderid = Long.parseLong(gettrainorderinfodatabyMapkey(traininfodataMap, "ID"));
            Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);//根据ID查询交易单号
            String keys = TongChengCallBackServletUtil.getValueByMap(map_data, "C_KEY");
            String merchantCode = TongChengCallBackServletUtil.getValueByMap(map_data, "C_USERNAME");
            YiLongCallBackMethod yiLongCallBackMethod = new YiLongCallBackMethod(trainorder);
            if ("N".equalsIgnoreCase(isSuccess)) {
                result = "false";
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知_yiLong", trainorderid + ":isSuccess" + isSuccess + ":result"
                        + result);
            }

            result = yiLongCallBackMethod.payCallBack(trainorder, merchantCode, payCallbackUrl_temp, keys, result);
            WriteLog.write("t同程火车票接口_4.16确认出票回调通知_yiLong", trainorderid + ":payCallbackUrl_temp:" + payCallbackUrl_temp
                    + ":qunarOrderid:" + qunarOrderid + ":" + transactionid + ":result结果:" + result + ":keys:" + keys
                    + ":供应商:" + merchantCode + "isSuccess" + isSuccess);
            if ("success".equalsIgnoreCase(result)) {
                return "success";
            }
            else {
                return "false";
            }
        }
        else {
            String sqlTemp = "";
            if (transactionid == null) {
                sqlTemp = "SELECT top 1 tor.C_ORDERNUMBER C_ORDERNUMBER from T_TRAINORDER as tor with(nolock) "
                        + "where tor.C_QUNARORDERNUMBER='" + qunarOrderid + "'";
                List list1 = Server.getInstance().getSystemService().findMapResultBySql(sqlTemp, null);
                if (list1.size() > 0) {
                    Map map = (Map) list1.get(0);
                    try {
                        transactionid = map.get("C_ORDERNUMBER").toString();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            int r1 = new Random().nextInt(1000000);

            try {
                String time = gettimeString(1);
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知", r1 + ":orderid:" + qunarOrderid + ":transactionid:"
                        + transactionid + ":errorCount:" + errorCount + ":isSuccess:" + isSuccess + ":iskefu:" + iskefu);
                String sign = ElongHotelInterfaceUtil.MD5(this.key);
                sign = this.partnerid + time + sign;
                sign = ElongHotelInterfaceUtil.MD5(sign);
                String payCallbackUrl_temp = this.payCallbackUrl;
                Map map_data = getcallbackurl((pkid == null || "".equals(pkid)) ? 0l : Long.valueOf(pkid), 2,
                        qunarOrderid, transactionid);
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知", r1 + ":" + qunarOrderid + ":map_data:" + map_data);
                String payCallbackUrl_temp_other = TongChengCallBackServletUtil.getValueByMap(map_data,
                        "C_PAYCALLBACKURL");
                //如果这里为true说明是非同程的订单
                if (payCallbackUrl_temp_other != null && !"-1".equals(payCallbackUrl_temp_other)) {
                    payCallbackUrl_temp = payCallbackUrl_temp_other;
                    String partnerid = TongChengCallBackServletUtil.getValueByMap(map_data, "C_USERNAME");
                    String key = TongChengCallBackServletUtil.getValueByMap(map_data, "C_KEY");
                    WriteLog.write("t同程火车票接口_4.16确认出票回调通知", r1 + ":key:" + key);
                    sign = ElongHotelInterfaceUtil.MD5(key);
                    sign = partnerid + time + sign;
                    sign = ElongHotelInterfaceUtil.MD5(sign);
                }
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知", r1 + ":orderid:" + qunarOrderid + ":transactionid:"
                        + transactionid + ":errorCount:" + errorCount + ":isSuccess:" + isSuccess + ":iskefu:" + iskefu);
                String parm = "";
                if ("Y".equals(isSuccess)) {
                    parm = "reqtime=" + time + "&sign=" + sign + "&orderid=" + qunarOrderid + "&transactionid="
                            + transactionid.trim() + "&isSuccess=" + isSuccess;
                }
                else {
                    String msg = "支付失败";
                    msg = URLEncoder.encode(msg, "UTF-8");
                    parm = "reqtime=" + time + "&sign=" + sign + "&orderid=" + qunarOrderid + "&transactionid="
                            + transactionid.trim() + "&isSuccess=" + isSuccess + "&msg=" + msg;
                }

                //请求同程
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知", r1 + ":" + qunarOrderid + ":backurl:" + payCallbackUrl_temp
                        + ":parm:" + parm);
                try {
                    ret = SendPostandGet.submitPost(payCallbackUrl_temp, parm, "utf-8").toString();
                }
                catch (Exception e) {
                    WriteLog.write("t同程火车票接口_4.16确认出票回调通知_train_order_budan_callback_Exception", "orderid:"
                            + qunarOrderid + ":" + e.fillInStackTrace().toString());
                }
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知", qunarOrderid + ":回调接口返回:" + ret);
                //成功
                if ("success".equalsIgnoreCase(ret)) {
                    yuepiao(qunarOrderid);
                    return "success";
                }
                else {
                    throw new Exception(ret);
                }
            }
            catch (Exception e) {
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知", qunarOrderid + ":e:" + e.getMessage());
                if (!"1".equals(iskefu)) {
                    try {
                        Thread.sleep(15000L);
                    }
                    catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    errorCount = errorCount + 1;
                    if (errorCount >= 5) {
                        return "连续通知异常5次的,停止通知,需人工介入处理";
                    }
                    else {
                        ret = payCallBack(qunarOrderid, transactionid, errorCount, isSuccess, iskefu, pkid);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 
     * @param qunarOrderid
     * @time 2016年1月11日 下午8:34:12
     * @author chendong
     */
    private void yuepiao(String qunarOrderid) {
        String sql = " sp_TrainOrderIsBespeak_Insert @C_QUNARORDERNUMBER='" + qunarOrderid + "'";
        WriteLog.write("出票回调成功后实现约票兼容", qunarOrderid + ":" + sql);
        try {
            List list = Server.getInstance().getSystemService().findMapResultByProcedure(sql);
            WriteLog.write("出票回调成功后实现约票兼容", qunarOrderid + ":" + JSONObject.toJSONString(list));
        }
        catch (Exception e) {
            WriteLog.write("ERROR_出票回调成功后实现约票兼容", sql);
            ExceptionUtil.writelogByException("ERROR_出票回调成功后实现约票兼容", e);
        }
    }

    /**
     * /**
     * reqtoken，是我申请退票时传给你的值     你回调时要回传过来    如果是线下车站退票的退款回调，reqtoken你可以自定义，但是每笔退款不能重复
     * @param returntickets 车票退票信息(json字符串数组形式，每张车票包含乘车人信息和退票相关信息，如：
    ["ticket_no":" E2610890401070051","passengername":"王二","passporttypeseid":1,"passportseno":"421116198907143795","returnsuccess":true,"returnmoney":"20.05","returntime":"2014-02-13 15:00:05","returnfailid":"","returnfailmsg":""}] 
     * @param token 退票信息特征值  注：当为线下退票时，此值为空
     * @time 2014年12月26日 下午10:10:19
     * @author chendong
     * @param returntype  退票回调通知类型  0：表示线下退票退款； 1：表示线上退票退款；2：线下改签退款；3：线上改签退款
     * @param apiorderid 同程订单号
     * @param trainorderid 火车票取票单号
     * @param reqtoken  （唯一）退票回调特征值(1.当回调内容是客人在线申请退票的退款，该值为在调用退票请求API时，由同程传入；2.当回调内容是客人在线下车站退票的退款，该值由供应商分配。)
     * @param returnstate 退票状态 true:表示成功  false:表示退票失败  
     * @param returnmsg  退票后消息描述（当returnstate=false时，需显示退票失败原因等）
     * @param ticket_no
     * @param passengername
     * @param passporttypeseid
     * @param passportseno
     * @param returnsuccess
     * @param returnmoney 退款金额（成功需有值） 当为线上退票时，此值为退款总额
     * @param returntime
     * @param returnfailid
     * @param returnfailmsg   
     * @return
     * @time 2015年1月13日 下午10:33:57
     * @author chendong
     * @param callbackurl  回调地址
     * @param mohutui 模糊退
     */
    public String refunCallBack(JSONObject json) {
        String passengerid = "";
        WriteLog.write("t同程火车票接口_4.10退票回调通知", "refunCallBack:" + json.toJSONString());
        String result = "false";
        String apiorderid = json.containsKey("apiorderid") ? json.getString("apiorderid") : "";
        WriteLog.write("t同程火车票接口_4.10退票回调通知", apiorderid + ":json:0:" + json);
        String trainorderid = json.containsKey("trainorderid") ? json.getString("trainorderid") : "";
        String reqtoken = json.containsKey("reqtoken") ? json.getString("reqtoken") : "";
        String returnmoney = json.containsKey("returnmoney") ? json.getString("returnmoney") : "";
        String returnmsg = json.containsKey("returnmsg") ? json.getString("returnmsg") : "";
        String returntype = json.containsKey("returntype") ? json.getString("returntype") : "";
        String ticket_no = json.getString("ticket_no");
        String passengername = json.getString("passengername");
        String passporttypeseid = json.getString("passporttypeseid");
        String passportseno = json.getString("passportseno");
        boolean returnsuccess = json.getBooleanValue("returnsuccess");
        String returntime = json.getString("returntime");
        String returnfailid = json.getString("returnfailid");
        String returnfailmsg = json.getString("returnfailmsg");
        boolean returnstate = json.containsKey("returnstate") ? json.getBoolean("returnstate") : true;
        String agentid = json.getString("agentid");
        String refundTimeStamp = json.containsKey("refundTimeStamp") ? json.getString("refundTimeStamp") : "";
        String changeTimeStamp = json.containsKey("changeTimeStamp") ? json.getString("changeTimeStamp") : "";
        boolean remarkTimeStamp = json.containsKey("remarkTimeStamp") ? json.getBoolean("remarkTimeStamp") : false;
        //模糊退
        boolean mohutui = json.containsKey("mohutui") ? json.getBooleanValue("mohutui") : false;
        //改签请求特征值
        String changereqtoken = json.containsKey("changereqtoken") ? json.getString("changereqtoken") : "";
        String transactionid = json.getString("transactionid") == null ? "" : json.getString("transactionid");
        passengerid = json.getString("passengerid") == null ? "" : json.getString("passengerid");
        WriteLog.write("艺龙火车票接口_4.10退票回调通知returnmsg", returnmsg + ":map:" + trainorderid + "|passporttypeseid"
                + passporttypeseid + "|ticket_no" + ticket_no + "|passportseno" + passportseno + "|apiorderid"
                + apiorderid + "|returntype" + returntype);
        WriteLog.write("艺龙火车票接口_4.10退票回调通知returnmsg", returnmsg + ":map:" + trainorderid + "|passporttypeseid"
                + passporttypeseid + "|ticket_no" + ticket_no + "|passportseno" + passportseno + "|apiorderid"
                + apiorderid + "|returntype" + returntype);

        //当前
        long currentTime = System.currentTimeMillis();
        //线下退款操作
        if ("0".equals(returntype) || "2".equals(returntype)) {
            currentTime = Long.parseLong(reqtoken);
        }
        //时间戳
        String timestamp = String.valueOf(currentTime / 1000);
        //用原时间戳
        if (remarkTimeStamp) {
            //已操作过退票
            if (!ElongHotelInterfaceUtil.StringIsNull(refundTimeStamp)
                    && ("0".equals(returntype) || "1".equals(returntype))) {
                //线下退款操作
                if ("0".equals(returntype) || "2".equals(returntype)) {
                    reqtoken = refundTimeStamp;
                }
                currentTime = Long.parseLong(refundTimeStamp);
                timestamp = String.valueOf(currentTime / 1000);
            }
            //已操作过改签
            else if (!ElongHotelInterfaceUtil.StringIsNull(changeTimeStamp)
                    && ("2".equals(returntype) || "3".equals(returntype))) {
                //线下退款操作
                if ("0".equals(returntype) || "2".equals(returntype)) {
                    reqtoken = changeTimeStamp;

                }
                currentTime = Long.parseLong(changeTimeStamp);
                timestamp = String.valueOf(currentTime / 1000);
            }
        }
        Map map = getkeybyagentid(agentid);
        WriteLog.write("t同程火车票接口_4.10退票回调通知", apiorderid + ":map:" + map);
        //退票回调地址
        String callbackurl = gettrainorderinfodatabyMapkey(map, "C_REFUNDCALLBACKURL");
        //请求同程
        String interfacetype = gettrainorderinfodatabyMapkey(map, "C_INTERFACETYPE");
        String username = gettrainorderinfodatabyMapkey(map, "C_USERNAME");
        //车票退票信息，包括乘客和退票相关信息
        JSONArray returntickets = new JSONArray();
        //非模糊退
        if (!mohutui) {
            JSONObject o1 = new JSONObject();
            o1.put("ticket_no", ticket_no);
            o1.put("passengername", passengername);
            o1.put("passporttypeseid", passporttypeseid);
            o1.put("passportseno", passportseno);
            o1.put("returnsuccess", returnsuccess);
            o1.put("returnmoney", returnmoney);
            o1.put("returntime", returntime);
            o1.put("returnfailid", returnfailid);
            o1.put("returnfailmsg", returnfailmsg);
            if (passengerid == null || "".equals(passengerid)) {
                passengerid = json.getString("passengerid");
            }
            if (username.contains("gaotie")) {//高铁管家才要这个数据的
                o1.put("passengerid", passengerid);
            }
            returntickets.add(o1);
            WriteLog.write("t同程火车票接口_4.10退票回调通知", "|apiorderid:" + apiorderid + ":passengerid:" + passengerid
                    + "|passportseno" + passportseno);
        }
        if (interfacetype != null
                && ((TrainInterfaceMethod.YILONG1 + "").equals(interfacetype) || (TrainInterfaceMethod.YILONG2 + "")
                        .equals(interfacetype))) {
            String C_LOGINNAME = gettrainorderinfodatabyMapkey(map, "C_USERNAME");
            key = gettrainorderinfodatabyMapkey(map, "C_KEY");
            WriteLog.write("t同程火车票接口_4.10退票回调通知_yilong", returnmsg + ":map:" + C_LOGINNAME);
            /***
             * 退票结果退款
             * 艺龙
             * 2015年12月9日 18:25:05
             * */
            String merchantCode = C_LOGINNAME;
            try {
                //                long trainorderids = Long.parseLong(trainorderid);
                //                trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderids);//根据ID查询交易单号
                //                String ordernumber = trainorder.getOrdernumber();//交易单号
                //                String passporttypeseids = trainorder.getPassengers().get(0).getPassengerid();
                result = YiLongCallBackMethod.refunCallBackYl(merchantCode, apiorderid, passengerid, returnstate,
                        callbackurl, key, returnmoney, returnmsg, returntype, ticket_no);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            //请求同程
            WriteLog.write("t同程火车票接口_4.10退票回调通知", apiorderid + ":map:" + map);
            String key = this.key;
            String partnerid = this.partnerid;
            String C_LOGINNAME = gettrainorderinfodatabyMapkey(map, "C_USERNAME");
            if (C_LOGINNAME.indexOf("tongcheng") >= 0) {
            }
            else {
                partnerid = C_LOGINNAME;
                key = gettrainorderinfodatabyMapkey(map, "C_KEY");
            }
            try {
                //            String sign = ElongHotelInterfaceUtil.MD5(partnerid + time + ElongHotelInterfaceUtil.MD5(key));
                String sign = "";
                //线下退票数字签名
                //=md5(partnerid+returntype+timestamp+apiorderid+trainorderid+returnmoney+returnstate+md5(key))
                //线上退票数字签名
                //=md5(partnerid+returntype+timestamp+apiorderid+trainorderid+token+returnmoney+returnstate+md5(key))
                JSONObject obj = new JSONObject();
                String sign1 = "";
                //0：表示线下退票退款  2：线下改签退款
                if ("0".equals(returntype) || "2".equals(returntype)) {
                    sign1 = partnerid + returntype + timestamp + apiorderid + trainorderid + returnmoney + returnstate
                            + ElongHotelInterfaceUtil.MD5(key);
                    sign = ElongHotelInterfaceUtil.MD5(sign1);
                }
                //1：表示线上退票退款  3：线上改签退款
                else/* if ("1".equals(returntype) || "3".equals(returntype))*/{
                    String token = System.currentTimeMillis() + "";
                    sign1 = partnerid + returntype + timestamp + apiorderid + trainorderid + token + returnmoney
                            + returnstate + ElongHotelInterfaceUtil.MD5(key);
                    sign = ElongHotelInterfaceUtil.MD5(sign1);
                    obj.put("token", token);
                }
                obj.put("returntype", returntype);
                obj.put("apiorderid", apiorderid);
                obj.put("sign", sign);
                obj.put("trainorderid", trainorderid);
                obj.put("reqtoken", reqtoken);
                obj.put("returntickets", returntickets);
                obj.put("returnstate", returnstate);
                obj.put("returnmoney", returnmoney);
                obj.put("timestamp", timestamp);
                obj.put("returnmsg", returnmsg);
                if (username.contains("gaotie")) {//高铁管家才要这个数据的
                    obj.put("transactionid", transactionid);
                }
                if (!ElongHotelInterfaceUtil.StringIsNull(changereqtoken)) {
                    obj.put("changereqtoken", changereqtoken);
                }
                //请求同程
                WriteLog.write("t同程火车票接口_4.10退票回调通知", apiorderid + ":partnerid:" + partnerid + ":key:" + key + ":"
                        + ":backurl:" + callbackurl + ":parm:data=" + obj.toString() + ":timeStamp:" + currentTime);
                String ret = SendPostandGet.submitPost(callbackurl, "data=" + obj.toString(), "utf-8").toString();
                WriteLog.write("t同程火车票接口_4.10退票回调通知", apiorderid + ":partnerid:" + partnerid + ":" + ":同程返回:" + ret);
                //成功
                if ("success".equalsIgnoreCase(ret)) {
                    if (remarkTimeStamp) {
                        result = "success@" + currentTime;//时间戳用于多次请求用同一个时间戳，勿修改
                    }
                    else {
                        result = "success";
                    }
                }
                else {
                    WriteLog.write("t同程火车票接口_4.10退票回调通知", apiorderid + ":sign1:" + sign1);
                    //                    throw new Exception(ret);
                    if (remarkTimeStamp) {
                        result = "false@" + currentTime;//时间戳用于多次请求用同一个时间戳，勿修改
                    }
                    else {
                        result = "false";
                    }
                }
            }
            catch (Exception e) {
                //            errorCount = errorCount + 1;
                //            if (errorCount >= 5) {
                //                return "连续通知异常5次的，停止通知，需人工介入处理";
                //            }
            }
        }
        WriteLog.write("t同程火车票接口_4.10退票回调通知", apiorderid + ":result:" + result);
        return result;
    }

    /**
     * 
     * @param apiorderid
     * @param passportseno
     * @return
     * @time 2016年1月8日 上午11:52:03
     * @author chendong
     * @param ticket_no 
     */
    private String getPassengerid(String apiorderid, String passportseno, String ticket_no) {
        String passengerid = "";
        Map map = new HashMap();
        String sql = "SELECT C_PASSENGERID FROM T_TRAINPASSENGER with(nolock) "
                + "where C_ORDERID in (select ID from T_TRAINORDER with(nolock) where C_QUNARORDERNUMBER='"
                + apiorderid + "') and C_IDNUMBER='" + passportseno + "'";
        try {
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                map = (Map) list.get(0);
                passengerid = (String) map.get("C_PASSENGERID");
            }
        }
        catch (Exception e) {
        }
        return passengerid;
    }

    /**
     * 异步改签回调
     * @param json 请求json
     * @param type 1:请求改签，2:确认改签
     * @return 回调结果
     * @author WH
     */
    @SuppressWarnings("rawtypes")
    private String changeCallBack(String orderid, JSONObject reqjson, int type) {
        //结果
        String result = "";
        //日志名称
        String logName = "t同程火车票接口_异步改签回调";
        if (type == 1) {
            logName = "t同程火车票接口_4.12.改签占座回调";
        }
        else if (type == 2) {
            logName = "t同程火车票接口_4.14.改签确认回调";
        }
        try {
            //代理ID
            String agentId = reqjson.getString("agentId");
            //回调地址
            String callBackUrl = reqjson.getString("callBackUrl");
            //移除属性
            reqjson.remove("agentId");
            reqjson.remove("callBackUrl");
            //获取KEY
            String key = this.key;
            String partnerid = this.partnerid;
            Map map = getkeybyagentid(agentId);
            String C_LOGINNAME = gettrainorderinfodatabyMapkey(map, "C_USERNAME");
            //非同程
            if (C_LOGINNAME.indexOf("tongcheng") < 0) {
                partnerid = C_LOGINNAME;
                key = gettrainorderinfodatabyMapkey(map, "C_KEY");
            }
            String reqJsonString = reqjson.toJSONString();
            try {
                reqJsonString = URLDecoder.decode(reqJsonString, "UTF-8");
                reqjson = JSONObject.parseObject(reqJsonString);
            }
            catch (Exception e) {
            }
            //拼参数
            reqjson.put("partnerid", partnerid);
            //请求时间
            String reqtime = gettimeString(2);
            reqjson.put("reqtime", reqtime);
            WriteLog.write(logName, orderid + ":callBackUrl:" + callBackUrl + ":partnerid=" + partnerid + ":reqtime="
                    + reqtime + ":key=" + key);
            //数字签名
            reqjson.put("sign", ElongHotelInterfaceUtil.MD5(partnerid + reqtime + ElongHotelInterfaceUtil.MD5(key)));
            //根据msg   转换新增code值
            reqjson=TongChengCodeSwitch.getNewCode(reqjson);
            //记录日志
            WriteLog.write(logName, orderid + ":callBackUrl:" + callBackUrl + ":param:backjson=" + reqjson);
            String reqjsonString = reqjson.toJSONString();
            if (partnerid.contains("tuniu")) {
                WriteLog.write(logName, orderid + ":tuniu:reqjsonString:before:" + reqjsonString);
                reqjsonString = ValueFilterTuniuChangeValue.getNewJSONString(reqjsonString);
            }
            else {
                reqjsonString = URLEncoder.encode(reqjsonString, "UTF-8");
            }
            WriteLog.write(logName, orderid + ":reqjsonString:" + reqjsonString);
            //连续通知5次，直到成功
            String backResult = "";
            for (int i = 1; i <= 5; i++) {
                if (i > 1) {
                    Thread.sleep(15000l);
                }
                //请求代理商
                //                backResult = RequestUtil.post(callBackUrl, "backjson=" + reqjson, "UTF-8",
                //                        new HashMap<String, String>(), 0);//这个方法有乱码
                //中兴商旅>>Content-Type>>application/x-www-form-urlencoded
                if (partnerid.contains("shanglvelutong")) {
                    backResult = SendPostandGet.submitPostMeiTuan(callBackUrl, "backjson=" + reqjsonString, "UTF-8")
                            .toString();
                }
                else {
                    backResult = SendPostandGet.submitPost(callBackUrl, "backjson=" + reqjsonString, "UTF-8")
                            .toString();
                }
                //记录日志
                WriteLog.write(logName, orderid + ":第" + i + "次回调返回:" + backResult);
                //成功
                if ("success".equalsIgnoreCase(backResult)) {
                    break;
                }
            }
            //成功
            if ("success".equalsIgnoreCase(backResult)) {
                result = "success";
            }
            else {
                result = "连续通知异常5次,停止通知,需人工介入处理"
                        + (ElongHotelInterfaceUtil.StringIsNull(backResult) ? "" : "---" + backResult);
            }
        }
        catch (Exception e) {
            WriteLog.write(logName, orderid + ":Exception:" + ElongHotelInterfaceUtil.errormsg(e));
        }
        return result;
    }

    /**
     * 根据12306订单号找到这个接口用户的key和loginname
     * 
     * @param trainorderid
     * @return
     * @time 2015年3月30日 下午7:55:59
     * @author chendong
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map getkeybyagentid(String agentid) {
        if (Server.getInstance().getKeyMap().get(agentid) == null) {
            Map keymapbydb = null;
            try {
                Map keyMap = Server.getInstance().getKeyMap();
                keymapbydb = getkeybyagentidDB(agentid);
                keyMap.put(agentid, keymapbydb);
                //                Server.getInstance().setKeyMap(keyMap);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return keymapbydb;
        }
        else {
            return Server.getInstance().getKeyMap().get(agentid);
        }

    }

    /**
     * 修改这个方法不用customeruser里的workphone了
     * 
     * @param agentid
     * @return
     * @time 2015年7月31日 下午1:02:02
     * @author chendong
     */
    private Map getkeybyagentidDB(String agentid) {
        Map map = new HashMap();
        String sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE,C_REFUNDCALLBACKURL,C_QUEUECALLBACKURL "
                + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID=(" + agentid + ")";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    private String gettimeString(int type) {
        if (type == 1) {
            return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
        else if (type == 2) {
            return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        }
        else {
            return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
    }

    /**
     * 
     * 根据订单号 和type类型获取到回调地址
     * 此方法是用来获取到除同程以外的其他接口用户的回调地址
     * @param orderid 
     * @param type 获取回调连接url类型 1:占座结果callback 2:出票结果callback
     * @param orderid_no :接口用户订单号
     * @param transactionid : 交易单号
     * @time 2015年3月4日 上午10:43:26
     * @author chendong
     */
    public Map getcallbackurlbyAgentId(String agentid) {
        Map map = new HashMap();
        String sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME FROM T_INTERFACEACCOUNT with(nolock) "
                + "WHERE C_AGENTID=" + agentid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    /**
     * 
     * 根据订单号 和type类型获取到回调地址
     * 此方法是用来获取到除同程以外的其他接口用户的回调地址
     * @param orderid
     * @param type 获取回调连接url类型 1:占座结果callback 2:出票结果callback
     * @param orderid_no :接口用户订单号
     * @param transactionid : 交易单号
     * @time 2015年3月4日 上午10:43:26
     * @author chendong
     */
    public Map getcallbackurl(Long orderid, int type, String orderid_no, String transactionid) {
        String key = "";
        Map map = new HashMap();
        String sql_agentid = "SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE ID=" + orderid;
        if (type == 2) {
            if (orderid == 0) {
                sql_agentid = "SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE C_QUNARORDERNUMBER='"
                        + orderid_no + "' and C_ORDERNUMBER='" + transactionid + "'";
            }
        }
        else if (type == 3) {
            sql_agentid = "SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE C_QUNARORDERNUMBER='"
                    + orderid_no + "' order by id desc";
        }
        String sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE "
                + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID=(" + sql_agentid + ")";
        WriteLog.write("t同程火车票接口_4.16确认出票回调通知_getcallbackurl", orderid + ":sql:" + sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        else {
            sql = "SELECT C_PAYCALLBACKURL,C_ZHANZUOHUIDIAO,C_KEY,C_USERNAME,C_INTERFACETYPE "
                    + " FROM T_INTERFACEACCOUNT with(nolock) WHERE C_AGENTID="
                    + "(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER with(nolock) WHERE ID=" + orderid_no + ")";
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                map = (Map) list.get(0);
            }
        }
        WriteLog.write("t同程火车票接口_4.16确认出票回调通知_getcallbackurl", orderid + ":map:" + map);
        return map;
    }

    /**
     * 根据接口订单号查找对应退票回调地址
     * @param jiekouordernamber
     * @time 2015年3月27日 下午2:48:51
     * @author fiend
     */
    public String changeRefundCallbackUrl(String jiekouordernamber) {
        String callbackurl = this.refunCallBackUrl;
        String url = "-1";
        Map map = new HashMap();
        String key = "";
        key = "C_REFUNDCALLBACKURL";
        String sql = "SELECT C_REFUNDCALLBACKURL FROM T_INTERFACEACCOUNT WHERE C_AGENTID=(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER WHERE C_QUNARORDERNUMBER='"
                + jiekouordernamber + "' )";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
            if (map.get(key) != null) {
                try {
                    url = map.get(key).toString();
                    callbackurl = url;
                }
                catch (Exception e) {
                }
            }
        }
        return callbackurl;
    }

    /**
     * 占座结果回调统一方法
     * 
     * @param url
     * @param paramContent
     * @return
     * @time 2015年2月6日 下午4:57:15
     * @author chendong
     */
    private String train_order_callback_sendpostandget(String url, String paramContent) {
        String ret = "";
        try {
            ret = SendPostandGet.submitPost(url, paramContent, "utf-8").toString();
        }
        catch (Exception e) {
            WriteLog.write("t同程火车票接口_1.1申请分配座位席别回调_train_order_callback_Exception", "paramContent:" + paramContent);
        }
        return ret;
    }

    /**
     * 根据订单id获取 一些信息
     * 
     * @param type 1老的同程的 2新的美团的需要获取到信息判断接口类型然后回调
     * @param trainorderid
     * @return
     * @time 2015年1月22日 下午1:05:36
     * @author chendong
     * @param transactionid 
     * @param qunarOrderid 
     */
    private Map getTrainorderstatus(int type, Long trainorderid, String qunarOrderid, String transactionid) {
        Map map = new HashMap();
        String sql = "SELECT C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,C_INTERFACETYPE,C_EXTORDERCREATETIME"
                + " from T_TRAINORDER where ID=" + trainorderid;
        if (type == 2) {
            if (transactionid != null) {
                sql = "SELECT ID,C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,C_INTERFACETYPE,C_EXTORDERCREATETIME"
                        + " from T_TRAINORDER where C_QUNARORDERNUMBER='"
                        + qunarOrderid
                        + "' and C_ORDERNUMBER='"
                        + transactionid + "'";
            }
            else {
                sql = "SELECT ID,C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,C_INTERFACETYPE,C_EXTORDERCREATETIME"
                        + " from T_TRAINORDER where C_QUNARORDERNUMBER='" + qunarOrderid + "' order by id desc";
            }
        }
        WriteLog.write("t同程火车票接口_4.16确认出票回调通知", qunarOrderid + ":sql:" + sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    /**
     * 通过订单ID获取参数
     * @param pkid
     * @return
     * @time 2015年9月14日 上午11:09:02
     * @author fiend
     */
    private Map getTrainorderstatusByPkid(String pkid) {
        Map map = new HashMap();
        String sql = "SELECT ID,C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE,"
                + "C_INTERFACETYPE,C_ENREFUNDABLE from T_TRAINORDER where ID=" + pkid;
        WriteLog.write("t同程火车票接口_4.16确认出票回调通知", pkid + ":sql:" + sql);
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            map = (Map) list.get(0);
        }
        return map;
    }

    /**
     * 处理一些字符信息
     * 
     * @param r1
     * @return
     * @time 2015年1月18日 上午11:13:28
     * @author Administrator
     */
    private String chuliretrunmsg1(String returnmsg, int r1) {
        WriteLog.write("t同程回调_chuliretrunmsg", r1 + ":returnmsg:0:" + returnmsg);
        returnmsg = returnmsg.replace("订单填写页，", "").replace("订单填写页,", "").replace("校验订单信息失败：", "")
                .replace("校验订单信息失败:", "").replace("<span ", "").replace("style='color:red'>", "").replace("<i>", "")
                .replace("<b>", "").replace("</b>", "").replace("</i>", "").replace("</span>", "");
        //        returnmsg = returnmsg.replace("订单填写页，校验订单信息失败：", "").replace("<span ", "")
        //                .replace("style='color:red'><i><b>", "").replace("</b></i></span>", "");
        WriteLog.write("t同程回调_chuliretrunmsg", r1 + ":returnmsg:1:" + returnmsg);
        return returnmsg;
    }

    /**
     * 根据查到的map信息获取value
     * 
     * @param key
     * @time 2015年1月22日 下午1:08:54
     * @author chendong
     */
    private String gettrainorderinfodatabyMapkey(Map map, String key) {
        String value = "";
        if (map.get(key) != null) {
            try {
                value = map.get(key).toString();
            }
            catch (Exception e) {
            }
        }
        return value;
    }

    private String geturldecode(String oldstring) {
        try {
            oldstring = URLDecoder.decode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    public final static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    public final static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static String getreqtime() {
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    public static String getcurrentTimeMillis() {
        return System.currentTimeMillis() + "";
    }

    /**
     * 根据订单号查询返回同程使用过的帐号
     * 
     * @param trainorderid
     * @return
     * @time 2015年11月23日 下午1:03:08
     * @author wcl
     */
    private static JSONArray getAccountlist(long trainorderid) {
        JSONArray accountlist = new JSONArray();
        String sql = "select Account12306Id,AccountMsg from TongchengUnmatchedPassenger with(nolock) where OrderId="
                + trainorderid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        WriteLog.write("返回同程未匹配有效帐号的乘客", "sql:" + sql + ":list:" + list.size());
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                JSONObject passengerJson = new JSONObject();
                Map map = (Map) list.get(i);
                if (accountlist.toString().contains(map.get("Account12306Id").toString())) {
                    WriteLog.write("返回同程未匹配有效帐号的乘客_again", map.get("Account12306Id").toString());
                    continue;
                }
                passengerJson.put("accountname", map.get("Account12306Id").toString());
                try {
                    passengerJson
                            .put("accountstatusname", URLEncoder.encode(map.get("AccountMsg").toString(), "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                passengerJson.put("accountstatusid", AccountStatusId(map.get("AccountMsg").toString()));
                WriteLog.write("返回同程未匹配有效帐号的乘客", ":passengerJson:" + passengerJson.toString());
                accountlist.add(passengerJson);
            }
        }
        else {
            JSONObject passengerJson = new JSONObject();
            passengerJson.put("accountname", "0");
            try {
                passengerJson.put("accountstatusname", URLEncoder.encode("其他", "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            passengerJson.put("accountstatusid", AccountStatusId("其他"));
            WriteLog.write("返回同程未匹配有效帐号的乘客", ":passengerJson:" + passengerJson.toString());
            accountlist.add(passengerJson);
        }
        WriteLog.write("返回同程未匹配有效帐号的乘客", ":accountlist:" + accountlist.toString());
        return accountlist;
    }

    /**
     * 映射帐号状态
     * 
     * @param isenable
     * @return
     * @time 2015年11月23日 上午10:50:30
     * @author wcl
     */
    private static int AccountStatusId(String accountstatusname) {
        int accountstatusid = 5;
        if ("账号被封".equals(accountstatusname)) {
            return 1;
        }
        if ("可用".equals(accountstatusname)) {
            return 2;
        }
        if ("未绑定手机号".equals(accountstatusname)) {
            return 3;
        }
        if ("该账号当天取消三次,不可使用".equals(accountstatusname)) {
            return 4;
        }
        return accountstatusid;
    }

    /**
     * md5(partnerid+method+reqtime+md5(key))，
     * 
     * @time 2014年12月12日 下午2:44:31
     * @author chendong
     */
    public static String getsign(String partnerid, String method, String reqtime, String key) {
        key = MD5Util.MD5Encode(key, "UTF-8");
        String jiamiqian = partnerid + method + reqtime + key;
        String sign = MD5Util.MD5Encode(jiamiqian, "UTF-8");
        return sign;
    }

    /**
     * 4.6. 火车票确认出票
     * 美团的话调这个接口
     * @param orderid
     * @param transactionid
     * @return
     * @time 2014年12月12日 下午3:38:59
     * @author chendong
     * @param qunarordernumber 
     * @param key2 
     * @param partnerid2 
     */
    private String train_confirm(String zhanzuojieguoBackUrl, String transactionid, long trainorderid,
            String qunarordernumber, String partnerid, String key) {
        String method = "train_confirm";
        String reqtime = getreqtime();
        String sign = getsign(partnerid, method, reqtime, key);
        String jsonStr = "{\"partnerid\":\"" + partnerid + "\",\"method\":\"" + method + "\",\"reqtime\":\"" + reqtime
                + "\",\"sign\":\"" + sign + "\",\"orderid\":\"" + qunarordernumber + "\",\"transactionid\":\""
                + transactionid + "\"}";
        WriteLog.write("t同程火车票接口_1.1火车票确认出票_callback", trainorderid + ":meituan:" + trainorderid + ":transactionid="
                + transactionid + ":" + zhanzuojieguoBackUrl);
        WriteLog.write("t同程火车票接口_1.1火车票确认出票_callback", trainorderid + ":jsonStr:" + jsonStr);
        String resultString = SendPostandGet.submitPost(zhanzuojieguoBackUrl, "jsonStr=" + jsonStr, "UTF-8").toString();
        JSONObject jsonObject = JSONObject.parseObject(resultString);
        String set = "fail";
        if (jsonObject.getBooleanValue("success")) {
            set = "SUCCESS";
        }
        WriteLog.write("t同程火车票接口_1.1火车票确认出票_callback", trainorderid + ":set:" + set + ":resultString:" + resultString);
        return set;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPayCallbackUrl() {
        return payCallbackUrl;
    }

    public void setPayCallbackUrl(String payCallbackUrl) {
        this.payCallbackUrl = payCallbackUrl;
    }

    public String getRefunCallBackUrl() {
        return refunCallBackUrl;
    }

    public void setRefunCallBackUrl(String refunCallBackUrl) {
        this.refunCallBackUrl = refunCallBackUrl;
    }

    /**
     * 4.18. 确认取消回调通知[同程]
     * @param orderid
     * @param json
     * @return
     * @time 2016年1月29日 下午1:07:43
     * @author chendong
     */
    private String confirm_cancel(String orderid, JSONObject json) {
        String url = "";
        String result = SendPostandGet.submitPost(url, "backjson=" + json.toString(), "UTF-8").toString();
        return result;
    }

}

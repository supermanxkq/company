package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.QunarTrain.Thread.MyThreadCancelMessageAgain;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengCancelChange;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengCancelTrain;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengConfirmChange;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengConfirmTrain;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengReqChange;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainQueryInfo;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainQueryStatus;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainReturnTicket;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengCancelOrderNight;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengTrainOrder;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainAccountPassengerUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainAccountUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainServerUtil;
import com.ccservice.b2b2c.atom.servlet.bespeak.TrainBespeskMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DBHelper2;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

@SuppressWarnings("serial")
public class TongChengTrainServlet extends HttpServlet {
    Logger logger = Logger.getLogger("TongChengTrainServlet");

    TongChengReqChange tongchengreqchange;

    TongChengCancelChange tongchengcancelchange;

    TongChengConfirmChange tongchengconfirmchange;

    TongChengTrainQueryInfo tongchengtrainqueryinfo;

    TongChengConfirmTrain tongchengconfirmtrain;

    TongchengTrainOrder tongchengtrainorder;

    TongchengCancelOrderNight tongchengcancelordernight;

    TrainAccountPassengerUtil trainAccountPassengerUtil;

    TrainAccountUtil trainAccountUtil;

    Map<String, InterfaceAccount> interfaceAccountMap;

    /**
     * 测试地址
     */
    public String callbackurl = "http://tsflightopenapi.17usoft.com/train/services/confirmCancelNotify";

    @Override
    public void init() throws ServletException {
        super.init();
        //        this.key = this.getInitParameter("key");
        //        this.partnerid = this.getInitParameter("partnerid");
        this.callbackurl = this.getInitParameter("callbackurl");
        tongchengreqchange = new TongChengReqChange();
        tongchengcancelchange = new TongChengCancelChange();
        tongchengconfirmchange = new TongChengConfirmChange();
        tongchengtrainqueryinfo = new TongChengTrainQueryInfo();
        tongchengtrainorder = new TongchengTrainOrder();
        interfaceAccountMap = new HashMap<String, InterfaceAccount>();
        tongchengcancelordernight = new TongchengCancelOrderNight();
        trainAccountPassengerUtil = new TrainAccountPassengerUtil();
        trainAccountUtil = new TrainAccountUtil();
        tongchengconfirmtrain = new TongChengConfirmTrain();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        int r1 = new Random().nextInt(10000000);
        Long starttime = System.currentTimeMillis();
        String result = "";
        PrintWriter out = null;
        String param = "";
        String inter_partnerid = "";//接口请求的partnerid
        String userPartnerid = "";//接口请求的partnerid
        String userDbKey = "";//数据库的key
        try {
            out = res.getWriter();
            try {
                param = req.getParameter("jsonStr");
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            //            }
            WriteLog.write("t同程火车票接口", r1 + ":jsonStr:" + param);
            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
                JSONObject obj = new JSONObject();
                obj.put("success", false);
                obj.put("code", "101");
                obj.put("msg", "传	的json为空对象");
                result = obj.toString();
            }
            else {
                //解析请求JSON
                try {
                    JSONObject json = JSONObject.parseObject(param);
                    //                    //客户端账号
                    //                    String partnerid = json.containsKey("partnerid") ? json.getString("partnerid") : "";
                    //请求时间
                    String reqtime = json.containsKey("reqtime") ? json.getString("reqtime") : "";
                    //数字签名
                    String sign = json.containsKey("sign") ? json.getString("sign") : "";
                    //请求方法
                    String method = json.containsKey("method") ? json.getString("method") : "";
                    //=================user是否保存到数据库===========================================
                    //                    boolean isuserdbinfo = false;//是否使用数据库里面的数据
                    //                    String partnerid = "";
                    userPartnerid = json.getString("partnerid");//传过来的partnerid
                    inter_partnerid = json.getString("partnerid");//传过来的partnerid
                    //-----加缓存机制不用每次都去数据库查-----S
                    //chendong 2015年4月11日19:18:11
                    InterfaceAccount interfaceAccount = interfaceAccountMap.get(userPartnerid);
                    if (interfaceAccount == null) {
                        interfaceAccount = getInterfaceAccountByLoginname(userPartnerid);
                        if (interfaceAccount != null && interfaceAccount.getKeystr() != null
                                && interfaceAccount.getInterfacetype() != null) {
                            interfaceAccountMap.put(userPartnerid, interfaceAccount);
                        }
                    }
                    //-----加缓存机制不用每次都去数据库查-----E
                    userDbKey = interfaceAccount.getKeystr();
                    String key = interfaceAccount.getKeystr();
                    int Interfacetype = interfaceAccount.getInterfacetype().intValue();
                    //============================================================
                    //判断签名，数字签名=md5(partnerid+method+reqtime+md5(key))
                    WriteLog.write("t同程火车票接口_key", r1 + ":userDbKey:" + userDbKey);
                    userDbKey = ElongHotelInterfaceUtil.MD5(userDbKey);
                    WriteLog.write("t同程火车票接口_key", r1 + ":userDbKey1:" + userDbKey);
                    String signflag = inter_partnerid + method + reqtime + userDbKey;
                    WriteLog.write("t同程火车票接口_key", r1 + ":signflag:" + signflag);
                    signflag = ElongHotelInterfaceUtil.MD5(signflag);
                    WriteLog.write("t同程火车票接口_key", r1 + ":signflag1:" + signflag);
                    if (signflag.equalsIgnoreCase(sign)) {
                        //4.0 预约票
                        if ("qiang_piao_order".equals(method) || "qiang_piao_t_order".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.0预约票", r1 + ":jsonStr:" + json);
                            result = getBespeakParam(json, Interfacetype);
                            WriteLog.write("t同程火车票接口_4.0预约票", r1 + ":result:" + result);
                        }
                        //4.1. 查询账户余额
                        else if ("query_money".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.1查询账户余额", r1 + ":jsonStr:" + json);
                            result = tongchengconfirmtrain.query_money(json);
                            WriteLog.write("t同程火车票接口_4.1查询账户余额", r1 + ":result:" + result);
                        }
                        //4.2. 查询授权的未完成订单使用情况
                        else if ("train_query_unfinished_order_count".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.2查询授权的未完成订单使用情况", r1 + ":jsonStr:" + json);
                            result = tongchengconfirmtrain.train_query_unfinished_order_count(json);
                            WriteLog.write("t同程火车票接口_4.2查询授权的未完成订单使用情况", r1 + ":result:" + result);
                        }
                        //申请分配座位席别
                        else if ("train_order".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":method:train_order:jsonStr:" + json);
                            json.put("interfacetype", interfaceAccount.getInterfacetype() == null ? 4
                                    : interfaceAccount.getInterfacetype());
                            result = tongchengtrainorder.submittrainorder(json, r1);
                            WriteLog.write("t同程火车票接口_4.5申请分配座位席别", r1 + ":时间:"
                                    + (System.currentTimeMillis() - starttime) + ":method:train_order:result:" + result);
                        }
                        //查询订单状态
                        else if ("train_query_status".equals(method)) {
                            result = TongChengTrainQueryStatus.trainorderstatus(json);
                        }
                        //获取订单列表
                        else if ("train_order_list".equals(method)) {
                            WriteLog.write("t同程火车票接口_train_order_list_查询订单列表", r1 + ":jsonStr:" + param);
                            result = tongchengtrainqueryinfo.trainListInfo(json);
                            WriteLog.write("t同程火车票接口_train_order_list_查询订单列表", r1 + ":result:" + result);
                        }
                        //查看订单详情
                        else if ("train_query_info".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.9查询订单详情", r1 + ":jsonStr:" + param);
                            result = tongchengtrainqueryinfo.trainqueryinfo(json);
                            WriteLog.write("t同程火车票接口_4.9查询订单详情", r1 + ":result:" + result);
                        }
                        //在线退票
                        else if ("return_ticket".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.10在线退票", r1 + ":jsonStr:" + param);
                            result = new TongChengTrainReturnTicket().returnticket(json, r1);
                            WriteLog.write("t同程火车票接口_4.10在线退票", r1 + ":result:" + result);
                        }
                        //确认出票
                        else if ("train_confirm".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.6火车票确认出票", r1 + ":jsonStr:" + param);
                            result = new TongChengConfirmTrain().opeate(json, interfaceAccount);
                            WriteLog.write("t同程火车票接口_4.6火车票确认出票", r1 + ":result:" + result);
                        }
                        //取消订单
                        else if ("train_cancel".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.7取消火车票订单", r1 + ":jsonStr:" + json);
                            result = new TongChengCancelTrain().operate(json, r1);
                            WriteLog.write("t同程火车票接口_4.7取消火车票订单", r1 + ":result:" + result);
                        }
                        //请求改签
                        else if ("train_request_change".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.12请求改签", r1 + ":jsonStr:" + param);
                            result = tongchengreqchange.operate(json);
                            WriteLog.write("t同程火车票接口_4.12请求改签", r1 + ":result:" + result);
                        }
                        //取消改签
                        else if ("train_cancel_change".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.13取消改签", r1 + ":jsonStr:" + param);
                            result = tongchengcancelchange.operate(json, r1);
                            WriteLog.write("t同程火车票接口_4.13取消改签", r1 + ":result:" + result);
                        }
                        //确认改签
                        else if ("train_confirm_change".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.14确认改签", r1 + ":jsonStr:" + param);
                            result = tongchengconfirmchange.operate(json, r1);
                            WriteLog.write("t同程火车票接口_4.14确认改签", r1 + ":result:" + result);
                        }
                        //取消夜间单
                        else if ("train_night_cancel".equals(method)) {
                            WriteLog.write("t同程火车票接口_4取消夜间单train_night_cancel", r1 + ":jsonStr:" + param);
                            result = tongchengcancelordernight.operate(json, r1);
                            WriteLog.write("t同程火车票接口_4取消夜间单train_night_cancel", r1 + ":result:" + result);
                        }
                        //4.22. 核验登录状态接口
                        else if ("get_trainAccount_status".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.22get_trainAccount_status", r1 + ":jsonStr:" + param);

                            WriteLog.write("t同程火车票接口_4.22get_trainAccount_status", r1 + ":result:" + result);
                        }
                        //4.23.获取供应商12306 host IP地址
                        else if ("get_train_12306hostip".equals(method)) {
                            WriteLog.write("t同程火车票接口_4.23get_train_12306hostip", r1 + ":jsonStr:" + param);
                            result = TrainServerUtil.get_train_12306hostip();
                            WriteLog.write("t同程火车票接口_4.23get_train_12306hostip", r1 + ":result:" + result);
                        }
                        //通过用户账号获取订单信息
                        else if ("get_12306_orderbyUserAccount".equals(method)) {
                            WriteLog.write("t同程火车票接口_get_12306_orderbyUserAccount", r1 + ":jsonStr:" + param);
                            result = trainAccountPassengerUtil.querymy12306trainorder(json);
                            WriteLog.write("t同程火车票接口_get_12306_orderbyUserAccount", r1 + ":result:" + result);
                        }
                        //操作常旅客接口增删改查
                        else if ("update_12306_orderbyUserAccount".equals(method)) {
                            WriteLog.write("t同程火车票接口_update_12306_orderbyUserAccount", r1 + ":jsonStr:" + param);
                            result = trainAccountPassengerUtil.editPassenger(json);
                            WriteLog.write("t同程火车票接口_update_12306_orderbyUserAccount", r1 + ":result:" + result);
                        }
                        //发送12306手机验证短信
                        else if ("send12306sms".equals(method)) {
                            WriteLog.write("t同程火车票接口_send12306sms", r1 + ":jsonStr:" + param);
                            result = trainAccountUtil.send12306sms(json);
                            WriteLog.write("t同程火车票接口_send12306sms", r1 + ":result:" + result);
                        }//接收12306手机验证短信,并验证
                        else if ("push12306sms".equals(method)) {
                            WriteLog.write("t同程火车票接口_push12306sms", r1 + ":jsonStr:" + param);
                            result = trainAccountUtil.push12306sms(json);
                            WriteLog.write("t同程火车票接口_push12306sms", r1 + ":result:" + result);
                        }
                        else if ("BespeakFrequency".equals(method)) {
                            WriteLog.write("抢票下单次数接口_BespeakFrequency", r1 + ":jsonStr:" + param);
                            result = BespeakFrequency(json);
                            WriteLog.write("抢票下单次数接口_BespeakFrequency", r1 + ":result:" + result);
                        }
                        else if ("BespeakSearchCount".equals(method)) {
                            result = BespeakSearchCount(json); // result = "查询次数";
                        }
                        //其他未知方法
                        else {
                            WriteLog.write("t同程火车票接口_错误", r1 + ":jsonStr:" + json);
                            JSONObject obj = new JSONObject();
                            obj.put("success", false);
                            obj.put("code", "106");
                            obj.put("msg", "接口不存在");
                            result = obj.toString();
                            WriteLog.write("t同程火车票接口_错误", r1 + ":result:" + result);
                        }
                    }
                    else {
                        JSONObject obj = new JSONObject();
                        //如果是更新接口账号的话就做移出操作
                        //chendong 2015年4月11日19:18:11
                        if ("updateInterfaceAccount".equals(method)) {
                            interfaceAccountMap.remove(userPartnerid);
                            obj.put("remove", userPartnerid);
                        }
                        else if ("1".equals(method)) {//初始化账号信息!!!!!!
                            interfaceAccountMap = new HashMap<String, InterfaceAccount>();
                            obj.put("removeall", "OK");
                        }
                        else if ("2".equals(method)) {//缓存同步到数据库
                        }
                        else if ("3".equals(method)) {//查看缓存的数据
                            obj.put("interfaceAccountMap", interfaceAccountMap);
                        }
                        WriteLog.write("t同程火车票接口_错误", r1 + ":jsonStr:" + json);
                        obj.put("success", false);
                        obj.put("code", "105");
                        obj.put("msg", "签名错误");
                        result = obj.toString();
                        WriteLog.write("t同程火车票接口_错误", r1 + ":result:" + result);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    String msg = "当前时间不提供服务";
                    String code = "113";
                    if (message.startsWith("103 err")) {
                        msg = message;
                        code = "103";
                    }
                    JSONObject obj = new JSONObject();
                    obj.put("success", false);
                    obj.put("code", code);
                    obj.put("msg", msg);
                    result = obj.toString();
                    WriteLog.write("t同程火车票接口_异常", r1 + ":" + ElongHotelInterfaceUtil.errormsg(e));
                }
            }
        }
        catch (Exception e) {
            logger.error("tongchengTrainorder_createtrainorder", e.fillInStackTrace());
            JSONObject obj = new JSONObject();
            obj.put("success", false);
            obj.put("code", "113");
            obj.put("msg", "当前时间不提供服务");
            result = obj.toString();
            //日志
            WriteLog.write("t同程火车票接口_异常", r1 + ":" + ElongHotelInterfaceUtil.errormsg(e));
        }
        finally {
            if (out != null) {
                WriteLog.write("t同程火车票接口", r1 + ":reslut:" + result);
                cancelyibuagain(param, result, inter_partnerid, userPartnerid, userDbKey);
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

    /**
     * @param json
     * @return
     * @time 2016年4月13日 上午9:35:26
     * @author 杨荣强
     */
    private String BespeakSearchCount(JSONObject json) {
        JSONObject jsonObject = new JSONObject();
        String OrderId = "";//订单号
        int Count = -1;
        try {
            int CreateStatus = 0;
            Date date = new Date();//当前时间
            String begintime = "";//开始时间
            String BespeakDateList = "";//发车时间
            long allquerytime = 0L;//总查询时间
            OrderId = json.getString("orderid");
            String Sql = "EXECUTE [dbo].[sp_TrainOrderBespeak_Select_COUNT] @OrderId='" + OrderId + "'";
            DataTable dataTable = DBHelper2.GetDataTable(Sql, null);
            if (dataTable.GetRow().size() > 0) {
                CreateStatus = dataTable.GetRow().get(0).GetColumnInt("CreateStatus");
                begintime = dataTable.GetRow().get(0).GetColumnString("BeginTime");
                BespeakDateList = dataTable.GetRow().get(0).GetColumnString("BespeakDateList");
                int PKId = dataTable.GetRow().get(0).GetColumnInt("PKId");
                if (BespeakDateList.contains(",")) {
                    BespeakDateList = BespeakDateList.split(",")[0];
                }
                if (dataTable.GetRow().size() > 0) {
                    if (CreateStatus == 2 || CreateStatus == 3) {
                        String endDateSql = "EXECUTE [dbo].[sp_TrainOrderBespeakCancel_select] @orderid=" + PKId;
                        DataTable endDateDataTable = DBHelper2.GetDataTable(endDateSql, null);
                        if (endDateDataTable.GetRow().size() > 0) {
                            String endTime = endDateDataTable.GetRow().get(0).GetColumnString("Date");
                            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(endTime);
                        }
                    }
                    long comparttime = getInttime(begintime, gettime(date.getTime()));
                    if (comparttime > 60) {
                        begintime = getneedtime(formattime(BespeakDateList).getTime() - 60);
                    }
                    allquerytime = getquerytime(begintime, date);
                    //                    long result_1 = querydata();
                    //                    long result_2 = querytime();
                    long queryfrequency = 1000;//(result_1 * 1000) / result_2;//得到当前的查询频率
                    int result_3 = queryallcount();
                    //得到平均每个订单的查询间隔（多少秒查一次）
                    long querytime_gap = result_3 / queryfrequency;
                    //得到查询次数
                    Count = (int) (allquerytime / querytime_gap);
                    WriteLog.write("tn_查询次数接口", "OrderId--->" + OrderId + "--->begintime--->" + begintime
                            + "--->date--->" + date.getTime() + "--->allquerytime--->" + allquerytime
                            + "--->result_3--->" + result_3 + "--->querytime_gap--->" + querytime_gap);
                }
            }
            if (Count == -1) {
                jsonObject.put("number", 0);
                jsonObject.put("success", false);
                jsonObject.put("msg", "无此订单");
                jsonObject.put("code", 103);
            }
            else {
                jsonObject.put("number", Count);
                jsonObject.put("success", true);
                jsonObject.put("msg", "操作成功");
                jsonObject.put("code", 100);
            }
        }
        catch (Exception e) {
            jsonObject.put("orderid", OrderId);
            jsonObject.put("number", Count);
            jsonObject.put("success", false);
            jsonObject.put("code", 999);
            jsonObject.put("msg", "接口异常");
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 获取当前内存中的所有查询数
     * 杨荣强
     */
    public static int queryallcount() {
        int count = 1;
        String result = SendPostandGet.submitGet("http://121.41.35.117:19544/trainorder_bespeak/view.jsp", "UTF-8");
        String[] re = result.split("----->");
        String result_3 = "";
        for (int i = 0; i < re.length; i++) {
            if (re[i].contains("SearchTable:")) {
                result_3 = re[i];
                String result_4 = result_3.substring(12);
                count = Integer.parseInt(result_4);
            }
        }
        return count;
    }

    /**
     *获取当前的查询间隔 
     * @time 2016年4月14日 上午10:35:35
     * @author 杨荣强
     * 
     */
    public static long querytime() {
        long timeString = new Date().getTime();
        String result = SendPostandGet.submitGet(
                "http://c.hangtian123.net/WebSearch/iSearch?Type=Search&Value=SleepTime", "UTF-8");
        if (result.contains("success")) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String resString = jsonObject.getString("success");
            timeString = Integer.parseInt(resString);
        }
        return timeString;
    }

    /**
     * 获取当前的查询个数
     * @return
     * @time 2016年4月14日 上午10:35:35
     * @author 杨荣强
     */
    public static long querydata() {
        long result_1 = 1L;
        String result = SendPostandGet.submitGet(
                "http://c.hangtian123.net/WebSearch/iSearch?Type=Search&Value=SearchCount", "UTF-8");
        if (result.contains("success")) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String resString = jsonObject.getString("success");
            result_1 = Long.parseLong(resString);
        }
        return result_1;
    }

    /**
     * 比较开始时间与当前时间的总查询天数
     */

    public Long getquerytime(String starttime, Date date) {
        long time;
        //总时长
        long alltime = Math.abs(formattime_1(starttime).getTime() - date.getTime());
        //查询天数
        long Count = alltime / (24 * 60 * 60 * 1000);
        //每天晚上23点-次日早7点的时间
        long trimtime = 0L;
        long parttime = Count * (8 * 60 * 60 * 1000);
        //截取starttime的hour时间
        long hourtime = Long.parseLong(starttime.substring(11, 13));
        //截取starttime的分钟时间
        long minute = Long.parseLong(starttime.substring(14));
        if (hourtime >= 0 && hourtime < 7) {
            trimtime = parttime + 60 * 60 * 1000 + hourtime * 60 * 60 * 1000 + minute * 60 * 1000;
        }
        else if (hourtime >= 7 && hourtime < 23) {
            trimtime = parttime;
        }
        else if (hourtime >= 23 && hourtime < 24) {
            trimtime = parttime + 60 * 60 * 1000 + minute * 60 * 1000;
        }
        time = alltime / 1000 - trimtime;
        return time;

    }

    /**比较两个时间段之间的天数
     * 杨荣强
     */
    private int getInttime(String starttime, String endtime) {
        int day = 0;
        return (int) Math.ceil((formattime(starttime).getTime() - formattime(endtime).getTime()) / 24 * 60 * 60 * 1000
                - 1);
    }

    /**
     * 格式化后的时间转换成long型以天为单位的 时间
     */
    private static Date formattime(String time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sf.parse(time);
        }
        catch (ParseException e) {

            e.printStackTrace();
        }
        return date;
    }

    /**
     * 格式化后的时间转换成long型以dd为单位的 时间
     */
    private static Date formattime_1(String time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        try {
            date = sf.parse(time);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 格式化时间
     */
    private static String gettime(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * @param comarttime
     * @return
     * @time 2016年4月13日 下午2:44:26
     * @author 杨荣强
     */
    private String getneedtime(Long comarttime) {
        String time = "";
        Date date = new Date(comarttime);
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDay();
        time = year + "-" + month + "-" + day + " " + "07:00:00";
        return time;
    }

    /**
     * BespeakFrequency
     */
    public String BespeakFrequency(JSONObject Json) {
        JSONObject jsonObject = new JSONObject();
        String OrderId = "";
        int Count = -1;
        try {
            OrderId = Json.getString("orderid");
            String Sql = "EXECUTE [dbo].[sp_TrainBespeakNum_Select_COUNT] @OrderId='" + OrderId + "'";
            DataTable dataTable = DBHelper2.GetDataTable(Sql, null);
            if (dataTable.GetRow().size() > 0) {
                Count = dataTable.GetRow().get(0).GetColumnInt("count");
            }
            jsonObject.put("orderid", OrderId);
            jsonObject.put("number", Count);
            jsonObject.put("success", true);
            jsonObject.put("code", 100);
            jsonObject.put("msg", "操作成功");
        }
        catch (Exception e) {
            jsonObject.put("orderid", OrderId);
            jsonObject.put("number", Count);
            jsonObject.put("success", false);
            jsonObject.put("code", 999);
            jsonObject.put("msg", "接口异常");
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 根据用户名获取到这个用户的key
     * 
     * @param loginname
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author chendong
     */
    private String getcustomeruserKEYbyloginname(String loginname) {
        String dbkey = "-1";
        String sql = "SELECT " + Customeruser.COL_workphone + " FROM T_CUSTOMERUSER WHERE C_LOGINNAME='" + loginname
                + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            try {
                dbkey = map.get("C_WORKPHONE").toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dbkey;
    }

    /**
     * 根据用户名获取到这个用户的key
     * @param loginname
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author fiend
     */
    private InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            String sql = "SELECT * FROM T_INTERFACEACCOUNT WHERE C_USERNAME='" + loginname + "'";
            DataTable datatable = DBHelper.GetDataTable(sql);
            List<DataRow> dataRows = datatable.GetRow();
            for (DataRow dataRow : dataRows) {
                interfaceAccount.setInterfacetype(dataRow.GetColumnInt("C_INTERFACETYPE"));
                interfaceAccount.setKeystr(dataRow.GetColumnString("C_KEY"));
            }
        }
        catch (Exception e) {
        }
        return interfaceAccount;
    }

    /**
     * 防止同步超时,追加一个异步回调 
     * @param param
     * @param result
     * @time 2015年4月8日 下午9:14:11
     * @author fiend
     * @param inter_partnerid 
     */
    public void cancelyibuagain(String param, String result, String inter_partnerid, String userPartnerid, String key) {
        try {
            if ("tongcheng_train".equals(inter_partnerid)) {
                JSONObject json = JSONObject.parseObject(param);
                String methodString = json.getString("method") == null ? "" : json.getString("method");
                if ("train_cancel_change".equals(methodString)) {
                    JSONObject jso = JSONObject.parseObject(result);
                    try {
                        ExecutorService pool = Executors.newFixedThreadPool(1);
                        Thread t1 = new MyThreadCancelMessageAgain(jso.getBoolean("success"), jso.getString("code"),
                                jso.getString("msg"), userPartnerid, key, json.getString("orderid"), 2, callbackurl,
                                json.containsKey("changereqtoken") ? json.getString("changereqtoken") : "");
                        pool.execute(t1);
                        pool.shutdown();
                    }
                    catch (Exception e) {

                        e.printStackTrace();
                    }
                }
                else if ("train_cancel".equals(methodString)) {
                    JSONObject jso = JSONObject.parseObject(result);
                    try {
                        ExecutorService pool = Executors.newFixedThreadPool(1);
                        Thread t1 = new MyThreadCancelMessageAgain(jso.getBoolean("success"), jso.getString("code"),
                                jso.getString("msg"), userPartnerid, key, json.getString("orderid"), 1, callbackurl, "");
                        pool.execute(t1);
                        pool.shutdown();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 约票数据传递线程
     * @time 2015年10月29日 13:54:45
     * @author QingXin
     **/
    public String getBespeakParam(JSONObject json, int Interfacetype) {
        boolean istrue = verifyData(json);
        JSONObject jo = new JSONObject();
        if (istrue == false) {
            jo.put("success", false);
            jo.put("code", "107");
            jo.put("orderid", json.getString("qorderid"));
            jo.put("msg", "业务参数缺失");
        }
        else {
            jo = new TrainBespeskMethod().transferData(json, Interfacetype);
        }
        WriteLog.write("Q_约票旧接口获取参数", "数据验证:" + istrue + ";result:" + jo.toJSONString());
        return jo.toJSONString();
    }

    /**
     * 约票参数验证
     * @time 2015年10月28日 21:04:38
     * @author QingXin
     **/
    public boolean verifyData(JSONObject json) {
        JSONArray jsonArray = json.getJSONArray("passengers");
        String BeginTime = json.containsKey("qorder_start_time") ? json.getString("qorder_start_time") : "";
        String BeginBespeakDate = json.containsKey("start_begin_time") ? json.getString("start_begin_time") : "";
        String EndBespeakDate = json.containsKey("start_end_time") ? json.getString("start_end_time") : "";
        String WantedTrainType = json.containsKey("train_type") ? json.getString("train_type") : "";
        //String WantedSeatType = json.containsKey("seat_type") ? json.getString("seat_type") : "";
        String FromCity = json.containsKey("from_station_name") ? json.getString("from_station_name") : "";
        String ToCity = json.containsKey("to_station_name") ? json.getString("to_station_name") : "";
        int qorder_type = json.containsKey("qorder_type") ? json.getInteger("qorder_type") : 0;
        String BespeakDateList = json.containsKey("start_date") ? json.getString("start_date") : "";
        for (int i = 0; i < jsonArray.size(); i++) {
            String Name = jsonArray.getJSONObject(i).getString("passengersename");// 乘客姓名
            String IdNumber = jsonArray.getJSONObject(i).getString("passportseno");// 乘客证件号码
            String DepartTime = jsonArray.getJSONObject(i).getString("DepartTime");// 发车时间
            String Departure = jsonArray.getJSONObject(i).getString("from_station_name");// 始发站
            String Arrival = jsonArray.getJSONObject(i).getString("to_station_name");// 到达站
            int TicketType = jsonArray.getJSONObject(i).getIntValue("piaotype");// 票类型 
            String SeatType = jsonArray.getJSONObject(i).getString("SeatType");// 坐席类型
            String SeatNo = jsonArray.getJSONObject(i).getString("SeatNo");// 坐席号
        }
        if (ElongHotelInterfaceUtil.StringIsNull(BeginTime) || ElongHotelInterfaceUtil.StringIsNull(BeginBespeakDate)
                || ElongHotelInterfaceUtil.StringIsNull(EndBespeakDate)
                || ElongHotelInterfaceUtil.StringIsNull(WantedTrainType)
                || ElongHotelInterfaceUtil.StringIsNull(FromCity) || ElongHotelInterfaceUtil.StringIsNull(ToCity)
                || qorder_type == 0 || ElongHotelInterfaceUtil.StringIsNull(BespeakDateList)

        ) {
            return false;
        }
        else {
            return true;
        }
    }

    public Map<String, InterfaceAccount> getInterfaceAccountMap() {
        return interfaceAccountMap;
    }

    public void setInterfaceAccountMap(Map<String, InterfaceAccount> interfaceAccountMap) {
        this.interfaceAccountMap = interfaceAccountMap;
    }

}
package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;

@SuppressWarnings("serial")
public class CallBackServlet extends HttpServlet {

    public String partnerid;

    public String key;

    //测试key
    //    public String key = "lmh46c63ubh1h8oj6680wbtgfi40btqh";
    //ceshi
    //    public String partnerid = "tongcheng_train_test";
    //1,退票回调通知url：
    ////测试地址：http://tsflightopenapi.17usoft.com/train/services/hthyRefundAsyncNotify
    ////正式地址：http://train.17usoft.com/trainOrder/services/hthyRefundAsyncNotify
    //
    //2,确认出票通知url：
    ////测试地址：http://tsflightopenapi.17usoft.com/train/services/hthyConfirmIssueTicketNotify
    ////正式地址：http://train.17usoft.com/trainOrder/services/hthyConfirmIssueTicketNotify
    public String payCallbackUrl;//支付回调

    public String refunCallBackUrl;//退款回调

    public String zhanzuojieguoBackUrl;//占座结果回调url

    public String budanBackUrl;//补单通知回调 URL

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
                //解析请求JSON
                WriteLog.write("t同程火车票接口_回调", r1 + ":" + param);
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
                    String isSuccess = "Y";
                    if ("N".equals(isSuccess_String)) {
                        isSuccess = "N";
                    }
                    result = payCallBack(orderid, transactionid, 0, isSuccess, iskefu);
                }
                else if ("train_refund_callback".equals(method)) {//==============================线上退票通知
                    String apiorderid = json.containsKey("apiorderid") ? json.getString("apiorderid") : "";
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
                    //                        returntype 退票回调通知类型 0：表示线下退票 1：表示线上退票
                    //                        * @param apiorderid 同程订单号
                    //                        * @param trainorderid 火车票取票单号
                    //                        * @param reqtoken （唯一）退票回调特征值(1.当回调内容是客人在线申请退票的退款，该值为在调用退票请求API时，由同程传入；2.当回调内容是客人在线下车站退票的退款，该值由供应商分配。)
                    //                        * @param returntickets 车票退票信息(json字符串数组形式，每张车票包含乘车人信息和退票相关信息，如：
                    //                       ["ticket_no":" E2610890401070051","passengername":"王二","passporttypeseid":1,"passportseno":"421116198907143795","returnsuccess":true,"returnmoney":"20.05","returntime":"2014-02-13 15:00:05","returnfailid":"","returnfailmsg":""}] 
                    //                        * @param token 退票信息特征值  注：当为线下退票时，此值为空
                    //                        * @param returnstate 退票状态 true:表示成功  false:表示退票失败  
                    //                        * @param returnmoney 退款金额（成功需有值） 当为线上退票时，此值为退款总额
                    //                        * @param returnmsg 退票后消息描述（当returnstate=false时，需显示退票失败原因等）
                    result = refunCallBack(returntype, apiorderid, trainorderid, reqtoken, returnstate, returnmsg,
                            ticket_no, passengername, passporttypeseid, passportseno, returnsuccess, returnmoney,
                            returntime, returnfailid, returnfailmsg);
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
     * 4.16. 确认出票回调通知
     * 
     * 支付通知，约定：
     * 1.遇到通知异常的，要间隔1分钟再尝试通知。
     * 2.连续通知异常5次的，要停止通知，程序加监控，人工介入处理。
     * 3.人工介入处理后，可以触发单次通知。
     * @param orderid 同程订单号
     * @param transactionid 交易单号 我方订单号
     * @param errorCount 错误次数，默认0
     * @param iskefu 
     */
    public String payCallBack(String orderid, String transactionid, int errorCount, String isSuccess, String iskefu) {
        int r1 = new Random().nextInt(1000000);
        String ret = "false";
        try {
            String time = gettimeString(1);
            WriteLog.write("t同程火车票接口_4.16确认出票回调通知", r1 + ":this.key:" + this.key);
            String sign = ElongHotelInterfaceUtil.MD5(this.key);
            sign = this.partnerid + time + sign;
            sign = ElongHotelInterfaceUtil.MD5(sign);
            String payCallbackUrl_temp = this.payCallbackUrl;
            String payCallbackUrl_temp_other = getcallbackurl(0L, 2, orderid, transactionid);
            //如果这里为true说明是非同程的订单
            if (payCallbackUrl_temp_other != null && !"-1".equals(payCallbackUrl_temp_other)) {
                payCallbackUrl_temp = payCallbackUrl_temp_other;
                String partnerid = "";
                String key = "";
                String sql = "SELECT C_LOGINNAME,C_WORKPHONE from T_CUSTOMERUSER WHERE C_AGENTID=(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER WHERE C_QUNARORDERNUMBER='"
                        + orderid + "' and C_ORDERNUMBER='" + transactionid + "') and C_ISADMIN=1";
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    try {
                        partnerid = map.get("C_LOGINNAME").toString();
                        key = map.get("C_WORKPHONE").toString();
                    }
                    catch (Exception e) {

                    }
                }
                sign = ElongHotelInterfaceUtil.MD5(key);
                sign = partnerid + time + sign;
                sign = ElongHotelInterfaceUtil.MD5(sign);
            }

            String parm = "reqtime=" + time + "&sign=" + sign + "&orderid=" + orderid + "&transactionid="
                    + transactionid.trim() + "&isSuccess=" + isSuccess;

            //请求同程
            WriteLog.write("t同程火车票接口_4.16确认出票回调通知", r1 + ":" + orderid + ":backurl:" + payCallbackUrl_temp + ":parm:"
                    + parm);
            try {
                ret = SendPostandGet.submitPost(payCallbackUrl_temp, parm, "utf-8").toString();
            }
            catch (Exception e) {
                WriteLog.write("t同程火车票接口_4.16确认出票回调通知_train_order_budan_callback_Exception", "orderid:" + orderid + ":"
                        + e.fillInStackTrace().toString());
            }
            WriteLog.write("t同程火车票接口_4.16确认出票回调通知", orderid + ":回调接口返回:" + ret);
            //成功
            if ("success".equalsIgnoreCase(ret)) {
                return "success";
            }
            else {
                throw new Exception(ret);
            }
        }
        catch (Exception e) {
            if (!"1".equals(iskefu)) {
                try {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                errorCount = errorCount + 1;
                if (errorCount >= 5) {
                    return "连续通知异常5次的,停止通知,需人工介入处理";
                }
                else {
                    ret = payCallBack(orderid, transactionid, errorCount, isSuccess, iskefu);
                }
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        String ret = SendPostandGet
                .submitPost(
                        "http://tang110520.dns-dns.com:8082/SubmitOrderPostbackHandler.ashx",
                        "data={\"reqtoken\":\"\",\"from_station_name\":\"%E5%A4%AA%E5%8E%9F\",\"ordernumber\":\"EC22929067\",\"runtime\":\"00:05\",\"checi\":\"6818\",\"code\":100,\"msg\":\"%E5%A4%84%E7%90%86%E6%88%96%E6%93%8D%E4%BD%9C%E6%88%90%E5%8A%9F\",\"from_station_code\":\"TYV\",\"orderamount\":\"1.00\",\"to_station_name\":\"%E5%A4%AA%E5%8E%9F%E4%B8%9C\",\"arrive_time\":\"2015-03-19 07:23:00\",\"passengers\":[{\"piaotype\":\"1\",\"reason\":0,\"passporttypeseidname\":\"%E4%BA%8C%E4%BB%A3%E8%BA%AB%E4%BB%BD%E8%AF%81\",\"passporttypeseid\":\"1\",\"zwname\":\"%E7%A1%AC%E5%BA%A7\",\"price\":\"1.0\",\"piaotypename\":\"%E6%88%90%E4%BA%BA%E7%A5%A8\",\"ticket_no\":\"EC229290671050002\",\"passengersename\":\"%E6%B1%A4%E5%88%A9%E9%94%8B\",\"zwcode\":\"1\",\"passportseno\":\"440224197907072877\",\"passengerid\":1,\"cxin\":\"05%E8%BD%A6%E5%8E%A2%2C002%E5%BA%A7\"}],\"to_station_code\":\"TDV\",\"train_date\":\"2015-03-19\",\"ordersuccess\":true,\"transactionid\":\"T1503041624007479283\",\"start_time\":\"2015-03-19 07:18:00\",\"orderid\":\"20150304033647\",\"success\":true}",
                        "utf-8").toString();
        System.out.println("ret:" + ret);
    }

    /**
     * /**
     * reqtoken，是我申请退票时传给你的值     你回调时要回传过来    如果是线下车站退票的退款回调，reqtoken你可以自定义，但是每笔退款不能重复
     * @param returntickets 车票退票信息(json字符串数组形式，每张车票包含乘车人信息和退票相关信息，如：
    ["ticket_no":" E2610890401070051","passengername":"王二","passporttypeseid":1,"passportseno":"421116198907143795","returnsuccess":true,"returnmoney":"20.05","returntime":"2014-02-13 15:00:05","returnfailid":"","returnfailmsg":""}] 
     * @param token 退票信息特征值  注：当为线下退票时，此值为空
     * @time 2014年12月26日 下午10:10:19
     * @author chendong
     * 
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
     */
    public String refunCallBack(String returntype, String apiorderid, String trainorderid, String reqtoken,
            boolean returnstate, String returnmsg, String ticket_no, String passengername, String passporttypeseid,
            String passportseno, boolean returnsuccess, String returnmoney, String returntime, String returnfailid,
            String returnfailmsg) {
        //        refunCallBackUrl = "http://tsflightopenapi.17usoft.com/train/services/hthyRefundAsyncNotify";
        JSONArray returntickets = new JSONArray();
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
        returntickets.add(o1);
        long l1 = System.currentTimeMillis();
        l1 = l1 / 1000;
        String timestamp = l1 + "";
        try {
            //            String sign = ElongHotelInterfaceUtil.MD5(partnerid + time + ElongHotelInterfaceUtil.MD5(key));
            String sign = "";
            //线下退票数字签名
            //=md5(partnerid+returntype+timestamp+apiorderid+trainorderid+returnmoney+returnstate+md5(key))
            //线上退票数字签名
            //=md5(partnerid+returntype+timestamp+apiorderid+trainorderid+token+returnmoney+returnstate+md5(key))
            JSONObject obj = new JSONObject();
            //0：表示线下退票退款  2：线下改签退款
            if ("0".equals(returntype) || "2".equals(returntype)) {
                sign = ElongHotelInterfaceUtil.MD5(this.partnerid + returntype + timestamp + apiorderid + trainorderid
                        + returnmoney + returnstate + ElongHotelInterfaceUtil.MD5(this.key));
            }
            //1：表示线上退票退款  3：线上改签退款
            else {
                String token = System.currentTimeMillis() + "";
                sign = this.partnerid + returntype + timestamp + apiorderid + trainorderid + token + returnmoney
                        + returnstate + ElongHotelInterfaceUtil.MD5(this.key);
                sign = ElongHotelInterfaceUtil.MD5(sign);
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
            //请求同程
            WriteLog.write("t同程火车票接口_4.10退票回调通知",
                    apiorderid + ":backurl:" + refunCallBackUrl + ":parm:data=" + obj.toString());
            String ret = SendPostandGet.submitPost(refunCallBackUrl, "data=" + obj.toString(), "utf-8").toString();
            WriteLog.write("t同程火车票接口_4.10退票回调通知", apiorderid + ":同程返回:" + ret);
            //成功
            if ("success".equalsIgnoreCase(ret)) {
                return "success";
            }
            else {
                throw new Exception(ret);
            }
        }
        catch (Exception e) {
            //            errorCount = errorCount + 1;
            //            if (errorCount >= 5) {
            //                return "连续通知异常5次的，停止通知，需人工介入处理";
            //            }
        }
        return "false";
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
    public String getcallbackurl(Long orderid, int type, String orderid_no, String transactionid) {
        String url = "-1";
        //        String sql = "SELECT C_ZHANZUOJIEGUOBACKURL FROM T_CALLBACK_TRAIN WHERE C_AGENTID=(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER WHERE ID="
        //                + orderid + ")";
        String sql = "SELECT C_ZHANZUOHUIDIAO FROM T_INTERFACEACCOUNT WITH(nolock) WHERE C_AGENTID=(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER WITH(nolock) WHERE ID="
                + orderid + ")";
        Map map = new HashMap();
        String key = "";
        if (type == 1) {
            //            key = "C_ZHANZUOJIEGUOBACKURL";
            key = "C_ZHANZUOHUIDIAO";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                map = (Map) list.get(0);
                if (map.get(key) != null) {
                    try {
                        url = map.get(key).toString();
                    }
                    catch (Exception e) {
                    }
                }
            }
        }
        else if (type == 2) {
            //            key = "C_PAYCALLBACKURL";
            //            sql = "SELECT C_PAYCALLBACKURL FROM T_CALLBACK_TRAIN WHERE C_AGENTID=(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER WHERE C_QUNARORDERNUMBER='"
            //                    + orderid_no + "' and C_ORDERNUMBER='" + transactionid + "')";
            key = "C_PAYCALLBACKURL";
            sql = "SELECT C_PAYCALLBACKURL FROM T_INTERFACEACCOUNT WITH(nolock) WHERE C_AGENTID=(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER WITH(nolock) WHERE C_QUNARORDERNUMBER='"
                    + orderid_no + "' and C_ORDERNUMBER='" + transactionid + "')";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() == 0) {//如果是空的重新只根据C_QUNARORDERNUMBER 来查找订单再获取到回调地址 2015年7月13日14:13:03 chendong
                sql = "SELECT C_PAYCALLBACKURL FROM T_INTERFACEACCOUNT WITH(nolock) WHERE C_AGENTID=(SELECT TOP 1 C_AGENTID FROM T_TRAINORDER WITH(nolock) WHERE C_QUNARORDERNUMBER='"
                        + orderid_no + "')";
                list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            }
            if (list.size() > 0) {
                map = (Map) list.get(0);
                if (map.get(key) != null) {
                    try {
                        url = map.get(key).toString();
                    }
                    catch (Exception e) {
                    }
                }
            }
        }
        return url;
    }

    public void sendMQUrlMethod(String ret, long orderstatus, long trainorderid) {
        try {
            if ("SUCCESS".equalsIgnoreCase(ret) && orderstatus == 1) {
                //new TrainpayMqMSGUtil(MQMethod.ORDERGETURL_NAME).sendGetUrlMQmsg(trainorderid);
                WriteLog.write("12306_TongChengCallBackServlet_MQ_GetUrl", " ：回调同程：" + trainorderid);
            }
        }
        catch (Exception e) {
        }
    }

    /**
     * 根据订单id获取 一些信息
     * 
     * @param trainorderid
     * @return
     * @time 2015年1月22日 下午1:05:36
     * @author chendong
     */
    private Map getTrainorderstatus(Long trainorderid) {
        Map map = new HashMap();
        String sql = "SELECT C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,C_TOTALPRICE from T_TRAINORDER where ID="
                + trainorderid;
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

    private String geturlencode(String oldstring) {
        try {
            oldstring = URLEncoder.encode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    private String geturldecode(String oldstring) {
        try {
            oldstring = URLDecoder.decode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
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

}

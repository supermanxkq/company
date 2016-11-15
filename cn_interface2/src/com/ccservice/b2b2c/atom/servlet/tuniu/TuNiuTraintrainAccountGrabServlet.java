package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread.MyThreadTransferData;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 途牛抢票接口3.13
 * @time 2015年11月19日 下午5:43:30
 **/
@SuppressWarnings("serial")
public class TuNiuTraintrainAccountGrabServlet extends HttpServlet {

    private final String logname = "途牛_抢票接口";

    private final TuNiuServletUtil tuNiuServletUtil = new TuNiuServletUtil();

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        final AsyncContext ctx = req.startAsync();
        ctx.setTimeout(50000L);
        //监听
        ctx.addListener(new AsyncListener() {
            public void onTimeout(AsyncEvent event) throws IOException {
                tuNiuServletUtil.respByUnknownError(ctx, logname);
            }

            public void onError(AsyncEvent event) throws IOException {
                tuNiuServletUtil.respByUnknownError(ctx, logname);
            }

            public void onComplete(AsyncEvent event) throws IOException {
            }

            public void onStartAsync(AsyncEvent event) throws IOException {

            }
        });
        BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getRequest().getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String reqString = buf.toString();
        WriteLog.write(logname, reqString);
        try {
            if (reqString == null || "".equals(reqString)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            //请求json
            JSONObject reqjso = JSONObject.parseObject(reqString);
            String account = tuNiuServletUtil.getParamByJsonStr("account", reqjso);//账号
            String sign = tuNiuServletUtil.getParamByJsonStr("sign", reqjso);//加密结果
            String timestamp = tuNiuServletUtil.getParamByJsonStr("timestamp", reqjso);//请求时间
            String data = tuNiuServletUtil.getParamByJsonStr("data", reqjso);//加密的请求体
            if ("".equals(account) || "".equals(sign) || "".equals(timestamp) || "".equals(data)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            TuNiuTraintrainAccountGrab tuNiuTraintrainAccountGrab = new TuNiuTraintrainAccountGrab();
            //获取账户信息
            Map map = tuNiuServletUtil.getInterfaceAccount(account);
            String agentid = tuNiuServletUtil.getParamByMapStr("C_AGENTID", map);
            String key = tuNiuServletUtil.getParamByMapStr("C_KEY", map);
            String password = tuNiuServletUtil.getParamByMapStr("C_ARG2", map);
            String interfacetype = tuNiuServletUtil.getParamByMapStr("C_INTERFACETYPE", map);
            if ("".equals(agentid) || "".equals(key) || "".equals(password)) {
                tuNiuServletUtil.respByUserNotExists(ctx, logname);
                return;
            }
            JSONObject object = JSONObject.parseObject(reqString);
            object.put("sign", "");
            //获取key加密
            String localsign = SignUtil.generateSign(object.toString(), key);
            WriteLog.write(logname + "_加密", "localsign:" + localsign + ";sign:" + sign);
            if (!sign.equalsIgnoreCase(localsign)) {
                WriteLog.write("Error_抢票签名异常", "sign:" + sign + ";localsign:" + localsign);
                tuNiuServletUtil.respBySignatureError(ctx, logname);
                return;
            }
            String paramStr = SignUtil.generateSign(object.toString(), key);
            WriteLog.write(logname, "data:" + paramStr);
            JSONObject jsonString = new JSONObject();
            try {
                jsonString = JSONObject.parseObject(paramStr);
            }
            catch (Exception e1) {
                ExceptionUtil.writelogByException(paramStr, e1);
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            String orderId = tuNiuServletUtil.getParamByJsonStr("orderId", jsonString);//    string  Y       途牛订单号
            String cheCi = tuNiuServletUtil.getParamByJsonStr("cheCi", jsonString);//  string  Y       车次
            String fromStationCode = tuNiuServletUtil.getParamByJsonStr("fromStationCode", jsonString);//    string  Y       出发站简码
            String fromStationName = tuNiuServletUtil.getParamByJsonStr("fromStationName", jsonString);//    string  Y       出发站名称
            String toStationCode = tuNiuServletUtil.getParamByJsonStr("toStationCode", jsonString);//  string  Y       到达站简码
            String toStationName = tuNiuServletUtil.getParamByJsonStr("toStationName", jsonString);//  string  Y       到达站名称
            String trainDate = tuNiuServletUtil.getParamByJsonStr("trainDate", jsonString);//  string  Y       乘车日期
            String callBackUrl = tuNiuServletUtil.getParamByJsonStr("callBackUrl", jsonString);//    string  Y       回调地址
            boolean hasSeat = tuNiuServletUtil.getParamByJsonBoolean("hasSeat", jsonString);//    boolean  Y      是否需要无座
            String passengers = tuNiuServletUtil.getParamByJsonStr("passengers", jsonString);// string  Y       乘客信息的json字符串。可以是多个乘客信息，最多5个，如：[{乘客1信息},{乘客2信息},...]，也可以只有一个，[{乘客1信息}]。乘客参数见附注1。重要提示：不能只购买儿童票，如果购买儿童票，必须使用随行成人的成人票证件信息（包括姓名、证件号码）。
            String contact = tuNiuServletUtil.getParamByJsonStr("contact", jsonString);//    string  Y       联系人姓名
            String phone = tuNiuServletUtil.getParamByJsonStr("phone", jsonString);//  string  Y       联系人手机
            String trainAccount = tuNiuServletUtil.getParamByJsonStr("trainAccount", jsonString);//   string  N       12306用户名
            String pass = tuNiuServletUtil.getParamByJsonStr("pass", jsonString);//   string  N       12306密码
            int ordertype = getOrdertype(trainAccount, pass, "");
            if ("".equals(orderId) || "".equals(cheCi) || "".equals(fromStationCode) || "".equals(fromStationName)
                    || "".equals(toStationCode) || "".equals(toStationName) || "".equals(trainDate)
                    || "".equals(callBackUrl) || "".equals(passengers)) {
                tuNiuServletUtil.respByParamError(ctx, logname);
                return;
            }
            Trainorder trainorder = new Trainorder();
            trainorder.setAgentid(Long.parseLong(agentid));// 代理ID
            trainorder.setCreateuid(Long.parseLong(agentid));//
            List<Trainpassenger> passengerlist = tuNiuTraintrainAccountGrab.gettrainpassenger(
                    JSONArray.parseArray(passengers), null, cheCi, toStationName, trainDate, fromStationName);
            trainorder.setPassengers(passengerlist);
            trainorder.setOrderstatus(Trainorder.WAITPAY);
            trainorder.setQunarOrdernumber(orderId);
            trainorder.setOrderprice(0f);
            trainorder.setAgentprofit(0f);// 采购利润
            trainorder.setInterfacetype(8);
            trainorder.setCommission(0f);
            trainorder.setSupplyprice(0f);
            trainorder.setCreateuser("接口");
            trainorder.setPaymethod(4);
            trainorder.setState12306(Trainorder.WAITORDER);// 12306状态--等待下单
            trainorder.setContactuser(contact);
            trainorder.setContacttel(phone);
            trainorder.setOrdertype(ordertype);
            boolean isTimeoutOrder = false;
            String localOrderNumber = "";
            try {
                String sql = "SELECT C_ORDERNUMBER FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='"
                        + orderId + "' AND C_AGENTID=" + agentid;
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                if (list.size() > 0) {
                    Map map1 = (Map) list.get(0);
                    String ordernumberString = map1.get("C_ORDERNUMBER").toString();
                    localOrderNumber = ordernumberString;
                    isTimeoutOrder = true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (!isTimeoutOrder) {

                try {
                    //存老库
                    trainorder = Server.getInstance().getTrainService().createTrainorder(trainorder);
                    localOrderNumber = tuNiuTraintrainAccountGrab.getordernumberbyid(trainorder.getId());
                    tuNiuTraintrainAccountGrab.createtrainorderrc(1, "提交订单成功", trainorder.getId(), 0L,
                            trainorder.getOrderstatus(), "约票接口");
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                    WriteLog.write("Error_抢票存老库异常", "orderId:" + orderId);
                    tuNiuServletUtil.respByParamError(ctx, logname);
                    return;
                }
                try {
                    //入账号库
                    String sql = " insert TrainAccountSrc (username,password,trainorderid,accountSrc) values" + "('"
                            + trainAccount + "'" + ",'" + pass + "'," + trainorder.getId() + "," + agentid + ")";
                    Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    //入约票库
                    JSONArray arrayPassenger = JSONArray.parseArray(passengers);
                    JSONArray arrayPassengernew = new JSONArray();
                    for (int i = 0; i < arrayPassenger.size(); i++) {
                        JSONObject json5 = (JSONObject) arrayPassenger.get(i);
                        JSONObject json0 = new JSONObject();
                        json0.put("passengerid", json5.getString("passengerId"));
                        json0.put("passengersename", json5.getString("passengerName"));
                        json0.put("passportseno", json5.getString("passportNo"));
                        json0.put("passporttypeseid", json5.getString("passportTypeId"));
                        json0.put("passporttypeidname", json5.getString("passportTypeName"));
                        json0.put("piaotype", json5.getString("piaoType"));
                        json0.put("piaotypename", json5.getString("piaoTypeName"));
                        if (json5.containsKey("provinceName") || json5.containsKey("provinceCode")
                                || json5.containsKey("schoolCode") || json5.containsKey("schoolName")
                                || json5.containsKey("studentNo") || json5.containsKey("schoolSystem")
                                || json5.containsKey("enterYear") || json5.containsKey("preferenceFromStationName")
                                || json5.containsKey("preferenceFromStationCode")
                                || json5.containsKey("preferenceToStationName")
                                || json5.containsKey("preferenceToStationCode")) {
                            json0.put("province_name",
                                    json5.getString("provinceName") == null ? "" : json5.getString("provinceName"));
                            json0.put("province_code",
                                    json5.getString("provinceCode") == null ? "" : json5.getString("provinceCode"));
                            json0.put("school_code",
                                    json5.getString("schoolCode") == null ? "" : json5.getString("schoolCode"));
                            json0.put("school_name",
                                    json5.getString("schoolName") == null ? "" : json5.getString("schoolName"));
                            json0.put("student_no",
                                    json5.getString("studentNo") == null ? "" : json5.getString("studentNo"));
                            json0.put("school_system",
                                    json5.getString("schoolSystem") == null ? "" : json5.getString("schoolSystem"));
                            json0.put("enter_year",
                                    json5.getString("enterYear") == null ? "" : json5.getString("enterYear"));
                            json0.put(
                                    "preference_from_station_name",
                                    json5.getString("preferenceFromStationName") == null ? "" : json5
                                            .getString("preferenceFromStationName"));
                            json0.put(
                                    "preference_from_station_code",
                                    json5.getString("preferenceFromStationCode") == null ? "" : json5
                                            .getString("preferenceFromStationCode"));
                            json0.put(
                                    "preference_to_station_name",
                                    json5.getString("preferenceToStationName") == null ? "" : json5
                                            .getString("preferenceToStationName"));
                            json0.put(
                                    "preference_to_station_code",
                                    json5.getString("preferenceToStationCode") == null ? "" : json5
                                            .getString("preferenceToStationCode"));
                        }
                        arrayPassengernew.add(json0);

                    }
                    String seat_type = "";
                    if (arrayPassenger.size() != 0) {
                        for (int i = 0; i < arrayPassenger.size(); i++) {
                            JSONObject jso = (JSONObject) arrayPassenger.get(0);
                            if (i == 0) {
                                seat_type += jso.getString("zwName");
                            }
                            else {
                                seat_type += "," + jso.getString("zwName");
                            }
                        }
                    }
                    else {
                        tuNiuServletUtil.respByParamError(ctx, logname);
                        return;
                    }
                    JSONObject jsonNew = new JSONObject();
                    jsonNew.put("trainOldorderid", trainorder.getId());
                    jsonNew.put("callback_url", callBackUrl);
                    jsonNew.put("train_type", "");
                    jsonNew.put("qorder_start_time", getQiangpiaoStartTime(trainDate));
                    jsonNew.put("start_date", formate(trainDate));
                    jsonNew.put("qorder_end_time", getQiangpiaoEndTime(trainDate));
                    jsonNew.put("start_begin_time", "00:00");
                    jsonNew.put("start_end_time", "24:00");
                    jsonNew.put("train_codes", cheCi);
                    jsonNew.put("seat_type", seat_type);
                    jsonNew.put("from_station_name", fromStationName);
                    jsonNew.put("from_station_code", fromStationCode);
                    jsonNew.put("to_station_code", toStationCode);
                    jsonNew.put("to_station_name", toStationName);
                    jsonNew.put("qorderid", orderId);
                    jsonNew.put("passengers", arrayPassengernew);
                    jsonNew.put("method", "qiang_piao_order");
                    jsonNew.put("partnerid", account);
                    jsonNew.put("AgentId", agentid);
                    jsonNew.put("hasseat", hasSeat);
                    if (ordertype == 3) {
                        jsonNew.put("LoginName12306", trainAccount);
                        jsonNew.put("LoginPassword12306", pass);
                    }
                    jsonNew.put("interfacetype", interfacetype);
                    new MyThreadTransferData(jsonNew).start();
                }
                catch (Exception e) {
                    tuNiuServletUtil.respByHighFrequencyError(ctx, logname);
                    return;
                }
                JSONObject json = new JSONObject();
                json.put("vendorOrderId", localOrderNumber);
                tuNiuServletUtil.respBySuccess(ctx, logname, json);
            }
            else {
                tuNiuServletUtil.respByHighFrequencyError(ctx, logname);
            }

        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_TaobaoRefundPriceServlet", e);
            tuNiuServletUtil.respByUnknownError(ctx, logname);
        }
    }

    //格式化出发日期
    private String formate(String trainDate) {
        String riqi = trainDate.substring(0, 4) + trainDate.substring(5, 7) + trainDate.substring(8, 10);
        return riqi;
    }

    //可以抢票时间
    public String getQiangpiaoStartTime(String StartTime) {
        SimpleDateFormat yMdHm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat yMd = new SimpleDateFormat("yyyy-MM-dd");
        String sfstr = "";
        try {
            sfstr = yMdHm.format(new Date(yMd.parse(StartTime).getTime() - 20 * 24 * 60 * 60 * 1000 - 20 * 24 * 60 * 60
                    * 1000 - 20 * 24 * 60 * 60 * 1000));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return sfstr;

    }

    //抢票截止时间
    @SuppressWarnings("deprecation")
    public String getQiangpiaoEndTime(String endTime) {
        SimpleDateFormat yMdHm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat yMd = new SimpleDateFormat("yyyy-MM-dd");
        String sfstr = "";
        try {
            Date date = new Date(yMd.parse(endTime).getTime() - 30 * 60 * 1000);
            date.setDate(date.getDate() + 1);
            sfstr = yMdHm.format(date);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return sfstr;
    }

    /**
     * 1、线上 订单
     * 2、线上 订单使用客户 账号下单
     * 3、线上 订单使用客户 账号下单 cookie方式下单
     * @param username
     * @param userpassword
     * @param cookie
     * @time 2015年12月3日 下午3:47:22
     * @author QingXin
     */
    public static int getOrdertype(String username, String userpassword, String cookie) {
        int ordertype = 1;
        try {
            if (!ElongHotelInterfaceUtil.StringIsNull(username) && !ElongHotelInterfaceUtil.StringIsNull(userpassword)) {
                ordertype = 3;
            }
            else if (!ElongHotelInterfaceUtil.StringIsNull(cookie)) {
                ordertype = 4;
            }
        }
        catch (Exception e) {
        }
        return ordertype;
    }
}

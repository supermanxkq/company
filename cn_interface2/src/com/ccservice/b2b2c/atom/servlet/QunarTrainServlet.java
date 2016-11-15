package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.QunarTrain.QunarTrainorder;
import com.ccservice.b2b2c.util.InterfaceTimeRuleUtil;

/**
 * 去哪儿 申请站座
 * @time 2015年7月10日14:17:33
 * @author luoqingxin
 **/
@SuppressWarnings("serial")
public class QunarTrainServlet extends HttpServlet {

    public String key;

    public String agentid;

    QunarTrainorder qunartrainorder;

    public void init() throws ServletException {
        super.init();
        this.qunartrainorder = new QunarTrainorder();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        String result = "{\"ret\":false,\"errCode\":999,\"errMsg\":\"nodata\"}";
        long starttime = System.currentTimeMillis();
        int r1 = new Random().nextInt(10000000);
        PrintWriter out = null;
        try {
            out = res.getWriter();

            //            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            //            String line = "";
            //            StringBuffer sb = new StringBuffer(1024);
            //            while ((line = br.readLine()) != null) {
            //                sb.append(line);
            //            }
            //            String param = sb.toString();
            //            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
            //                JSONObject obj = new JSONObject();
            //                obj.put("errCode", "101");
            //                obj.put("ret", false);
            //                obj.put("errMsg", "传入的 json 为空对象");
            //                result = obj.toString();
            //            }
            //            else {
            //            WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":jsonStr:" + param);
            boolean nowTime = InterfaceTimeRuleUtil.isNightCreateOrder();//判断当前时间是否可以下单

            if (nowTime) {
                try {
                    String orderNo = req.getParameter("orderNo") != null ? req.getParameter("orderNo") : "";//qunar订单号
                    //取数据库获取数据开始
                    int index = orderNo.indexOf("1");
                    String str = orderNo.substring(0, index);
                    String sqlstr = "SELECT top 1  * FROM QunarTrainMerchantInfo WHERE MerchantCode like '%" + str
                            + "%'";
                    List sqlResultList = Server.getInstance().getSystemService().findMapResultBySql(sqlstr, null);
                    for (int i = 0; i < sqlResultList.size(); ++i) {
                        Map map = (Map) sqlResultList.get(i);
                        this.key = map.get("MerchantKey").toString();
                        this.agentid = map.get("Agentid").toString();
                    }
                    WriteLog.write("qunar参数", "MerchantKey:" + key);
                    //取数据库获取数据结束
                    WriteLog.write("QUANRTRAINSERVLET_orderNo", orderNo);
                    int count = getorderNO(orderNo);
                    if (count == 0) {
                        String reqFrom = req.getParameter("reqFrom") != null ? req.getParameter("reqFrom") : "";//请求来源
                        String reqTime = req.getParameter("reqTime") != null ? req.getParameter("reqTime") : ""; //请求时间
                        String trainNo = req.getParameter("trainNo") != null ? req.getParameter("trainNo") : "";// 车次
                        String from_station_code = req.getParameter("from_station_code") != null ? req
                                .getParameter("from_station_code") : "";//出发站简码
                        String from = req.getParameter("from") != null ? req.getParameter("from") : "";//出发站名称
                        String to_station_code = req.getParameter("to_station_code") != null ? req
                                .getParameter("to_station_code") : "";//到达站简码
                        String to = req.getParameter("to") != null ? req.getParameter("to") : "";//到达站名称
                        String date = req.getParameter("date") != null ? req.getParameter("date") : "";//乘车日期
                        String retUrl = req.getParameter("retUrl") != null ? req.getParameter("retUrl") : "";//回调地址
                        String reqtoken = req.getParameter("reqtoken") != null ? req.getParameter("reqtoken") : "";//请求物证值
                        String extSeat = req.getParameter("extSeat") != null ? req.getParameter("extSeat") : "";//备选座席
                        String jsons = req.getParameter("passengers") != null ? req.getParameter("passengers") : "";//乘客
                        JSONArray array = JSONArray.parseArray(jsons);
                        String HMAC = req.getParameter("HMAC") != null ? req.getParameter("HMAC") : "";//加密

                        String HMACflag = null;
                        //如果有备选座席则一起拼接做加密，没有备选座席则不拼接加密
                        if (ElongHotelInterfaceUtil.StringIsNull(extSeat)) {
                            HMACflag = ElongHotelInterfaceUtil.MD5(
                                    key + orderNo + reqFrom + reqTime + trainNo + from + to + date + retUrl
                                            + jsons.toString()).toUpperCase();
                        }
                        else {
                            HMACflag = ElongHotelInterfaceUtil.MD5(
                                    key + orderNo + reqFrom + reqTime + trainNo + from + to + date + retUrl
                                            + jsons.toString() + extSeat).toUpperCase();
                        }

                        WriteLog.write("QUNAR火车票接口_申请占座_错误",
                                r1 + ":jsonStr:" + orderNo + reqFrom + trainNo + from_station_code + from
                                        + to_station_code + to + date + retUrl + reqtoken + array.toString() + HMAC
                                        + ":" + HMACflag);
                        date = date.replace("_", "-");
                        if (HMACflag.equals(HMAC)) {
                            WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":method:train_order:jsonStr:" + r1 + orderNo
                                    + reqFrom + trainNo + from_station_code + from + to_station_code + to + date
                                    + retUrl + reqtoken + array.toString() + extSeat);
                            result = qunartrainorder.submittrainorder(r1, orderNo, agentid, trainNo, from_station_code,
                                    from, to_station_code, to, date, retUrl, reqtoken, array, extSeat);
                            WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":时间:" + (System.currentTimeMillis() - starttime)
                                    + ":method:train_order:result:" + result);
                        }
                        else {
                            WriteLog.write("QUNAR火车票接口_申请占座_错误", r1 + ":jsonStr:" + orderNo + reqFrom + trainNo
                                    + from_station_code + from + to_station_code + to + date + retUrl + reqtoken
                                    + array.toString());
                            JSONObject obj = new JSONObject();
                            obj.put("errCode", "111");
                            obj.put("ret", false);
                            obj.put("errMsg", "处理失败");
                            result = obj.toString();
                            WriteLog.write("QUNAR火车票接口_申请占座_错误", r1 + ":秘钥验证错误:result:" + result);
                        }
                    }
                    else {
                        JSONObject obj = new JSONObject();
                        obj.put("errCode", "112");
                        obj.put("ret", false);
                        obj.put("errMsg", "订单已存在");
                        result = obj.toString();
                        WriteLog.write("QUNAR火车票接口_申请占座_错误", r1 + ":result:" + result);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    JSONObject obj = new JSONObject();
                    obj.put("errCode", "999");
                    obj.put("ret", false);
                    obj.put("errMsg", "数据有误");
                    result = obj.toString();
                    WriteLog.write("QUNAR火车票接口_申请占座_错误", r1 + ":" + ElongHotelInterfaceUtil.errormsg(e));
                }

            }
            else {
                WriteLog.write("QUNAR火车票接口_申请占座_错误", r1 + "------>当前时间不提供服务");
                JSONObject obj = new JSONObject();
                obj.put("errCode", "112");
                obj.put("ret", false);
                obj.put("errMsg", "当前时间不提供服务");
                result = obj.toString();
            }
            //            }
        }
        catch (Exception e) {
            e.printStackTrace();
            JSONObject obj = new JSONObject();
            obj.put("errCode", "999");
            obj.put("ret", false);
            obj.put("errMsg", "数据有误");
            result = obj.toString();
            WriteLog.write("QUNAR火车票接口_申请占座_错误", r1 + ":" + ElongHotelInterfaceUtil.errormsg(e));
        }
        finally {
            if (out != null) {
                WriteLog.write("QUNAR火车票接口_申请占座", r1 + ":reslut:" + result);
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 查询 去哪儿订单号状态
     * @time 2015年7月10日14:41:39
     * @author luoqingxin
     */
    private int getorderNO(String orderNo) {
        int count = 0;
        try {
            String sql_trainorder = "SELECT count(ID) FROM T_TRAINORDER WITH(NOLOCK) WHERE C_QUNARORDERNUMBER ='"
                    + orderNo + "'";
            count = Server.getInstance().getSystemService().countAdvertisementBySql(sql_trainorder);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}

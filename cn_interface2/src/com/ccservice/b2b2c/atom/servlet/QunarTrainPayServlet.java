package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengTrainOrder;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

/**
 * 代付接口
 * @author Administrator
 *
 */

@SuppressWarnings("serial")
public class QunarTrainPayServlet extends HttpServlet {
    public String key;

    public String qunartrainpayUrl;

    public String merchantCode;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        WriteLog.write("QunarTrainPay_doPost", "进入接口");
        String result = "";
        PrintWriter out = null;
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        int r1 = new Random().nextInt(10000);
        try {
            out = res.getWriter();
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
                WriteLog.write("QUNAR支付接口_3.5", r1 + ":" + param);
                JSONObject json = JSONObject.parseObject(param);
                //请求方法
                String method = json.getString("method");
                if ("qunartrain_pay".equals(method)) {
                    Long trainorderid = json.getLong("trainorderid");
                    WriteLog.write("QunarTrainPay_doPost", r1 + ":trainorderid:" + trainorderid);
                    if (trainorderid > 0) {
                        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);
                        result = qunarTrainPay(trainorder);
                    }
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
            WriteLog.write("QUNAR支付接口_3.5", r1 + ":" + result);
            if (out != null) {
                out.print(result);
                out.flush();
                out.close();
            }
        }
        WriteLog.write("QUNAR", "进入接口");
    }

    /**
     * 发给qunar需要支付
     * @param trainorder
     * @return
     * @time 2015年1月15日 下午9:14:06
     * @author fiend
     */
    private String qunarTrainPay(Trainorder trainorder) {
        String status = "false";//        true:成功，false:失败
        int code = 100;
        String msg = "";
        String param = "";
        //qunar订单号
        String orderNo = trainorder.getQunarOrdernumber();
        WriteLog.write("QunarTrainPay_doPost", "orderNo:" + orderNo);
        //取数据库获取数据开始
        int index = orderNo.indexOf("1");
        String str = orderNo.substring(0, index);
        String sql = "SELECT top 1  * FROM QunarTrainMerchantInfo WHERE MerchantCode like '%" + str + "%'";
        WriteLog.write("QunarTrainPay_doPost", "sql:" + sql);
        List sqlResultList = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        for (int i = 0; i < sqlResultList.size(); ++i) {
            Map map = (Map) sqlResultList.get(i);
            this.merchantCode = map.get("MerchantCode").toString();
            this.key = map.get("MerchantKey").toString();
            this.qunartrainpayUrl = map.get("QunarUrl").toString();
        }
        WriteLog.write("QunarTrainPay_doPost", "MerchantCode:" + this.merchantCode + ";MerchantKey:" + this.key
                + ";QunarUrl:" + this.qunartrainpayUrl);
        WriteLog.write("qunar参数", "MerchantCode:" + merchantCode + ";MerchantKey:" + key + ";QunarUrl:"
                + qunartrainpayUrl);
        String payurl = this.qunartrainpayUrl + "ProcessGoPay.do";
        //取数据库获取数据结束

        //订单12306总价
        float price = 0f;
        //订单12306账号
        String un = trainorder.getSupplyaccount().split("/")[0];
        //订单备注
        String comment = "";
        //签名字段
        String HMAC = "";
        for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                price += trainticket.getPrice();
            }
        }

        try {
            HMAC = ElongHotelInterfaceUtil.MD5(key + merchantCode + orderNo + price + un + comment).toUpperCase();
        }
        catch (Exception e1) {
        }
        param = "merchantCode=" + merchantCode + "&orderNo=" + orderNo + "&price=" + price + "&un=" + un + "&comment="
                + comment + "&HMAC=" + HMAC;
        WriteLog.write("QunarTrainPay_doPost", "请求参数:" + param);
        WriteLog.write("QUNAR火车票接口_3.5代付接口", trainorder.getId() + ":backurl:" + payurl + ":parm:data=" + param);
        WriteLog.write("QunarTrainPay_doPost", "请求地址:" + payurl);
        String ret = SendPostandGet.submitPost(payurl, param, "utf-8").toString();
        WriteLog.write("QUNAR火车票接口_3.5代付接口", trainorder.getId() + ":qunar返回:" + ret);
        WriteLog.write("QunarTrainPay_doPost", "代付返回:" + ret);
        JSONObject jsoret = JSONObject.parseObject(ret);
        int i = 0;
        if ("true".equalsIgnoreCase(jsoret.getString("ret"))) {
            ret = "success";
        }
        else {
            while (i < 5) {
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                WriteLog.write("QUNAR火车票接口_3.5代付接口", trainorder.getId() + ":backurl:" + qunartrainpayUrl
                        + ":parm:data=" + param);
                ret = SendPostandGet.submitPost(qunartrainpayUrl, param, "utf-8").toString();
                WriteLog.write("QUNAR火车票接口_3.5代付接口", trainorder.getId() + ":qunar返回:" + ret);
                jsoret = JSONObject.parseObject(ret);
                if ("true".equalsIgnoreCase(jsoret.getString("ret"))) {
                    ret = "success";
                    i = 5;
                }
                else {
                    i++;
                }
            }
        }
        return ret;
    }
}

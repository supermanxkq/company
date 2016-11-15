package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;

/**
 * 占座回调接口
 * @time 2015年7月10日15:53:04
 * @author luoqingxin
 */
@SuppressWarnings("serial")
public class QunarCallBackServlet extends HttpServlet {

    QunarTrainOrderCallBack qunarTrainOrderCallBack;

    public void init() throws ServletException {
        super.init();
        this.qunarTrainOrderCallBack = new QunarTrainOrderCallBack();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String zhanzuojieguoBackUrl = "";
        String key = "";
        PrintWriter out = null;
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        String result = "{\"ret\":false,\"errCode\":999,\"errMsg\":\"nodata\"}";
        int r1 = new Random().nextInt(10000);

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
                obj.put("ret", false);
                obj.put("errCode", "101");
                obj.put("errMsg", "请求参数为空");
                result = obj.toString();
            }
            else {
                WriteLog.write("QUNAR火车票接口_占座回调", r1 + ":" + param);
                JSONObject json = JSONObject.parseObject(param);
                //请求方法
                String method = json.getString("method");

                if ("train_order_callback".equals(method)) {
                    Long trainorderid = json.getLong("trainorderid");
                    WriteLog.write("QUNAR火车票接口_占座回调", r1 + ":trainorderid:" + trainorderid);
                    if (trainorderid > 0) {
                        String returnmsg = json.getString("returnmsg");
                        String Returnmsg = URLDecoder.decode(returnmsg, "utf-8");
                        String merchantCode = "";
                        WriteLog.write("QUNAR火车票接口_占座回调", r1 + ":returnmsg:" + Returnmsg);
                        try {
                            Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(trainorderid);
                            String orderNo = trainorder.getQunarOrdernumber();
                            WriteLog.write("QUNAR火车票接口_占座回调", r1 + ":orderNo:" + orderNo);
                            //取数据库获取数据开始
                            int index = orderNo.indexOf("1");
                            String str = orderNo.substring(0, index);
                            String sqlstr = "SELECT top 1  * FROM QunarTrainMerchantInfo WHERE MerchantCode like '%"
                                    + str + "%'";
                            List sqlResultList = Server.getInstance().getSystemService()
                                    .findMapResultBySql(sqlstr, null);
                            for (int i = 0; i < sqlResultList.size(); ++i) {
                                Map map = (Map) sqlResultList.get(i);
                                key = map.get("MerchantKey").toString();
                                merchantCode = map.get("MerchantCode").toString();
                            }
                            WriteLog.write("qunar参数", "MerchantKey:" + key + ":zhanzuojieguoBackUrl");
                            //取数据库获取数据结束
                            zhanzuojieguoBackUrl = qunarCallbackUrl(trainorderid);
                            WriteLog.write("QunarCallBackServlet_zhanzuojieguoBackUrl", zhanzuojieguoBackUrl);
                            result = qunarTrainOrderCallBack.train_order_callback(trainorder, merchantCode, Returnmsg,
                                    key, zhanzuojieguoBackUrl);
                        }
                        catch (Exception e) {
                            JSONObject obj = new JSONObject();
                            obj.put("ret", false);
                            obj.put("errCode", "314");
                            obj.put("errMsg", "连接异常");
                            result = obj.toString();
                            e.printStackTrace();
                        }
                        if ("returnmsg:true".equals(result)) {
                            result = "success";
                        }
                    }
                }
                else {
                    JSONObject obj = new JSONObject();
                    obj.put("ret", false);
                    obj.put("errCode", "101");
                    obj.put("errMsg", "请求接口功能错误");
                    result = obj.toString();
                }
            }
        }
        catch (Exception e) {
            JSONObject obj = new JSONObject();
            obj.put("ret", false);
            obj.put("errCode", "999");
            obj.put("errMsg", "未知异常");
            result = obj.toString();
        }
        finally {
            WriteLog.write("QUNAR火车票接口_占座回调", r1 + ":" + result);
            if (out != null) {
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

    public static void main(String[] args) {
        try {
            String returnmsg = "%E8%BD%A6%E6%AC%A1%5B6233%5D7%E6%9C%8818%E6%97%A58%E7%82%B9%E8%B5%B7%E5%94%AE%E3%80%82";
            returnmsg = URLDecoder.decode(returnmsg, "utf-8");
            System.out.println(returnmsg);
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //        callBackQunarOrdered(4002, "true");
    }

    public static String callBackQunarOrdered(long orderid, String returnmsg) {
        String result = "false";
        String url = "http://211.103.207.133/cn_interface/qunarTrainCallBack";
        try {
            returnmsg = URLEncoder.encode(returnmsg, "utf-8");
        }
        catch (Exception e) {
        }
        JSONObject jso = new JSONObject();
        jso.put("trainorderid", orderid);
        jso.put("method", "train_order_callback");
        jso.put("returnmsg", returnmsg);
        jso.put("merchantCode", "bjxyw");
        try {
            result = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 到DB中查找回调地址
     * 
     * @param orderid
     * @return
     * @time 2015年7月15日 下午2:43:37
     * @author Administrator
     */
    private String qunarCallbackUrl(long orderid) {
        String callbackurl = "";
        String sql = "SELECT top 1 CreateOrderCallbackUrl url from QunarTrainCallbackInfo where OrderId=" + orderid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            callbackurl = map.get("url").toString();
        }
        return callbackurl;
    }
}

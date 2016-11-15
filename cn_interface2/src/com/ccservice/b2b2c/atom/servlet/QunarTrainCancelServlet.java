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

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.QunarTrain.QunarCancelTrain;

/**
 * 取消占座
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class QunarTrainCancelServlet extends HttpServlet {
    public String key;//qunar提供的秘钥

    QunarCancelTrain qunarcanceltrain;

    public void init() throws ServletException {
        super.init();
        qunarcanceltrain = new QunarCancelTrain();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        res.setContentType("text/plain; charset=utf-8");
        Long starttime = System.currentTimeMillis();
        int r1 = new Random().nextInt(10000000);
        String result = "";
        PrintWriter out = null;
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        try {
            out = res.getWriter();
            String orderNo = req.getParameter("orderNo") != null ? req.getParameter("orderNo") : "";
            WriteLog.write("QUNAR火车票接口_取消占座", "orderNo:" + orderNo);
            //取数据库获取数据开始
            int index = orderNo.indexOf("1");
            String str = orderNo.substring(0, index);
            String sqlstr = "SELECT top 1  * FROM QunarTrainMerchantInfo WHERE MerchantCode like '%" + str + "%'";
            List sqlResultList = Server.getInstance().getSystemService().findMapResultBySql(sqlstr, null);
            for (int i = 0; i < sqlResultList.size(); ++i) {
                Map map = (Map) sqlResultList.get(i);
                this.key = map.get("MerchantKey").toString();
            }
            WriteLog.write("qunar参数", "MerchantKey:" + key);

            //请求来源
            String reqFrom = req.getParameter("reqFrom") != null ? req.getParameter("reqFrom") : "";
            //请求时间
            String reqTime = req.getParameter("reqTime") != null ? req.getParameter("reqTime") : "";
            //                加密
            String HMAC = req.getParameter("HMAC") != null ? req.getParameter("HMAC") : "";
            try {

                WriteLog.write("QUNAR火车票接口_取消占座", r1 + ":业务参数：" + "orderNo:" + orderNo + "reqFrom:" + reqFrom
                        + "reqTime:" + reqTime + "HMAC:" + HMAC);
                result = qunarcanceltrain.operate(orderNo, reqFrom, reqTime, HMAC, r1, key);
                WriteLog.write("QUNAR火车票接口_取消占座", r1 + ":时间:" + (System.currentTimeMillis() - starttime)
                        + ":method:train_order:result:" + result);
            }
            catch (Exception e) {
                e.printStackTrace();
                JSONObject obj = new JSONObject();
                obj.put("errCode", "999");
                obj.put("ret", false);
                obj.put("errMsg", "数据有误");
                result = obj.toString();
                WriteLog.write("QUNAR火车票接口_取消占座_异常", r1 + ":" + ElongHotelInterfaceUtil.errormsg(e));
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
            //日志
            WriteLog.write("QUNAR火车票接口_取消占座_异常", r1 + ":" + ElongHotelInterfaceUtil.errormsg(e));
        }
        finally {
            if (out != null) {
                WriteLog.write("QUNAR火车票接口_取消占座_异常", r1 + ":reslut:" + result);
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

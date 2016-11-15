package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.QunarTrain.Thread.MyThreadQunarPayResult;
import com.ccservice.b2b2c.base.train.Trainorder;

/**
 * 代付回调
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class QunarTrainPayCallBackServlet extends HttpServlet {
    public String key;

    public String merchantCode;

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
        out = res.getWriter();
        try {
            String orderNo = req.getParameter("orderNo");
            //取数据库获取数据开始
            int index = orderNo.indexOf("1");
            WriteLog.write("QUNAR火车票接口_代付回调", "orderNo:" + orderNo);
            String str = orderNo.substring(0, index);
            String sqlstr = "SELECT top 1  * FROM QunarTrainMerchantInfo WHERE MerchantCode like '%" + str + "%'";
            List sqlResultList = Server.getInstance().getSystemService().findMapResultBySql(sqlstr, null);
            for (int i = 0; i < sqlResultList.size(); ++i) {
                Map map = (Map) sqlResultList.get(i);
                this.merchantCode = map.get("MerchantCode").toString();
                this.key = map.get("MerchantKey").toString();
            }
            WriteLog.write("qunar参数", "MerchantCode:" + merchantCode + ";MerchantKey:" + key);
            //取数据库获取数据结束
            String payStatus = req.getParameter("payStatus");
            String HMAC = req.getParameter("HMAC");
            WriteLog.write("QUNAR火车票接口_代付回调", r1 + ":" + orderNo + payStatus + HMAC);
            if (ElongHotelInterfaceUtil.StringIsNull(orderNo) || ElongHotelInterfaceUtil.StringIsNull(payStatus)
                    || ElongHotelInterfaceUtil.StringIsNull(HMAC)) {
                JSONObject obj = new JSONObject();
                obj.put("ret", false);
                obj.put("msg", "请求参数为空");
                result = obj.toString();
            }
            else {
                //请求方法
                String myHmac = ElongHotelInterfaceUtil.MD5(this.key + this.merchantCode + orderNo + payStatus);
                if (!HMAC.equalsIgnoreCase(myHmac)) {
                    JSONObject obj = new JSONObject();
                    obj.put("ret", false);
                    obj.put("msg", "安全校验未通过");
                    result = obj.toString();
                }
                else {
                    WriteLog.write("QUNAR火车票接口_代付回调", r1 + ":trainorderid:" + orderNo);
                    if ("".equals(orderNo)) {
                        JSONObject obj = new JSONObject();
                        obj.put("ret", false);
                        obj.put("msg", "订单号不能为空");
                        result = obj.toString();
                    }
                    else {
                        String sql = "SELECT ID FROM T_TRAINORDER WHERE C_QUNARORDERNUMBER='" + orderNo + "'";
                        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                        if (list.size() != 1) {
                            JSONObject obj = new JSONObject();
                            obj.put("ret", false);
                            obj.put("msg", "该订单号:" + orderNo + (list.size() == 0 ? "不存在" : "不唯一,无法匹配"));
                            result = obj.toString();
                        }
                        else {
                            Map map = (Map) list.get(0);
                            long orderid = Long.valueOf(map.get("ID").toString());
                            qunarPayEnd(orderid, payStatus);
                            JSONObject obj = new JSONObject();
                            obj.put("ret", true);
                            result = obj.toString();
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            JSONObject obj = new JSONObject();
            obj.put("ret", false);
            obj.put("msg", "未知异常");
            result = obj.toString();
        }
        finally {
            WriteLog.write("QUNAR火车票接口_代付回调", r1 + ":" + result);
            if (out != null) {
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 处理qunar返回支付状态 
     * @param orderid
     * @param payStatus
     * @time 2015年1月20日 上午10:45:02
     * @author fiend
     */
    public void qunarPayEnd(long orderid, String payStatus) {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Thread t1 = null;
        t1 = new MyThreadQunarPayResult(orderid, payStatus);
        pool.execute(t1);
        pool.shutdown();
    }

    public static void main(String[] args) {
        try {
            new MyThreadQunarPayResult(4546l, "3").run();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

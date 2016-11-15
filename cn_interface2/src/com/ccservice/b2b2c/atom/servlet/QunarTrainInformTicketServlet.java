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
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 去哪儿 通知出票
 * @time 2015年7月13日10:30:06
 * @author luoqingxin
 **/

public class QunarTrainInformTicketServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public String key;

    public String merchantCode;

    public String qunarurl;

    QunarTrainInformTicket qunartraininformticket;

    public void init() throws ServletException {
        super.init();
        this.qunartraininformticket = new QunarTrainInformTicket();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        String result = "{\"ret\":true}";
        WriteLog.write("QUNAR火车票接口_通知出票", "进入接口:" + result);
        long starttime = System.currentTimeMillis();
        int r1 = new Random().nextInt(10000000);
        PrintWriter out = null;
        try {
            out = res.getWriter();
            try {
                String orderNo = req.getParameter("orderNo") != null ? req.getParameter("orderNo") : "";//qunar订单号
                WriteLog.write("QUNAR火车票接口_通知出票:", "orderNo:" + orderNo);
                //取数据库获取数据开始
                int index = orderNo.indexOf("1");
                String str = orderNo.substring(0, index);
                String sqlstr = "SELECT top 1  * FROM QunarTrainMerchantInfo WHERE MerchantCode like '%" + str + "%'";
                List sqlResultList = Server.getInstance().getSystemService().findMapResultBySql(sqlstr, null);
                for (int i = 0; i < sqlResultList.size(); ++i) {
                    Map map = (Map) sqlResultList.get(i);
                    this.key = map.get("MerchantKey").toString();
                    this.merchantCode = map.get("MerchantCode").toString();
                    this.qunarurl = map.get("QunarUrl").toString();
                }
                WriteLog.write("qunar参数", "MerchantKey:" + key + ";merchantCode:" + merchantCode + ";qunarurl:"
                        + qunarurl);
                String payurl = this.qunarurl + "ProcessGoPay.do";
                //取数据库获取数据结束
                String Type = req.getParameter("Type") != null ? req.getParameter("Type") : "";//通知类型
                String reqFrom = req.getParameter("reqFrom") != null ? req.getParameter("reqFrom") : "";// 请求来源
                String reqTime = req.getParameter("reqTime") != null ? req.getParameter("reqTime") : ""; //请求时间
                String HMAC = req.getParameter("HMAC") != null ? req.getParameter("HMAC") : "";//加密
                String HMACflag = ElongHotelInterfaceUtil.MD5(this.key + orderNo + Type + reqFrom + reqTime)
                        .toUpperCase();
                WriteLog.write("QUNAR火车票接口_通知出票", "HMAC:" + HMAC + ":HMACflag:" + HMACflag);
                if (HMACflag.equals(HMAC)) {
                    if (ElongHotelInterfaceUtil.StringIsNull(orderNo) || ElongHotelInterfaceUtil.StringIsNull(Type)
                            || ElongHotelInterfaceUtil.StringIsNull(reqFrom)
                            || ElongHotelInterfaceUtil.StringIsNull(reqTime)) {
                        JSONObject obj = new JSONObject();
                        obj.put("errCode", "111");

                        obj.put("ret", false);
                        obj.put("errMsg", "业务参数缺失");
                        result = obj.toString();
                        WriteLog.write("QUNAR火车票接口_通知出票", r1 + ":秘钥验证错误:result:" + result);
                        WriteLog.write("QUNAR火车票接口_通知出票", r1 + ":业务参数：" + "param:orderNo" + orderNo + "Type" + Type
                                + "reqFrom" + reqFrom + "reqTime" + reqTime + "HMAC" + HMAC + "HMACflag" + HMACflag);
                    }
                    else {
                        result = qunartraininformticket.InformTicket(orderNo, Type, reqFrom, reqTime, this.key,
                                this.merchantCode, payurl);
                        WriteLog.write("QUNAR火车票接口_通知出票", r1 + ":业务参数：" + "param:orderNo" + orderNo + "Type" + Type
                                + "reqFrom" + reqFrom + "reqTime" + reqTime + "HMAC" + HMAC + "HMACflag" + HMACflag);
                    }
                    WriteLog.write("QUNAR火车票接口_通知出票", r1 + ":时间:" + (System.currentTimeMillis() - starttime)
                            + ":method:train_order:result:" + result);
                }
                else {
                    JSONObject obj = new JSONObject();
                    obj.put("ret", true);
                    result = obj.toString();
                    WriteLog.write("QUNAR火车票接口_通知出票", r1 + ":秘钥验证错误:result:" + result);
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("QUNAR通知出票接口", e);
                JSONObject obj = new JSONObject();
                obj.put("ret", true);
                result = obj.toString();
                WriteLog.write("QUNAR火车票接口_通知出票", r1 + ":" + ElongHotelInterfaceUtil.errormsg(e));
            }
            //            }
        }
        catch (Exception e) {
            e.printStackTrace();
            JSONObject obj = new JSONObject();
            obj.put("ret", true);
            result = obj.toString();
            WriteLog.write("QUNAR火车票接口_通知出票_异常", r1 + ":" + ElongHotelInterfaceUtil.errormsg(e));
        }
        finally {
            if (out != null) {
                WriteLog.write("QUNAR火车票接口_通知出票", r1 + ":reslut:" + result);
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }
}

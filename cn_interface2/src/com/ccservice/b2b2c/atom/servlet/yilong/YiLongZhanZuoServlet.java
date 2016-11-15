package com.ccservice.b2b2c.atom.servlet.yilong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.tuniu.method.TuNiuServletUtil;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.sun.java_cup.internal.runtime.virtual_parse_stack;

/**
 * 艺龙先占座后支付接口
 * 
 * @time 2016年5月22日 下午1:00:18
 * @author 杨荣强
 */
public class YiLongZhanZuoServlet extends HttpServlet {
    String key = "";

    private final String logname = "MeiTuan_3_艺龙先占座后支付接口";

    Map<String, InterfaceAccount> interfaceAccountMap = new HashMap<String, InterfaceAccount>();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        int r1 = new Random().nextInt(10000000);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        final AsyncContext ctx = request.startAsync();
        ctx.setTimeout(50000L);
        ServletRequest req = ctx.getRequest();
        String merchantId = req.getParameter("merchantId");
        String timeStamp = req.getParameter("timeStamp");
        String orderId = req.getParameter("orderId");
        String paramJson = req.getParameter("paramJson");
        String sign = req.getParameter("sign");
        WriteLog.write(logname, r1 + "----->merchantId:" + merchantId + "----->timeStamp:" + timeStamp
                + "----->orderId:" + orderId + "----->paramJson:" + paramJson + "----->sign:" + sign);
        InterfaceAccount interfaceAccount = interfaceAccountMap.get("merchantId");
        if (interfaceAccount == null) {
            interfaceAccount = getInterfaceAccountByLoginname(merchantId);
            if (interfaceAccount != null && interfaceAccount.getKeystr() != null
                    && interfaceAccount.getInterfacetype() != null) {
                interfaceAccountMap.put("merchantId", interfaceAccount);
            }
        }
        WriteLog.write(logname, r1 + "----->merchantId:" + merchantId + "----->timeStamp:" + timeStamp
                + "----->orderId:" + orderId + "----->paramJson:" + paramJson + "----->sign:" + sign);
        String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId
                + "&paramJson=" + paramJson;
        this.key = interfaceAccount.getKeystr();
        localSign = getSignMethod(localSign) + this.key;
        WriteLog.write(logname, r1 + "----->传值：" + localSign + "----->获取key" + this.key);
        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        WriteLog.write(logname, r1 + "----->相比较：" + localSign + ":==:" + sign);
        if (localSign.equals(sign)) {
            WriteLog.write(logname, r1 + "----->对比两个签名结果");
            JSONObject json = new JSONObject();
            json.put("retcode", "200");
            getResponeOut(ctx, json.toString(), logname);
            return;
        }
        else {
            JSONObject json = new JSONObject();
            json.put("retcode", "403");
            json.put("retdesc", "签名校验失败");
            getResponeOut(ctx, json.toString(), logname);
            return;
        }

    }

    private InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        WriteLog.write(logname, "----->loginname:" + loginname);
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + loginname + "'", null, -1, 0);
        }
        catch (Exception e) {
        }
        WriteLog.write(logname, "----->list_interfaceAccount:" + list_interfaceAccount.size());
        if (list_interfaceAccount.size() > 0) {
            interfaceAccount = list_interfaceAccount.get(0);
        }
        else {
            interfaceAccount.setUsername(loginname);
            interfaceAccount.setKeystr(this.key);
            interfaceAccount.setInterfacetype(TrainInterfaceMethod.YILONG1);
        }
        return interfaceAccount;
    }

    private static String getSignMethod(String sign) {
        if (!ElongHotelInterfaceUtil.StringIsNull(sign)) {
            String[] signParam = sign.split("&");
            sign = ElongHotelInterfaceUtil.sort(signParam);
            return sign;
        }
        return "";
    }

    public void getResponeOut(AsyncContext ctx, String result, String logName) {
        try {
            ServletResponse response = ctx.getResponse();
            //编码
            response.setCharacterEncoding("UTF-8");
            //输出
            response.getWriter().write(result);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_" + logName, e);
        }
        finally {
            try {
                ctx.complete();
            }
            catch (Exception e) {
            }
        }
    }
}

package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.Random;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.payChange.CallBackMethodResultAliPayResult;

/**
 * 高改支付结果回调处理
 * @time 2015年12月30日 下午7:48:56
 * @author 彩娜
 */
@SuppressWarnings("serial")
public class TongChengChangePayResultCallBack extends HttpServlet implements Servlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        //日志名
        String logName = "TongChengChangePayResultCallBack";
        //随机数
        int random = new Random().nextInt(9000000) + 1000000;
        //请求数据
        String cmd = request.getParameter("cmd");
        String data = request.getParameter("data");
        String orderid = request.getParameter("orderid");
        //记录日志
        WriteLog.write(logName, random + "-->orderid:" + orderid + "-->cmd:" + cmd + "-->请求:" + data);
        //返回结果
        String backstr = "";
        //数据非空
        if (!ElongHotelInterfaceUtil.StringIsNull(data)) {
            //处理方法
            CallBackMethodResultAliPayResult method = new CallBackMethodResultAliPayResult();
            //支付结果回调
            if ("payresult".equals(cmd)) {
                long changeId = Long.valueOf(orderid);
                String payset = request.getParameter("payset");
                String paymethodtype = request.getParameter("paymethodtype");
                //银联
                if ("1".equals(paymethodtype)) {
                    //暂不支持
                }
                else {
                    backstr = method.parsePayResult(data, changeId, random, payset, paymethodtype);
                }
            }
            //支付链接回调
            else if ("payurl".equals(cmd)) {
                backstr = method.parsePayUrlResult(data, Long.valueOf(orderid), random);
            }
            //超时处理
            else if ("timeout".equals(cmd)) {
                backstr = method.payTimeOut(data, Long.valueOf(orderid), random);
            }
        }
        WriteLog.write(logName, random + "-->orderid:" + orderid + "-->cmd:" + cmd + "-->请求:" + backstr);
        //输出结果
        PrintWriter out = response.getWriter();
        out.print(backstr);
        out.flush();
        out.close();
    };
}
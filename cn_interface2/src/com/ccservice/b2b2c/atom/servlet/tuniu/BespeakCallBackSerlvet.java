package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;

/**
 * 约票结果统一回调
 * @time 2015年12月7日 下午1:56:53
 */
@SuppressWarnings("serial")
public class BespeakCallBackSerlvet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        String result = "";
        PrintWriter out = null;
        try {
            out = res.getWriter();
            String orderid = req.getParameter("orderid");
            String flag = req.getParameter("flag");
            String refuseMsg = URLDecoder.decode(req.getParameter("refuseMsg"), "utf-8");
            WriteLog.write("约票结果统一回调", "orderid:" + orderid + ";flag:" + flag + ";refuseMsg:" + refuseMsg);
            if (orderid.equals("") || orderid == null || flag.equals("") || flag == null) {
                result = "false";
            }
            else {
                toCallBackTaoBao(orderid, flag, refuseMsg);
                result = "success";
            }
            WriteLog.write("约票结果统一回调", "orderid:" + orderid + ";同步回调参数:" + result);
        }
        catch (Exception e) {
            result = "false";
        }
        finally {
            out.print(result);
            out.flush();
            out.close();
        }
    }

    public void toCallBackTaoBao(String orderid, String flag, String refuseMsg) {
        new BespeakCallBack(orderid, flag, refuseMsg).start();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}

package com.ccservice.b2b2c.atom.servlet.tb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.Request;

public class TaobaoSendSmsServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        int r1 = new Random().nextInt(10000000);
        Long starttime = System.currentTimeMillis();
        String result = "";
        PrintWriter out = null;
        String json = "";
        String mima = "";
        try {
            out = res.getWriter();
            String mobileNumber = req.getParameter("mobile");//获取到传过来的需要发送短信的手机号
            WriteLog.write("TaobaoSendSmsServlet", r1 + ":mobileNumber:" + mobileNumber);
            TaobaoHotelInterfaceUtil taobaoHotelInterfaceUtil = new TaobaoHotelInterfaceUtil();
            if (mobileNumber != null && mobileNumber.length() == 11) {
                //#TODO mark#chendong
                //这个方法是让淘宝发短信的方法,调用的方法写好需要解析一下格式返回true|false
                result = taobaoHotelInterfaceUtil.Sendsms(mobileNumber);
            }
            else {
                result = "mobile_ERR";
            }
            WriteLog.write("TaobaoSendSmsServlet", r1 + ":jsonStr:" + mima + ":" + result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                WriteLog.write("TaobaoSendSmsServlet", r1 + ":reslut:" + result);
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }
}

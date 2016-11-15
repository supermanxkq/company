package com.ccservice.b2b2c.atom.mobileApp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class TongchengPhoneSMSServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;
        String result = "false";
        try {
            out = response.getWriter();
            String jsonStr = request.getParameter("jsonStr");
            if (!ElongHotelInterfaceUtil.StringIsNull(jsonStr)) {
                JSONObject json = JSONObject.parseObject(jsonStr);
                String src = json.containsKey("src") ? json.getString("src") : "";
                String dest = json.containsKey("dest") ? json.getString("dest") : "";
                String message = json.containsKey("message") ? json.getString("message") : "";
                String recvtime = json.containsKey("recvtime") ? json.getString("recvtime") : "";
                System.out.println("短信发送方号码:" + src + "  短信接收方号码:" + dest + " 短信内容:" + message + " 短信接收到的时间:"
                        + recvtime);
                if (!ElongHotelInterfaceUtil.StringIsNull(src) && !ElongHotelInterfaceUtil.StringIsNull(dest)
                        && !ElongHotelInterfaceUtil.StringIsNull(message)
                        && !ElongHotelInterfaceUtil.StringIsNull(recvtime)) {
                    result = "true";
                    System.out.println("短信发送方号码:" + src + "  短信接收方号码:" + dest + " 短信内容:" + message + " 短信接收到的时间:"
                            + recvtime);
                    String url = "http://localhost:8080/cn_interface/TongchengPhoneSMSServlet";
                }
            }
        }
        
        
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            out.print(result);
            out.flush();
            out.close();
        }
    }
}
package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class QunarBindPhoneCallBackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        String result = "{\"ret\":false,\"errCode\":999,\"errMsg\":\"nodata\"}";
        PrintWriter out = null;
        JSONObject obj = new JSONObject();
        try {
            out = response.getWriter();
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }
            String param = buf.toString();
            WriteLog.write("QunarBindPhone_BindPhoneCallBack", "param:" + param);
            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
                obj.put("ret", false);
                obj.put("msg", "无数据");
                result = obj.toString();
            }
            else {
                JSONObject jsons = JSONObject.parseObject(param);
                String un = jsons.containsKey("un") ? jsons.getString("un") : "";
                String Status = jsons.containsValue("status") ? jsons.getString("status") : "";
                String errMsg = jsons.getString("errMsg");

                if (ElongHotelInterfaceUtil.StringIsNull(un) || ElongHotelInterfaceUtil.StringIsNull(Status)) {
                    obj.put("ret", false);
                    obj.put("msg", "数据参数为空");
                    result = obj.toString();
                }
                else {
                    if ("1".equals(Status)) {
                        obj.put("ret", true);
                        result = obj.toString();
                    }
                    else if ("0".equals(Status)) {
                        obj.put("ret", false);
                        result = obj.toString();
                    }
                    else {
                        obj.put("ret", false);
                        result = obj.toString();
                    }
                }
            }
        }
        catch (Exception e) {
            // TODO: handle exception
        }
        finally {
            out.print(result);
            out.flush();
            out.close();
        }
    }
}

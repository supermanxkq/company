package com.travelGuide;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;

/**
 * 12306返回的车次
 * @time 2015年5月21日 下午2:04:18
 * @author baiyushan
 */
public class TravelGuide12306BackTrainCode extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public TravelGuide12306BackTrainCode() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/plain;charset = utf-8");
        response.setHeader("content-Type", "text/html;charset = utf-8");
        int r1 = new Random().nextInt(100000000);
        JSONObject obj = new JSONObject();
        JSONObject jsonO = null;
        PrintWriter out = response.getWriter();
        String jsonStr = "";
        String resultOut = "";
        try {
            //获取请求的json字符串
            jsonStr = request.getParameter("json");
            //写入日志
            WriteLog.write("12306返回的车次请求接口：", r1 + "json:" + jsonStr);
            if ("".equals(jsonStr)) {
                obj.put("success", false);
                obj.put("code", "101");
                obj.put("msg", "传入的JsonStr对象为空");
                resultOut = obj.toString();
            }
            else {
                jsonO = JSONObject.parseObject(jsonStr);
                if (!jsonO.containsKey("")) {

                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}

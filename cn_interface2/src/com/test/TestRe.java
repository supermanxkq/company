package com.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSONObject;

public class TestRe extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    @SuppressWarnings({ "rawtypes" })
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        PrintWriter out = null;
        Date date = new Date();
        String time = format.format(date);
        String result = "";
        JSONObject resultJsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Set<Integer> set = TomcatStatusMem.getTomcatStatusMethods().keySet();
        for (Integer i : set) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", TomcatStatusMem.getTomcatStatusMethods().get(i).getTomcatUrl());
            jsonObject.put("name", TomcatStatusMem.getTomcatStatusMethods().get(i).getTomcatName());
            jsonObject.put("status", TomcatStatusMem.getTomcatStatusMethods().get(i).isTomcatStatus());
            Date date2 = TomcatStatusMem.getTomcatStatusMethods().get(i).getErrTimeDate();
            jsonObject.put("date", date2 == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date2));
            jsonArray.add(jsonObject);
        }
        resultJsonObject.put("tomcat", jsonArray);
        try {
            out = res.getWriter();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            result = resultJsonObject.toString();
            out.print(result);
            out.flush();
            out.close();
        }

    }
}
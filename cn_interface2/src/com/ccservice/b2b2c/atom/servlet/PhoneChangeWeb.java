package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.PhoneChangeWebMb;
import com.ccservice.b2b2c.atom.server.PhoneChangeWebs;


/**
 * Servlet implementation class PhoneChangeWeb
 */
public class PhoneChangeWeb extends HttpServlet {
	private static final long serialVersionUID = 1L;



    public PhoneChangeWeb() {
        super();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        String json = request.getParameter("json");
        JSONObject jsonjie = JSONObject.parseObject(json);
        String msg = jsonjie.containsKey("obj") ? jsonjie.getString("obj") : "";
      
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        String date = df.format(new Date());
        PhoneChangeWebs pcb = new PhoneChangeWebs(date, getIpAddr(request), msg);
        if (PhoneChangeWebMb.hb.containsKey(date)) {

            PhoneChangeWebMb.hb.get(date).add(pcb);
        }
        else if (!PhoneChangeWebMb.hb.containsKey(date)) {
            ArrayList<PhoneChangeWebs> list = new ArrayList<PhoneChangeWebs>();
            list.add(pcb);
            PhoneChangeWebMb.hb.put(date, list);
        }

	    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}

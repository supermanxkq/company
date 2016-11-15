package com.ccservice.b2b2c.atom.qunar;

import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import com.ccservice.huamin.WriteLog;
import com.ccservice.elong.inter.PropertyUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.server.Server;

@SuppressWarnings("serial")
public class FindChangePriceHotel extends HttpServlet {

    @SuppressWarnings("rawtypes")
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version='1.0' encoding='utf-8'?>");
        sb.append("<changed>");
        sb.append("<hotels>");
        PrintWriter out = response.getWriter();
        try {
            String time = request.getParameter("lastupdate");//秒
            Long lastupdate = Long.parseLong(time);
            Date date = new Date(lastupdate * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = new String(PropertyUtil.getValue("pushSql").getBytes("iso8859-1"), "utf-8");
            sql = sql.replace("QunarRequestTime", sdf.format(date));
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    try {
                        String hotelid = ((Map) list.get(i)).get("hotelid").toString();
                        String updatetime = ((Map) list.get(i)).get("updatetime").toString();
                        updatetime = sdf.parse(updatetime).getTime() / 1000 + "";
                        sb.append("<hotel id='" + hotelid + "' updatetime='" + updatetime + "'/>");
                    }
                    catch (Exception e) {
                    }
                }
            }
        }
        catch (Exception e) {
            StackTraceElement stack = e.getStackTrace()[0];
            WriteLog.write("去哪儿推送接口",
                    "异常信息：" + e.getMessage() + "；类名：" + stack.getFileName() + "；方法名：" + stack.getMethodName() + "；行数："
                            + stack.getLineNumber());
        }
        sb.append("</hotels>");
        sb.append("</changed>");
        out.write(sb.toString());
        out.flush();
        out.close();
    }
}

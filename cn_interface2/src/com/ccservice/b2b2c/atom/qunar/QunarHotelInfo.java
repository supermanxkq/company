package com.ccservice.b2b2c.atom.qunar;

import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

@SuppressWarnings("serial")
public class QunarHotelInfo extends HttpServlet {

    @SuppressWarnings("rawtypes")
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        //请求IP
        String ip = "";
        if (request.getHeader("X-real-ip") == null) {
            ip = request.getRemoteAddr();
        }
        else {
            ip = request.getHeader("X-real-ip");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date()) + "=====去哪儿酒店列表接口，请求IP：" + ip);
        //设置编码方式
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String outputstr = "";
        //缓存
        String type = request.getParameter("type");//ReloadData：更新缓存、暂无用
        //MemcachedClient cache = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
        Object obj = null;//cache.get("str");
        if (obj == null || "YdxReloadData".equals(type)) {
            //SQL
            String sql = new String(PropertyUtil.getValue("jlHotelSql").getBytes("iso8859-1"), "utf-8");
            List hotels = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            System.out.println(sdf.format(new Date()) + "=====去哪儿酒店列表接口，酒店数量：" + hotels.size());
            //酒店备注
            String remark_a = new String(PropertyUtil.getValue("remark_a").getBytes("iso8859-1"), "utf-8");
            String remark_b = new String(PropertyUtil.getValue("remark_b").getBytes("iso8859-1"), "utf-8");
            String remark_c = new String(PropertyUtil.getValue("remark_c").getBytes("iso8859-1"), "utf-8");
            String remark_d = new String(PropertyUtil.getValue("remark_d").getBytes("iso8859-1"), "utf-8");
            String remark_e = new String(PropertyUtil.getValue("remark_e").getBytes("iso8859-1"), "utf-8");
            //发票快递费
            String nationwidefee = new String(PropertyUtil.getValue("nationwidefee").getBytes("iso8859-1"), "utf-8");
            String cityfee = new String(PropertyUtil.getValue("cityfee").getBytes("iso8859-1"), "utf-8");
            //拼XML
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version='1.0' encoding='utf-8'?>");
            sb.append("<list>");
            for (int i = 0; i < hotels.size(); i++) {
                try {
                    Map map = (Map) hotels.get(i);
                    String id = map.get("ID").toString();
                    String name = map.get("C_NAME").toString().trim();
                    String C_QUNARID = map.get("C_QUNARID").toString();
                    String C_ADDRESS = map.get("C_ADDRESS") == null ? "" : map.get("C_ADDRESS").toString();
                    String C_MARKETTELL = map.get("C_MARKETTELL") == null ? "" : map.get("C_MARKETTELL").toString();
                    String C_TORTELL = map.get("C_TORTELL") == null ? "" : map.get("C_TORTELL").toString();
                    //城市、地址、电话
                    String city = C_QUNARID.substring(0, C_QUNARID.lastIndexOf("_")).trim();
                    String address = ElongHotelInterfaceUtil.StringIsNull(C_ADDRESS) ? "" : C_ADDRESS.trim();
                    String tel = ElongHotelInterfaceUtil.StringIsNull(C_MARKETTELL) ? "" : C_MARKETTELL.trim();
                    if (ElongHotelInterfaceUtil.StringIsNull(tel)) {
                        tel = ElongHotelInterfaceUtil.StringIsNull(C_TORTELL) ? "" : C_TORTELL.trim();
                    }
                    //酒店信息
                    sb.append("<hotel id='" + id + "' city='" + city + "' name='" + name + "' address='" + address
                            + "' tel='" + tel + "'>");
                    //发票，旅行社发票
                    sb.append("<invoice type='0' content='1' dispatch='1' nationwidefee='" + nationwidefee
                            + "' cityfee='" + cityfee + "' province='北京' city='北京市' desc='0,1'/>");
                    //退款信息，不可退款
                    sb.append("<refund type='0'/>");
                    //备注
                    sb.append("<remarks>");
                    sb.append("<remark value='" + remark_a + "'/>");
                    sb.append("<remark value='" + remark_b + "'/>");
                    sb.append("<remark value='" + remark_c + "'/>");
                    sb.append("<remark value='" + remark_d + "'/>");
                    sb.append("<remark value='" + remark_e + "'/>");
                    sb.append("</remarks>");
                    sb.append("</hotel>");
                }
                catch (Exception e) {
                }
            }
            sb.append("</list>");
            //cache.set("str", 3600*3, sb.toString());//秒
            outputstr = sb.toString();
        }
        //else {
        //    outputstr = (String) cache.get("str");
        //}
        out.write(outputstr);
        long end = System.currentTimeMillis();
        System.out.println(sdf.format(new Date()) + "=====去哪儿酒店列表接口，用时：" + (end - start) / 1000 + "秒。");
        out.flush();
        out.close();
    }
}
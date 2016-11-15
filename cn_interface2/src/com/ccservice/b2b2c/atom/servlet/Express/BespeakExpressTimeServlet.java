package com.ccservice.b2b2c.atom.servlet.Express;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.callback.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.elong.inter.PropertyUtil;
import com.tenpay.util.MD5Util;

/**
 * Servlet implementation class BespeakExpressTimeServlet
 */
@WebServlet("/BespeakExpressTimeServlet")
public class BespeakExpressTimeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BespeakExpressTimeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String address = request.getParameter("address");
        String orderid = request.getParameter("orderid");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select C_DEPARTTIME from T_TRAINPASSENGER p,T_TRAINTICKET t where p.ID=t.C_TRAINPID and p.C_ORDERID="
                + orderid;
        List list = null;
        try {
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        boolean result = false;
        Map map = (Map) list.get(0);
        String departtime = map.get("C_DEPARTTIME").toString();
        String sendTime = "";
        Date dt1 = null;
        Date dt2 = null;
        String sfTime = "";
        String jdTime = "";
        String msg = "";
        JSONObject object = new JSONObject();
        if (!ElongHotelInterfaceUtil.StringIsNull(address) && !ElongHotelInterfaceUtil.StringIsNull(departtime)) {
            Date date = new Date();
            String timeStamp = sdf.format(date);
            String partnerName = "bespeak_offline";
            String key = "DFTo6feCCCI5Le3i";
            String messageIdentity = MD5Util.MD5Encode(partnerName + timeStamp + key, "UTF-8").toUpperCase();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("partnerName", partnerName);
            jsonObject.put("timeStamp", timeStamp);
            jsonObject.put("messageIdentity", messageIdentity);
            jsonObject.put("address", address);
            String url = "";
            String res = SendPostandGet.submitPost(url, jsonObject.toString(), "utf-8").toString();
            JSONObject resObj = JSONObject.parseObject(res);
            String code = resObj.getString("code");
            if (code.equals("true")) {
                if (res.contains("sf")) {
                    sfTime = resObj.getString("sf");
                }
                if (res.contains("jd")) {
                    jdTime = resObj.getString("jd");
                }
            }
            if (!ElongHotelInterfaceUtil.StringIsNull(sfTime)) {
                sendTime = sfTime;
                if (!ElongHotelInterfaceUtil.StringIsNull(jdTime)) {
                    sendTime = sfTime;
                }
            }
            else if (!ElongHotelInterfaceUtil.StringIsNull(jdTime)) {
                sendTime = jdTime;
            }
            else {
                msg = resObj.getString("msg");
            }
            try {
                dt1 = sdf.parse(departtime);
                dt2 = sdf.parse(sendTime);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            long time1 = dt1.getTime();
            long time2 = dt2.getTime();
            if ((time1 - time2) < (2 * 60 * 60 * 1000)) {
                //线下
                result = true;
            }
        }
        object.put("success", result);
        object.put("msg", msg);
        PrintWriter out = null;
        out = response.getWriter();
        out.print(object.toString());
        out.flush();
        out.close();
    }
}

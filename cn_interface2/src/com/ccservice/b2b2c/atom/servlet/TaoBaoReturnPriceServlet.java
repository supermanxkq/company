package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

/**
 * 淘宝回传卧铺价格
 * @author fiend
 *
 */
public class TaoBaoReturnPriceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        int r1 = (int) (Math.random() * 100000);
        PrintWriter out = response.getWriter();
        String jsonString = request.getParameter("json") == null ? "" : request.getParameter("json");
        try {
            if (!"".equals(jsonString)) {
                jsonString = URLDecoder.decode(jsonString, "UTF-8");
            }
        }
        catch (Exception e1) {
            WriteLog.write("TAOBAORETURNPRICESERVLET_EXCEPTION", r1 + "--->" + jsonString);
            ExceptionUtil.writelogByException("TAOBAORETURNPRICESERVLET_EXCEPTION", e1);
        }
        TaobaoHotelInterfaceUtil tbiu = new TaobaoHotelInterfaceUtil();
        JSONObject json = new JSONObject();
        try {
            json = json.parseObject(jsonString);
            //车次
            String traincode = json.getString("traincode") == null ? "" : json.getString("traincode");
            //到达站
            String tostationname = json.getString("tostationname") == null ? "" : json.getString("tostationname");
            //坐席价格
            String seatpriceint = json.getString("seatpriceint") == null ? "" : json.getString("seatpriceint");
            //出发站
            String fromstationname = json.getString("fromstationname") == null ? "" : json.getString("fromstationname");
            //发车时间
            String depdate = json.getString("depdate") == null ? "" : json.getString("depdate");
            //坐席类型
            String seattype = json.getString("seattype") == null ? "" : json.getString("seattype");
            //坐席类型
            String seatno = json.getString("seatno") == null ? "" : json.getString("seatno");
            String seattype_taobao = TaobaoHotelInterfaceUtil.CackBackSuccessseao(seattype, seatno);
            if (json == null) {
                WriteLog.write("TAOBAORETURNPRICESERVLET_ERROR", r1 + "--->null");
            }
            else {
                //回传淘宝卧铺价格
                tbiu.TrainAgentseatpriceSet(traincode, tostationname, seatpriceint, fromstationname, depdate,
                        seattype_taobao);
                WriteLog.write("TAOBAORETURNPRICESERVLET", r1 + "--->SUCCESS");
                out.print("SUCCESS");
            }
        }
        catch (Exception e) {
            WriteLog.write("TAOBAORETURNPRICESERVLET_EXCEPTION", r1 + "--->" + jsonString);
            ExceptionUtil.writelogByException("TAOBAORETURNPRICE_EXCEPTION", e);
        }
        finally {
            out.flush();
            out.close();
        }
    }
}

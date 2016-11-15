package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;

/**
 * Servlet implementation class TaoBaoMealCackBack
 */
public class TaoBaoMealCackBack extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaoBaoMealCackBack() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String jsonString = request.getParameter("json");
        String statue = request.getParameter("statue");
        try {
            jsonString = URLDecoder.decode(jsonString, "UTF-8");
        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        TaobaoHotelInterfaceUtil tbiu = new TaobaoHotelInterfaceUtil();
        JSONObject json = new JSONObject();
        jsonString = URLDecoder.decode(jsonString, "UTF-8");
        System.out.println("-------1------" + jsonString);
        System.out.println("-------2------" + statue);
        json = json.parseObject(jsonString);
        PrintWriter out = null;
        out = response.getWriter();
        if (json == null || statue == null) {
            WriteLog.write("淘宝改签", "接受空");
        }
        else {
            try {
                String xx = null;
                if (statue.equals("1"))//成功
                {
                    JSONArray jsonarry = json.getJSONArray("jsonarry");
                    xx = tbiu.CommitChangOrderage(json, jsonarry);
                }
                else {
                    xx = tbiu.CommitChangOrderageOver(json);

                }

                if (xx == null) {
                    out.print(xx);
                }
                else {
                    out.print("SUCCESS");
                }
                out.flush();
                out.close();
            }
            catch (Exception e) {
                WriteLog.write("淘宝改签", e.getMessage() + "CODE:" + e.toString() + " JSON:" + json);
                out.flush();
                out.close();
            }
        }
        out.flush();
        out.close();
    }

}

package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;

/**
 * Servlet implementation class TaoBaoRefundTicketCallback
 */
public class TaoBaoRefundTicketCallback extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaoBaoRefundTicketCallback() {
        super();
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
        System.out.println("coming----->" + jsonString);
        //        jsonString = URLDecoder.decode(jsonString, "UTF-8");
        TaobaoHotelInterfaceUtil tbiu = new TaobaoHotelInterfaceUtil();
        JSONObject json = new JSONObject();
        json = json.parseObject(jsonString);
        PrintWriter out = null;
        out = response.getWriter();
        try {
            String xx = tbiu.taobaoDrawerNotice(json);

            if (xx == null) {
                out.print(json);
            }
            else {
                try {
                    if (JSONObject.parseObject(xx).getJSONObject("train_agent_returnticket_confirm_response")
                            .getBooleanValue("is_success")) {
                        out.print("SUCCESS");
                    }
                    else {
                        out.print(false);
                    }
                }
                catch (Exception e) {
                    out.print(false);
                }
            }
            out.flush();
            out.close();
        }
        catch (Exception e) {
            WriteLog.write("淘宝退票", e.getMessage() + "CODE:" + e.toString() + " JSON:" + json);
            out.flush();
            out.close();
        }
    }

    public static void main(String[] args) {
        String xx = "{\"train_agent_returnticket_confirm_response\":{\"result_code\":\"refund_forse_agree\",\"is_success\":true}}";
        System.out.println(JSONObject.parseObject(xx).getJSONObject("train_agent_returnticket_confirm_response")
                .getBooleanValue("is_success"));
    }
}

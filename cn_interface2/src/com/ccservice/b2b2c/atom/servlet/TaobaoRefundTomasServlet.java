package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

@SuppressWarnings("serial")
public class TaobaoRefundTomasServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=utf-8");
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        String jsonString = req.getParameter("json");
        PrintWriter out = null;
        out = resp.getWriter();
        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("success", false);
        resultJsonObject.put("taobaoMsg", "");
        try {
            JSONObject json = JSONObject.parseObject(jsonString);
            String result = "";
            try {
                result = new TaobaoHotelInterfaceUtil().taobaoDrawerNotice(json);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (result == null) {
                resultJsonObject.put("taobaoMsg", "null");
            }
            else {
                try {
                    JSONObject taobaoResultJsonObject = JSONObject.parseObject(result);
                    if (taobaoResultJsonObject.getJSONObject("train_agent_returnticket_confirm_response")
                            .getBooleanValue("is_success")) {
                        resultJsonObject.put("success", true);
                        resultJsonObject.put("taobaoMsg", taobaoResultJsonObject);
                    }
                    else {
                        resultJsonObject.put("success", false);
                        resultJsonObject.put("taobaoMsg", taobaoResultJsonObject);
                    }
                }
                catch (Exception e) {
                    resultJsonObject.put("success", false);
                    resultJsonObject.put("taobaoMsg", result);
                    resultJsonObject.put("Exception", e.getMessage());
                    ExceptionUtil.writelogByException("ERROR_TaobaoRefundTomasServlet", e);
                }
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_TaobaoRefundTomasServlet", e);
        }
        finally {
            out.print(resultJsonObject.toString());
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

}

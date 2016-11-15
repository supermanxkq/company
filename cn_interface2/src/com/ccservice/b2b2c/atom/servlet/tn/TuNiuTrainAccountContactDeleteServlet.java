package com.ccservice.b2b2c.atom.servlet.tn;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class TuNiuTrainAccountContactDeleteServlet extends HttpServlet {
    /**
     * 
     * */
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        int r1 = new Random().nextInt(10000000);
        Long starttime = System.currentTimeMillis();
        String result = "";
        PrintWriter out = null;
        String param = "";
        String mima = "";
        String trainAccount = "";
        String pass = "";
        try {
            out = res.getWriter();
            param = req.getParameter("json");
            JSONObject jsonjie = JSONObject.parseObject(param);
            String data = jsonjie.getString("data");
            param = data;
            String jj = param;
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = decoder.decodeBuffer(jj);
            mima = new String(b);
            WriteLog.write("t同程火车票接口", r1 + ":jsonStr:" + mima);
            JSONArray tos = JSONObject.parseArray(mima.toString());
            for (int i = 0; i < tos.size(); i++) {
                JSONObject arr = (JSONObject) tos.get(i);
                trainAccount = arr.getString("trainAccount");
                pass = arr.getString("pass");
            }
            result = "帐号:" + trainAccount + "密码:" + pass;
            WriteLog.write("途牛接口删除:", r1 + ":值:" + result + "时间:" + starttime);
            JSONObject JsonFin = new JSONObject();
            JsonFin.put("trainAccount", trainAccount);
            JsonFin.put("pass", pass);
            result = JsonFin.toString();
            if (!result.equals("") || result != null) {
                TuNiuTrainAccountContactDelete tuniu = new TuNiuTrainAccountContactDelete();
                tuniu.DelPassenger(JsonFin);
            }
            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
                JSONObject obj = new JSONObject();
                obj.put("success", false);
                obj.put("code", "101");
                obj.put("msg", "传入的json为空对象");
                result = obj.toString();
            }
            WriteLog.write("途牛接口删除:", r1 + ":值:" + result + "时间:" + starttime);
            out.print(result);
            System.out.println(result);
        }
        catch (Exception e) {
            result = "Exception异常";
            WriteLog.write("途牛接口删除", r1 + ":reslut:" + e);
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                WriteLog.write("t途牛接口查询", r1 + ":reslut:" + result);
                if ("".equals(result) || result == null) {

                    result = "结果为空，返回失败";
                }
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }
}

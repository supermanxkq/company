package com.ccservice.b2b2c.atom.servlet.tn;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

@SuppressWarnings("serial")
public class TuNiuTrainAccountContactQueryServlet extends HttpServlet {

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
        JSONObject jresult = new JSONObject();
        String result = "";
        PrintWriter out = null;
        String param = "";
        String trainAccount = "";
        String pass = "";
        try {
            out = res.getWriter();
            param = req.getParameter("json");
            if (ElongHotelInterfaceUtil.StringIsNull(param)) {
                jresult.put("success", false);
                jresult.put("code", "101");
                jresult.put("msg", "传入的json为空对象");
                result = jresult.toJSONString();
            }
            else {
                JSONObject jsonjie = JSONObject.parseObject(param);
                String data = jsonjie.containsKey("data") ? jsonjie.getString("data") : "";
                if (data.equals("")) {
                    jresult.put("success", false);
                    jresult.put("msg", "获取data出错");
                    result = jresult.toJSONString();
                }
                else {
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] b = decoder.decodeBuffer(data);
                    JSONArray jsonarray = JSON.parseArray(new String(b));
                    WriteLog.write("t同程火车票接口", r1 + ":jsonStr:" + jsonarray.toJSONString());
                    for (int i = 0; i < jsonarray.size(); i++) {
                        JSONObject arr = (JSONObject) jsonarray.get(i);
                        trainAccount = arr.getString("trainAccount");
                        pass = arr.getString("pass");
                    }
                    result = "帐号:" + trainAccount + "密码:" + pass;
                    WriteLog.write("t途牛接口查询:", r1 + ":值:" + result + "时间:" + starttime);
                    jresult.put("trainAccount", trainAccount);
                    jresult.put("pass", pass);
                    result = jresult.toJSONString();
                    TuNiuTrainAccountContactQuery tuniu = new TuNiuTrainAccountContactQuery();
                    result = tuniu.find(jresult).toJSONString();
                }
            }
            WriteLog.write("t途牛接口查询:", r1 + ":值:" + result + "时间:" + starttime);
            out.print(result);
        }
        catch (Exception e) {
            e.printStackTrace();
            result = "帐号:" + trainAccount + "密码:" + pass;
            WriteLog.write("t途牛接口查询", r1 + ":reslut:" + e + "值:" + result);
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

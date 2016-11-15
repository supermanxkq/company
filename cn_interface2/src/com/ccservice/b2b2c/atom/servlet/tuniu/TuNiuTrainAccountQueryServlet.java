package com.ccservice.b2b2c.atom.servlet.tuniu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;

/**
 * 此类弃用，请参考TrainAccountOperateMethod类
 * @author WH
 * @time 2015年12月25日 上午9:53:59
 * @version 1.0
 */

@SuppressWarnings("serial")
public class TuNiuTrainAccountQueryServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=utf-8");
        res.setHeader("content-type", "text/html;charset=UTF-8");
        PrintWriter out = null;
        JSONObject jsonObject = new JSONObject();
        TuNiuTrainAccount account = new TuNiuTrainAccount();
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String reqString = buf.toString();
        String result = "";
        try {
            out = res.getWriter();
            if (ElongHotelInterfaceUtil.StringIsNull(reqString)) {
                jsonObject.put("success", Boolean.valueOf(false));
                jsonObject.put("code", "101");
                jsonObject.put("msg", "传入的json为空对象");
            }
            else {
                String trainAccount = "";
                String pass = "";
                JSONObject jsonjie = JSON.parseObject(reqString);
                String data = (jsonjie.containsKey("data")) ? jsonjie.getString("data") : "";
                if (data.equals("")) {
                    jsonObject.put("success", false);
                    jsonObject.put("code", "102");
                    jsonObject.put("msg", "获取data出错");
                }
                else {
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] b = decoder.decodeBuffer(data);
                    JSONArray jsonarray = JSON.parseArray(new String(b));
                    for (int i = 0; i < jsonarray.size(); ++i) {
                        JSONObject arr = jsonarray.getJSONObject(i);
                        trainAccount = arr.getString("trainAccount");
                        pass = arr.getString("pass");
                    }
                    JSONObject jresult = new JSONObject();
                    jresult.put("trainAccount", trainAccount);
                    jresult.put("pass", pass);
                    result = account.AccountQuery(jresult).toJSONString();
                }

            }

        }
        catch (Exception e) {
            jsonObject.put("success", false);
            jsonObject.put("code", "103");
            jsonObject.put("msg", "系统异常");
            e.printStackTrace();
        }
        finally {
            if (result.equals("")) {
                result = jsonObject.toJSONString();
            }
            out.write(result);
            out.flush();
            out.close();
        }
    }
}
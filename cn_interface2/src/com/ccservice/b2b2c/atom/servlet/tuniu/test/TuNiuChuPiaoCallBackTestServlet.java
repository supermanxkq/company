package com.ccservice.b2b2c.atom.servlet.tuniu.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;

public class TuNiuChuPiaoCallBackTestServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String result = null;
        PrintWriter out = null;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;chartset=UTF-8");
        try {
            out = response.getWriter();
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer sb = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                sb.append(line);

            }
            String pararm = sb.toString();
            if (pararm == null || "".equals(pararm.trim())) {
                JSONObject obj = new JSONObject();
                obj.put("success", false);
                obj.put("code", "101");
                obj.put("msg", "请求参数为空");
                result = obj.toString();
            }
            else {
                System.out.println("pararm" + pararm);
                //                JSONObject jsonObject = JSONObject.parseObject(pararm);
                //                String dataString = jsonObject.getString("data");
                //                dataString = TuNiuDesUtil.decrypt(dataString);
                //                System.out.println(dataString);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                out.print(result);
                out.flush();
                out.close();
            }

        }

    }

}

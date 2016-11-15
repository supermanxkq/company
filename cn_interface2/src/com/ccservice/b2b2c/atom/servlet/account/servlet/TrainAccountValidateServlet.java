package com.ccservice.b2b2c.atom.servlet.account.servlet;

import java.util.Random;
import java.io.PrintWriter;
import java.io.IOException;
import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import com.ccservice.b2b2c.util.TrainUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.account.method.TrainAccountOperateMethod;

/**
 * 12306账号验证接口
 * @author WH
 * @time 2015年12月15日 下午6:32:22
 * @version 1.0
 */

@SuppressWarnings("serial")
public class TrainAccountValidateServlet extends HttpServlet implements Servlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doPost(req, res);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        //结果
        String result = "";
        //输出
        PrintWriter out = null;
        //日志
        String logName = "TrainAccountValidateServlet";
        //随机数据
        int random = new Random().nextInt(9000000) + 1000000;
        //操作
        try {
            out = response.getWriter();
            //请求参数
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            //请求参数
            String param = buf.toString();
            //记录日志
            WriteLog.write(logName, random + "-->Ip:" + TrainUtil.getBrowserIp(request) + "-->req-->" + param);
            //处理方法
            result = new TrainAccountOperateMethod().accountValidate(param, logName, random).toString();
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logName + "Exception", e, String.valueOf(random));
        }
        finally {
            if (out != null) {
                //日志
                WriteLog.write(logName, random + "-->res-->" + result);
                //输出
                out.print(result);
                out.flush();
                out.close();
            }
        }
    }

}
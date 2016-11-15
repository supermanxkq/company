package com.ccservice.b2b2c.back.policy.gp.yepos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * 易宝业务接口
 * @author wzc
 *
 */
public class YeePosService extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        int r1 = new Random().nextInt(10000000);
        ServletInputStream inStream = request.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = -1;
        byte[] buffer = new byte[1024];
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String send = new String(outputStream.toByteArray(), "UTF-8");//易宝发送给商户的报文
        //商户自己的处理逻辑根据 service_code 走不同的业务逻辑
        String result = "";//商户返回给易宝的报文
        response.setContentType("text/xml; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        try {
            response.getWriter().write("success");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

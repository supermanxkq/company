package com.ccservice.b2b2c.atom.pay.gp.yeepos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.callback.WriteLog;

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
        try {
            String send = new String(outputStream.toByteArray(), "UTF-8");//易宝发送给商户的报文
            WriteLog.write("易宝通知", r1 + ":" + send);
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new StringReader(send));
            Element root = doc.getRootElement();
            Element SessionHead = root.element("SessionHead");
            //            String mac = SessionHead.elementText("HMAC");
            //            String selfmac = new YeeCodeUtil().putMD5Sign(send, "DFE23HLAW198820SQWE1224SDAQQ3319203945");
            Element SessionBody = root.element("SessionBody");
            String ServiceCode = SessionHead.elementText("ServiceCode");
            String result = "";//商户返回给易宝的报文
            if ("COD406".equals(ServiceCode)) {

            }
            else if ("COD403".equalsIgnoreCase(ServiceCode)) {//付款交易接口
                result = new YeeCode403Method().payMethod(SessionHead, SessionBody, r1);
            }
            else if ("COD201".equalsIgnoreCase(ServiceCode)) {//登陆交易接口

            }
            else if ("COD402".equalsIgnoreCase(ServiceCode)) {//查询交易接口

            }
            else if ("COD404".equalsIgnoreCase(ServiceCode)) {//签收交易接口

            }
            else if ("COD407".equalsIgnoreCase(ServiceCode)) {//退款交易接口

            }
            //商户自己的处理逻辑根据 service_code 走不同的业务逻辑
            WriteLog.write("易宝通知", r1 + ":响应" + result);
            response.setContentType("text/xml; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            try {
                response.getWriter().write(result);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

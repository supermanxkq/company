package com.ccservice.b2b2c.atom.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.taobao.thread.MyThreadRefund;
import com.ccservice.b2b2c.util.ExceptionUtil;

@SuppressWarnings("serial")
public class TaobaoRefundPriceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("content-type", "text/html;charset=UTF-8");
            req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
            final AsyncContext ctx = req.startAsync();
            ctx.setTimeout(20000L);
            //监听
            ctx.addListener(new AsyncListener() {
                public void onTimeout(AsyncEvent event) throws IOException {
                    getResponeOut(ctx, "TIMEOUT");
                }

                public void onError(AsyncEvent event) throws IOException {
                    getResponeOut(ctx, "ERROR");
                }

                public void onComplete(AsyncEvent event) throws IOException {
                }

                public void onStartAsync(AsyncEvent event) throws IOException {
                }
            });
            BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getRequest().getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            String reqString = buf.toString();
            WriteLog.write("TaobaoRefundPriceServlet", reqString);
            try {
                JSONObject jsonString = JSONObject.parseObject(reqString);
                if (jsonString == null) {
                    getResponeOut(ctx, "JSON为空");
                }
                else {
                    ExecutorService pool = Executors.newFixedThreadPool(1);
                    Thread t1 = new MyThreadRefund(ctx, jsonString);
                    pool.execute(t1);
                    pool.shutdown();
                }
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("ERROR_TaobaoRefundPriceServlet", e);
                getResponeOut(ctx, "false");
            }
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_TaobaoRefundPriceServlet", e);
        }
    }

    //生成返回信息
    private void getResponeOut(AsyncContext ctx, String result) {
        try {
            ServletResponse response = ctx.getResponse();
            //编码
            response.setCharacterEncoding("UTF-8");
            WriteLog.write("TaobaoRefundPriceServlet_respones", result);
            try {
                result = URLEncoder.encode(result, "UTF-8");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //输出
            response.getWriter().write(result);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_TaobaoRefundPriceServlet", e);
        }
        finally {
            try {
                ctx.complete();
            }
            catch (Exception e) {
            }
        }
    }
}

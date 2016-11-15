package com.ccservice.b2b2c.atom.service12306.offlineRefund.servlet;

import java.util.Map;
import java.util.Random;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.concurrent.Executors;
import com.alibaba.fastjson.JSONObject;
import java.util.concurrent.ExecutorService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.thread.TaoBaoPushRefundPriceThread;

/**
 * 淘宝推送退款处理
 * @author WH
 * @time 2015年8月31日 下午2:47:39
 * @version 1.0
 */

@SuppressWarnings("serial")
public class TaoBaoPushRefundPriceServlet extends HttpServlet {

    public void init() throws ServletException {
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //UTF8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html; charset=UTF-8");
        //结果
        String result = "";
        //异步
        final AsyncContext ctx = request.startAsync();
        try {
            //数据
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buf = new StringBuffer(1024);
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            //随机数据
            int random = new Random().nextInt(9000000) + 1000000;
            //日志
            WriteLog.write("t淘宝推送退款处理_请求", random + "-->" + buf.toString());
            //请求
            JSONObject req = JSONObject.parseObject(buf.toString());
            //为空
            if (req == null || !req.containsKey("orderId")) {
                throw new Exception("请求参数错误！");
            }
            //地址
            String callBackUrl = PropertyUtil.getValue("TaoBaoPushRefundPriceCallBack");
            //为空
            if (ElongHotelInterfaceUtil.StringIsNull(callBackUrl)) {
                throw new Exception("回调地址为空！");
            }
            //MAP
            Map<String, String> dataMap = Server.getInstance().getDateHashMap();
            //正在退款，尝试等待
            int defaultWaitTime = 5;
            int defaultWaitCount = 120;
            //正在退款，尝试等待时间，单位：秒
            int waitTime = Integer.parseInt(getMemoryData(dataMap, "WaitTimeWhenRefunding",
                    String.valueOf(defaultWaitTime)));
            //正在退款，尝试等待次数，120次，合计10分钟
            int waitCount = Integer.parseInt(getMemoryData(dataMap, "WaitCountWhenRefunding",
                    String.valueOf(defaultWaitCount)));
            //本地无退款时即时抓支付宝数据开关
            String catchOpenValue = getMemoryData(dataMap, "CatchRefundPriceWhenNoOpen", "0");
            //异步
            ExecutorService pool = Executors.newFixedThreadPool(1);
            pool.execute(new TaoBaoPushRefundPriceThread(req, callBackUrl, dataMap, catchOpenValue,
                    waitTime < 1 ? defaultWaitTime : waitTime, waitCount < 1 ? defaultWaitCount : waitCount, random));
            pool.shutdown();
            //结果
            result = "Success";
        }
        catch (Exception e) {
            //日志
            ExceptionUtil.writelogByException("t淘宝推送退款处理_异常", e);
            //结果
            String msg = e.getMessage();
            result = "Exception: " + (ElongHotelInterfaceUtil.StringIsNull(msg) ? "Null." : msg.trim());
        }
        //输出结果
        try {
            ctx.getResponse().getWriter().write(result);
        }
        catch (Exception e) {
        }
        //响应结果
        finally {
            try {
                ctx.complete();
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 内存取数据，无的时候设置默认值
     * @param defaultValue 默认值
     */
    private String getMemoryData(Map<String, String> dataMap, String key, String defaultValue) {
        //结果
        String result = defaultValue;
        //包含
        if (dataMap.containsKey(key)) {
            result = dataMap.get(key);
        }
        else {
            dataMap.put(key, defaultValue);
        }
        //返回
        return result;
    }
}
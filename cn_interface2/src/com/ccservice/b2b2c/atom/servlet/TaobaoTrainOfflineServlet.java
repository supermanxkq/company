package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;

/**
 * 线下火车票出票回调
 * @author guozhengju
 * 2016-01-12
 *
 */
public class TaobaoTrainOfflineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private Logger logger = Logger.getLogger("TaobaoTrainOfflineServlet");

    private String appScret;

    private String agentId;

    private String apiUrl;

    private String appKey;

    private String sessionKey;

    private TaobaoHotelInterfaceUtil tbiu;

    public void init() throws ServletException {
        super.init();
        tbiu = new TaobaoHotelInterfaceUtil();

    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //POST请求参数
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("utf-8");
        res.setHeader("content-type", "text/html;charset=utf-8");

        // int r1 = new Random().nextInt(10000);
        String result = "";
        PrintWriter out = null;
        out = res.getWriter();
        long l1 = System.currentTimeMillis();

        /** 出票回调开始 ***********************************************************************/
        // 获取订单信息
        JSONObject exception = new JSONObject();
        String issuccess=req.getParameter("issuccess");
        String fail_msg = req.getParameter("fail_msg");
        String expr = "";
        if (fail_msg != null) {
            try {
                fail_msg = new String(fail_msg.getBytes("ISO-8859-1"), "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String order_id = req.getParameter("order_id");
        WriteLog.write("TAOBAO_8.2线下火车票出票通知", l1 + "淘宝回调：接受的参数:orderid=" + order_id +";issuccess="+issuccess+ ";fail_msg=" + fail_msg);
        Map mp =new HashMap();
            mp = tbiu.trainOfflineOrderid(order_id, issuccess,Integer.parseInt(fail_msg));
            WriteLog.write("TAOBAO_7.00线下火车票出票通知", l1 + "出票请求淘宝mp,拿到的mp：" + mp);   
        if (mp != null) {
            // 请求淘宝开始
            String obj = "";
            int i = 0;
            WriteLog.write("TAOBAO_7.2线下火车票出票通知", l1 + "淘宝回调失败循环请求----mp：" + mp);
            do {
            	
                obj = tbiu.taobaoOfflineDrawer(mp,order_id);
                WriteLog.write("TAOBAO_7.2线下火车票出票通知", l1 + "淘宝出票回调返回结果：" + obj);
                if (!"SUCCESS".equals(obj)) {
                    try {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException e) {
                    }
                }
                i++;
            }
            while (!obj.equals("SUCCESS") && i < 5);
            if (obj == null || obj.equals("")) {
                result = "error";
            }
            else if (obj.equals("SUCCESS")) {
                result = "SUCCESS";
            }
            else {
                exception.put("success", false);
                exception.put("msg", "线下淘宝退票接口请求失败，session过期或参数不匹配，错误信息：" + tbiu.exceptionS);
                result = exception.toString();
            }
        }
        else {
            WriteLog.write("TAOBAO_7.2线下火车票出票通知", l1 + "淘宝回调返回结果：" + tbiu.exceptionS);
            result = tbiu.exceptionS;
        }
        WriteLog.write("TAOBAO_7.2线下火车票出票通知",
                l1 + "回调详细信息" + fail_msg + "," + order_id + ":" + (System.currentTimeMillis() - l1) + ":" + result);

        out.print(result);
        out.flush();
        out.close();
        /** 出票结束 ***********************************************************************/
    
	}
	 

}

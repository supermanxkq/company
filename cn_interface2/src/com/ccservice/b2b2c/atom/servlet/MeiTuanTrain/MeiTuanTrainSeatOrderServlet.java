package com.ccservice.b2b2c.atom.servlet.MeiTuanTrain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.MeiTuanTrain.method.MeiTuanTrainOrder;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;

/**
 * Servlet implementation class MeiTuanTrainSeatOrder
 */
@WebServlet("/MeiTuanTrainSeatOrder")
public class MeiTuanTrainSeatOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final String logname = "MeiTuan_先占座后支付_提交订单";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MeiTuanTrainSeatOrderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        int r1 = new Random().nextInt(10000000);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = null;
        String result = "";
        out = response.getWriter();
        JSONObject obj = new JSONObject();
        //POST请求参数
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String param = buf.toString();
        WriteLog.write(logname, "接受美团数据：" + param);
        if (ElongHotelInterfaceUtil.StringIsNull(param)) {
            obj.put("success", false);
            obj.put("code", "101");
            obj.put("msg", "请求参数为空");
            result = obj.toString();
        }
        else {
            JSONObject jsonObject = JSONObject.parseObject(param);
            MeiTuanTrainOrder meiTuanTrainOrder = new MeiTuanTrainOrder();
            result = meiTuanTrainOrder.submitOrder(jsonObject);
        }
        WriteLog.write(logname, "美团订单提交返回值" + result);
        out.print(result);
        out.flush();
        out.close();
    }

}

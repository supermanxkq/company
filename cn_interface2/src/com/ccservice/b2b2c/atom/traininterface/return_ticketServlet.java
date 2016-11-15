package com.ccservice.b2b2c.atom.traininterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

/**
 * 在线退票
 **/
public class return_ticketServlet extends HttpServlet {

    public ITrainTestDao dao = new TrainTestImpl();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        String resultString = "";
        String orderid = "";
        //订单号
        String orderids = request.getParameter("orderid");
        //我方交易单号
        String transactionids = request.getParameter("transactionid");
        //取票单号
        String ordernumbers = request.getParameter("ordernumber");
        //请求特征（唯一）
        String reqtokens = request.getParameter("reqtoken");
        //异步通知接口回调地址
        String callbackurls = request.getParameter("callbackurl");
        //获得乘客人数
        int passenger = Integer.parseInt(request.getParameter("id"));
        Trainorder trainorder = new Trainorder();
        List<Trainpassenger> passengers = new ArrayList<Trainpassenger>();
        for (int i = 0; i < passenger; i++) {
            //乘客姓名
            String name = request.getParameter("name" + (i + 1));
            //证件号
            String idnumber = request.getParameter("idnumber" + (i + 1));
            //证件类型
            int idtype = Integer.parseInt(request.getParameter("idtype" + (i + 1)));
            //座位编码
            String seattype = request.getParameter("seattype" + (i + 1));
            //价格
            Float price = Float.parseFloat(request.getParameter("price" + (i + 1)));
            //票号
            String ticketno = request.getParameter("ticketno" + (i + 1));
            //乘客类型
            int tickettype = Integer.parseInt(request.getParameter("tickettype" + (i + 1)));
            passengers.add(dao.getTrainpassenger(name, idnumber, idtype, seattype, price, ticketno, tickettype));
            trainorder.setPassengers(passengers);
        }

        orderid = orderids;
        String transactionid = transactionids;
        String ordernumber = ordernumbers;
        String reqtoken = reqtokens;
        String callbackurl = callbackurls;
        resultString = dao.return_ticket(orderid, transactionid, ordernumber, reqtoken, callbackurl, trainorder);
        PrintWriter printWriter = response.getWriter();
        printWriter.println("<p>" + resultString + "</p>");
    }

    public static String getcurrentTimeMillis() {
        return System.currentTimeMillis() + "";
    }
}

package com.ccservice.b2b2c.atom.traininterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

/**
 * 火车票订单提交
 **/
public class train_orderServlet extends HttpServlet {

    public ITrainTestDao dao = new TrainTestImpl();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("content-type", "text/html;charset=UTF-8");
            String resultString = "";
            String orderid = "";
            //订单号
            String orderids = request.getParameter("orderid");
            //车次
            String checis = request.getParameter("checi");
            //出发站简码
            String from_station_codes = request.getParameter("from_station_code");
            //出发站名称
            String from_station_names = request.getParameter("from_station_name");
            //到达站简码
            String to_station_codes = request.getParameter("to_station_code");
            //到达站名称
            String to_station_names = request.getParameter("to_station_name");
            //乘车日期
            String train_dates = request.getParameter("train_date");
            //占座成功回调地址[选填]
            String callbackurls = request.getParameter("callbackurl");
            if (callbackurls.equals("")) {
                callbackurls = "123456";
            }
            //接口账号
            String partnerids = request.getParameter("partnerid");
            if (partnerids.equals("")) {
                partnerids = "tongcheng_train_test";
            }
            //请求物证值 [异步时填写]
            String reqtoken = request.getParameter("reqtoken");
            //是否出无座票
            String hasseat = request.getParameter("hasseat");
            //是否收单
            String waitfororder = request.getParameter("waitfororder");
            //是否是自己发的请求
            String shoudan = request.getParameter("shoudan");
            //获得乘客人数
            int passenger = Integer.parseInt(request.getParameter("id"));
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
            }
            orderid = orderids;
            String checi = checis;
            String from_station_code = from_station_codes;
            String from_station_name = from_station_names;
            String to_station_code = to_station_codes;
            String to_station_name = to_station_names;
            String train_date = train_dates;
            String callbackurl = callbackurls;
            String partnerid = partnerids;
            resultString = dao.train_order(orderid, checi, from_station_code, from_station_name, to_station_code,
                    to_station_name, train_date, passengers, callbackurl, partnerid, reqtoken, hasseat, waitfororder,
                    shoudan);
            PrintWriter printWriter = response.getWriter();
            printWriter.println("<p>" + resultString + "</p>");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getcurrentTimeMillis() {
        return System.currentTimeMillis() + "";
    }

}

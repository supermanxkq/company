package com.ccservice.b2b2c.atom.traininterface;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengReqChange;
import com.ccservice.b2b2c.base.train.Trainorderchange;
/**
 * 请求改签
 **/
public class train_request_changeServlet extends HttpServlet{

	public ITrainTestDao dao = new TrainTestImpl();
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		String resultString = "";
		//使用方订单号
		String orderid = request.getParameter("orderid");
		//交易单号
		String transactionid = request.getParameter("transactionid");
		//取票单号
		String ordernumber = request.getParameter("ordernumber");
		//改签新车票的车次
		String change_checi = request.getParameter("change_checi");
		//改签新车票出发时间
		String change_datetime = request.getParameter("change_datetime");
		//改签新车票的座位席别编码
		String change_zwcode = request.getParameter("change_zwcode");
		//原票的座位席别编码
		String old_zwcode = request.getParameter("old_zwcode");
		//乘客姓名
		String passengersename = request.getParameter("passengersename");
		//证件类别
		String passporttypeseid = request.getParameter("passporttypeseid");
		//证件号码
		String passportseno = request.getParameter("passportseno");
		//票种类别 
		String piaotype = request.getParameter("piaotype");
		//原车票票号
		String old_ticket_no = request.getParameter("old_ticket_no");
		resultString = dao.train_request_change(orderid, transactionid, ordernumber, change_checi, change_datetime, change_zwcode, old_zwcode, passengersename, passporttypeseid, passportseno, piaotype, old_ticket_no);
		PrintWriter printWriter = response.getWriter();
		printWriter.println("<p>"+resultString+"</p>");
	}
}

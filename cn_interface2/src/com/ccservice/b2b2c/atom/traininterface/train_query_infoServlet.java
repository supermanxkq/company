package com.ccservice.b2b2c.atom.traininterface;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 查询订单详情
 **/
public class train_query_infoServlet extends HttpServlet {
	
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
		resultString = dao.train_query_info(orderid,transactionid);
		PrintWriter printWriter = response.getWriter();
		printWriter.println("<p>"+resultString+"</p>");
	}
}

package com.ccservice.b2b2c.atom.traininterface;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 余票查询（无价格）
 **/
public class train_query_remainServlet extends HttpServlet {

	public ITrainTestDao dao = new TrainTestImpl();

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		String resultString = "";
		// 乘车日期（yyyy-MM-dd）
		String train_date = request.getParameter("train_date");
		// 出发站简码
		String from_station = request.getParameter("from_station");
		// 到达站简码
		String to_station = request.getParameter("to_station");
		// 订票类别，固定值“ADULT”表示普通票
		String purpose_codes = request.getParameter("purpose_codes");
		// 是否需要里程(“1”需要；其他值不需要),默认为0
		String needdistance = request.getParameter("needdistance");

		resultString = dao.train_query_remain(train_date, from_station, to_station, purpose_codes, needdistance);
		PrintWriter printWriter = response.getWriter();
		printWriter.println("<p>" + resultString + "</p>");
	}

	public static String getcurrentTimeMillis() {
		return System.currentTimeMillis() + "";
	}
}

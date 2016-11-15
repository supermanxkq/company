package com.ccservice.b2b2c.atom.traininterface;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class get_train_infoServlet extends HttpServlet {

    public ITrainTestDao dao = new TrainTestImpl();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
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
        // 【选填】官方系统的车次内部编码，如：54000G703931
        String train_no = request.getParameter("train_no");
        // 车次号，如：G7039
        String train_code = request.getParameter("train_code");

        resultString = dao.get_train_info(train_date, from_station, to_station, train_no, train_code);
        PrintWriter printWriter = response.getWriter();
        printWriter.println("<p>" + resultString + "</p>");
    }
}

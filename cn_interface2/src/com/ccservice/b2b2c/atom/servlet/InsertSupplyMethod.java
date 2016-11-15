package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.util.db.DBHelperAccount;


/**
 * @author zhaohongbo
 * 记录添加常旅客
 * Servlet implementation class InsertSupplyMethod
 */
public class InsertSupplyMethod extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InsertSupplyMethod() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        String json = request.getParameter("json");

        try {
            if (json.equals("") || json != null) {
                JSONObject jsonjie = JSONObject.parseObject(json);
                int orderId = jsonjie.containsKey("orderId") ? jsonjie.getIntValue("orderId") : 0;
                int insert = jsonjie.containsKey("insert") ? jsonjie.getIntValue("insert") : 1;
                int sum = jsonjie.containsKey("sum") ? jsonjie.getIntValue("sum") : 1;

                String updateSql = "exec [UpdateSupply] @OrderId=" + orderId + "@InsertSupply=" + insert
                        + "@SumSupply=" + sum;
                DBHelperAccount.executeSql(updateSql);
            }

        }
        catch (Exception e) {
            // TODO: handle exception
        }
        PrintWriter out = response.getWriter();
        out.print("更新成功数据库！！！！");
        out.flush();
    }

}

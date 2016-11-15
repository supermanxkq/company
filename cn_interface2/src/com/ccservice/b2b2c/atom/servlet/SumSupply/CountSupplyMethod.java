package com.ccservice.b2b2c.atom.servlet.SumSupply;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

/**
 * Servlet implementation class CountSupplyMethod
 */
public class CountSupplyMethod extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");

        String result = "-1";
        result = getCountSupplyMethod();
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
	}

    private String getCountSupplyMethod() {
        DataTable table = null;
        String sql = "exec [CountSupplyMethod]";

        try {
            table = DBHelperAccount.GetDataTable(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonobject = new JSONObject();
        JSONArray jsonobject_series = new JSONArray();
        JSONArray jsonobject_series_data1 = new JSONArray();
        JSONArray jsonobject_series_data2 = new JSONArray();
        List<DataRow> dataRows = table.GetRow();
        if (dataRows.size() > 0) {
            for (int i = 0; i < dataRows.size(); i++) {
                DataRow datarow = dataRows.get(i);
                int total = Integer.parseInt(datarow.GetColumnString("total"));
                int success = Integer.parseInt(datarow.GetColumnString("multiplexing"));
                String Date = datarow.GetColumnString("TimeByMin").toString();
                String DateCOUNT = datarow.GetColumnString("TimeByMin").toString();
                System.out.println(total + "   " + success + "   " + Date + "   " + DateCOUNT);
                JSONObject totalItem = new JSONObject();
                JSONObject successItem = new JSONObject();
                totalItem.put("x", DateCOUNT);
                totalItem.put("y", total);
                successItem.put("x", Date);
                successItem.put("y", success);
                jsonobject_series_data1.add(totalItem);
                jsonobject_series_data2.add(successItem);
            }
        }
        jsonobject_series.add(jsonobject_series_data1);
        jsonobject_series.add(jsonobject_series_data2);
        jsonobject.put("series", jsonobject_series);
        System.out.println(JSONObject.toJSONString(jsonobject));
        return JSONObject.toJSONString(jsonobject);

    }
}

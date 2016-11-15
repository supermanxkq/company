package com.ccservice.b2b2c.atom.pay;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;
import com.ccservice.b2b2c.atom.server.Server;

@SuppressWarnings("serial")
public class Reselect extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			String helpername = request.getParameter("helpername");
			long orderid = Long.parseLong(new String(request.getParameter(
					"orderid").getBytes("ISO8859-1"), "UTF-8"));
			
			Payhelper payhelper = (Payhelper) Class.forName(
					Payhelper.class.getPackage().getName() + "." + helpername)
					.getConstructor(long.class).newInstance(orderid);
			String sql="DELETE T_TRADERECORD  WHERE  C_STATE!=1  and  C_ORDERCODE='"+payhelper.getOrdernumber()+"'";
			WriteLog.write("删除未支付记录", sql);
			Server.getInstance().getSystemService().findMapResultBySql(sql, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		this.doGet(request, response);
	}

}

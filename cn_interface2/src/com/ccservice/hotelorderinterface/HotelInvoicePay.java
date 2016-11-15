package com.ccservice.hotelorderinterface;

import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.logging.Log;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.commons.logging.LogFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 补开发票支付
 */

@SuppressWarnings("serial")
public class HotelInvoicePay extends HttpServlet{
	
	Log log = LogFactory.getLog(HotelInvoicePay.class);
	
    @SuppressWarnings("unchecked")
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// 设置编码方式
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String orderid = request.getParameter("orderid");
		String md = request.getParameter("md");
		//在易订行上进行支付配置
		if(!ElongHotelInterfaceUtil.StringIsNull(md)){
			int value = Integer.valueOf(md);
			if(value%49==0){
				PrintWriter out = response.getWriter();
				List<Hotelorder> hotelorders = Server.getInstance().getHotelService().findAllHotelorder("where c_orderid='"+orderid+"'", "", -1, 0);
				if(hotelorders!=null && hotelorders.size()==1){
					Hotelorder hotelorder = hotelorders.get(0);
					String url = "http://" + request.getServerName() + ":" + request.getServerPort() + "/cn_interface/pay?";
					String param = "payname=Alipay&helpername=HotelInvoicePayHelper&orderid=" + hotelorder.getId();
					System.out.println(url + param);
					this.log.error("支付请求地址：" + url + param);
					response.sendRedirect(url+param);
				}else{
					out.write("<h1>没有找到对应的订单，请联系供应商……</h1>");
					out.flush();
					out.close();
				}
			}
		}
		//在客户平台上进行转发
		else{
			PrintWriter out = response.getWriter();
			List<Hotelorder> hotelorders = Server.getInstance().getHotelService().findAllHotelorder("where c_orderid='"+orderid+"'", "", -1, 0);
			if(hotelorders!=null && hotelorders.size()==1){
				Hotelorder hotelorder = hotelorders.get(0);
				List<Sysconfig> configs = Server.getInstance().getSystemService().findAllSysconfig("where c_name='payurl'", "", -1, 0);
				if(configs!=null && configs.size()==1){
					Sysconfig config = configs.get(0);
					String url = config.getValue() + "/cn_interface/HotelInvoicePay?orderid=" + hotelorder.getYeeordernum() + "&md=" + ((int)(Math.random()*1000))*49;
					response.sendRedirect(url);
				}else{
					out.write("<h1>支付失败，请联系供应商……</h1>");
				}
			}else{
				out.write("<h1>没有找到对应的订单，请联系供应商……</h1>");
				out.flush();
				out.close();
			}
			
		}
    }
}

package com.ccservice.hotelorderinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
/**
 * 支付订单
 * @author wzc
 *
 */
public class HotelOrderPay extends HttpServlet{
	Log log=LogFactory.getLog(HotelOrderPay.class);
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
    	// 设置编码方式
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String orderid=request.getParameter("orderid");
		String md=request.getParameter("md");
		if(md!=null&&!md.equals("")&&md.length()>0){//在易订行上进行支付配置
			int value=Integer.valueOf(md);
			if(value%49==0){
				PrintWriter out=response.getWriter();
				List<Hotelorder> hotelorders=Server.getInstance().getHotelService().findAllHotelorder("where c_orderid='"+orderid+"'", "", -1, 0);
				if(hotelorders.size()==1){
					Hotelorder hotelorder=hotelorders.get(0);
					String url="http://"+request.getServerName()+":"+request.getServerPort()+"/cn_interface/pay?";
					String param="payname=Alipay&helpername=Hotelorderpayhelper&orderid="+ hotelorder.getId();
					System.out.println(url+param);
					this.log.error("支付请求地址："+url+param);
					response.sendRedirect(url+param);
				}else{
					out.write("<h1>没有找到对应的订单，请联系供应商……</h1>");
					out.flush();
					out.close();
				}
			}
		}else{//在客户平台上进行转发
			PrintWriter out=response.getWriter();
			List<Hotelorder> hotelorders=Server.getInstance().getHotelService().findAllHotelorder("where c_orderid='"+orderid+"'", "", -1, 0);
			if(hotelorders.size()==1){
				Hotelorder hotelorder=hotelorders.get(0);
				List<Sysconfig> configs=Server.getInstance().getSystemService().findAllSysconfig("where c_name='payurl'", "", -1, 0);
				if(configs.size()==1){
					String url=configs.get(0).getValue()+"/cn_interface/HotelOrderPay?orderid="+hotelorder.getYeeordernum()+"&md="+((int)(Math.random()*1000))*49;
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

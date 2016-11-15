package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.capinfo.crypt.RSA_MD5;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.orderinforc.Orderinforc;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.base.traderecord.Traderecord;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

public class PayEase extends HttpServlet{
	Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String str = "";
		// TODO Auto-generated method stub
		//v_count、v_oid、v_pmode、v_pstatus、v_pstring、v_amount、v_moneytype
		//v_mac、v_md5money、v_sign
		//订单个数（v_count）：本次发送的订单个数；（最少为1，最大为4）
		String v_count = req.getParameter("v_count");
		//订单编号组（v_oid）：定义同商户提交待付款订单接口中的订单编号定义；
		String v_oid = req.getParameter("v_oid");
		//支付方式组（v_pmode）：支付方式中文说明，如“中行长城信用卡”。
		String v_pmode = req.getParameter("v_pmode");
		v_pmode = new String(v_pmode.getBytes("ISO8859-1"), "GB2312"); 
		//支付状态组（v_pstatus）：支付结果，0→待处理（支付结果未确定）； 1支付完成； 3支付被拒绝； 
		String v_pstatus = req.getParameter("v_pstatus");
		//支付结果说明（v_pstring）：对支付结果的说明，成功时（v_pstatus=1）为“支付成功”，支付被拒绝时（v_pstatus=3）为失败原因。
		String v_pstring = req.getParameter("v_pstring");
		//订单支付金额(v_amount):订单实际支付金额
		String v_amount = req.getParameter("v_amount");
		//订单支付币种(v_moneytype):订单实际支付币种
		String v_moneytype = req.getParameter("v_moneytype");
		String v_mac = req.getParameter("v_mac");
		String v_md5money = req.getParameter("v_md5money");
		String v_sign = req.getParameter("v_sign");
		RSA_MD5 myRSA=new RSA_MD5();
		  String source=v_oid + v_pstatus + v_amount + v_moneytype + v_count;
//		  System.out.println(req.getSession().getServletContext().getRealPath("") +"\\Public1024.key");
		  int verifyStatus = myRSA.PublicVerifyMD5(req.getSession().getServletContext().getRealPath("/") + "/Public1024.key", v_sign, source);
		  PrintWriter out = resp.getWriter();
		  if(verifyStatus==0){
			  System.out.println("验证成功！");
			  if(Integer.parseInt(v_count)>0){
				  String[] oid = v_oid.split("[|][_][|]");//外部订单号 给首信易发送的订单号
				  String[] pstatus = v_pstatus.split("[|][_][|]");//支付结果，0→待处理（支付结果未确定）； 1支付完成； 3支付被拒绝； 
				  String[] amount = v_amount.split("[|][_][|]");//订单实际支付金额
				  String[] pmode = v_pmode.split("[|][_][|]");
 				  ISystemService service = Server.getInstance().getSystemService();
				  for(int i=0;i<Integer.parseInt(v_count);i++){
					  String orderid = oid[i].split("-")[2];
					  logger.info("首信易交易后台返回接口处理订单：" + orderid);
					  System.out.println("首信易交易后台返回接口处理订单：" + orderid);
					  Orderinfo order = (Orderinfo) Server.getInstance().getAirService()
						.findAllOrderinfo("WHERE C_ORDERNUMBER='" + orderid.trim() + "'", "",
								-1, 0).get(0);
					  
					
					  if (order.getPaystatus() == 0&&order.getOrderstatus()==1) {
						// 写入支付记录
							try {
								Traderecord traderecord = new Traderecord();
								traderecord.setCreateuser("payEase");
								traderecord.setGoodsname("机票");
								traderecord.setCode(oid[i]);// 外部订单号。
								traderecord
										.setCreatetime(new Timestamp(System.currentTimeMillis()));
								traderecord.setOrdercode(order.getOrdernumber());
								traderecord.setPayname("首信易支付");
								traderecord.setPaytype(2);// 0支付宝 1财付通 2首信易支付
//								traderecord.setRetcode(royalty_parameters);
								if("0".equals(pstatus[i])){
									traderecord.setState(0);// 0等待支付1支付成功2支付失败
								}else if("1".equals(pstatus[i])){
									traderecord.setState(1);// 0等待支付1支付成功2支付失败
								}else{
									traderecord.setState(2);// 0等待支付1支付成功2支付失败
								}
								
								traderecord.setTotalfee(Float.parseFloat(amount[i]));// 支付金额分为单位
								traderecord.setType(1);// 订单类型
								traderecord.setPaymothed("网上支付");// 支付方式
								if("1".equals(pstatus[i])){
									traderecord.setBankcode(pmode[i]);// 支付银行
								}
								traderecord = Server.getInstance().getMemberService()
										.createTraderecord(traderecord);
								System.out.println(traderecord.getId());
							} catch (Exception e) {
								logger.info("交易记录失败", e.fillInStackTrace());
			
								// return;
							}
							try {
								Float totalprice = order.getTotalticketprice()+order.getTotalairportfee()+order.getTotalfuelfee();
								if(order.getTotalanjian()!=null){
									totalprice+=order.getTotalanjian();
								}
								if(order.getTotalotherfee()!=null){
									totalprice+=order.getTotalotherfee();
								}
								if(order.getTotalinsurprice()!=null){
									totalprice+=order.getTotalinsurprice();
								}
								if(order.getTotalgeneralprice()!=0){
									totalprice+=order.getTotalgeneralprice();
								}
								if(order.getTotalbusinessprice()!=0){
									totalprice+=order.getTotalbusinessprice();
								}
//								如果支付价格大于订单的未加支付手续费的订单总价格 设置总支付手续费
								if(Float.parseFloat(amount[i])-totalprice>=0){
									order.setTotalpayprice(Float.parseFloat(amount[i])-totalprice);
								}
								logger.info("订单" + orderid + "交易成功！");
								String sql = "UPDATE T_ORDERINFO SET C_TRADENO='"
										+ oid[i]
										+ "', C_ORDERSTATUS=2,C_PAYMETHOD="+Paymentmethod.PAYEASE+",C_PAYSTATUS=1,C_PAYTIME='"
										+ new Timestamp(System.currentTimeMillis())
										+ "',C_PAYMENTURL = '"+order.getTotalpayprice()+"', C_TOTALPAYPRICE ='"+order.getTotalpayprice()+"' WHERE C_ORDERNUMBER='"
										+ orderid.trim()
										+ "'";
								logger.info("修改订单：" + sql);
								service.findMapResultBySql(sql, null);
								String tsql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='"
										+ new Timestamp(System.currentTimeMillis())
										+ "' WHERE C_ORDERCODE='"
										+ orderid
										+ "'";
								service.findMapResultBySql(tsql, null);
								logger.info("修改交易记录" + tsql);
										Orderinforc rc=new Orderinforc();
										rc.setOrderinfoid(order.getId());
										rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
										rc.setCustomeruserid(order.getSaleagentid());
										rc.setContent("订票方完成支付，外部交易号："+oid[i]);
										rc.setState(888);
										Server.getInstance().getAirService().createOrderinforc(rc);
																				
							}catch (Exception e) {
								// TODO: handle exception
							}			  
					  }
				  }
			  }
			  out.print("sent");
		  }else{
			  System.out.println("验证失败！");
			  out.print("error");
		  }
		  out.close();
		

	}

}

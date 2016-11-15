package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.orderinforc.Orderinforc;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.base.traderecord.Traderecord;
import com.ccservice.b2b2c.ben.B2cAgent;
import com.ccservice.b2b2c.ben.Paymentmethod;

public class PayEasePage extends HttpServlet {
	Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		// TODO Auto-generated method stub
		  System.out.println("进来了");
		  String v_oid = req.getParameter("v_oid");//订单编号
		  String v_pmode = req.getParameter("v_pmode");//支付方式
		  String v_pstatus = req.getParameter("v_pstatus");//支付结果 20支付成功 30 支付失败
		  String v_pstring = req.getParameter("v_pstring");//支付结果信息说明
		  String v_amount = req.getParameter("v_amount");//订单金额
		  String v_moneytype = req.getParameter("v_moneytype");//币种
		  if(v_oid!=null && !"".equals(v_oid)){
			  String orderid = v_oid.split("-")[2];
			  logger.info("首信易交易处理订单：" + orderid);
			  System.out.println(orderid);
			  ISystemService service = Server.getInstance().getSystemService();
			  Orderinfo order = (Orderinfo) Server.getInstance().getAirService()
				.findAllOrderinfo("WHERE C_ORDERNUMBER='" + orderid.trim() + "'", "",
						-1, 0).get(0);
			 if (order.getPaystatus() == 0&&order.getOrderstatus()==1) {
				// 写入支付记录
					try {
						Traderecord traderecord = new Traderecord();
						traderecord.setCreateuser("payEase");
						traderecord.setGoodsname("机票");
						traderecord.setCode(v_oid);// 外部订单号。
						traderecord
								.setCreatetime(new Timestamp(System.currentTimeMillis()));
						traderecord.setOrdercode(order.getOrdernumber());
						traderecord.setPayname("首信易支付");
						traderecord.setPaytype(2);// 0支付宝 1财付通 2首信易支付
//						traderecord.setRetcode(royalty_parameters);
						if("0".equals(v_pstatus)){
							traderecord.setState(0);// 0等待支付1支付成功2支付失败
						}else if("20".equals(v_pstatus)){
							traderecord.setState(1);// 0等待支付1支付成功2支付失败
						}else{
							traderecord.setState(2);// 0等待支付1支付成功2支付失败
						}
						
						traderecord.setTotalfee(Float.parseFloat(v_amount));// 支付金额分为单位
						traderecord.setType(1);// 订单类型
						traderecord.setPaymothed("网上支付");// 支付方式
						if("20".equals(v_pstatus)){
							traderecord.setBankcode(v_pmode);// 支付银行
						}
						traderecord = Server.getInstance().getMemberService()
								.createTraderecord(traderecord);
						System.out.println(traderecord.getId());
					} catch (Exception e) {
						logger.info("交易记录失败", e.fillInStackTrace());
	
						// return;
					}
					try {
						//计算订单的支付费用 = 实际支付价格-订单总价格
						//订单总价格 
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
//						如果支付价格大于订单的未加支付手续费的订单总价格 设置总支付手续费
						if(Float.parseFloat(v_amount)-totalprice>=0){
							order.setTotalpayprice(Float.parseFloat(v_amount)-totalprice);
						}
//						if(order.getTotalpayprice()!=0){
//							totalprice+=order.getTotalpayprice();
//						}
						logger.info("订单" + orderid + "交易成功！");
						String sql = "UPDATE T_ORDERINFO SET C_TRADENO='"
								+ v_oid
								+ "', C_ORDERSTATUS=2,C_PAYMETHOD="+Paymentmethod.PAYEASE+",C_PAYSTATUS=1,C_PAYTIME='"
								+ new Timestamp(System.currentTimeMillis())
								+ "',C_PAYMENTURL = '"+order.getTotalpayprice()+"', C_TOTALPAYPRICE ='"+order.getTotalpayprice()+"' WHERE C_ORDERNUMBER='"
								+ orderid.trim()
								+ "' ";
						logger.info("修改订单：" + sql);
						service.findMapResultBySql(sql, null);
						String tsql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='"
								+ new Timestamp(System.currentTimeMillis())
								+ "' WHERE C_ORDERCODE='"
								+ orderid
								+ "'";
						service.findMapResultBySql(tsql, null);
						logger.info("修改交易记录" + tsql);
							//B2cAgent b2cAgent = (B2cAgent) req.getSession().getAttribute("b2cAgent");
								Orderinforc rc=new Orderinforc();
								rc.setOrderinfoid(order.getId());
								rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
								rc.setCustomeruserid(order.getSaleagentid());
								rc.setContent("订票方完成支付，外部交易号："+v_oid);
								rc.setState(888);
								Server.getInstance().getAirService().createOrderinforc(rc);
								String[] mobiles = { order.getContactmobile() };
								String content="尊敬的客户您好，您的机票订单："+order.getOrdernumber()+",已经支付成功。";
								//Server.getInstance().getAtomService().sendSms(mobiles, content, 0l, String.valueOf(b2cAgent.getAgentid()), b2cAgent, 0);
								resp.sendRedirect("paysuc.jsp");
					}catch (Exception e) {
						// TODO: handle exception
						resp.sendRedirect("pay_filed.jsp");
					}	
					
			  }else if(order.getPaystatus() == 1&&order.getOrderstatus()==2){
				  System.out.println("已经支付了");
				  resp.sendRedirect("pay_filed.jsp");
			  }else if(order.getPaystatus() == 2){
				  System.out.println("已退款了");
				 resp.sendRedirect("pay_filed.jsp");
			}
			  
		  }else{
			  resp.sendRedirect("pay_filed.jsp");
		  }
		/*
		 * try {
			// 注册完成之后发送的短信
			b2cAgent = (B2cAgent) session.getAttribute("b2cAgent");
			String smstemplet = "";
			smstemplet = SMSTemplet.getSMSTemplet(SMSType.REGISTRATION);
			smstemplet = smstemplet.replace("[用户名]", customeruser
					.getLoginname());
			Server.getInstance().getAtomService().sendSms(
					new String[] { "" + customeruser.getMobile() + "" },
					smstemplet, 0, String.valueOf(b2cAgent.getAgentid()),
					b2cAgent, 0);

			System.out.println("=========短信发送成功=========");
		} catch (Exception e) {
			System.out.println("发送短信失败");
		}
		 */
		
	}

}

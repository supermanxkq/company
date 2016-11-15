package com.ccservice.b2b2c.framework;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;

public class Treadenotify {
  static	Logger logger = Logger.getLogger(Treadenotify.class.getSimpleName());
	
	/**
	 * 订单拒绝通知到相应的航天华有客户
	 * 
	 * @param order
	 */
	public  static String  refuseRefundnotify(long orderid) {
		logger.error(orderid+"拒单退款交易通知");
		String result="-1";
		try {
			Orderinfo order = Server.getInstance().getAirService()
					.findOrderinfo(orderid);
			if(order.getOrdertype()==5){
			Customeragent customeragent = Server.getInstance()
					.getMemberService().findCustomeragent(order.getBuyagentid());
			String url =customeragent.getAgentother()+"?service=order_refuse_notify&order_no="
					+ order.getExtorderid() + "&yeebookingOrdernum="
					+ order.getOrdernumber() + "&reasondesc=&paystatus="+order.getPaystatus();
			logger.error(orderid+"YDX拒单退款通知：" + url);
			
				result = URLSupport.request(url);
			logger.error("YDX拒单：" + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}

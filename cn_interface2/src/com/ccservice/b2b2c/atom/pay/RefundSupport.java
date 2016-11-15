package com.ccservice.b2b2c.atom.pay;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.refund.helper.Refundhelper;
import com.ccservice.b2b2c.atom.refund.helper.Refundinfo;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.Refundtrade;

/**
 * @author hanmh
 * 退款父类
 *
 */
public abstract class RefundSupport {
	public HttpServletRequest request;	
	public HttpServletResponse response;
	public Refundhelper refundhelper;
	public RefundSupport(HttpServletRequest request,HttpServletResponse response,Refundhelper refundhelper){
		this.request=request;
		this.response=response;
		this.refundhelper=refundhelper;
	}
	
	public Refundtrade createRefundtrade(String detail_data,
			List<Refundinfo> reundinfos) {
		Refundtrade refundtrade = new Refundtrade();
		refundtrade.setHandleclass(refundhelper.getProfitHandle()
				.getSimpleName());
		refundtrade.setOrderid(refundhelper.getOrderid());
		refundtrade.setBtype(refundhelper.getTradetype());
		float totalrefundprice = 0;
		for (Refundinfo reundinfo : reundinfos) {
			totalrefundprice += reundinfo.getRefundprice();
		}
		refundtrade.setRefundprice(totalrefundprice);
		refundtrade.setMemo("退款数据集" + detail_data);
		return Server.getInstance().getMemberService().createRefundtrade(
				refundtrade);
	}


}

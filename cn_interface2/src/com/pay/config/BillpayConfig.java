package com.pay.config;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.PayInfo;


/**
 * 快钱 支付商家信息
 * @author Administrator
 *
 */
public class BillpayConfig extends PayConfig{
	
	private BillpayConfig(){};
	private static BillpayConfig billpayconfig=null;
	public static BillpayConfig getInstance(){
		if(billpayconfig==null){
			billpayconfig = new BillpayConfig();
			PayInfo payinfo=Server.getInstance().getB2BSystemService().findPayInfoBytypeId(2);
			billpayconfig.setKey(payinfo.getKey());
			billpayconfig.setPartnerID(payinfo.getPartnerId());
			billpayconfig.setSellerEmail(payinfo.getSellerEmail());
			billpayconfig.setRefundkey(payinfo.getRefundkey());
			billpayconfig.setSelfaccount(payinfo.getAccounttype()==0);
		}
		return billpayconfig;
	}
 
    public String refundkey ;
  

	

	public String getRefundkey() {
		return refundkey;
	}

	public void setRefundkey(String refundkey) {
		this.refundkey = refundkey;
	}

	

}

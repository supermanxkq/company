package com.ccservice.b2b2c.atom.refund.helper;

import java.util.Map;

public class Refundinfo {
	public String  tradeno;
	
	public float   refundprice;
	
	public Map<String,Float> Royalty_parameters;

	public String getTradeno() {
		return tradeno;
	}

	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}

	public float getRefundprice() {
		return refundprice;
	}

	public void setRefundprice(float refundprice) {
		this.refundprice = refundprice;
	}

	public Map<String, Float> getRoyalty_parameters() {
		return Royalty_parameters;
	}

	public void setRoyalty_parameters(Map<String, Float> royalty_parameters) {
		Royalty_parameters = royalty_parameters;
	}


}

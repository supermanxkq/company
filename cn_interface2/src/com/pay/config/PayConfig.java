package com.pay.config;

/**
 * @author hanmh
 * 账户配置信息父类
 *
 */
public abstract class PayConfig {
	
	  private  String partnerID; //ht"2088701454373226";
	  private  String key ;//HT"c9c0w3opz4wdmv6fjbxmtybvlg0t66b6";
	  private  String sellerEmail ;//"hyccservice@126.com";
	  public   boolean selfaccount;
	
	public String getPartnerID() {
		return partnerID;
	}
	public void setPartnerID(String partnerID) {
		this.partnerID = partnerID;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSellerEmail() {
		return sellerEmail;
	}
	public void setSellerEmail(String sellerEmail) {
		this.sellerEmail = sellerEmail;
	}
	public boolean isSelfaccount() {
		return selfaccount;
	}
	public void setSelfaccount(boolean selfaccount) {
		this.selfaccount = selfaccount;
	}
	
	

}

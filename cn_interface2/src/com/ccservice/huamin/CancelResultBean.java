package com.ccservice.huamin;

import java.io.Serializable;

/**
 * 
 * @author wzc
 * 华闵取消订单返回信息封装类
 *
 */
public class CancelResultBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public String waicode;
	public String bookstatus;//预订状态
	public String errorcode;//错误状态
	public String errormsg;//错误信息
	
	
	public String getWaicode() {
		return waicode;
	}
	public void setWaicode(String waicode) {
		this.waicode = waicode;
	}
	public String getBookstatus() {
		return bookstatus;
	}
	public void setBookstatus(String bookstatus) {
		this.bookstatus = bookstatus;
	}
	public String getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	
}

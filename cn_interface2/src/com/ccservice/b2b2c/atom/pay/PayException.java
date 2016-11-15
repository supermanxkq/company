package com.ccservice.b2b2c.atom.pay;

@SuppressWarnings("serial")
public class PayException extends Exception{
	
	private String message;
	
	public PayException(String exmessage){
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}
	private  void setMessage(String message) {
		this.message= message;
	}

}

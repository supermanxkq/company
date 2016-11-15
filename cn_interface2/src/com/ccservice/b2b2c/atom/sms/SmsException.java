package com.ccservice.b2b2c.atom.sms;

public class SmsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6546878793238816416L;

	public SmsException() {
		super();
	}

	public SmsException(String message, Throwable cause) {
		super(message, cause);
	}

	public SmsException(String message) {
		super(message);
	}

	public SmsException(Throwable cause) {
		super(cause);
	}

}

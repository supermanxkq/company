package com.ccservice.b2b2c.atom.mail;

public class MailException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3127232628823571005L;

	public MailException() {
		super();
	}

	public MailException(String message, Throwable cause) {
		super(message, cause);
	}

	public MailException(String message) {
		super("发送邮件异常\n" + message);
	}

	public MailException(Throwable cause) {
		super(cause);
	}

}

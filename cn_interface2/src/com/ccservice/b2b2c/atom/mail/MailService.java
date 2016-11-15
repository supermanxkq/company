package com.ccservice.b2b2c.atom.mail;

public interface MailService {
	/**
	 * 发送纯文本邮件
	 * 
	 * @param mails           收件人组
	 * @param subject    	  邮件主题
	 * @param content		  邮件正文
	 * @return
	 */
	public void sendSimpleMails(String[] mails, String subject, String content);

	/**
	 * 发送HTML格式邮件
	 * 
	 * @param mails           收件人组
	 * @param subject    	  邮件主题
	 * @param content		  邮件正文
	 * @return
	 * @throws MailException
	 */
	public void sendHTMLMails(String[] mails, String subject, String content)
			throws MailException;

	/**
	 * 发送带附件邮件
	 * 
	 * @param mails           收件人组
	 * @param subject    	  邮件主题
	 * @param content		  邮件正文
	 * @param filepaths       附件路径组
	 * @return
	 * @throws MailException
	 */
	public void sendAttachmentMails(String[] mails, String subject,
			String content, String[] filepaths)
			throws MailException;
}

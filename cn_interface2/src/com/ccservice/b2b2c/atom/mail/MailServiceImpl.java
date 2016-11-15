package com.ccservice.b2b2c.atom.mail;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailServiceImpl implements MailService {

	private JavaMailSender sender = null;
	// 发件人地址
	private String from;
	// 发件人别名
	private String alias;
	
	/**
	 * 发送普通文本邮件
	 * 
	 * @param mails
	 *            收件人地址组
	 * @param content
	 *            邮件正文
	 * @param subject
	 *            邮件标题
	 */
	public void sendSimpleMails(String[] mails, String subject, String content) {
		SimpleMailMessage smm = new SimpleMailMessage();
		for (int i = 0; i < mails.length; i++) {
			String to = mails[i];
			smm.setFrom(from);
			smm.setTo(to);
			smm.setSubject(subject);
			smm.setText(content);
			sender.send(smm);
		}
	}
	
	/**
	 * 发送HTML格式的邮件
	 * 
	 * @param mails
	 *            收件人地址组
	 * @param content
	 *            邮件正文
	 * @param subject
	 *            邮件标题
	 * @throws MailException
	 */
	public void sendHTMLMails(String[] mails, String subject, String content)
			throws MailException {
		MimeMessage message = sender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, false,
					"UTF-8");
			
			StringBuffer sb = new StringBuffer();
			// 解析收件人地址
			if (mails != null && mails.length > 0) {
				for (String m : mails) {
					sb.append(m + ",");
				}
				helper.setTo(InternetAddress.parse(sb.toString()));
			}
			helper.setSubject(subject);
			helper.setText(content, true); // true表示为HTML邮件
			helper.setFrom(new InternetAddress(from, MimeUtility.encodeText(alias,
					"UTF-8", "b")));
			
			sender.send(message);
		} catch (MessagingException e) {
			throw new MailException("发送HTML格式邮件时出错，错误信息：" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new MailException("发送HTML格式邮件时出错，错误信息：" + e.getMessage());
		}
	}
	
	/**
	 * 发送带附件的邮件
	 */
	public void sendAttachmentMails(String[] mails, String subject,
			String content, String[] filepaths)
			throws MailException {
		MimeMessage message = sender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true,
					"UTF-8");
			
			StringBuffer sb = new StringBuffer();
			// 解析收件人地址
			if (mails != null && mails.length > 0) {
				for (String m : mails) {
					sb.append(m + ",");
				}
				helper.setTo(InternetAddress.parse(sb.toString()));
			}
			helper.setSubject(subject);
			helper.setText(content, true); // true表示为HTML邮件
			helper.setFrom(new InternetAddress(from, MimeUtility.encodeText(alias,
					"UTF-8", "b")));
			
			// 添加附件
			for (int i = 0; i < filepaths.length; i++) {
				String fp = filepaths[i];
				FileSystemResource file = new FileSystemResource(new File(fp));
				helper.addAttachment(MimeUtility.encodeWord(file.getFilename()), file.getFile());
			}
			sender.send(message);
		} catch (MessagingException e) {
			throw new MailException("发送带附件的邮件时出错，错误信息：" + e.getMessage());
		} catch (IOException e) {
			throw new MailException("解析附件时出错，错误信息：" + e.getMessage());
		}
	}
	
	public JavaMailSender getSender() {
		return sender;
	}

	public void setSender(JavaMailSender sender) {
		this.sender = sender;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}

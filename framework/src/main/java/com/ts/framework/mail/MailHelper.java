package com.ts.framework.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * 邮件工具
 * @author wl
 */
@Component
public class MailHelper {
	private static final Logger logger = LoggerFactory.getLogger(MailHelper.class);
	private String sendMail;
	private JavaMailSenderImpl javaMailSender;

	public void openMail(String mailConfig) throws IOException {
		Properties properties = new Properties();
		properties.load(ClassLoader.getSystemResource(mailConfig).openStream());

		sendMail = properties.getProperty("account");

		javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setJavaMailProperties(properties);
		javaMailSender.setPassword(sendMail);
		javaMailSender.setPassword(properties.getProperty("password"));
		javaMailSender.setDefaultEncoding("utf-8");
	}

	/**
	 * 发送邮件
	 * @param receiveMail 接收邮箱
	 * @param title 邮件标题
	 * @param text 邮件内容
	 */
	public void sendMail(String receiveMail, String title, String text) {
		if (javaMailSender == null) {
			return;
		}
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(title);
		message.setText(text);
		message.setFrom(sendMail);
		message.setTo(receiveMail);
		javaMailSender.send(message);
	}

}

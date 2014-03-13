package com.czt.log.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
    public static void mail(MailConfigInfo configInfo, String subject, String text){
        
    	try {
			//根据规范设置key=value形式的环境变量
			Properties props = new Properties();
			props.setProperty("mail.host", configInfo.getHost());
			props.setProperty("mail.smtp.port", configInfo.getPort());
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.debug", "true");
			Session session = Session.getInstance(props);
			//创建发送器
			Transport ts = session.getTransport();
			ts.connect(configInfo.getUserName(), configInfo.getPassword());
			//创建邮件对象
			MimeMessage mm = new MimeMessage(session);
			mm.setFrom(new InternetAddress(configInfo.getUserName()));
			mm.setRecipients(Message.RecipientType.TO,
					new InternetAddress[] { new InternetAddress(configInfo.getToEmail()) });
			mm.setSubject(subject);
			mm.setText(text);
			mm.saveChanges();
			ts.sendMessage(mm, mm.getAllRecipients());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public static class MailConfigInfo{
    	private String mSmtpHost;
    	private String mSmtpPort;
    	private String mUserName;
    	private String mPassword;
    	private String mToEmail;
    	public MailConfigInfo(String smtpHost, String smtpPort, String userName, String password, String toEmail){
    		mSmtpHost = smtpHost;
    		mSmtpPort = smtpPort;
    		mUserName = userName;
    		mPassword = password;
    		mToEmail = toEmail;
    	}
    	public String getHost(){
    		return mSmtpHost;
    	}
    	public String getPort(){
    		return mSmtpPort;
    	}
    	public String getUserName(){
    		return mUserName;
    	}
    	public String getPassword(){
    		return mPassword;
    	}
    	public String getToEmail(){
    		return mToEmail;
    	}
    }
}

package com.czt.log;

import java.util.ResourceBundle;

import android.content.Context;

import com.czt.log.core.CrashHandler;
import com.czt.log.util.MailUtil.MailConfigInfo;


public class LogExceptionLibrary {
	public static void init(Context ctx){
		try {
			CrashHandler crashHandler = CrashHandler.getInstance(); 
			ResourceBundle localResource = ResourceBundle.getBundle("config"); 
			String smtpHost = localResource.getString("smtp_host");
			String smtpPort = localResource.getString("smtp_port");
			String userName = localResource.getString("user");
			String password = localResource.getString("password");
			String toEmail = localResource.getString("wiz_group_email");
			MailConfigInfo info = new MailConfigInfo(smtpHost, smtpPort, userName, password, toEmail);
			crashHandler.init(ctx, info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

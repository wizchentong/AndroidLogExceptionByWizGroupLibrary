package com.czt.log.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;

import com.czt.log.util.MD5Util;
import com.czt.log.util.MailUtil;
import com.czt.log.util.MailUtil.MailConfigInfo;
/**
 * 异常日志处理核心
 * @author 陈潼
 *
 */
public class LogExceptionCore {
	public static void logException(Context ctx, Throwable ex, MailConfigInfo configInfo){
		logException(ctx, null, ex, configInfo);
	}
	public static void logException(final Context ctx, final HashMap<String, String> messages, final Throwable ex, final MailConfigInfo configInfo) {
		//
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				logExceptionByMail(ctx, messages, ex, configInfo);
				return null;
			}
		}.execute();
	}
	private static void logExceptionByMail(Context ctx,
			HashMap<String, String> messages, Throwable ex, MailConfigInfo configInfo) {
		try {
			ConcurrentHashMap<String, String> infos = getDeviceInfo(ctx);
			//
			StringBuffer sb = new StringBuffer();
			sb.append("folder=/" + getVersionName(ctx) + "/ \n");
			for (Map.Entry<String, String> entry : infos.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				sb.append(key + "=" + value + "\n");
			}
			if (messages != null){
				for (Map.Entry<String, String> entry : messages.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					sb.append(key + "=" + value + "\n");
				}
			}

			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			printWriter.close();
			String result = writer.toString();
			sb.append(result);
			String errorMessage = sb.toString();
			String bugId = getBugId(result);
			String errorTitle = bugId + ";Version=" + getVersionName(ctx);
			MailUtil.mail(configInfo, errorTitle, errorMessage);
		}catch (Throwable e) {
		}
	}
	private static String getBugId(String exception){
		return "BugId=" + getExceptionMD5(exception);
	}
	private static String getExceptionMD5(String exception){
		String ignoreRegularExpression = "(at (com.android|android|java|dalvik)+[^\\n]+)|(\\.\\.\\. \\d+ more)";
		String tempKeyException = exception.replaceAll(ignoreRegularExpression, "").replaceAll("\\s+", "");
		return MD5Util.makeMD5(tempKeyException);
	}
	/**
	 * 收集设备参数信息
	 * @param ctx
	 */
	private static ConcurrentHashMap<String, String> mInfos = null;

	@SuppressLint("DefaultLocale")
	private synchronized static  ConcurrentHashMap<String, String> getDeviceInfo(Context ctx) {
		if (mInfos != null)
			return mInfos;
		//
		mInfos = new ConcurrentHashMap<String, String>();
		//
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				mInfos.put("VERSIONNAME", versionName);
				mInfos.put("VERSIONCODE", versionCode);
			}
		} catch (NameNotFoundException e) {
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mInfos.put(field.getName().toUpperCase(), field.get(null).toString());
			} catch (Exception e) {
			}
		}
		//
		return mInfos;
	}
	private synchronized static  String getVersionName(Context ctx) {
		
		try {
			ConcurrentHashMap<String, String> infos = getDeviceInfo(ctx);
			return infos.get("VERSIONNAME");
		} catch (Exception e) {
			return "";
		}
	}
}

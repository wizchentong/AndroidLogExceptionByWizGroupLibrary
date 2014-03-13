package com.czt.log.core;

import java.lang.Thread.UncaughtExceptionHandler;

import com.czt.log.util.MailUtil.MailConfigInfo;

import android.content.Context;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author 陈潼
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	
	//系统默认的UncaughtException处理类 
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	//CrashHandler实例
	private static final CrashHandler INSTANCE = new CrashHandler();
	//程序的Context对象
	private Context mContext;
	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}
	private MailConfigInfo mConfigInfo;
	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context, MailConfigInfo configInfo) {
		mContext = context;
		//获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		mConfigInfo = configInfo;
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		ex.printStackTrace();
		//
		LogExceptionCore.logException(mContext, ex, mConfigInfo);
		return true;
	}
}

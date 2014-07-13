package com.mipt.ottloghelper.common;

import com.mipt.ottloghelper.MainActivity;

public interface Constants {
	public static final String TAG = MainActivity.class.getSimpleName();

	public static final String LOGSWITCH = "logswitch";

	public static final String CMD_STRING = "cmd";

	public static final String PATH_STRING = "path";

	public static final String ACTION_LOGBROADCAST = "com.mipt.action.LOGBROADCAST";

	public final String LOGCONFIGUREPREF = "log_configure_preference";

	public final String LOGCOMMANDPRE = "log_command_preference";

	public final String DEFAULTCMD = "logcat";

	public final String LOGPATHPRE = "log_path_preference";

	public static final String DEFAULTPATH = "/mnt/sdcard/mipt_log.txt";

}

package com.mipt.ottloghelper;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;

import com.mipt.ottloghelper.common.Constants;

public class LogApplication extends Application {
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private String mCmd;
	private String mPath;

	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

	public WindowManager.LayoutParams getMywmParams() {
		return wmParams;
	}

	public String getCmd() {
		return mCmd;
	}

	public void setCmd(String cmd) {
		this.mCmd = cmd;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String path) {
		this.mPath = path;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		initPreferences();
	}

	private void initPreferences() {
		preferences = getSharedPreferences(Constants.LOGCONFIGUREPREF,
				MODE_PRIVATE);
		ReadConfPreferences();
	}

	public void ReadConfPreferences() {
		mCmd = preferences.getString(Constants.LOGCOMMANDPRE,
				Constants.DEFAULTCMD);
		mPath = preferences.getString(Constants.LOGPATHPRE,
				Constants.DEFAULTPATH);
	}

	public void WriteConfPreferences() {
		this.WriteConfPreferences(mCmd, mPath);
	}

	public void WriteConfPreferences(String cmd, String path) {
		if (null == editor) {
			Log.d(Constants.TAG, "init editor");
			editor = preferences.edit();
		}
		if ((null != cmd) && (null != path)) {
			this.mCmd = cmd;
			this.mPath = path;
			Log.d(Constants.TAG, "put string to preferences: cmd = " + cmd
					+ "; mPath = " + path);
			editor.putString(Constants.LOGCOMMANDPRE, cmd);
			editor.putString(Constants.LOGPATHPRE, path);

			editor.commit();
		}
	}
}

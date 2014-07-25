package com.mipt.ottloghelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;

import com.mipt.ottloghelper.common.Constants;

public class MainActivity extends Activity {
	private EditText mCmdText;
	private EditText mPathText;
	private Switch mLogSwitch;
	private Intent mlogServiceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);
		mlogServiceIntent = new Intent();
		mlogServiceIntent.setClass(this, LogHelperService.class);
		startService(mlogServiceIntent);
		initView();
		initEvent();
	}

	private void initView() {
		mCmdText = (EditText) findViewById(R.id.command);
		mPathText = (EditText) findViewById(R.id.savepath);
		mLogSwitch = (Switch) findViewById(R.id.logswitch);

		mCmdText.setText(((LogApplication) getApplication()).getCmd());
		mPathText.setText(((LogApplication) getApplication()).getPath());
	}

	private void initEvent() {
		mLogSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Intent intent = new Intent();

				intent.setAction(Constants.ACTION_LOGBROADCAST);
				intent.putExtra(Constants.LOGSWITCH, isChecked);

				Log.d(Constants.TAG, "mLogSwitch isChecked:" + isChecked);

				if (isChecked) {
					String cmd = null;
					String path = null;
					
					mCmdText.setEnabled(false);
					mPathText.setEnabled(false);

					if (null == mCmdText.getText().toString()) {
						mCmdText.setText(((LogApplication) getApplication())
								.getCmd());
					}
					cmd = mCmdText.getText().toString();

					if (null == mPathText.getText().toString()) {
						mPathText.setText(((LogApplication) getApplication())
								.getPath());
					}
					path = mPathText.getText().toString();

					intent.putExtra(Constants.CMD_STRING, cmd);
					intent.putExtra(Constants.PATH_STRING, path);
				} else {
					mCmdText.setEnabled(true);
					mPathText.setEnabled(true);
				}

				MainActivity.this.sendBroadcast(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		stopService(mlogServiceIntent);
	}
}

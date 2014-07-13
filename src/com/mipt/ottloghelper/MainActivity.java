package com.mipt.ottloghelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;

import com.mipt.ottloghelper.common.Constants;

public class MainActivity extends Activity implements OnClickListener {
	private EditText mCmdText;
	private EditText mPathText;
	private Switch mLogSwitch;
	private Intent mlogServiceIntent;
	private Button mButton;

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
		mButton = (Button) findViewById(R.id.button);

		mCmdText.setText(((LogApplication) getApplication()).getCmd());
		mPathText.setText(((LogApplication) getApplication()).getPath());
	}

	private void initEvent() {
		mButton.setOnClickListener(this);
		mLogSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Intent intent = new Intent();

				intent.setAction(Constants.ACTION_LOGBROADCAST);
				intent.putExtra(Constants.LOGSWITCH, isChecked);

				if (isChecked) {
					Log.d(Constants.TAG, "mLogSwitch isChecked:" + isChecked);
					
					intent.putExtra(Constants.CMD_STRING,
							((LogApplication) getApplication()).getCmd());
					intent.putExtra(Constants.PATH_STRING,
							((LogApplication) getApplication()).getPath());
				}

				MainActivity.this.sendBroadcast(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button:
			String cmd = mCmdText.getText().toString();
			String path = mPathText.getText().toString();

			Intent intent = new Intent();
			intent.setAction(Constants.ACTION_LOGBROADCAST);
			intent.putExtra(Constants.LOGSWITCH, true);
			intent.putExtra(Constants.CMD_STRING, cmd);
			intent.putExtra(Constants.PATH_STRING, path);

			MainActivity.this.sendBroadcast(intent);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		stopService(mlogServiceIntent);
	}
}

package com.mipt.ottloghelper;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;

import com.mipt.ottloghelper.common.Constants;
import com.mipt.ottloghelper.common.LogcatTool;

@SuppressLint("HandlerLeak")
public class LogHelperService extends Service {
	WindowManager wm = null;
	WindowManager.LayoutParams ballWmParams = null;

	private View mBallView;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	private Button myFloatBall;
	private boolean ismoving = false;

	private LogSwitchReciver logSwitchReciver;
	private final int STARTON = 1;
	private final int STOPOFF = 2;

	private Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();

		mContext = this;
		mBallView = LayoutInflater.from(this).inflate(R.layout.floatserver,
				null);
		myFloatBall = (Button) mBallView.findViewById(R.id.float_image);
		createView();

		registerLogReciver();
	}

	private void registerLogReciver() {
		logSwitchReciver = new LogSwitchReciver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_LOGBROADCAST);

		registerReceiver(logSwitchReciver, filter);
	}

	private void startLog(final String cmd, final String path) {

		LogcatTool.closeReader();
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (!"ON".equals(myFloatBall.getText())) {
						mHandler.sendEmptyMessage(STARTON);
					}
					if (LogcatTool.sendCommand(cmd, path) == true) {
						Log.d(Constants.TAG, "receive broadcast: cmd = " + cmd
								+ "; mPath = " + path);
						LogApplication logApplication = (LogApplication) mContext
								.getApplicationContext();
						logApplication.WriteConfPreferences(cmd, path);
					}else {
						Log.d(Constants.TAG, "return false");
					}

				} catch (Exception e) {
					Log.d(Constants.TAG, "startLog occurs error" + e.toString());
					e.printStackTrace();
				}
			}
		});

		thread.start();
	}

	private void stopLog() {
		LogcatTool.closeReader();
		mHandler.sendEmptyMessage(STOPOFF);
	}

	class LogSwitchReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.ACTION_LOGBROADCAST.equals(intent.getAction())) {
				boolean bLogSwitch = intent.getBooleanExtra(
						Constants.LOGSWITCH, false);
				String sCmd = intent.getStringExtra(Constants.CMD_STRING);
				String sPath = intent.getStringExtra(Constants.PATH_STRING);

				if (bLogSwitch) {
					startLog(sCmd, sPath);
				} else {
					stopLog();
				}
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(logSwitchReciver);
	}

	/**
	 * 通过MyApplication创建view，并初始化显示参数
	 */
	private void createView() {
		Point size = new Point();
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getSize(size);

		ballWmParams = ((LogApplication) getApplication()).getMywmParams();
		ballWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		ballWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		ballWmParams.gravity = Gravity.LEFT | Gravity.TOP;
		ballWmParams.x = 0;
		ballWmParams.y = size.y / 2;
		ballWmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		ballWmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		ballWmParams.format = PixelFormat.RGBA_8888;

		// 添加显示层
		wm.addView(mBallView, ballWmParams);

		// 注册触碰事件监听器
		myFloatBall.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getRawX();
				y = event.getRawY();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					ismoving = false;
					mTouchStartX = (float) event.getX();
					mTouchStartY = (float) event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					ismoving = true;
					updateViewPosition();
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				// 如果拖动则返回false，否则返回true
				if (ismoving == false) {
					return true;
				} else {
					return true;
				}
			}

		});
	}

	/**
	 * 更新view的显示位置
	 */
	private void updateViewPosition() {
		ballWmParams.x = (int) (x - mTouchStartX);
		ballWmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(mBallView, ballWmParams);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STARTON:
				if (myFloatBall.getText() != "ON") {
					myFloatBall.setText("ON");
					myFloatBall.setBackgroundColor(0xff0000);
				}
				break;

			case STOPOFF:
				myFloatBall.setText("OFF");
				myFloatBall.setBackgroundColor(0x333333);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}

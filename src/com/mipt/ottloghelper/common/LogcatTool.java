package com.mipt.ottloghelper.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import android.util.Log;

public class LogcatTool {
	private static BufferedReader reader = null;

	public static void closeReader() {
		if (null != reader) {
			try {
				reader.close();
				reader = null;
			} catch (Exception e) {
				Log.d(Constants.TAG,
						"closeReader occurs error: " + e.toString());
			}
		}
	}

	public static boolean closeLogcat() {
		Process process = null;
		DataOutputStream dos = null;
		BufferedReader br = null;

		try {
			process = Runtime.getRuntime().exec("su");
			dos = new DataOutputStream(process.getOutputStream());
			br = new BufferedReader(new InputStreamReader(new DataInputStream(
					process.getInputStream()), "UTF-8"));

			dos.writeBytes("ps | grep logcat \n");
			dos.writeBytes("exit\n");
			dos.flush();

			String str = null;

			while ((str = br.readLine()) != null) {
				int logPid = parseLogPID(str);
				if (logPid > 0 && logPid < 0x7fff) {
					dos.writeBytes("kill -9 " + logPid + " \n");
					dos.writeBytes("exit \n");
					dos.flush();
				}
			}

		} catch (Exception e) {
			Log.d(Constants.TAG,
					"closeLogcat Close source occurs error" + e.toString());
			return false;
		} finally {
			try {
				if (null != process) {
					process.destroy();
				}
				if (null != dos) {
					dos.close();
				}
			} catch (Exception exception) {
				Log.d(Constants.TAG,
						"Close source occurs error" + exception.toString());
				return false;
			}
		}

		return true;
	}

	public static boolean sendCommand(String cmd, String path) {
		Process process = null;
		BufferedWriter bw = null;
		DataOutputStream os = null;

		if (!(cmd.length() < 6 && cmd.substring(0, 6).equals("logcat"))) {
			return false;
		}
		try {
			bw = new BufferedWriter(new FileWriter(path, true));
			bw.write("\n -----> Mipt log start: " + cmd + " <-----\n");

			process = Runtime.getRuntime().exec("su");

			os = new DataOutputStream(process.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(
					new DataInputStream(process.getInputStream()), "UTF-8"));

			os.writeBytes("cmd \n");
			os.writeBytes("exit\n");
			os.flush();

			char[] readlog = new char[1024];
			int len = 0;

			while ((null != reader) && (len = reader.read(readlog)) != -1) {
				bw.write(readlog, 0, len);
				bw.flush();
			}
		} catch (Exception e) {
			Log.d(Constants.TAG,
					"sendCommand Close source occurs error" + e.toString());
			return false;
		} finally {
			try {
				if (null != os) {
					os.close();
				}
				if (null != reader) {
					reader.close();
					reader = null;
				}
				if (null != bw) {
					bw.close();
				}
				if (null != process) {
					process.destroy();
				}
			} catch (Exception e2) {
				Log.d(Constants.TAG,
						"Close source occurs error" + e2.toString());
				return false;
			}
		}
		return true;
	}

	public static int parseLogPID(String psStr) {
		char[] ch = psStr.toCharArray();
		int back = -1;
		int i = 0;

		while (i < ch.length) {
			if (back == -1) {
				if (ch[i] > '0' && ch[i] < '9') {
					back = ch[i] - '0';
				}
			} else {
				if (ch[i] != ' ') {
					back = back * 10 + (int) ch[i] - '0';
				} else {
					break;
				}
			}
			i++;
		}

		return back;
	}

}

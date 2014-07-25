package com.mipt.ottloghelper.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.util.Log;

public class LogcatTool {
	public static void closeLogcat() {
		setProperty("ctl.start", "kill-logcat");

		String ret = getProperty("init.svc.kill-logcat");

		if (null != ret) {
			if (!ret.equals("stopped")) {
				setProperty("ctl.stop", "kill-logcat");
			}
		}
	}

	public static final String getProperty(String key) {
		String id = "";

		try {
			Class<?> c = Class.forName(("android.os.SystemProperties"));
			Method m = c.getMethod("get", new Class[] { String.class,
					String.class });
			id = (String) m.invoke(c, new Object[] { key, "" });
		} catch (ClassNotFoundException cnfe) {
			Log.e("Error", "Error", cnfe);
		} catch (NoSuchMethodException nsme) {
			Log.e("Error", "Error", nsme);
		} catch (SecurityException se) {
			Log.e("Error", "Error", se);
		} catch (IllegalAccessException iae) {
			Log.e("Error", "Error", iae);
		} catch (IllegalArgumentException iarge) {
			Log.e("Error", "Error", iarge);
		} catch (InvocationTargetException ite) {
			Log.e("Error", "Error", ite);
		} catch (ClassCastException cce) {
			Log.e("Error", "Error", cce);
		} catch (Throwable th) {
			Log.e("Error", "Error: ", th);
		}

		return id;
	}

	public static final String setProperty(String key, String value) {
		String id = "";

		try {
			Class<?> c = Class.forName(("android.os.SystemProperties"));
			Method m = c.getMethod("set", new Class[] { String.class,
					String.class });
			id = (String) m.invoke(c, new Object[] { key, value });
		} catch (ClassNotFoundException cnfe) {
			Log.e("Error", "Error", cnfe);
		} catch (NoSuchMethodException nsme) {
			Log.e("Error", "Error", nsme);
		} catch (SecurityException se) {
			Log.e("Error", "Error", se);
		} catch (IllegalAccessException iae) {
			Log.e("Error", "Error", iae);
		} catch (IllegalArgumentException iarge) {
			Log.e("Error", "Error", iarge);
		} catch (InvocationTargetException ite) {
			Log.e("Error", "Error", ite);
		} catch (ClassCastException cce) {
			Log.e("Error", "Error", cce);
		} catch (Throwable th) {
			Log.e("Error", "Error: ", th);
		}

		return id;
	}

	public static boolean sendCommand(String cmd, String path) {
		Process process = null;
		BufferedWriter bw = null;
		DataOutputStream os = null;
		BufferedReader reader = null;

		if (!(cmd.length() > 5 && cmd.substring(0, 6).equals("logcat"))) {
			Log.d(Constants.TAG, "No command found, invalid command '" + cmd
					+ "'" + "; length = " + cmd.length() + "; substring = " + cmd.substring(0, 6));
			return false;
		}
		try {
			bw = new BufferedWriter(new FileWriter(path, true));
			bw.write("\n -----> Mipt log start: " + cmd + " <-----\n");

			process = Runtime.getRuntime().exec("sh");

			os = new DataOutputStream(process.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(
					new DataInputStream(process.getInputStream()), "UTF-8"));

			os.writeBytes(cmd +"\n");
			os.writeBytes("exit\n");
			os.flush();

			char[] readlog = new char[1024];
			int len = 0;

			while ((null != reader) && (len = reader.read(readlog)) != -1) {
				System.out.println("read successfully!!!");
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

	// public static int parseLogPID(String psStr) {
	// char[] ch = psStr.toCharArray();
	// int back = -1;
	// int i = 0;
	//
	// while (i < ch.length) {
	// if (back == -1) {
	// if (ch[i] > '0' && ch[i] < '9') {
	// back = ch[i] - '0';
	// }
	// } else {
	// if (ch[i] != ' ') {
	// back = back * 10 + (int) ch[i] - '0';
	// } else {
	// break;
	// }
	// }
	// i++;
	// }
	//
	// return back;
	// }

}

LogcatHelper
============
Redirect "logcat" to  destination path (DEFAULTPATH = "/mnt/sdcard/mipt_log.txt")

via init.rc set the "kill-logcat" service,

	service kill-logcat /system/bdroot/usr/bin/killall -9 logcat
		class main
		user root
		group system
		disabled
		oneshot

Try "\android\platform\system\core\init\Readme.txt" for more information.

LogcatHelper must have signature system

package com.skio.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	private static long lastShowTime = 0l;
	private static String lastShowMsg = null;
	private static String curShowMsg = null;
	private static int time=2000;

	public static void showToast(Context context, CharSequence s) {
		curShowMsg = s.toString();
		long curShowTime = System.currentTimeMillis();
		if (curShowMsg.equals(lastShowMsg)) {
			if (curShowTime - lastShowTime > time) {
				Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
				lastShowTime = curShowTime;
				lastShowMsg = curShowMsg;
			}
		} else {
			Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
			lastShowTime = curShowTime;
			lastShowMsg = curShowMsg;
		}
	}

}

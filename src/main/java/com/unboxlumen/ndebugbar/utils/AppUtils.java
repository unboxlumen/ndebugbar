package com.unboxlumen.ndebugbar.utils;

import android.os.SystemClock;

public class AppUtils {
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = SystemClock.elapsedRealtime();
        if (curClickTime - lastClickTime <= 500L) {
            flag = true;
        }

        lastClickTime = curClickTime;
        return flag;
    }
}


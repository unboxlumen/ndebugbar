package com.unboxlumen.ndebugbar.utils;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static final DateFormat DEFAULT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.getDefault());
    public static final DateFormat NO_MILLIS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final DateFormat HHMMSS = new SimpleDateFormat("HH:mm:ss SSS", Locale.getDefault());
    private static Context CONTEXT;
    private static Handler mainHandler;

    private Utils() {
    }

    public static void init(Context context) {
        CONTEXT = context.getApplicationContext();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @SuppressLint({"PrivateApi"})
    @NonNull
    public static Context getContext() {
        return CONTEXT;
    }

    public static <V> boolean isNotEmpty(List<V> sourceList) {
        return sourceList != null && sourceList.size() != 0;
    }

    public static <V> int getCount(V[] sourceList) {
        return sourceList != null && sourceList.length != 0 ? sourceList.length : 0;
    }

    public static <T> T[] newArray(T... value) {
        return value;
    }

    public static String millis2String(long millis) {
        return millis2String(millis, DEFAULT);
    }

    public static String millis2String(long millis, DateFormat format) {
        return format.format(new Date(millis));
    }

    public static void toast(@StringRes int resId) {
        ToastUtils.show(resId);
    }

    public static void toast(String msg) {
        ToastUtils.show(msg);
    }

    public static void copy2ClipBoard(String msg) {
        ClipboardManager cm = (ClipboardManager) CONTEXT.getSystemService("clipboard");

        try {
            ClipData mClipData = ClipData.newPlainText("text", msg);
            cm.setPrimaryClip(mClipData);
            toast("已复制到剪切板");
        } catch (Throwable t) {
            toast(t.getMessage());
        }

    }

    public static void post(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        mainHandler.postDelayed(runnable, delayMillis);
    }

    public static void cancelTask(Runnable runnable) {
        mainHandler.removeCallbacks(runnable);
    }

    public static String formatSize(long origin) {
        BigDecimal size = new BigDecimal(Long.toString(origin));
        String value;
        if (size.compareTo(new BigDecimal("1024")) < 0) {
            value = size + "B";
        } else {
            size = size.divide(new BigDecimal("1024"));
            if (size.compareTo(new BigDecimal("1024")) > 0) {
                value = size.divide(new BigDecimal("1024"), 2, 1) + "MB";
            } else {
                value = size.setScale(2, 1) + "KB";
            }
        }

        return value;
    }

    public static String formatDuration(long ms) {
        String time = "";
        ms /= 1000L;
        long hour = ms / 3600L;
        long mint = ms % 3600L / 60L;
        long sed = ms % 60L;
        if (hour > 0L) {
            String hourStr = String.valueOf(hour);
            time = time + hourStr + "h ";
        }

        if (mint > 0L) {
            String mintStr = String.valueOf(mint);
            time = time + mintStr + "m ";
        }

        if (sed > 0L) {
            String sedStr = String.valueOf(sed);
            time = time + sedStr + "s";
        }

        return time;
    }

    public static void removeViewFromWindow(View v) {
        try {
            WindowManager windowManager = (WindowManager) getContext().getSystemService("window");
            windowManager.removeView(v);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public static boolean addViewToWindow(View v, WindowManager.LayoutParams params) {
        try {
            if (isPermissionDenied()) {
                return false;
            } else {
                WindowManager windowManager = (WindowManager) getContext().getSystemService("window");
                windowManager.addView(v, params);
                return true;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            removeViewFromWindow(v);
            return false;
        }
    }

    public static void updateViewLayoutInWindow(View v, WindowManager.LayoutParams params) {
        try {
            WindowManager windowManager = (WindowManager) getContext().getSystemService("window");
            windowManager.updateViewLayout(v, params);
        } catch (Throwable var3) {
        }

    }

    private static boolean isPermissionDenied() {
        if (VERSION.SDK_INT >= 23) {
            return !Settings.canDrawOverlays(getContext());
        } else {
            if (VERSION.SDK_INT >= 19) {
                AppOpsManager appOpsMgr = (AppOpsManager) CONTEXT.getSystemService("appops");

                try {
                    int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", Process.myUid(), CONTEXT.getPackageName());
                    if (mode == 2) {
                        return true;
                    }
                } catch (Throwable var2) {
                }
            }

            if (!Config.ifPermissionChecked()) {
                Config.setPermissionChecked();
                return true;
            } else {
                return false;
            }
        }
    }

    public static List<String> getActivities() {
        List<String> result = new ArrayList();

        try {
            PackageManager packageManager = CONTEXT.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(CONTEXT.getPackageName(), 1);

            for (ActivityInfo activity : packageInfo.activities) {
                result.add(activity.name);
            }
        } catch (PackageManager.NameNotFoundException var7) {
        }

        Collections.sort(result);
        return result;
    }

    public static String collectThrow(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);

        for (Throwable cause = ex.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }

        printWriter.close();
        return writer.toString();
    }

    public static Context makeContextSafe(Context context) {
        if (context != null) {
            return context;
        } else {
            try {
                Class actThreadClass = Class.forName("android.app.ActivityThread");
                Method method = actThreadClass.getDeclaredMethod("currentApplication");
                return (Context) method.invoke((Object) null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}


package com.unboxlumen.ndebugbar.crash;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.unboxlumen.ndebugbar.cache.Crash;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler mInstance;
    private long launchTime;
    private Thread.UncaughtExceptionHandler defHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (mInstance == null) {
            mInstance = new CrashHandler();
        }

        return mInstance;
    }

    public void init(final Application app) {
        this.launchTime = System.currentTimeMillis();
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                app.unregisterActivityLifecycleCallbacks(this);
                CrashHandler.this.defHandler = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(CrashHandler.this);
            }

            public void onActivityStarted(Activity activity) {
            }

            public void onActivityResumed(Activity activity) {
            }

            public void onActivityPaused(Activity activity) {
            }

            public void onActivityStopped(Activity activity) {
            }

            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    public void uncaughtException(Thread t, Throwable e) {
        Crash.insert(e, this.launchTime);
        if (this.defHandler != null) {
            this.defHandler.uncaughtException(t, e);
        }

    }
}


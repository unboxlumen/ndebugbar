package com.unboxlumen.ndebugbar;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.unboxlumen.ndebugbar.cache.History;
import com.unboxlumen.ndebugbar.ui.Dispatcher;

public class HistoryRecorder implements Application.ActivityLifecycleCallbacks {
    private static final int CODE = 2;
    private Handler handler;
    private Activity topActivity;

    public HistoryRecorder(Application application) {
        WorkThread thread = new WorkThread();
        thread.start();
        this.handler = new Handler(thread.getLooper(), thread);
        application.registerActivityLifecycleCallbacks(this);
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        this.record(activity, "onCreate");
    }

    public void onActivityStarted(Activity activity) {
        this.record(activity, "onStart");
    }

    public void onActivityResumed(Activity activity) {
        this.record(activity, "onResume");
        if (!(activity instanceof Dispatcher)) {
            this.topActivity = activity;
        }

    }

    public void onActivityPaused(Activity activity) {
        this.record(activity, "onPause");
    }

    public void onActivityStopped(Activity activity) {
        this.record(activity, "onStop");
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        this.record(activity, "onSaveInstanceState");
    }

    public void onActivityDestroyed(Activity activity) {
        this.record(activity, "onDestroy");
        if (this.topActivity == activity) {
            this.topActivity = null;
        }

    }

    private void record(Activity activity, String event) {
        History history = new History();
        history.createTime = System.currentTimeMillis();
        history.activity = activity.getClass().getSimpleName();
        history.event = event;
        this.handler.sendMessage(Message.obtain(this.handler, 2, history));
    }

    public Activity getTopActivity() {
        return this.topActivity;
    }

    static class WorkThread extends HandlerThread implements Handler.Callback {
        WorkThread() {
            super("HistoryRecorder");
        }

        public boolean handleMessage(Message msg) {
            if (msg.what == 2) {
                History.insert((History) msg.obj);
            }

            return true;
        }
    }
}



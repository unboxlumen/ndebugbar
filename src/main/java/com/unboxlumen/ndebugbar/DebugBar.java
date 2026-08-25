package com.unboxlumen.ndebugbar;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.core.content.FileProvider;

import com.unboxlumen.ndebugbar.database.Databases;
import com.unboxlumen.ndebugbar.preference.SharedPref;
import com.unboxlumen.ndebugbar.crash.CrashHandler;
import com.unboxlumen.ndebugbar.network.OkHttpInterceptor;
import com.unboxlumen.ndebugbar.sensor.SensorDetector;
import com.unboxlumen.ndebugbar.utils.Utils;

public final class DebugBar extends FileProvider implements SensorDetector.Callback {
    private static DebugBar INSTANCE;
    private SharedPref sharedPref;
    private boolean notHostProcess;
    private OkHttpInterceptor interceptor;
    private Databases databases;
    private CrashHandler crashHandler;
    private HistoryRecorder historyRecorder;
    private FuncController funcController;
    private SensorDetector sensorDetector;

    public DebugBar() {
        if (INSTANCE != null) {
            throw new RuntimeException();
        }
    }

    public static DebugBar get() {
        if (INSTANCE == null) {
            DebugBar mB = new DebugBar();
            mB.notHostProcess = true;
            mB.onCreate();
        }

        return INSTANCE;
    }

    public OkHttpInterceptor getInterceptor() {
        return this.interceptor;
    }

    /** 旧名字，新逻辑在 toggleVisibility。 */
    @Deprecated
    public void shakeValid() {
        this.toggleVisibility();
    }

    public void toggleVisibility() {
        if (this.funcController.isOpen()) {
            this.close();
        } else {
            this.open();
        }
    }

    public boolean isOpen() {
        return this.funcController.isOpen();
    }

    public Activity getTopActivity() {
        return this.historyRecorder.getTopActivity();
    }

    public boolean onCreate() {
        INSTANCE = this;
        Context context = Utils.makeContextSafe(this.getContext());
        this.init((Application) context);
        return super.onCreate();
    }

    private void init(Application app) {
        Utils.init(app);
        this.funcController = new FuncController(app);
        this.sensorDetector = new SensorDetector(this.notHostProcess ? null : this);
        this.interceptor = new OkHttpInterceptor();
        this.databases = new Databases();
        this.sharedPref = new SharedPref();
        CrashHandler.getInstance().init(app);
        this.historyRecorder = new HistoryRecorder(app);
    }

    public void open() {
        if (!this.notHostProcess) {
            this.funcController.open();
        }
    }

    public void close() {
        this.funcController.close();
    }

    public void disableShakeSwitch() {
        this.sensorDetector.unRegister();
    }

    public Databases getDatabases() {
        return this.databases;
    }

    public SharedPref getSharedPref() {
        return this.sharedPref;
    }
}



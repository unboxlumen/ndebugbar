package com.unboxlumen.ndebugbar.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;

import com.unboxlumen.ndebugbar.recyclerview.MultiItemEntity;
import com.unboxlumen.ndebugbar.utils.Utils;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

@CacheDatabase.Table("log_crash")
public class Crash implements Serializable, MultiItemEntity {
    public static final int TYPE_CONTENT = 0;
    public static final int TYPE_TITLE = 1;
    @CacheDatabase.Column(
            value = "_id",
            primaryKey = true
    )
    public int id;
    @CacheDatabase.Column("createTime")
    public long createTime;
    @CacheDatabase.Column("startTime")
    public long startTime;
    @CacheDatabase.Column("type")
    public String type;
    @CacheDatabase.Column("cause")
    public String cause;
    @CacheDatabase.Column("stack")
    public String stack;
    @CacheDatabase.Column("versionCode")
    public int versionCode;
    @CacheDatabase.Column("versionName")
    public String versionName;
    @CacheDatabase.Column("sys_sdk")
    public String systemSDK;
    @CacheDatabase.Column("sys_version")
    public String systemVersion;
    @CacheDatabase.Column("rom")
    public String rom;
    @CacheDatabase.Column("cpu")
    public String cpuABI;
    @CacheDatabase.Column("phone")
    public String phoneName;
    @CacheDatabase.Column("locale")
    public String locale;
    public int viewType;

    public Crash() {
    }

    public Crash(int viewType) {
        this.viewType = viewType;
    }

    public static void clear() {
        CacheDatabase.delete(Crash.class);
    }

    public static List<Crash> query() {
        List<Crash> result = CacheDatabase.<Crash>queryList(Crash.class, (String) null, "order by createTime DESC");
        return result;
    }

    public static void insert(Throwable t, long launchTime) {
        Crash crash = new Crash();
        crash.startTime = launchTime;
        crash.createTime = System.currentTimeMillis();
        crash.type = t.getClass().getSimpleName();
        crash.cause = t.getMessage();
        crash.stack = Utils.collectThrow(t);
        crash.versionCode = packageCode(Utils.getContext());
        crash.versionName = packageName(Utils.getContext());
        crash.systemVersion = VERSION.RELEASE;
        crash.systemSDK = "Android " + VERSION.SDK_INT;
        crash.rom = Build.MANUFACTURER;
        crash.cpuABI = Build.CPU_ABI;
        crash.phoneName = Build.MODEL;
        crash.locale = Locale.getDefault().getLanguage();
        crash.viewType = 0;
        CacheDatabase.insert(crash);
    }

    private static int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;

        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return code;
    }

    private static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;

        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    public int getItemType() {
        return this.viewType;
    }
}


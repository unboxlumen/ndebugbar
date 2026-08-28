package com.unboxlumen.ndebugbar.cache;

import android.util.Pair;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * 网络请求摘要（纯内存模型，不再落 SQLite，存储见 {@link NetLogStore}）。
 */
public class Summary {
    public long id;
    public int status;
    public int code;
    public String url;
    public String query;
    public String host;
    public String method;
    public String protocol;
    public boolean ssl;
    public long start_time;
    public long end_time;
    public String request_content_type;
    public String response_content_type;
    public long request_size;
    public long response_size;
    public String requestHeader;
    public String responseHeader;
    public int source;
    public List<Pair<String, String>> request_header;
    public List<Pair<String, String>> response_header;

    public static List<Summary> queryList() {
        return NetLogStore.get().queryList();
    }

    public static List<Summary> queryWebkitList() {
        return NetLogStore.get().queryWebkitList();
    }

    public static Summary query(long id) {
        return NetLogStore.get().query(id);
    }

    public static long insert(Summary summary) {
        return NetLogStore.get().insert(summary);
    }

    public static void update(Summary summary) {
        NetLogStore.get().update(summary);
    }

    public static void clear() {
        NetLogStore.get().clear();
    }

    public static void clearWebkit() {
        NetLogStore.get().clearWebkit();
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        int REQUESTING = 0;
        int ERROR = 1;
        int COMPLETE = 2;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Source {
        int OKHTTP = 0;
        int WEBKIT = 1;
    }
}

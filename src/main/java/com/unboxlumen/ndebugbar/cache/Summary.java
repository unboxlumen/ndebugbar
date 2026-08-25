package com.unboxlumen.ndebugbar.cache;

import android.util.Pair;

import com.unboxlumen.ndebugbar.utils.Config;
import com.unboxlumen.ndebugbar.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@CacheDatabase.Table("http_summary")
public class Summary {
    static {
        clear();
    }

    @CacheDatabase.Column(
            value = "_id",
            primaryKey = true
    )
    public long id;
    @CacheDatabase.Column("status")
    public int status;
    @CacheDatabase.Column("code")
    public int code;
    @CacheDatabase.Column("url")
    public String url;
    @CacheDatabase.Column("query")
    public String query;
    @CacheDatabase.Column("host")
    public String host;
    @CacheDatabase.Column("method")
    public String method;
    @CacheDatabase.Column("protocol")
    public String protocol;
    @CacheDatabase.Column("ssl")
    public boolean ssl;
    @CacheDatabase.Column("start_time")
    public long start_time;
    @CacheDatabase.Column("end_time")
    public long end_time;
    @CacheDatabase.Column("request_content_type")
    public String request_content_type;
    @CacheDatabase.Column("response_content_type")
    public String response_content_type;
    @CacheDatabase.Column("request_size")
    public long request_size;
    @CacheDatabase.Column("response_size")
    public long response_size;
    @CacheDatabase.Column("request_header")
    public String requestHeader;
    @CacheDatabase.Column("response_header")
    public String responseHeader;
    @CacheDatabase.Column("source")
    public int source;
    public List<Pair<String, String>> request_header;
    public List<Pair<String, String>> response_header;

    public static List<Summary> queryList() {
        String condition = "order by start_time desc limit " + String.valueOf(Config.getNETWORK_PAGE_SIZE());
        List<Summary> result = CacheDatabase.<Summary>queryList(Summary.class, (String) null, condition);
        return result;
    }

    public static List<Summary> queryWebkitList() {
        String condition = "source = 1";
        String suffix = "order by start_time desc limit " + String.valueOf(Config.getNETWORK_PAGE_SIZE());
        List<Summary> result = CacheDatabase.<Summary>queryList(Summary.class, condition, suffix);
        return result;
    }

    public static Summary query(long id) {
        List<Summary> result = CacheDatabase.<Summary>queryList(Summary.class, "_id = " + String.valueOf(id), "limit 1");
        return Utils.isNotEmpty(result) ? (Summary) result.get(0) : null;
    }

    public static long insert(Summary summary) {
        return CacheDatabase.insert(summary);
    }

    public static void update(Summary summary) {
        CacheDatabase.update(summary);
    }

    public static void clear() {
        CacheDatabase.delete(Summary.class);
    }

    public static void clearWebkit() {
        CacheDatabase.delete(Summary.class, "source = 1");
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


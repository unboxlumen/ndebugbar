package com.unboxlumen.ndebugbar.cache;

import java.util.List;

@CacheDatabase.Table("activity_history")
public class History {
    static {
        clear();
    }

    @CacheDatabase.Column(
            value = "_id",
            primaryKey = true
    )
    public int id;
    @CacheDatabase.Column("createTime")
    public long createTime;
    @CacheDatabase.Column("activity")
    public String activity;
    @CacheDatabase.Column("event")
    public String event;

    public static void clear() {
        CacheDatabase.delete(History.class);
    }

    public static void insert(History history) {
        CacheDatabase.insert(history);
    }

    public static List<History> query() {
        String condition = "order by createTime desc";
        return CacheDatabase.<History>queryList(History.class, (String) null, condition);
    }
}


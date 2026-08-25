package com.unboxlumen.ndebugbar.cache;

import com.unboxlumen.ndebugbar.utils.Utils;

import java.util.List;

@CacheDatabase.Table("http_content")
public class Content {
    static {
        clear();
    }

    @CacheDatabase.Column(
            value = "_id",
            primaryKey = true
    )
    public long id;
    @CacheDatabase.Column("requestBody")
    public String requestBody;
    @CacheDatabase.Column("responseBody")
    public String responseBody;

    public static Content query(long id) {
        List<Content> result = CacheDatabase.<Content>queryList(Content.class, "_id = " + String.valueOf(id), "limit 1");
        return Utils.isNotEmpty(result) ? (Content) result.get(0) : null;
    }

    public static long insert(Content content) {
        return CacheDatabase.insert(content);
    }

    public static void update(Content content) {
        CacheDatabase.update(content);
    }

    public static void clear() {
        CacheDatabase.delete(Content.class);
    }
}


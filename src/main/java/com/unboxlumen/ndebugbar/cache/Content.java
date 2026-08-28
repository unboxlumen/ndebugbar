package com.unboxlumen.ndebugbar.cache;

/**
 * 网络请求/响应正文（纯内存模型，不再落 SQLite，存储见 {@link NetLogStore}）。
 * 存不下的正文（图片、超大 body）由写入方跳过，此处允许为 null。
 */
public class Content {
    public long id;
    public String requestBody;
    public String responseBody;

    public static Content query(long id) {
        return NetLogStore.get().queryContent(id);
    }

    public static long insert(Content content) {
        NetLogStore.get().insertContent(content);
        return content.id;
    }

    public static void update(Content content) {
        NetLogStore.get().updateContent(content);
    }

    public static void clear() {
        NetLogStore.get().clearContents();
    }
}

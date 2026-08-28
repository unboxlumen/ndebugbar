package com.unboxlumen.ndebugbar.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 网络日志内存存储（替代 SQLite 的 http_summary / http_content 表）。
 * <p>
 * 规则：
 * - 纯内存，不落 SQLite，进程重启即清空；
 * - webkit（WebView）与 OkHttp 两条来源<b>分开计数</b>，各自先入先出只保留最近
 *   {@link #MAX_SIZE} 条（合并展示时最多 200 条）；
 * - 存不进内存的数据（图片 body、超过 {@link #MAX_BODY_LENGTH} 的正文）由写入方自行跳过。
 * <p>
 * 线程安全：shouldInterceptRequest / OkHttp 拦截运行在后台线程，日志页在 UI 线程
 * 读取，所有读写都走同一把锁。
 */
public class NetLogStore {

    /** 每个来源（webkit / OkHttp）最多保留的日志条数（先入先出，各保留最后 100 条） */
    public static final int MAX_SIZE = 100;

    /** 单条 request/response 正文允许进入内存的最大字节数（超过则跳过不存） */
    public static final int MAX_BODY_LENGTH = 256 * 1024;

    private static final NetLogStore INSTANCE = new NetLogStore();

    private final AtomicLong idCounter = new AtomicLong(1);
    private final Object lock = new Object();
    /** OkHttp 来源，按写入顺序保存，下标 0 最旧；淘汰时移除头部 */
    private final List<Summary> okhttpSummaries = new ArrayList<>();
    /** webkit（WebView）来源，按写入顺序保存，下标 0 最旧；淘汰时移除头部 */
    private final List<Summary> webkitSummaries = new ArrayList<>();
    /** 正文表，与 summary 一一对应（key = summary.id） */
    private final Map<Long, Content> contents = new HashMap<>();

    private NetLogStore() {
    }

    public static NetLogStore get() {
        return INSTANCE;
    }

    /** 按来源路由到对应的队列（未知 source 一律归入 OkHttp 队列） */
    private List<Summary> listOf(Summary summary) {
        return summary.source == Summary.Source.WEBKIT ? webkitSummaries : okhttpSummaries;
    }

    /** 插入一条摘要（自动分配 id），所属来源超限时按 FIFO 淘汰最旧的一条及其正文 */
    public long insert(Summary summary) {
        synchronized (lock) {
            summary.id = idCounter.getAndIncrement();
            List<Summary> list = listOf(summary);
            list.add(summary);
            while (list.size() > MAX_SIZE) {
                Summary evicted = list.remove(0);
                contents.remove(evicted.id);
            }
            return summary.id;
        }
    }

    /** 插入/覆盖正文（key = summary.id） */
    public void insertContent(Content content) {
        synchronized (lock) {
            contents.put(content.id, content);
        }
    }

    /** 按 id 替换摘要（拦截器拿到新对象后回写，两个来源都查） */
    public void update(Summary summary) {
        synchronized (lock) {
            List<Summary> list = listOf(summary);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id == summary.id) {
                    list.set(i, summary);
                    return;
                }
            }
            // 来源字段变了才可能走到这，兜底再查另一个队列
            List<Summary> other = list == okhttpSummaries ? webkitSummaries : okhttpSummaries;
            for (int i = 0; i < other.size(); i++) {
                if (other.get(i).id == summary.id) {
                    other.set(i, summary);
                    return;
                }
            }
        }
    }

    /** 按 id 更新正文 */
    public void updateContent(Content content) {
        synchronized (lock) {
            if (contents.containsKey(content.id)) {
                contents.put(content.id, content);
            }
        }
    }

    public Summary query(long id) {
        synchronized (lock) {
            for (int i = okhttpSummaries.size() - 1; i >= 0; i--) {
                Summary s = okhttpSummaries.get(i);
                if (s.id == id) {
                    return s;
                }
            }
            for (int i = webkitSummaries.size() - 1; i >= 0; i--) {
                Summary s = webkitSummaries.get(i);
                if (s.id == id) {
                    return s;
                }
            }
            return null;
        }
    }

    public Content queryContent(long id) {
        synchronized (lock) {
            return contents.get(id);
        }
    }

    /** 全部摘要（两个来源合并），按时间倒序（最新在前），返回副本 */
    public List<Summary> queryList() {
        synchronized (lock) {
            List<Summary> result = new ArrayList<>(okhttpSummaries.size() + webkitSummaries.size());
            int i = okhttpSummaries.size() - 1;
            int j = webkitSummaries.size() - 1;
            while (i >= 0 || j >= 0) {
                if (i < 0) {
                    result.add(webkitSummaries.get(j--));
                } else if (j < 0) {
                    result.add(okhttpSummaries.get(i--));
                } else if (okhttpSummaries.get(i).id > webkitSummaries.get(j).id) {
                    result.add(okhttpSummaries.get(i--));
                } else {
                    result.add(webkitSummaries.get(j--));
                }
            }
            return result;
        }
    }

    /** 仅 webkit（WebView）来源的摘要，按时间倒序 */
    public List<Summary> queryWebkitList() {
        synchronized (lock) {
            List<Summary> result = new ArrayList<>(webkitSummaries.size());
            for (int i = webkitSummaries.size() - 1; i >= 0; i--) {
                result.add(webkitSummaries.get(i));
            }
            return result;
        }
    }

    /** 清空全部摘要与正文 */
    public void clear() {
        synchronized (lock) {
            okhttpSummaries.clear();
            webkitSummaries.clear();
            contents.clear();
        }
    }

    /** 仅清空正文 */
    public void clearContents() {
        synchronized (lock) {
            contents.clear();
        }
    }

    /** 仅清除 webkit 来源的摘要及其正文（OkHttp 队列不动） */
    public void clearWebkit() {
        synchronized (lock) {
            for (Summary s : webkitSummaries) {
                contents.remove(s.id);
            }
            webkitSummaries.clear();
        }
    }
}

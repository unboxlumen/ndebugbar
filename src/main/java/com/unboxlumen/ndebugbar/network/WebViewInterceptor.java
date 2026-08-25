package com.unboxlumen.ndebugbar.network;

import android.net.Uri;
import android.webkit.WebResourceRequest;

import com.unboxlumen.ndebugbar.cache.Content;
import com.unboxlumen.ndebugbar.cache.Summary;
import com.unboxlumen.ndebugbar.utils.Config;

import org.json.JSONArray;

import java.util.Map;

/**
 * WebView 网络请求记录器
 * <p>
 * 通过 WebViewClient.shouldInterceptRequest 接入，记录 WebView 发出的请求摘要
 * 到 http_summary / http_content 表，与 OkHttpInterceptor 的网络日志并存。
 * <p>
 * 注意：WebView 不对外暴露响应数据（由 Chromium 内部加载），因此这里只能
 * 记录请求信息（URL / 方法 / 请求头 / 时间），响应内容无法获取。
 */
public class WebViewInterceptor {

    /**
     * 记录一次 WebView 请求（应在 shouldInterceptRequest 中调用，运行于后台线程）
     */
    public static void onRequest(WebResourceRequest request) {
        if (!Config.isNetLogEnable()) {
            return;
        }
        try {
            Uri uri = request.getUrl();
            Summary summary = new Summary();
            summary.status = 2; // WebView 无法获取响应，直接标记为完成
            summary.url = uri.toString();
            summary.host = uri.getHost() != null
                    ? uri.getHost() + (uri.getPort() >= 0 ? ":" + uri.getPort() : "")
                    : "";
            summary.method = request.getMethod();
            summary.ssl = "https".equalsIgnoreCase(uri.getScheme());
            summary.source = Summary.Source.WEBKIT;
            long now = System.currentTimeMillis();
            summary.start_time = now;
            summary.end_time = now;
            summary.requestHeader = formatHeaders(request.getRequestHeaders());
            long id = Summary.insert(summary);
            Content content = new Content();
            content.id = id;
            Content.insert(content);
        } catch (Throwable t) {
            // 记录失败不影响页面加载
        }
    }

    private static String formatHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        JSONArray array = new JSONArray();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            array.put(new JSONArray().put(entry.getKey()).put(entry.getValue()));
        }
        return array.toString();
    }
}


package com.unboxlumen.ndebugbar.log;

import android.util.Log;

import com.unboxlumen.ndebugbar.cache.LogEntry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogCollector {
    private static final int MAX_ENTRIES = 2000;
    private static final List<LogEntry> ENTRIES = Collections.synchronizedList(new ArrayList<LogEntry>());

    public static void v(String tag, String msg) {
        Log.v(tag, msg);
        add(LogEntry.LEVEL_V, tag, msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
        add(LogEntry.LEVEL_D, tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
        add(LogEntry.LEVEL_I, tag, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
        add(LogEntry.LEVEL_W, tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        add(LogEntry.LEVEL_E, tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
        add(LogEntry.LEVEL_E, tag, msg + "\n" + Log.getStackTraceString(tr));
    }

    public static void add(int level, String tag, String msg) {
        ENTRIES.add(new LogEntry(level, tag, msg));
        if (ENTRIES.size() > MAX_ENTRIES) {
            int remove = ENTRIES.size() - MAX_ENTRIES;
            synchronized (ENTRIES) {
                ENTRIES.subList(0, remove).clear();
            }
        }
    }

    public static List<LogEntry> getEntries() {
        synchronized (ENTRIES) {
            return new ArrayList<LogEntry>(ENTRIES);
        }
    }

    public static void clear() {
        synchronized (ENTRIES) {
            ENTRIES.clear();
        }
    }

    public static List<String> fetchLogcat(int maxLines) {
        List<String> lines = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec("logcat -d -v time *:V");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < maxLines) {
                lines.add(line);
                count++;
            }
            reader.close();
            process.destroy();
        } catch (Exception e) {
            lines.add("logcat failed: " + e.getMessage());
        }
        return lines;
    }
}


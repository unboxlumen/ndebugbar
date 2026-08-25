package com.unboxlumen.ndebugbar.cache;

public class LogEntry {
    public static final int LEVEL_V = 0;
    public static final int LEVEL_D = 1;
    public static final int LEVEL_I = 2;
    public static final int LEVEL_W = 3;
    public static final int LEVEL_E = 4;

    public final long timestamp;
    public final int level;
    public final String tag;
    public final String message;

    public LogEntry(int level, String tag, String message) {
        this.timestamp = System.currentTimeMillis();
        this.level = level;
        this.tag = tag != null ? tag : "";
        this.message = message != null ? message : "";
    }

    public static String levelName(int level) {
        switch (level) {
            case LEVEL_V:
                return "V";
            case LEVEL_D:
                return "D";
            case LEVEL_I:
                return "I";
            case LEVEL_W:
                return "W";
            case LEVEL_E:
                return "E";
            default:
                return "?";
        }
    }
}


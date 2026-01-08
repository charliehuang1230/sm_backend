package com.demo.todolist.config;

public final class DynamicDataSourceContext {
    public static final String DEFAULT_KEY = "default";
    private static final ThreadLocal<String> CURRENT_KEY = new ThreadLocal<>();

    private DynamicDataSourceContext() {
    }

    public static void setCurrentKey(String key) {
        CURRENT_KEY.set(key);
    }

    public static String getCurrentKey() {
        return CURRENT_KEY.get();
    }

    public static void clear() {
        CURRENT_KEY.remove();
    }
}

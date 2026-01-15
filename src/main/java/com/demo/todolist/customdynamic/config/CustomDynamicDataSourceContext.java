package com.demo.todolist.customdynamic.config;

public final class CustomDynamicDataSourceContext {
    public static final String DEFAULT_KEY = "default";
    private static final ThreadLocal<String> CURRENT_KEY = new ThreadLocal<>();

    private CustomDynamicDataSourceContext() {
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

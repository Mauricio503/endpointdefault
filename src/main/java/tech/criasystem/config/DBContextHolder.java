package tech.criasystem.config;

public class DBContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setCurrentSchema(String schemaType) {
        contextHolder.set(schemaType);
    }

    public static String getCurrentSchema() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}

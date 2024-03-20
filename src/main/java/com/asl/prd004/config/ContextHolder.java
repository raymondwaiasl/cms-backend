package com.asl.prd004.config;

/**
 * @author llh
 */
public class ContextHolder {
    public static ThreadLocal<String> context = new ThreadLocal<>();

    public static void setUserId(String userId) {
        context.set(userId);
    }

    public static String getUserId() {
        return context.get();
    }

    public static void setUserLoginId(String userLoginId) {
        context.set(userLoginId);
    }

    public static String getUserLoginId() {
        return context.get();
    }

    public static void setOffice(String office) {
        context.set(office);
    }

    public static String getOffice() {
        return context.get();
    }

    public static void setUserRole(String userRole) {
        context.set(userRole);
    }

    public static String getUserRole() {
        return context.get();
    }

    public static void shutdown() {
        context.remove();
    }
}

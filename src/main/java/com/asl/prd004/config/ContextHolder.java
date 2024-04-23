package com.asl.prd004.config;

/**
 * @author llh
 */
public class ContextHolder {
    public static ThreadLocal<String> userIdContext = new ThreadLocal<>();
    public static ThreadLocal<String> loginIdContext = new ThreadLocal<>();
    public static ThreadLocal<String> officeContext = new ThreadLocal<>();
    public static ThreadLocal<String> userRoleContext = new ThreadLocal<>();

    public static void setUserId(String userId) {
        userIdContext.set(userId);
    }

    public static String getUserId() {
        return userIdContext.get();
    }

    public static void setUserLoginId(String userLoginId) {
        loginIdContext.set(userLoginId);
    }

    public static String getUserLoginId() {
        return loginIdContext.get();
    }

    public static void setOffice(String office) {
        officeContext.set(office);
    }

    public static String getOffice() {
        return officeContext.get();
    }

    public static void setUserRole(String userRole) {
        userRoleContext.set(userRole);
    }

    public static String getUserRole() {
        return userRoleContext.get();
    }

    public static void shutdown() {
        userIdContext.remove();
        loginIdContext.remove();
        officeContext.remove();
        userRoleContext.remove();
    }
}

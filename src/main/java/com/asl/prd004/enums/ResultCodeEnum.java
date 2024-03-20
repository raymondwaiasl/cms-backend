package com.asl.prd004.enums;

/**
 *
 * @Description: 响应码枚举，参考HTTP状态码的语义
 * @author ZhiPengyu
 * @date: 2020年4月29日 上午9:27:40
 */
public enum ResultCodeEnum {
    /* 成功 */
    SUCCESS(200, "Operation success!"),
    /* 失败 */
    FAIL(300, "Operation failed!!"),

    ERROR(500, "Error!"),

    /* 参考HTTP状态码 */
    NO_PERMISSION(403, "Need Authorities!"),//没有权限
    LOGIN_NO(402, "Need Login!"),//未登录
    LOGIN_FAIL(401, "Login Failure!"),//登录失败
    LOGIN_SUCCESS(200, "Login Success!"),//登录成功
    LOGOUT_SUCCESS(200, "Logout Success!"),//退出登录
    SESSION_EXPIRES(101, "Session Expires!"),//会话到期
    SESSION_EXPIRES_OTHER_LOGIN(101, "Session Expires!Other users login！"),//会话到期,其他用户登录

    /*  */
    USER_ADMIN_CHANGE_PASSWORD_WRONG_PASSWORD(1000000001,"wrong password!"),

    USER_ADMIN_CHANGE_PASSWORD_OLD_PASSWORD(1000000001,"Please do not use the old password!"),

    ;
    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     *
     * @param code
     * @param msg
     */
    ResultCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}



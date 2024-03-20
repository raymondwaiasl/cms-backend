package com.asl.prd004.config;


import com.asl.prd004.enums.ResultCodeEnum;

/**
 * @author llh
 */
public class ResultGenerator<T> {
    private Integer code;
    private String msg;
    private T data;

    public ResultGenerator(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultGenerator(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResultGenerator<T> getResult(ResultCodeEnum resultCodeEnum){
        return new ResultGenerator<T>(resultCodeEnum.getCode(), resultCodeEnum.getMsg(), null);
    }

    public static <T> ResultGenerator<T> getResult(ResultCodeEnum resultCodeEnum,T t){
        return new ResultGenerator<T>(resultCodeEnum.getCode(), resultCodeEnum.getMsg(), t);
    }

    public static <T> ResultGenerator<T> getSuccessResult() {
        return new ResultGenerator<T>(200, "Operation success!", null);
    }

    public static <T> ResultGenerator<T> getSuccessResult(T t) {
        return new ResultGenerator<T>(200, "Operation success!", t);
    }

    public static <T> ResultGenerator<T> getFailResult(T t) {
        return new ResultGenerator<T>(300, "Operation failed!", t);
    }

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultGenerator{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}

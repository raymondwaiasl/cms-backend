package com.asl.prd004.enums;

import java.util.function.Function;

public enum ConditionEnum {
    LIKE("0","like",str -> "'%" + str + "%'"),
    EQ("1","=",str -> "'" + str + "'"),
    LE("2","<=",str -> "'" + str + "'"),
    GE("3",">=",str -> "'" + str + "'"),
    ;

    public static ConditionEnum getByCode(String code){
        for (ConditionEnum value : ConditionEnum.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        return null;
    }

    ConditionEnum(String code, String operator, Function<String, String> packValue) {
        this.code = code;
        this.operator = operator;
        this.packValue = packValue;
    }

    private String code;
    private String operator;
    private Function<String, String> packValue;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Function<String, String> getPackValue() {
        return packValue;
    }

    public void setPackValue(Function<String, String> packValue) {
        this.packValue = packValue;
    }
}

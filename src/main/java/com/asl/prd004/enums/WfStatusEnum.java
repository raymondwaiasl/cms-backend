package com.asl.prd004.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


public enum WfStatusEnum {
    Pending(0,"Pending"),
    Running(1, "Running"),
    Withdrawed(2, "Withdrawed"),
    Terminated(3, "Terminated"),
    Finished(4, "Finished"),
    Others(99,"Others");

    WfStatusEnum(Integer code , String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public static String getDescByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (WfStatusEnum status : WfStatusEnum.values()) {
            if (status.getCode().intValue() == code.intValue()) {
                return status.getDesc();
            }
        }
        return null;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    @JsonValue
    public String desc() {
        return desc;
    }
}

package com.asl.prd004.enums;

import com.asl.prd004.config.DefinitionException;
import com.asl.prd004.utils.StrUtil;

public enum TableMgmtColumnType {
    Boolean("0"),
    String("1"),
    Integer("2"),
    ID("3"),
    Date("4"),
    Double("5"),
    Text("6"),
    ;
    private String code;
    TableMgmtColumnType(String code){
        this.code = code;
    }
    public static TableMgmtColumnType getByCode(String code){
        for(TableMgmtColumnType e:TableMgmtColumnType.values()){
            if(StrUtil.equals(e.code,code)){
                return e;
            }
        }
        throw new DefinitionException(ResultCodeEnum.ERROR.getCode(), "fail to get TableMgmtColumnType,code:" + code);
    }
}

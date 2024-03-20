package com.asl.prd004.enums;

import com.asl.prd004.config.DefinitionException;
import com.asl.prd004.utils.StrUtil;

public enum TableMgmtColumnInputType {
    SEQUENCE("0"),
    TEXT_BOX("1"),
    COMBO_BOX("2"),
    CHECK_BOX("3"),
    RADIO_BUTTON("4"),
    SYSTEM_GENERATED_DATE_TIME("5"),
    DATE_PICKER("6"),
    DATE_INPUT("7"),
    TEXT_AREA("8"),
    EMAIL_EDITOR("9"),
    COMPUTE_FIELD("10"),
    QUERY_TEXT_BOX("11"),
    REPEATING_FIELD("12"),
    HYPERLINK("13"),
    FOLDER_PICKER("14"),
    ;
    private String code;
    TableMgmtColumnInputType(String code){
        this.code = code;
    }
    public static TableMgmtColumnInputType getByCode(String code){
        for(TableMgmtColumnInputType e: TableMgmtColumnInputType.values()){
            if(StrUtil.equals(e.code,code)){
                return e;
            }
        }
        throw new DefinitionException(ResultCodeEnum.ERROR.getCode(), "fail to get TableMgmtColumnInputType,code:" + code);
    }

    public String getCode() {
        return code;
    }
}

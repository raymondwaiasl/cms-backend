package com.asl.prd004.dto;

import java.util.List;

public class ColumnInputTypeDTO {
    private String columnName;
    private List<DicDto> dictList;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public List<DicDto> getDictList() {
        return dictList;
    }

    public void setDictList(List<DicDto> dictList) {
        this.dictList = dictList;
    }
}

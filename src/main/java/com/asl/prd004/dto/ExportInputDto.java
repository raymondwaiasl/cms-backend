package com.asl.prd004.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExportInputDto {

    private String typeId;
    private String colName0;
    private List columnData;
    private String condition0;
    private String value0;
    private String folderId;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getColName0() {
        return colName0;
    }

    public void setColName0(String colName0) {
        this.colName0 = colName0;
    }

    public List getColumnData() {
        return columnData;
    }

    public void setColumnData(List columnData) {
        this.columnData = columnData;
    }

    public String getCondition0() {
        return condition0;
    }

    public void setCondition0(String condition0) {
        this.condition0 = condition0;
    }

    public String getValue0() {
        return value0;
    }

    public void setValue0(String value0) {
        this.value0 = value0;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
}

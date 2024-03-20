package com.asl.prd004.dto;

import java.io.Serializable;
import java.util.List;

public class ColumnNestDTO implements Serializable {
    private String misColumnId;
    private String misColumnName;
    private String misColumnLabel;
    private String misColumnInputType;
    private String misColumnDictionary;
    private String misColumnType;
    private String misColumnLength;
    private String misColumnAllowEmpty;
    private Integer rowSize;
    private Integer colSize;
    private List<DicDto> ColumnLs;
    private List<EffectColumn> effect;

    private String misPropertySectionId;

    public ColumnNestDTO(String misColumnId,String misColumnLabel,  String misColumnName, String misColumnInputType,
                         String misColumnDictionary, String misColumnType, String misColumnLength, String misColumnAllowEmpty,
                         Integer rowSize, Integer colSize,String misPropertySectionId) {
        this.misColumnId = misColumnId;
        this.misColumnLabel = misColumnLabel;
        this.misColumnName = misColumnName;
        this.misColumnInputType = misColumnInputType;
        this.misColumnDictionary = misColumnDictionary;
        this.misColumnType = misColumnType;
        this.misColumnLength = misColumnLength;
        this.misColumnAllowEmpty = misColumnAllowEmpty;
        this.rowSize = rowSize;
        this.colSize = colSize;
        this.misPropertySectionId=misPropertySectionId;
    }

    public String getMisColumnId() {
        return misColumnId;
    }

    public void setMisColumnId(String misColumnId) {
        this.misColumnId = misColumnId;
    }

    public String getMisColumnName() {
        return misColumnName;
    }

    public void setMisColumnName(String misColumnName) {
        this.misColumnName = misColumnName;
    }

    public String getMisColumnLabel() {
        return misColumnLabel;
    }

    public void setMisColumnLabel(String misColumnLabel) {
        this.misColumnLabel = misColumnLabel;
    }

    public String getMisColumnInputType() {
        return misColumnInputType;
    }

    public void setMisColumnInputType(String misColumnInputType) {
        this.misColumnInputType = misColumnInputType;
    }

    public String getMisColumnDictionary() {
        return misColumnDictionary;
    }

    public void setMisColumnDictionary(String misColumnDictionary) {
        this.misColumnDictionary = misColumnDictionary;
    }

    public List<DicDto> getColumnLs() {
        return ColumnLs;
    }

    public void setColumnLs(List<DicDto> columnLs) {
        ColumnLs = columnLs;
    }

    public String getMisColumnType() {
        return misColumnType;
    }

    public void setMisColumnType(String misColumnType) {
        this.misColumnType = misColumnType;
    }

    public String getMisColumnLength() {
        return misColumnLength;
    }

    public void setMisColumnLength(String misColumnLength) {
        this.misColumnLength = misColumnLength;
    }

    public String getMisColumnAllowEmpty() {
        return misColumnAllowEmpty;
    }

    public void getMisColumnAllowEmpty(String misColumnAllowEmpty) {
        this.misColumnAllowEmpty = misColumnAllowEmpty;
    }

    public Integer getRowSize() {
        return rowSize;
    }

    public void setRowSize(Integer rowSize) {
        this.rowSize = rowSize;
    }

    public Integer getColSize() {
        return colSize;
    }

    public void setColSize(Integer colSize) {
        this.colSize = colSize;
    }

    public List<EffectColumn> getEffect() {
        return effect;
    }

    public void setEffect(List<EffectColumn> effect) {
        this.effect = effect;
    }

    public String getMisPropertySectionId() {
        return misPropertySectionId;
    }

    public void setMisPropertySectionId(String misPropertySectionId) {
        this.misPropertySectionId = misPropertySectionId;
    }
}

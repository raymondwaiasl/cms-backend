package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "MIS_COLUMN", schema = "MIS", catalog = "")
public class MisColumn {
    private String misColumnId;
    private String misTypeId;
    private String misColumnName;
    private String misColumnLabel;
    private String misColumnType;
    private Float misColumnWidth;
    private String misColumnLength;
    private String misColumnInputType;
    private String misColumnDictionary;
    private String misColumnAllowSearch;
    private String misColumnAllowEmpty;
    private String misColumnComputeFrom;
    private String misColumnComputeFormula;
    private String misColumnComputeQuery;

    private MisType type;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "MIS_COLUMN_ID", nullable = false, length = 16)
    public String getMisColumnId() {
        return misColumnId;
    }

    public void setMisColumnId(String misColumnId) {
        this.misColumnId = misColumnId;
    }

    @Basic
    @Column(name = "MIS_TYPE_ID", nullable = false, length = 16)
    public String getMisTypeId() {
        return misTypeId;
    }

    public void setMisTypeId(String misTypeId) {
        this.misTypeId = misTypeId;
    }

    @Basic
    @Column(name = "MIS_COLUMN_NAME", nullable = false, length = 40)
    public String getMisColumnName() {
        return misColumnName;
    }

    public void setMisColumnName(String misColumnName) {
        this.misColumnName = misColumnName;
    }
    @Basic
    @Column(name = "MIS_COLUMN_LABEL", nullable = false, length = 40)
    public String getMisColumnLabel() {
        return misColumnLabel;
    }

    public void setMisColumnLabel(String misColumnLabel) {
        this.misColumnLabel = misColumnLabel;
    }

    @Basic
    @Column(name = "MIS_COLUMN_TYPE", nullable = false, length = 255)
    public String getMisColumnType() {
        return misColumnType;
    }

    public void setMisColumnType(String misColumnType) {
        this.misColumnType = misColumnType;
    }

    @Basic
    @Column(name = "MIS_COLUMN_WIDTH", nullable = false, length = 4)
    public Float getMisColumnWidth() {
        return misColumnWidth;
    }

    public void setMisColumnWidth(Float misColumnWidth) {
        this.misColumnWidth = misColumnWidth;
    }

    @Basic
    @Column(name = "MIS_COLUMN_LENGTH", nullable = false, length = 255)
    public String getMisColumnLength() {
        return misColumnLength;
    }

    public void setMisColumnLength(String misColumnLength) {
        this.misColumnLength = misColumnLength;
    }

    @Basic
    @Column(name = "MIS_COLUMN_INPUT_TYPE", nullable = false, length = 40)
    public String getMisColumnInputType() {
        return misColumnInputType;
    }

    public void setMisColumnInputType(String misColumnInputType) {
        this.misColumnInputType = misColumnInputType;
    }

    @Basic
    @Column(name = "MIS_COLUMN_DICTIONARY", nullable = true, length = 40)
    public String getMisColumnDictionary() {
        return misColumnDictionary;
    }

    public void setMisColumnDictionary(String misColumnDictionary) {
        this.misColumnDictionary = misColumnDictionary;
    }

    @Basic
    @Column(name = "MIS_COLUMN_ALLOW_SEARCH", nullable = true, length = 40)
    public String getMisColumnAllowSearch() {
        return misColumnAllowSearch;
    }

    public void setMisColumnAllowSearch(String misColumnAllowSearch) {
        this.misColumnAllowSearch = misColumnAllowSearch;
    }

    @Basic
    @Column(name = "MIS_COLUMN_ALLOW_EMPTY", nullable = true, length = 40)
    public String getMisColumnAllowEmpty() {
        return misColumnAllowEmpty;
    }

    public void setMisColumnAllowEmpty(String misColumnAllowEmpty) {
        this.misColumnAllowEmpty = misColumnAllowEmpty;
    }

    @Basic
    @Column(name = "MIS_COLUMN_COMPUTE_FROM", nullable = true, length = 1)
    public String getMisColumnComputeFrom() {
        return misColumnComputeFrom;
    }

    public void setMisColumnComputeFrom(String misColumnComputeFrom) {
        this.misColumnComputeFrom = misColumnComputeFrom;
    }

    @Basic
    @Column(name = "MIS_COLUMN_COMPUTE_FORMULA", nullable = true, length = 1)
    public String getMisColumnComputeFormula() {
        return misColumnComputeFormula;
    }

    public void setMisColumnComputeFormula(String misColumnComputeFormula) {
        this.misColumnComputeFormula = misColumnComputeFormula;
    }

    @Basic
    @Column(name = "MIS_COLUMN_COMPUTE_QUERY", nullable = true, length = 1)
    public String getMisColumnComputeQuery() {
        return misColumnComputeQuery;
    }

    public void setMisColumnComputeQuery(String misColumnComputeQuery) {
        this.misColumnComputeQuery = misColumnComputeQuery;
    }

    @ManyToOne
    @JoinColumn(name="MIS_TYPE_ID", insertable = false,updatable = false)
    @JsonIgnore
//    @Transient
    public MisType getType() {
        return type;
    }

    public void setType(MisType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MisColumn misColumn = (MisColumn) o;
        return Objects.equals(misColumnId, misColumn.misColumnId) && Objects.equals(misTypeId, misColumn.misTypeId) && Objects.equals(misColumnLabel, misColumn.misColumnLabel) && Objects.equals(misColumnType, misColumn.misColumnType) && Objects.equals(misColumnWidth, misColumn.misColumnWidth) && Objects.equals(misColumnLength, misColumn.misColumnLength) && Objects.equals(misColumnInputType, misColumn.misColumnInputType) && Objects.equals(misColumnDictionary, misColumn.misColumnDictionary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(misColumnId, misTypeId, misColumnLabel, misColumnType, misColumnWidth, misColumnLength, misColumnInputType, misColumnDictionary);
    }
}

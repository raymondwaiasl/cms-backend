package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "mis_query_form_condition", schema = "MIS", catalog = "")
public class MisQueryFormCondition {
    private String misQfc2Id;
    private String misQfc2ColumnId;
    private String misQfId;
    private String misQfc2Condition;
    private String misQfc2Value;
    private String misRelation;
    private MisQueryForm queryForm;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_qfc2_id", nullable = false, length = 16)
    public String getMisQfc2Id() {
        return misQfc2Id;
    }

    public void setMisQfc2Id(String misQfc2Id) {
        this.misQfc2Id = misQfc2Id;
    }

    @Basic
    @Column(name = "mis_qfc2_column_id", nullable = false, length = 16)
    public String getMisQfc2ColumnId() {
        return misQfc2ColumnId;
    }

    public void setMisQfc2ColumnId(String misQfc2ColumnId) {
        this.misQfc2ColumnId = misQfc2ColumnId;
    }

    @Basic
    @Column(name = "mis_qf_id", nullable = false, length = 16)
    public String getMisQfId() {
        return misQfId;
    }

    public void setMisQfId(String misQfId) {
        this.misQfId = misQfId;
    }

    @Basic
    @Column(name = "mis_qfc2_condition", nullable = false, length = 50)
    public String getMisQfc2Condition() {
        return misQfc2Condition;
    }

    public void setMisQfc2Condition(String misQfc2Condition) {
        this.misQfc2Condition = misQfc2Condition;
    }

    @Basic
    @Column(name = "mis_qfc2_value", nullable = false, length = 50)
    public String getMisQfc2Value() {
        return misQfc2Value;
    }

    public void setMisQfc2Value(String misQfc2Value) {
        this.misQfc2Value = misQfc2Value;
    }

    @Basic
    @Column(name = "mis_relation", length = 3)
    public String getMisRelation() {
        return misRelation;
    }

    public void setMisRelation(String misRelation) {
        this.misRelation = misRelation;
    }

    @ManyToOne
    @JoinColumn(name="mis_qf_id", insertable = false,updatable = false)
    @JsonIgnore
    public MisQueryForm getQueryForm() {
        return queryForm;
    }

    public void setQueryForm(MisQueryForm queryForm) {
        this.queryForm = queryForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisQueryFormCondition that = (MisQueryFormCondition) o;

        if (misQfc2Id != null ? !misQfc2Id.equals(that.misQfc2Id) : that.misQfc2Id != null) return false;
        if (misQfc2ColumnId != null ? !misQfc2ColumnId.equals(that.misQfc2ColumnId) : that.misQfc2ColumnId != null)
            return false;
        if (misQfId != null ? !misQfId.equals(that.misQfId) : that.misQfId != null) return false;
        if (misQfc2Condition != null ? !misQfc2Condition.equals(that.misQfc2Condition) : that.misQfc2Condition != null)
            return false;
        if (misQfc2Value != null ? !misQfc2Value.equals(that.misQfc2Value) : that.misQfc2Value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misQfc2Id != null ? misQfc2Id.hashCode() : 0;
        result = 31 * result + (misQfc2ColumnId != null ? misQfc2ColumnId.hashCode() : 0);
        result = 31 * result + (misQfId != null ? misQfId.hashCode() : 0);
        result = 31 * result + (misQfc2Condition != null ? misQfc2Condition.hashCode() : 0);
        result = 31 * result + (misQfc2Value != null ? misQfc2Value.hashCode() : 0);
        return result;
    }
}

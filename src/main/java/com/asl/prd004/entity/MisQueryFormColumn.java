package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "mis_query_form_column", schema = "MIS", catalog = "")
public class MisQueryFormColumn {
    private String misQfcId;
    private String misQfId;
    private String misQfcColumnId;
    private MisQueryForm queryForm;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_qfc_id", nullable = false, length = 16)
    public String getMisQfcId() {
        return misQfcId;
    }

    public void setMisQfcId(String misQfcId) {
        this.misQfcId = misQfcId;
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
    @Column(name = "mis_qfc_column_id", nullable = false, length = 16)
    public String getMisQfcColumnId() {
        return misQfcColumnId;
    }

    public void setMisQfcColumnId(String misQfcColumnId) {
        this.misQfcColumnId = misQfcColumnId;
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

        MisQueryFormColumn that = (MisQueryFormColumn) o;

        if (misQfcId != null ? !misQfcId.equals(that.misQfcId) : that.misQfcId != null) return false;
        if (misQfId != null ? !misQfId.equals(that.misQfId) : that.misQfId != null) return false;
        if (misQfcColumnId != null ? !misQfcColumnId.equals(that.misQfcColumnId) : that.misQfcColumnId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misQfcId != null ? misQfcId.hashCode() : 0;
        result = 31 * result + (misQfId != null ? misQfId.hashCode() : 0);
        result = 31 * result + (misQfcColumnId != null ? misQfcColumnId.hashCode() : 0);
        return result;
    }
}

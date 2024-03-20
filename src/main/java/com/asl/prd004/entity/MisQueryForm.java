package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "mis_query_form", schema = "MIS", catalog = "")
public class MisQueryForm {
    private String misQfId;
    private String misQfName;
    private String misQfTableId;
    private String misQfPublic;
    private String misQfGroupId;
    private String misQfParentId;
    private List<MisQueryFormColumn> qfColumns;
    private List<MisQueryFormCondition> qfConditions;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_qf_id", nullable = false, length = 16)
    public String getMisQfId() {
        return misQfId;
    }

    public void setMisQfId(String misQfId) {
        this.misQfId = misQfId;
    }

    @Basic
    @Column(name = "mis_qf_name", nullable = false, length = 40)
    public String getMisQfName() {
        return misQfName;
    }

    public void setMisQfName(String misQfName) {
        this.misQfName = misQfName;
    }

    @Basic
    @Column(name = "mis_qf_table_id", nullable = false, length = 16)
    public String getMisQfTableId() {
        return misQfTableId;
    }

    public void setMisQfTableId(String misQfTableId) {
        this.misQfTableId = misQfTableId;
    }


    @Basic
    @Column(name = "mis_qf_public", nullable = false, length = 1)
    public String getMisQfPublic() {
        return misQfPublic;
    }

    public void setMisQfPublic(String misQfPublic) {
        this.misQfPublic = misQfPublic;
    }
    @Basic
    @Column(name = "mis_qf_group_id",length = 16)
    public String getMisQfGroupId() {
        return misQfGroupId;
    }

    public void setMisQfGroupId(String misQfGroupId) {
        this.misQfGroupId = misQfGroupId;
    }

    @Basic
    @Column(name = "mis_qf_parent_id",length = 16)
    public String getMisQfParentId() {
        return misQfParentId;
    }

    public void setMisQfParentId(String misQfParentId) {
        this.misQfParentId = misQfParentId;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "queryForm")
    public List<MisQueryFormColumn> getQfColumns() {
        return qfColumns;
    }

    public void setQfColumns(List<MisQueryFormColumn> qfColumns) {
        this.qfColumns = qfColumns;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "queryForm")
    public List<MisQueryFormCondition> getQfConditions() {
        return qfConditions;
    }

    public void setQfConditions(List<MisQueryFormCondition> qfConditions) {
        this.qfConditions = qfConditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisQueryForm that = (MisQueryForm) o;

        if (misQfId != null ? !misQfId.equals(that.misQfId) : that.misQfId != null) return false;
        if (misQfName != null ? !misQfName.equals(that.misQfName) : that.misQfName != null) return false;
        if (misQfTableId != null ? !misQfTableId.equals(that.misQfTableId) : that.misQfTableId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misQfId != null ? misQfId.hashCode() : 0;
        result = 31 * result + (misQfName != null ? misQfName.hashCode() : 0);
        result = 31 * result + (misQfTableId != null ? misQfTableId.hashCode() : 0);
        return result;
    }
}

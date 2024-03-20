package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "MIS_TYPE", schema = "MIS", catalog = "")
public class MisType {

    private String misTypeId;
    private String misTypeLabel;
    private String misTypeName;
    private List<MisColumn> columns;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "MIS_TYPE_ID", nullable = false, length = 16)
    public String getMisTypeId() {
        return misTypeId;
    }

    public void setMisTypeId(String misTypeId) {
        this.misTypeId = misTypeId;
    }

    @Basic
    @Column(name = "MIS_TYPE_LABEL", nullable = false, length = 40)
    public String getMisTypeLabel() {
        return misTypeLabel;
    }

    public void setMisTypeLabel(String misTypeLabel) {
        this.misTypeLabel = misTypeLabel;
    }

    @Basic
    @Column(name = "MIS_TYPE_NAME", nullable = false, length = 40)
    public String getMisTypeName() {
        return misTypeName;
    }

    public void setMisTypeName(String misTypeName) {
        this.misTypeName = misTypeName;
    }


    //    @OneToMany(mappedBy = "type")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "type")
    public List<MisColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<MisColumn> columns) {
        this.columns = columns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MisType misType = (MisType) o;
        return Objects.equals(misTypeId, misType.misTypeId) && Objects.equals(misTypeLabel, misType.misTypeLabel) && Objects.equals(misTypeName, misType.misTypeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(misTypeId, misTypeLabel, misTypeName);
    }
}

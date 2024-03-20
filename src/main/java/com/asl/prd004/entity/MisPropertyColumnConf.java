package com.asl.prd004.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "mis_property_column_conf", schema = "MIS", catalog = "")
public class MisPropertyColumnConf {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_property_col_conf_id", nullable = false, length = 16)
    private String misPropertyColConfId;

    @Basic
    @Column(name = "mis_property_config_detail_id", nullable = false, length = 40, unique = true)
    private String misPropertyConfigDetailId;

    @Basic
    @Column(name = "mis_property_config_regular_expression", nullable = false, length = 40)
    private String misPropertyConfigRegularExpression;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "propertyColumnConfConds")
    private List<MisPropertyColumnConfCond>  columnConfConds;

    public List<MisPropertyColumnConfCond> getColumnConfConds() {
        return columnConfConds;
    }

    public void setColumnConfConds(List<MisPropertyColumnConfCond> columnConfConds) {
        this.columnConfConds = columnConfConds;
    }

    public String getMisPropertyColConfId() {
        return misPropertyColConfId;
    }

    public void setMisPropertyColConfId(String misPropertyColConfId) {
        this.misPropertyColConfId = misPropertyColConfId;
    }

    public String getMisPropertyConfigDetailId() {
        return misPropertyConfigDetailId;
    }

    public void setMisPropertyConfigDetailId(String misPropertyConfigDetailId) {
        this.misPropertyConfigDetailId = misPropertyConfigDetailId;
    }

    public String getMisPropertyConfigRegularExpression() {
        return misPropertyConfigRegularExpression;
    }

    public void setMisPropertyConfigRegularExpression(String misPropertyConfigRegularExpression) {
        this.misPropertyConfigRegularExpression = misPropertyConfigRegularExpression;
    }
}

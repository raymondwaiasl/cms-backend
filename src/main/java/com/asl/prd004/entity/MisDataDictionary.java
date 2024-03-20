package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "mis_data_dictionary", schema = "MIS", catalog = "")
public class MisDataDictionary extends BaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_dd_id", nullable = false)
    private String misDdId;

    @Column(name = "mis_dd_name", nullable = false)
    private String misDdName;

    @Column(name = "mis_dd_prop_type", nullable = false)
    private Integer misDdPropType;

    @Column(name = "mis_dd_prop_sql")
    private String misDdPropSql;

    public String getMisDdId() {
        return misDdId;
    }

    public void setMisDdId(String misDdId) {
        this.misDdId = misDdId;
    }

    public String getMisDdName() {
        return misDdName;
    }

    public void setMisDdName(String misDdName) {
        this.misDdName = misDdName;
    }

    public Integer getMisDdPropType() {
        return misDdPropType;
    }

    public void setMisDdPropType(Integer misDdPropType) {
        this.misDdPropType = misDdPropType;
    }

    public String getMisDdPropSql() {
        return misDdPropSql;
    }

    public void setMisDdPropSql(String misDdPropSql) {
        this.misDdPropSql = misDdPropSql;
    }
}

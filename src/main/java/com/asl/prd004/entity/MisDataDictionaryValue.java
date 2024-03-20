package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "mis_data_dictionary_value", schema = "MIS", catalog = "")
public class MisDataDictionaryValue extends BaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_ddv_id", nullable = false)
    private String misDdvId;
    @Column(name = "mis_dd_id", nullable = false)
    private String misDdId;
    @Column(name = "mis_ddv_key", nullable = false)
    private String misDdvKey;
    @Column(name = "mis_ddv_value", nullable = false)
    private String misDdvValue;

    public String getMisDdvId() {
        return misDdvId;
    }

    public void setMisDdvId(String misDdvId) {
        this.misDdvId = misDdvId;
    }

    public String getMisDdvKey() {
        return misDdvKey;
    }

    public void setMisDdvKey(String misDdvKey) {
        this.misDdvKey = misDdvKey;
    }

    public String getMisDdvValue() {
        return misDdvValue;
    }

    public void setMisDdvValue(String misDdvValue) {
        this.misDdvValue = misDdvValue;
    }

    public String getMisDdId() {
        return misDdId;
    }

    public void setMisDdId(String misDdId) {
        this.misDdId = misDdId;
    }


}

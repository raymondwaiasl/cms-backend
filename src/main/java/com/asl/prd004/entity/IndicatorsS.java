package com.asl.prd004.entity;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "indicators_s")
@Data
public class IndicatorsS extends BaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "ind_code", nullable = false,length = 10)
    private String indCode;

    @Column(name = "subcategory_code", nullable = false, length = 20)
    private String subcategoryCode;

    @Column(name = "ind_name_en", length = 250)
    private String indNameEn;

    @Column(name = "ind_name_tc", length = 100)
    private String indNameTc;

    @Column(name = "ind_name_l_2_en", length = 250)
    private String indNameL2En;

    @Column(name = "ind_name_l_2_tc", length = 100)
    private String indNameL2Tc;

    @Column(name = "data_type", length = 20)
    private String dataType;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "active")
    private Integer active;

    @Column(name = "ind_name_l2_en", length = 250)
    private String subIndicatorNameEn;

    @Column(name = "ind_name_l2_tc", length = 100)
    private String subIndicatorNameTc;

    @Transient
    private String categoryCode;

}
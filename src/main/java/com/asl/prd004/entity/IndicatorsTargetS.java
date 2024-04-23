package com.asl.prd004.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Data
@Table(name = "indicators_target_s")
public class IndicatorsTargetS extends BaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "molu_code", nullable = false, length = 10)
    private String moluCode;

    @Column(name = "ind_code", nullable = false, length = 10)
    private String indCode;

    @Column(name = "fiscal_year", length = 10)
    private String fiscalYear;

    @Column(name = "year")
    private Integer year;

    @Column(name = "target")
    private Double target;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IndicatorsTargetS that = (IndicatorsTargetS) o;
        return Objects.equals(moluCode, that.moluCode) && Objects.equals(indCode, that.indCode) && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), moluCode, indCode, year);
    }
}
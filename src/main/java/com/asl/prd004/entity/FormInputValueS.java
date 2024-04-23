package com.asl.prd004.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "form_input_value_s")
@Data
public class FormInputValueS extends BaseModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "form_input_entry_id", length = 16)
    private String formInputEntryId;

    @Column(name = "year")
    private Integer year;

    @Column(name = "start_month")
    private Integer startMonth;

    @Column(name = "end_month")
    private Integer endMonth;

    @Column(name = "ind_code", length = 10)
    private String indCode;

    @Column(name = "data_amount")
    private Double data_amount;

    @Column(name = "data_count")
    private Integer dataCount;

    @Column(name = "data_text", length = 4000)
    private String dataText;

    @Column(name = "revised")
    private Integer revised;

    @Column(name = "revised_user", length = 50)
    private String revisedUser;

}
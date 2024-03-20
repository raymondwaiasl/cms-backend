package com.asl.prd004.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/6/5 16:09
 */
@Entity
@Data
@Table(name = "mis_wf_config_dtl", schema = "MIS", catalog = "")
public class MisWfConfigDtl extends BaseModel{
    @Id
    @Column(name = "mis_wf_config_dtl_id",  nullable = false, length = 30)
    private String misWfConfigDtlId;
    @Column(name = "mis_wf_config_id",  nullable = false, length = 30)
    private String misWfConfigId;
    @Column(name = "mis_type_id",   length = 30)
    private String misTypeId;
    @Column(name = "mis_is_draft",   length = 1)
    private String misIsDraft;


}



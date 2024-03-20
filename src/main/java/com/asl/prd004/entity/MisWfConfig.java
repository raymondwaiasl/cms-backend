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
@Table(name = "mis_wf_config", schema = "MIS", catalog = "")
public class MisWfConfig extends BaseModel{
    @Id
    @Column(name = "mis_wf_config_id",  nullable = false, length = 20)
    private String misWfConfigId;
    @Column(name = "mis_Profile_name",  nullable = false, length = 100)
    private String misProfileName;
    @Column(name = "mis_is_auto_submit",  nullable = false, length = 1)
    private String misIsAutoSubmit;
    @Column(name = "mis_init_status",  nullable = false, length = 1)
    private String misInitStatus;
    @Column(name = "wf_process_id",  nullable = true, length = 30)
    private String wfProcessId;



}



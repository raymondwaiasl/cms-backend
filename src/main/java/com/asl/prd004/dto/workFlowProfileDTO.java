package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class workFlowProfileDTO {

    private String wfConfigId;

    private String wfConfigProfileName;

    private String typeId;

    private String typeName;

    private String autoSubmit;

    private String processId;

    private String processName;

    private String misInitStatus;

    private String misIsDraft;

}

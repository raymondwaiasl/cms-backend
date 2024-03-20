package com.asl.prd004.dto;

import com.asl.prd004.enums.WfStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountWorkflowDTO {

    private WfStatusEnum columnName;
    private BigInteger countData;

}

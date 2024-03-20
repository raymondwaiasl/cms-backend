package com.asl.prd004.dto;

import com.asl.prd004.entity.MisPermissionDetail;
import lombok.Data;

import java.util.List;

@Data
public class MisPermissionDto {

    private String misPermissionId;


    private String misPermissionName;


    private String misPermissionType;


    public List<MisPermissionDetail> details;

}

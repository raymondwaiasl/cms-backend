package com.asl.prd004.dto;

import com.asl.prd004.entity.MisPermission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO implements Serializable {

//    private String misPdId;
//    private String misPermissionId;
//    private String misPdType;
//    private String typeName;
//    private String misPdPerformerId;
//    private String misPdRight;
//
//    public PermissionDTO(String misPdId, String misPermissionId, String misPdType, String typeName, String misPdPerformerId, String misPdRight) {
//        this.misPdId = misPdId;
//        this.misPermissionId = misPermissionId;
//        this.misPdType = misPdType;
//        this.typeName = typeName;
//        this.misPdPerformerId = misPdPerformerId;
//        this.misPdRight = misPdRight;
//    }

    private String folderId;
    private String folderPer;
    private MisPermission permission;
}

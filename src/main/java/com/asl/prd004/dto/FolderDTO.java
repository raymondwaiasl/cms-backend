package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO implements Serializable {
    private String misFolderId;
    private String misFolderName;
    private String misFolderFullPath;
    private String misFolderParentId;
    private String misPermissionId;
}

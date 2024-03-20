package com.asl.prd004.dto;

import lombok.Data;

import java.util.List;

@Data
public class NodeDto {
    private String parentId;
    private String id;
    private String misFolderId;
    private String path;
    private String misFolderName;
    private List<NodeDto> children;

    public NodeDto(String parentId, String id, String misFolderId, String path, String misFolderName,List<NodeDto> children) {
        this.parentId = parentId;
        this.id = id;
        this.misFolderId = misFolderId;
        this.path = path;
        this.misFolderName = misFolderName;
        this.children = children;
    }
}

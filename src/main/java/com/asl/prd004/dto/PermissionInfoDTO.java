package com.asl.prd004.dto;

import java.io.Serializable;

public class PermissionInfoDTO implements Serializable{
    private String id;
    private String name;
    private String misPermissionId;
    private String misPermissionName;
    private String misPermissionType;
    private String misPdType;
    private String misPdPerformerId;
    private String misPdRight;

    public PermissionInfoDTO(String id, String name, String misPermissionId,String misPermissionName, String misPermissionType, String misPdType,String misPdPerformerId, String misPdRight) {
        this.id = id;
        this.name = name;
        this.misPermissionId=misPermissionId;
        this.misPermissionName = misPermissionName;
        this.misPermissionType = misPermissionType;
        this.misPdType = misPdType;
        this.misPdPerformerId=misPdPerformerId;
        this.misPdRight = misPdRight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMisPermissionId() {
        return misPermissionId;
    }

    public void setMisPermissionId(String misPermissionId) {
        this.misPermissionId = misPermissionId;
    }

    public String getMisPermissionName() {
        return misPermissionName;
    }

    public void setMisPermissionName(String misPermissionName) {
        this.misPermissionName = misPermissionName;
    }

    public String getMisPermissionType() {
        return misPermissionType;
    }

    public void setMisPermissionType(String misPermissionType) {
        this.misPermissionType = misPermissionType;
    }

    public String getMisPdType() {
        return misPdType;
    }

    public void setMisPdType(String misPdType) {
        this.misPdType = misPdType;
    }

    public String getMisPdPerformerId() {
        return misPdPerformerId;
    }

    public void setMisPdPerformerId(String misPdPerformerId) {
        this.misPdPerformerId = misPdPerformerId;
    }

    public String getMisPdRight() {
        return misPdRight;
    }

    public void setMisPdRight(String misPdRight) {
        this.misPdRight = misPdRight;
    }
}

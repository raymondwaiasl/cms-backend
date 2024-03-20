package com.asl.prd004.dto;

import java.io.Serializable;

public class UserGroupInfoDTO implements Serializable{
    private String id;
    private String name;
    private String type;
    private String isAdmin;
    private String defaultFolderId;

    public UserGroupInfoDTO(String id, String name, String type, String isAdmin, String defaultFolderId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isAdmin = isAdmin;
        this.defaultFolderId = defaultFolderId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getDefaultFolderId() {
        return defaultFolderId;
    }

    public void setDefaultFolderId(String defaultFolderId) {
        this.defaultFolderId = defaultFolderId;
    }
}

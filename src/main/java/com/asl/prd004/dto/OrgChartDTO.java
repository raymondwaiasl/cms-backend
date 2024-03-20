package com.asl.prd004.dto;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrgChartDTO implements Serializable {
    private String parentId;
    private String id;
    private String level;
    private String name;

    public OrgChartDTO(String parentId,String id,String level, String name) {
        this.parentId=parentId;
        this.id = id;
        this.level=level;
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //@Transient
    private List children = new ArrayList<>();

    public List getChildren() {
        return children;
    }

    public void setChildren(List children) {
        this.children = children;
    }
}

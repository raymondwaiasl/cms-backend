package com.asl.prd004.entity;



import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mis_member", schema = "MIS", catalog = "")
public class MisMember extends BaseModel implements Serializable {

    @Id
    @Column(name = "mis_member_id", nullable = false)
    private String misMemberId;

    @Column(name = "mis_member_parent_id", nullable = false)
    private String misMemberParentId;

    @Column(name = "mis_member_parent_type", nullable = true)
    private String misMemberParentType;

    @Column(name = "mis_member_child_id", nullable = true)
    private String misMemberChildId;

    @Column(name = "mis_member_child_type", nullable = true)
    private String misMemberChildType;

    public String getMisMemberId() {
        return misMemberId;
    }

    public void setMisMemberId(String misMemberId) {
        this.misMemberId = misMemberId;
    }

    public String getMisMemberParentId() {
        return misMemberParentId;
    }

    public void setMisMemberParentId(String misMemberParentId) {
        this.misMemberParentId = misMemberParentId;
    }

    public String getMisMemberParentType() {
        return misMemberParentType;
    }

    public void setMisMemberParentType(String misMemberParentType) {
        this.misMemberParentType = misMemberParentType;
    }

    public String getMisMemberChildId() {
        return misMemberChildId;
    }

    public void setMisMemberChildId(String misMemberChildId) {
        this.misMemberChildId = misMemberChildId;
    }

    public String getMisMemberChildType() {
        return misMemberChildType;
    }

    public void setMisMemberChildType(String misMemberChildType) {
        this.misMemberChildType = misMemberChildType;
    }

    @Transient
    private List children = new ArrayList<>();

    public List getChildren() {
        return children;
    }

    public void setChildren(List children) {
        this.children = children;
    }
}

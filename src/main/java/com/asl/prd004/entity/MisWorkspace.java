package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "mis_workspace", schema = "MIS", catalog = "")
public class MisWorkspace implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_workspace_id", nullable = false)
    private String misWorkspaceId;

    @Column(name = "mis_workspace_name", nullable = false)
    private String misWorkspaceName;

    @Column(name = "mis_sort_num", nullable = true)
    private Integer misSortNum;

    @Column(name = "mis_workspace_parent_id", nullable = true)
    private String misWorkspaceParentId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "workspace")
    private List<MisWorkspaceWidget> widgets;

    public String getMisWorkspaceId() {
        return misWorkspaceId;
    }

    public void setMisWorkspaceId(String misWorkspaceId) {
        this.misWorkspaceId = misWorkspaceId;
    }

    public String getMisWorkspaceName() {
        return misWorkspaceName;
    }

    public void setMisWorkspaceName(String misWorkspaceName) {
        this.misWorkspaceName = misWorkspaceName;
    }

    public Integer getMisSortNum() {
        return misSortNum;
    }

    public void setMisSortNum(Integer misSortNum) {
        this.misSortNum = misSortNum;
    }

    public List<MisWorkspaceWidget> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<MisWorkspaceWidget> widgets) {
        this.widgets = widgets;
    }

    public String getMisWorkspaceParentId() {
        return misWorkspaceParentId;
    }

    public void setMisWorkspaceParentId(String misWorkspaceParentId) {
        this.misWorkspaceParentId = misWorkspaceParentId;
    }
}

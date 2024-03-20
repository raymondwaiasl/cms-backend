package com.asl.prd004.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "mis_workspace_widget", schema = "MIS", catalog = "")
public class MisWorkspaceWidget {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_ww_id", nullable = false, length = 16)
    private String misWwId;

    @Basic
    @Column(name = "mis_workspace_id", nullable = false, length = 16)
    private String misWorkspaceId;
    @Basic
    @Column(name = "mis_ww_alias", nullable = false, length = 50)
    private String misWwAlias;

    @Basic
    @Column(name = "mis_ww_title", nullable = false, length = 255)
    private String misWwTitle;

    @Basic
    @Column(name = "mis_bi_config_id", nullable = false, length = 255)
    private String misBiConfigId;

    @Basic
    @Column(name = "mis_widget_id", nullable = true, length = 16)
    private String misWidgetId;

    public String getMisWidgetId() {
        return misWidgetId;
    }

    public void setMisWidgetId(String misWidgetId) {
        this.misWidgetId = misWidgetId;
    }

    @Basic
    @Column(name = "i", nullable = false, length = 50)
    private String i;
    @Basic
    @Column(name = "x", nullable = false)
    private int x;
    @Basic
    @Column(name = "y", nullable = false)
    private int y;
    @Basic
    @Column(name = "w", nullable = false)
    private int w;
    @Basic
    @Column(name = "h", nullable = false)
    private int h;
    @ManyToOne
    @JoinColumn(name="mis_workspace_id", insertable = false,updatable = false)
    @JsonIgnore
    private MisWorkspace workspace;


    public String getMisWwId() {
        return misWwId;
    }

    public void setMisWwId(String misWwId) {
        this.misWwId = misWwId;
    }


    public String getMisWorkspaceId() {
        return misWorkspaceId;
    }

    public void setMisWorkspaceId(String misWorkspaceId) {
        this.misWorkspaceId = misWorkspaceId;
    }


    public String getMisWwAlias() {
        return misWwAlias;
    }

    public void setMisWwAlias(String misWwAlias) {
        this.misWwAlias = misWwAlias;
    }

    public String getMisWwTitle() {
        return misWwTitle;
    }

    public void setMisWwTitle(String misWwTitle) {
        this.misWwTitle = misWwTitle;
    }

    public String getMisBiConfigId() {
        return misBiConfigId;
    }

    public void setMisBiConfigId(String misBiConfigId) {
        this.misBiConfigId = misBiConfigId;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public MisWorkspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(MisWorkspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisWorkspaceWidget that = (MisWorkspaceWidget) o;


        if (x != that.x) return false;
        if (y != that.y) return false;
        if (w != that.w) return false;
        if (h != that.h) return false;
        if (misWwId != null ? !misWwId.equals(that.misWwId) : that.misWwId != null) return false;
        if (misWorkspaceId != null ? !misWorkspaceId.equals(that.misWorkspaceId) : that.misWorkspaceId != null)
            return false;
        if (misWwAlias != null ? !misWwAlias.equals(that.misWwAlias) : that.misWwAlias != null) return false;
        if (i != null ? !i.equals(that.i) : that.i != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misWwId != null ? misWwId.hashCode() : 0;
        result = 31 * result + (misWorkspaceId != null ? misWorkspaceId.hashCode() : 0);
        result = 31 * result + (misWwAlias != null ? misWwAlias.hashCode() : 0);
        result = 31 * result + (i != null ? i.hashCode() : 0);
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + w;
        result = 31 * result + h;
        return result;
    }
}

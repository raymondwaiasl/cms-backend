package com.asl.prd004.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mis_property_section", schema = "MIS", catalog = "")
public class MisPropertySection {
    private String misPropertySectionId;
    private String misPropertyId;
    private String misSectionLabel;
    private String misIsLock;
    private String misLockedBy;
    private String misSectionVisible;

    @Id
    @Column(name = "mis_property_section_id", nullable = false, length = 20)
    public String getMisPropertySectionId() {
        return misPropertySectionId;
    }

    public void setMisPropertySectionId(String misPropertySectionId) {
        this.misPropertySectionId = misPropertySectionId;
    }
    @Column(name = "mis_property_id", nullable = false, length = 20)
    public String getMisPropertyId() {
        return misPropertyId;
    }

    public void setMisPropertyId(String misPropertyId) {
        this.misPropertyId = misPropertyId;
    }
    @Column(name = "mis_section_label", nullable = false, length = 20)
    public String getMisSectionLabel() {
        return misSectionLabel;
    }

    public void setMisSectionLabel(String misSectionLabel) {
        this.misSectionLabel = misSectionLabel;
    }
    @Column(name = "mis_is_lock",  length = 1)
    public String getMisIsLock() {
        return misIsLock;
    }

    public void setMisIsLock(String misIsLock) {
        this.misIsLock = misIsLock;
    }
    @Column(name = "mis_locked_by",  length = 20)
    public String getMisLockedBy() {
        return misLockedBy;
    }

    public void setMisLockedBy(String misLockedBy) {
        this.misLockedBy = misLockedBy;
    }
    @Column(name = "mis_section_visible",  length = 1)
    public String getMisSectionVisible() {
        return misSectionVisible;
    }

    public void setMisSectionVisible(String misSectionVisible) {
        this.misSectionVisible = misSectionVisible;
    }
}

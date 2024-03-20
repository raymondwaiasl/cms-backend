package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "mis_widget", schema = "MIS", catalog = "")
public class MisWidget implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_widget_id", nullable = false)
    private String misWidgetId;

    @Basic
    @Column(name = "mis_widget_name", nullable = false)
    private String misWidgetName;

    @Basic
    @Column(name = "mis_basic_widget", nullable = false)
    private String misBasicWidget;

    @Basic
    @Column(name = "mis_widget_config", nullable = false)
    private String misWidgetConfig;

    @Basic
    @Column(name = "mis_widget_type", nullable = false)
    private String misWidgetType;

    @Basic
    @Column(name = "mis_default_table", nullable = true)
    private String misDefaultTable;

    @Basic
    @Column(name = "mis_simple_search_id", nullable = true)
    private String misSimpleSearchId;

    @Basic
    @Column(name = "mis_display_header", nullable = true)
    private String misDisplayHeader;

    @Basic
    @Column(name = "mis_header_title", nullable = true)
    private String misHeaderTitle;


    public String getMisWidgetId() {
        return misWidgetId;
    }

    public void setMisWidgetId(String misWidgetId) {
        this.misWidgetId = misWidgetId;
    }

    public String getMisWidgetName() {
        return misWidgetName;
    }

    public void setMisWidgetName(String misWidgetName) {
        this.misWidgetName = misWidgetName;
    }

    public String getMisBasicWidget() {
        return misBasicWidget;
    }

    public void setMisBasicWidget(String misBasicWidget) {
        this.misBasicWidget = misBasicWidget;
    }

    public String getMisWidgetConfig() {
        return misWidgetConfig;
    }

    public void setMisWidgetConfig(String misWidgetConfig) {
        this.misWidgetConfig = misWidgetConfig;
    }

    public String getMisWidgetType() {
        return misWidgetType;
    }

    public void setMisWidgetType(String misWidgetType) {
        this.misWidgetType = misWidgetType;
    }

    public String getMisDefaultTable() {
        return misDefaultTable;
    }

    public void setMisDefaultTable(String misDefaultTable) {
        this.misDefaultTable = misDefaultTable;
    }

    public String getMisSimpleSearchId() {
        return misSimpleSearchId;
    }

    public void setMisSimpleSearchId(String misSimpleSearchId) {
        this.misSimpleSearchId = misSimpleSearchId;
    }

    public String getMisDisplayHeader() {
        return misDisplayHeader;
    }

    public void setMisDisplayHeader(String misDisplayHeader) {
        this.misDisplayHeader = misDisplayHeader;
    }

    public String getMisHeaderTitle() {
        return misHeaderTitle;
    }

    public void setMisHeaderTitle(String misHeaderTitle) {
        this.misHeaderTitle = misHeaderTitle;
    }
}

package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "mis_widget_column", schema = "MIS", catalog = "")
public class MisWidgetColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_widget_column_id", nullable = false, length = 16)
    private String misWidgetColumnId;

    @Basic
    @Column(name = "mis_widget_id", nullable = false, length = 16)
    private String misWidgetId;

    @Basic
    @Column(name = "mis_column_id", nullable = false, length = 16)
    private String misColumnId;


    public String getMisWidgetColumnId() {
        return misWidgetColumnId;
    }

    public void setMisWidgetColumnId(String misWidgetColumnId) {
        this.misWidgetColumnId = misWidgetColumnId;
    }


    public String getMisWidgetId() {
        return misWidgetId;
    }

    public void setMisWidgetId(String misWidgetId) {
        this.misWidgetId = misWidgetId;
    }


    public String getMisColumnId() {
        return misColumnId;
    }

    public void setMisColumnId(String misColumnId) {
        this.misColumnId = misColumnId;
    }

}

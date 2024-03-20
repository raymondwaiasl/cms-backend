package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Ethan
 */

@Entity
@Table(name = "mis_simple_search", schema = "MIS", catalog = "")
public class MisSimpleSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_simple_search_id", nullable = false, length = 16)
    private String misSimpleSearchId;

    @Basic
    @Column(name = "mis_simple_search_name", nullable = false, length = 50)
    private String misSimpleSearchName;

    @Basic
    @Column(name = "mis_simple_search_sql", nullable = false, length = 500)
    private String misSimpleSearchSql;


    public String getMisSimpleSearchId() {
        return misSimpleSearchId;
    }

    public void setMisSimpleSearchId(String misSimpleSearchId) {
        this.misSimpleSearchId = misSimpleSearchId;
    }


    public String getMisSimpleSearchName() {
        return misSimpleSearchName;
    }

    public void setMisSimpleSearchName(String misSimpleSearchName) {
        this.misSimpleSearchName = misSimpleSearchName;
    }


    public String getMisSimpleSearchSql() {
        return misSimpleSearchSql;
    }

    public void setMisSimpleSearchSql(String misSimpleSearchSql) {
        this.misSimpleSearchSql = misSimpleSearchSql;
    }


}

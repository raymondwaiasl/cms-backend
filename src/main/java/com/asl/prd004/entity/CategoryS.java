package com.asl.prd004.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "category_s")
public class CategoryS extends BaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;


    @Column(name = "category_code", nullable = false, length = 20, unique = true)
    private String categoryCode;

    @Column(name = "category_name_en", nullable = false, length = 100)
    private String categoryNameEn;

    @Column(name = "category_name_tc", nullable = false, length = 50)
    private String categoryNameTc;

    @Column(name = "year_type", length = 20)
    private String yearType;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryNameEn() {
        return categoryNameEn;
    }

    public void setCategoryNameEn(String categoryNameEn) {
        this.categoryNameEn = categoryNameEn;
    }

    public String getCategoryNameTc() {
        return categoryNameTc;
    }

    public void setCategoryNameTc(String categoryNameTc) {
        this.categoryNameTc = categoryNameTc;
    }

    public String getYearType() {
        return yearType;
    }

    public void setYearType(String yearType) {
        this.yearType = yearType;
    }
}
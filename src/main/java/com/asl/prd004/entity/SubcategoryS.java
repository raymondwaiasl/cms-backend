package com.asl.prd004.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "subcategory_s")
public class SubcategoryS extends BaseModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "id", nullable = false, length = 16)
    private String id;

    @Column(name = "subcategory_code", nullable = false, length = 20)
    private String subcategoryCode;


    @Column(name = "category_code", nullable = false, length = 20)
    private String categoryCode;


    @Column(name = "subcategory_name_en", length = 100)
    private String subcategoryNameEn;

    @Column(name = "subcategory_name_tc", length = 50)
    private String subcategoryNameTc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubcategoryCode() {
        return subcategoryCode;
    }

    public void setSubcategoryCode(String subcategoryCode) {
        this.subcategoryCode = subcategoryCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getSubcategoryNameEn() {
        return subcategoryNameEn;
    }

    public void setSubcategoryNameEn(String subcategoryNameEn) {
        this.subcategoryNameEn = subcategoryNameEn;
    }

    public String getSubcategoryNameTc() {
        return subcategoryNameTc;
    }

    public void setSubcategoryNameTc(String subcategoryNameTc) {
        this.subcategoryNameTc = subcategoryNameTc;
    }
}
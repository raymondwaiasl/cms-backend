package com.asl.prd004.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author 86181
 */

@Entity
@Table(name = "mis_simple_search_item", schema = "MIS", catalog = "")
public class MisSimpleSearchItem {
    private String misSimpleSearchItemId;
    private String misSimpleSearchId;
    private String itemName;
    private String itemType;
    private String itemDictionary;
    private Integer itemColSize;
    private Integer itemRowSize;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy = "com.asl.prd004.utils.SeqGenerator")
    @Column(name = "mis_simple_search_item_id", nullable = false, length = 16)
    public String getMisSimpleSearchItemId() {
        return misSimpleSearchItemId;
    }

    public void setMisSimpleSearchItemId(String misSimpleSearchItemId) {
        this.misSimpleSearchItemId = misSimpleSearchItemId;
    }

    @Basic
    @Column(name = "mis_simple_search_id", nullable = false, length = 16)
    public String getMisSimpleSearchId() {
        return misSimpleSearchId;
    }

    public void setMisSimpleSearchId(String misSimpleSearchId) {
        this.misSimpleSearchId = misSimpleSearchId;
    }

    @Basic
    @Column(name = "item_name", nullable = false, length = 50)
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Basic
    @Column(name = "item_type", nullable = false, length = 2)
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @Basic
    @Column(name = "item_dictionary", nullable = true, length = 16)
    public String getItemDictionary() {
        return itemDictionary;
    }

    public void setItemDictionary(String itemDictionary) {
        this.itemDictionary = itemDictionary;
    }

    @Basic
    @Column(name = "item_col_size", nullable = true)
    public Integer getItemColSize() {
        return itemColSize;
    }

    public void setItemColSize(Integer itemColSize) {
        this.itemColSize = itemColSize;
    }

    @Basic
    @Column(name = "item_row_size", nullable = true)
    public Integer getItemRowSize() {
        return itemRowSize;
    }

    public void setItemRowSize(Integer itemRowSize) {
        this.itemRowSize = itemRowSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MisSimpleSearchItem that = (MisSimpleSearchItem) o;

        if (misSimpleSearchItemId != null ? !misSimpleSearchItemId.equals(that.misSimpleSearchItemId) : that.misSimpleSearchItemId != null)
            return false;
        if (misSimpleSearchId != null ? !misSimpleSearchId.equals(that.misSimpleSearchId) : that.misSimpleSearchId != null)
            return false;
        if (itemName != null ? !itemName.equals(that.itemName) : that.itemName != null) return false;
        if (itemType != null ? !itemType.equals(that.itemType) : that.itemType != null) return false;
        if (itemDictionary != null ? !itemDictionary.equals(that.itemDictionary) : that.itemDictionary != null)
            return false;
        if (itemColSize != null ? !itemColSize.equals(that.itemColSize) : that.itemColSize != null) return false;
        if (itemRowSize != null ? !itemRowSize.equals(that.itemRowSize) : that.itemRowSize != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = misSimpleSearchItemId != null ? misSimpleSearchItemId.hashCode() : 0;
        result = 31 * result + (misSimpleSearchId != null ? misSimpleSearchId.hashCode() : 0);
        result = 31 * result + (itemName != null ? itemName.hashCode() : 0);
        result = 31 * result + (itemType != null ? itemType.hashCode() : 0);
        result = 31 * result + (itemDictionary != null ? itemDictionary.hashCode() : 0);
        result = 31 * result + (itemColSize != null ? itemColSize.hashCode() : 0);
        result = 31 * result + (itemRowSize != null ? itemRowSize.hashCode() : 0);
        return result;
    }
}

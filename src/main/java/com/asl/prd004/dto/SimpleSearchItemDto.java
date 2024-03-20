package com.asl.prd004.dto;

import com.asl.prd004.entity.MisSimpleSearch;
import com.asl.prd004.entity.MisSimpleSearchItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSearchItemDto {

    public SimpleSearchItemDto(MisSimpleSearchItem simpleSearchItem) {
        this.id = simpleSearchItem.getMisSimpleSearchItemId();
        this.itemName = simpleSearchItem.getItemName();
        this.inputType = simpleSearchItem.getItemType();
        this.itemDictionary = simpleSearchItem.getItemDictionary();
        this.rowSize = simpleSearchItem.getItemRowSize();
        this.colSize = simpleSearchItem.getItemColSize();
    }

    private String id;
    private String itemName;
    private String inputType;
    private String itemDictionary;
    private List<DicDto> itemLs;
    private Integer rowSize;
    private Integer colSize;

}

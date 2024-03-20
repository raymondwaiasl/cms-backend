package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class MenuItemDto {

    public MenuItemDto(String to,String name,Integer sort,String parent){
        this.id = to;this.to = to;this.name = name;this.sort = sort;this.parent = parent;this.children = new ArrayList<>();
    }
    private String id;
    private String to;
    private String name;
    private Integer sort;
    private String parent;
    private List<MenuItemDto> children;
}

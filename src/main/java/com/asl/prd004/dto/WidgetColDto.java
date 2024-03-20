package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WidgetColDto {
    public WidgetColDto(DicDto d) {
        this.id = d.getKey();
        this.name = d.getValue();
    }
    private String id;
    private String name;

}

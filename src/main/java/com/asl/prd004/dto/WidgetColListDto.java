package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WidgetColListDto {

    private List<WidgetColDto> includeList;
    private List<WidgetColDto> excludeList;
}

package com.asl.prd004.dto;

import com.asl.prd004.entity.MisPropertyColumnConfCond;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyColumnCondDto {

    private String misPropertyColConfId;
    private String misPropertyConfigDetailId;
    private String misPropertyConfigRegularExpression;
    private LinkedHashMap<String, ArrayList<MisPropertyColumnConfCond>> columnConfigConditions;

}

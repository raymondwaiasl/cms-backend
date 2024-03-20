package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySearchDto {


    private String misSavedSearchId;
    private String misSavedSearchName;
    private String misQueryFormId;
    private String misSavedSearchUserId;
    private Date misSavedSearchDate;
    private String tableName;
}

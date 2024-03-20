package com.asl.prd004.dto;

import lombok.Data;

@Data
public class ExportCsvDTO extends SearchRecordDTO{
    private String exportType;//0单表，1复杂查询
}

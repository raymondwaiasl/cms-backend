package com.asl.prd004.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CalcColumnQueryResultDTO {
    private Map<String,Object> param;
    private String misColumnId;
}

package com.asl.prd004.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SystemConfigKeyDto {
    public SystemConfigKeyDto(String key){
        this.key = key;
    }

    private String key;
}

package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class TypeIdDto {
    public TypeIdDto(String id){
        this.id = id;
    }

    private String id;
    private String allowSearch;
}

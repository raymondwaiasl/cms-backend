package com.asl.prd004.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
//@AllArgsConstructor
public class DicDto {

    public DicDto(String key,String value){this.key=key;this.value=value;}
    public DicDto(String key,String value,String allowSearch){this.key=key;this.value=value;this.allowSearch=allowSearch;}

    private String keyId;
    private String key;

    private String value;

    private String allowSearch;
}

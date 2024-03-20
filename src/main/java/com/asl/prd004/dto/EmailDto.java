package com.asl.prd004.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailDto {
    public EmailDto(String email){
        this.email = email;
    }

    private String email;
}

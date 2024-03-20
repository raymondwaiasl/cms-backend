package com.asl.prd004.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ResetPasswordDto {
    private String token;
    private String password;
}

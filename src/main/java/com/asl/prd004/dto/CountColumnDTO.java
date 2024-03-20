package com.asl.prd004.dto;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountColumnDTO {

    private String date;
    private String columnName;
    private BigInteger countData;

}

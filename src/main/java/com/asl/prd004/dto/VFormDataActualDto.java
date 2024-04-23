package com.asl.prd004.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VFormDataActualDto {

    private String indicatorCode;
    private String type;
    private String value;
    private String isRevised;
}

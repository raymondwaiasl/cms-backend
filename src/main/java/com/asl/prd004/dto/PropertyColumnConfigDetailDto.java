package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyColumnConfigDetailDto {

    private String misPropertyConfigDetailId;
    private String misPropertyId;
    private String misPropertyConfigDetailColumnId;
    private Timestamp creationDate;
    private String creatorUserId;
    private Timestamp updatedDate;
    private String updatedUserId;
    private Integer rowSize;
    private Integer colSize;
    private String misPropertySectionId;
    private String misIsLock;
    private String misLockedBy;
    private PropertyColumnCondDto columnConfigDetail;
}

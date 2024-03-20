package com.asl.prd004.vo;

import lombok.Data;
import java.util.List;
@Data
public class TypeVO {
    private String misTypeId;
    private String misTypeLabel;
    private String misTypeName;
    private List<MisColumnVO> misColumnList;
}

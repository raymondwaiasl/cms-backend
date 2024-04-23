package com.asl.prd004.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserDto {



    private String misUserLoginId;
    private String misUserName;
    private String surnameEng;
    private String givenNameEng;
    private String otherNameEng;
    private String misEmail;
    private String district;
    private String tel;
    private String fax;
    private String misUserStatus;
    private String userPost;
    private String misUserType;
    private String office;
    private PageableDto pageable;
}

package com.asl.prd004.dao;

import com.asl.prd004.entity.CmsFormat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CmsFormatDao extends JpaRepository<CmsFormat, String> {

    List<CmsFormat> getCmsFormatsByCmsFormatDosExt(String ext);

}

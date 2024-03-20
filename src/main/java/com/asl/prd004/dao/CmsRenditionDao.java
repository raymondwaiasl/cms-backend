package com.asl.prd004.dao;

import com.asl.prd004.entity.CmsRendition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CmsRenditionDao extends JpaRepository<CmsRendition, String> {


    List<CmsRendition> getCmsRenditionsByMisRecordId(String recordId);

    CmsRendition getCmsRenditionByCmsRenditionId(String renditionId);
    @Modifying
    @Query(nativeQuery = true, value = "update cms_rendition set cms_rendition_file=?2 where cms_rendition_id =?1")
    void updateRendition(String renditionId, String newRenditionName);
}

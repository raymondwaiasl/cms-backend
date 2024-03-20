package com.asl.prd004.dao;

import com.asl.prd004.entity.MisCmsAutolink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * @description: TODO
 * @author: billy
 * @date: 2022/12/1 10:03
 * @version: 1.0
 */
public interface MisCmsAutolinkDao extends JpaRepository<MisCmsAutolink, String> {

    List<MisCmsAutolink> getMisCmsAutolinksByMisTypeIdAndMisFolderId(String typeId,String folderId);
}

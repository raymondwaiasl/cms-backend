package com.asl.prd004.dao;

import com.asl.prd004.entity.MisSimpleSearchItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimpleSearchItemDao extends JpaRepository<MisSimpleSearchItem, String> {

    List<MisSimpleSearchItem> getMisSimpleSearchItemsByMisSimpleSearchId(String id);

    void deleteByMisSimpleSearchId(String id);
}

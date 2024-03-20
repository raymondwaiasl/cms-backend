package com.asl.prd004.dao;

import com.asl.prd004.dto.WidgetDto;
import com.asl.prd004.entity.MisPasswordHistory;
import com.asl.prd004.entity.MisWidget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordHistoryDao extends JpaRepository<MisPasswordHistory, String> {


    List<MisPasswordHistory> getMisPasswordHistoriesByMisUserIdOrderByCreateTimeDesc(String userId);

}

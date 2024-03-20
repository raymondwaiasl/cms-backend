package com.asl.prd004.dao;

import com.asl.prd004.entity.MisWorkspaceWidget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkSpaceWidgetDao extends JpaRepository<MisWorkspaceWidget, String> {
    List<MisWorkspaceWidget> getMisWorkspaceWidgetsByMisWorkspaceId(String id);

}


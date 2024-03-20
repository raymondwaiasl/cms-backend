package com.asl.prd004.dao;

import com.asl.prd004.entity.ChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @description: TODO
 * @author: billy
 * @date: 2023/2/24 14:45
 * @version: 1.0
 */
public interface ChunkDao extends JpaRepository<ChunkEntity,String> {
    @Query(nativeQuery = true,value="select chunk_number from mis_chunk_bak where identifier=?1")
    List<String> queryChunkList(String identifier);
}

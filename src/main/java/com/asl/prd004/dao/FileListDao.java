package com.asl.prd004.dao;

import com.asl.prd004.entity.FileListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @description: TODO
 * @author: billy
 * @date: 2023/2/24 14:44
 * @version: 1.0
 */
public interface FileListDao extends JpaRepository<FileListEntity,String> {
    @Query(nativeQuery = true,value="select * from mis_file_list where identifier=?1")
    FileListEntity queryFileListByMD5(String identifier);
}

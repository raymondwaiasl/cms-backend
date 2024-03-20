package com.asl.prd004.service;
import com.asl.prd004.entity.ChunkEntity;
/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2022/11/21 17:44
 */
public interface BigFileService {

    String isUpload(String data);

    String uploadBigFile(ChunkEntity chunkEntity);

    String mergeChumksFile(String data);
}



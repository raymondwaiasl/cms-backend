package com.asl.prd004.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import com.asl.prd004.dto.PageDataDto;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2022/11/21 17:44
 */
public interface ContentService {

    boolean upLoadFile(MultipartFile[] files, String treeData, String tableList, String folderId, String isAll, String isAutoLink) throws IOException, JSONException;

    boolean saveAutoLink(JSONObject json);

    boolean insertTableData(JSONObject json);

    PageDataDto queryVersionData(String data);

    byte[] downloadFile(String data);

    boolean UpLoadCheckFile(MultipartFile files, String misTypeId, String misRecordId, String cmsVersionNo, String cmsFileLocation);

    boolean delVersion(String data);

    PageDataDto getAllAutoLink(String folderId,String typeId,String folderName,JSONObject pageState, JSONObject sort);

    boolean deleteAutolinkById(String data);

    boolean editAutolink(String data);

    boolean doContentData(String targetFile, String filename, String treeData, String tableList, String folderId, String isAll, String isAutoLink,String fileSize);

    boolean UpLoadBigFile(String targetFile, String filename, String misTypeId, String misRecordId, String cmsVersionNo, String cmsFileLocation,String fileSize);

    void updateVersionData(String versionId,String misTypeId, String misRecordId);
}



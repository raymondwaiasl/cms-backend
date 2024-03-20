package com.asl.prd004.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.asl.prd004.dao.ChunkDao;
import com.asl.prd004.dao.FileListDao;
import com.asl.prd004.entity.ChunkEntity;
import com.asl.prd004.entity.FileListEntity;
import com.asl.prd004.service.BigFileService;
import com.asl.prd004.service.ContentService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author billy
 * @version 1.0
 * @description: TODO
 * @date 2023/2/24 14:09
 */
@Service
public class BigFileServiceImpl implements BigFileService {
    @Autowired
    private FileListDao fileListDao;
    @Autowired
    private ChunkDao chunkDao;
    @Autowired
    private ContentService contentService;


    private String filePath="C:\\Users\\billy\\Desktop\\fsdownload";
    @Override
    public String isUpload(String data) {
        try {
            JSONObject json = new JSONObject(data);
            String identifier=json.getString("identifiler");
            FileListEntity fileListEntity=fileListDao.queryFileListByMD5(identifier);
            if (fileListEntity != null) {
                return "true";
            }
            List<String> chunkEntityList=chunkDao.queryChunkList(identifier);
            String chunkStr= (String) chunkEntityList.stream().collect(Collectors.joining(","));
            return chunkStr;
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            return "false";
        }
    }
    @Transactional
    @Override
    public String uploadBigFile(ChunkEntity chunkEntity) {
        try {
           /* ChunkEntity chunkEntity=new ChunkEntity();
            JSONObject json = new JSONObject(data);
            int chunkNumber= json.getInt("chunkNumber");
            int totalChunks=json.getInt("totalChunks");
            String identifier=json.getString("identifier");
            int chunkSize=json.getInt("chunkSize");
            int currentChunkSize=json.getInt("currentChunkSize");
            String relativePath=json.getString("relativePath");
            String filename=json.getString("filename");
            int totalSize=json.getInt("totalSize");
            File upfile= new File(json.getString("upfile"));*/
            Path path = Paths.get(generatePath(filePath, chunkEntity));
            try {
               Files.write(path,  chunkEntity.getUpfile());
                //写入数据库
                //chunkEntity.setId(String.valueOf(UUID.randomUUID()));
                //chunkDao.save(chunkEntity);
            } catch (Exception e) {
                return "false";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "true";
    }

    @Override
    public String mergeChumksFile(String data) {
        try {
            JSONObject json = new JSONObject(data);
            String filename=json.getString("filename");
            String identifier=json.getString("identifier");
            Integer totalSize=json.getInt("totalSize");
            boolean hasTableId=json.has("treeData");
            String file = filePath + "/" + identifier + "/" + filename;
            String folder = filePath + "/" + identifier;
            String misTypeId =hasTableId==false?json.getString("misTypeId"):"";
            String misRecordId =hasTableId==false?json.getString("misRecordId"):"";
            String cmsVersionNo =hasTableId==false?json.getString("cmsVersionNo"):"";
            String cmsFileLocation =hasTableId==false?json.getString("cmsFileLocation"):"";
            String treeData =hasTableId==true?json.getString("treeData"):"";
            String tableList =hasTableId==true?json.getString("tableList"):"";
            String folderId =hasTableId==true?json.getString("folderId"):"";
            String isAll =hasTableId==true?json.getString("isAll"):"";
            String isAutoLink =hasTableId==true?json.getString("isAutoLink"):"";
            String fileSize=json.getString("fileSize");
            String flag = mergeFile(file, folder, filename, misTypeId, misRecordId, cmsVersionNo, cmsFileLocation,treeData,tableList,folderId,isAll,isAutoLink,fileSize);
            return flag;

        } catch (JSONException e) {
            //throw new RuntimeException(e);
            return "false";
        }

    }

    /**
     * 文件合并
     *
     * @param targetFile      要形成的文件名
     * @param folder          要形成的文件夹地址
     * @param filename        文件的名称
     * @param misTypeId
     * @param misRecordId
     * @param cmsVersionNo
     * @param cmsFileLocation
     */
    private String mergeFile(String targetFile, String folder, String filename, String misTypeId, String misRecordId, String cmsVersionNo, String cmsFileLocation, String treeData,String tableList,String folderId,String isAll,String isAutoLink,String fileSize) {
        try {
            //先判断文件是否存在
            if(fileExists(targetFile)) {
                //文件已存在
                return "true";
            }

            Files.createFile(Paths.get(targetFile));
            Files.list(Paths.get(folder))
                    .filter(path -> !path.getFileName().toString().equals(filename))
                    .sorted((o1, o2) -> {
                        String p1 = o1.getFileName().toString();
                        String p2 = o2.getFileName().toString();
                        int i1 = p1.lastIndexOf("-");
                        int i2 = p2.lastIndexOf("-");
                        return Integer.valueOf(p2.substring(i2)).compareTo(Integer.valueOf(p1.substring(i1)));
                    })
                    .forEach(path -> {
                        try {
                            //以追加的形式写入文件
                            Files.write(Paths.get(targetFile), Files.readAllBytes(path), StandardOpenOption.APPEND);
                            //合并后删除该块
                            Files.delete(path);

                        } catch (IOException e) {
                            e.getMessage();
                        }
                    });

        if("".equals(treeData)){
            boolean isSucess=contentService.UpLoadBigFile(targetFile,filename,misTypeId,misRecordId,cmsVersionNo,cmsFileLocation,fileSize);
        }else{
            boolean isSucess=contentService.doContentData(targetFile,filename,treeData,tableList,folderId,isAll,isAutoLink,fileSize);
        }
            //new JSchUtil().put(targetFile,"/abc/"+filename);
        } catch (IOException e) {
            return "false";
        }
        return "true";
    }

    /**
     * 根据文件的全路径名判断文件是否存在
     * @param file
     * @return
     */
    private boolean fileExists(String file) {
        boolean fileExists = false;
        Path path = Paths.get(file);
        fileExists = Files.exists(path,new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});
        return fileExists;
    }

    private String generatePath(String uploadFolder, ChunkEntity chunk) {
        StringBuilder sb = new StringBuilder();
        //文件夹地址/md5
        sb.append(uploadFolder).append("/").append(chunk.getIdentifier());
        //判断uploadFolder/identifier 路径是否存在，不存在则创建
        if (!Files.isWritable(Paths.get(sb.toString()))) {
            try {
                Files.createDirectories(Paths.get(sb.toString()));
            } catch (IOException e) {
                e.getMessage();
            }
        }
        //文件夹地址/md5/文件名-1
        return sb.append("/")
                .append(chunk.getFilename())
                .append("-")
                .append(chunk.getChunkNumber()).toString();
    }
}



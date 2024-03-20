package com.asl.prd004.controller;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.dto.PageDataDto;
import com.asl.prd004.entity.ChunkEntity;
import com.asl.prd004.service.BigFileService;
import com.asl.prd004.service.ContentService;
import com.asl.prd004.utils.JSchUtil;
import com.asl.prd004.utils.Log;
import org.apache.catalina.connector.ClientAbortException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;


/**
 * @author billy
 * @version 1.0
 * @description: 内容创建处理类
 * @date 2022/11/16 10:40
 */
@RestController
@RequestMapping("/Content")
@CrossOrigin(origins = {"http://localhost:3000/"}, allowCredentials = "true", allowedHeaders = {"X-Requested-With"},
        maxAge = 3600L, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD})
public class ContentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentController.class);
    private static final String DOWNLOAD_PATH = "C:\\Users\\andyyuan\\Documents\\Virtual Machines\\ubuntu-22.04.2-desktop-amd64.iso";
    @Autowired
    private ContentService contentService;

    @Autowired
    private BigFileService bigFileService;

    @Log("UpLoad file")
    @PostMapping(value = "/upLoadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultGenerator upLoadFile(@RequestParam("files") MultipartFile[] files,
                                              @RequestParam("treeData") String treeData,
                                              @RequestParam("tableList") String tableList,
                                              @RequestParam("folderId") String folderId,
                                              @RequestParam("isAll") String isAll,
                                              @RequestParam("isAutoLink") String isAutoLink) throws Exception {

        boolean data = contentService.upLoadFile(files, treeData, tableList, folderId, isAll, isAutoLink);
        if (data) {
            return ResultGenerator.getSuccessResult(data);
        }else {
            return ResultGenerator.getFailResult("参数为空!");
        }
    }
    /**
     * @description:保存配置autoLink信息
     * @author: billy
     * @date: 2022/11/30 16:45
     * @param: [data]
     * @return: com.asl.prd004.common.utils.result.ResponseBody
     **/
    @Log("Save autoLink")
    @PostMapping(value="/saveAutoLink")
    public ResultGenerator saveAutoLink(@RequestBody String data) throws JSONException {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        boolean isSuccess=contentService.saveAutoLink(json);
        if(isSuccess){
            return ResultGenerator.getSuccessResult("save success data");
        }else {
            return ResultGenerator.getFailResult("save fail data");
        }
    }
    @Log("Insert table data")
    @PostMapping(value="/insertTableData")
    public ResultGenerator insertTableData(@RequestBody String data) throws JSONException {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json =new JSONObject(data);
        boolean isSuccess=contentService.insertTableData(json);
        if(isSuccess){
            return ResultGenerator.getSuccessResult("save success data");
        }else {
            return ResultGenerator.getFailResult("save fail data");
        }
    }
    @Log("Query version data")
    @PostMapping(value="/queryVersionData")
    public ResultGenerator queryVersionData(@RequestBody String data) throws JSONException {
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        PageDataDto versionDto=contentService.queryVersionData(data);
        return ResultGenerator.getSuccessResult(versionDto);
    }
    @Log("Download version file")
    @PostMapping(value="/downloadFile")
    public void downloadFile(@RequestBody String data,  HttpServletResponse response) throws Exception {
        OutputStream out = null;
        byte[] buffer=contentService.downloadFile(data);
        JSONObject json =new JSONObject(data);
        String downFile=json.getString("downFile");
        int index = downFile.indexOf("/");
        //根据第一个点的位置 获得第二个点的位置
        index = downFile.indexOf("/", index + 1);
        String fileName=downFile.substring(index+1);
        // 设置浏览器解析文件的mime类型，如果js中已设置，这里可以不设置
        response.setContentType("application/octet-stream");
        // 设置此项，在IE浏览器中下载Excel文件时可弹窗展示文件下载
        //response.setHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setHeader("Content-disposition","attachment; filename="+new String(fileName.getBytes("gb2312"), "ISO8859-1" )  );
        // 允许浏览器访问header中的FileName
        response.setHeader("Access-Control-Expose-Headers", "FileName");
        // 设置FileName，转码防止中文乱码
        response.setHeader("FileName", URLEncoder.encode(downFile, "UTF-8"));
        response.getOutputStream().write(buffer);
        out = response.getOutputStream();
        out.close();
    }
    @Log("UpLoad check file")
    @PostMapping(value="/UpLoadCheckFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultGenerator UpLoadCheckFile(MultipartFile files,String misTypeId,String misRecordId,String cmsVersionNo,String cmsFileLocation)throws Exception{
        if(files==null){
            return ResultGenerator.getFailResult("参数为空!");
        }
        boolean updatFlag = contentService.UpLoadCheckFile(files, misTypeId, misRecordId, cmsVersionNo, cmsFileLocation);
        if (updatFlag) {
            return ResultGenerator.getSuccessResult("save success data");
        } else {
            return ResultGenerator.getFailResult("save fail data");
        }
    }
    @Log("Del version")
    @PostMapping(value="/delVersion")
    public ResultGenerator delVersion(@RequestBody String data)throws Exception{
        if(data==null){
            return ResultGenerator.getFailResult("参数为空!");
        }
        boolean delFlag = contentService.delVersion(data);
        if (delFlag) {
            return ResultGenerator.getSuccessResult("delete data success");
        } else {
            return ResultGenerator.getFailResult("delete data fail");
        }
    }
    @Log("Query all autoLink")
    @RequestMapping(value="/getAllAutoLink",method = RequestMethod.POST, produces = "application/json")
    public ResultGenerator getAllAutoLink(@RequestBody String data)throws Exception{
        if(data==null||"".equals(data)){
            return ResultGenerator.getFailResult("参数为空!");
        }
        JSONObject json = new JSONObject(data);
        String  folderId = json.getString("folderId");
        String  typeId = json.getString("typeId");
        String  folderName = json.getString("folderName");
        JSONObject  pageState = json.getJSONObject("pageState");
        JSONObject  sort = json.getJSONObject("sortModel");
        PageDataDto autoDto=contentService.getAllAutoLink(folderId,typeId,folderName,pageState,sort);
        return ResultGenerator.getSuccessResult(autoDto);
    }
    @Log("Delete autolinkById")
    @PostMapping(value="/deleteAutolinkById")
    public ResultGenerator deleteAutolinkById(@RequestBody String data)throws Exception{
        if(data==null){
            return ResultGenerator.getFailResult("参数为空!");
        }
        boolean delFlag = contentService.deleteAutolinkById(data);
        if (delFlag) {
            return ResultGenerator.getSuccessResult("delete data success");
        } else {
            return ResultGenerator.getFailResult("delete data fail");
        }
    }
    @Log("Edit autoLink")
    @PostMapping(value="/editAutoLink")
    public ResultGenerator editAutoLink(@RequestBody String data)throws Exception{
        if(data==null){
            return ResultGenerator.getFailResult("参数为空!");
        }
        boolean editFlag = contentService.editAutolink(data);
        if (editFlag) {
            return ResultGenerator.getSuccessResult("edit data success");
        } else {
            return ResultGenerator.getFailResult("edit data fail");
        }
    }
    @Log("Check file is upload")
    @PostMapping(value="/isUpload")
    public ResultGenerator isUpload(@RequestBody String data)throws Exception{
        if(data==null){
            return ResultGenerator.getFailResult("参数为空!");
        }
       String editFlag = bigFileService.isUpload(data);
       return ResultGenerator.getSuccessResult(editFlag);
    }
    @Log("Upload big file")
    @PostMapping(value="/uploadBigFile")
    public ResultGenerator uploadBigFile(@RequestParam Integer chunkNumber, @RequestParam Long chunkSize, @RequestParam Long currentChunkSize, @RequestParam Long totalSize, @RequestParam String identifier, @RequestParam String filename, @RequestParam String relativePath, @RequestParam Integer totalChunks, @RequestParam(value="upfile",required = false) MultipartFile upfile)throws Exception{

        ChunkEntity chunkEntity=new ChunkEntity();
        chunkEntity.setChunkNumber(chunkNumber);
        chunkEntity.setChunkSize(chunkSize);
        chunkEntity.setCurrentChunkSize(currentChunkSize);
        chunkEntity.setTotalSize(totalSize);
        chunkEntity.setIdentifier(identifier);
        chunkEntity.setFilename(filename);
        chunkEntity.setRelativePath(relativePath);
        chunkEntity.setTotalChunks(totalChunks);
        chunkEntity.setUpfile(upfile.getBytes());
        String editFlag = bigFileService.uploadBigFile(chunkEntity);
        if (editFlag!=null&&"true".equals(editFlag)) {
            return ResultGenerator.getSuccessResult("true");
        } else {
            return ResultGenerator.getFailResult("false");
        }
    }
    @Log("Merge chumks file")
    @PostMapping(value="/mergeChumksFile")
    public ResultGenerator mergeChumksFile(@RequestBody String data)throws Exception{
        if(data==null){
            return ResultGenerator.getFailResult("参数为空!");
        }
        String editFlag = bigFileService.mergeChumksFile(data);
        if (editFlag!=null&&"true".equals(editFlag)) {
            return ResultGenerator.getSuccessResult(true);
        } else {
            return ResultGenerator.getFailResult("false");
        }
    }
    @Log("Merge content chumks file")
    @PostMapping(value="/mergeContentChumksFile")
    public ResultGenerator mergeContentChumksFile(@RequestBody String data)throws Exception{
        if(data==null){
            return ResultGenerator.getFailResult("参数为空!");
        }
        String editFlag = bigFileService.mergeChumksFile(data);
        if (editFlag!=null&&"true".equals(editFlag)) {
            return ResultGenerator.getSuccessResult(true);
        } else {
            return ResultGenerator.getFailResult("false");
        }
    }
    /**
    * @description:分片大文件下载
    * @author: billy
    * @date: 2023/6/13 11:35
    * @param: [request, response, fileName]
    * @return: void
    **/
    @Log("Download chumk file")
    @PostMapping(value="/downloadChunkFile", produces ="application/json; charset=utf-8")
    public void downloadChunkFile(HttpServletRequest request, HttpServletResponse response,@RequestBody String data) throws Exception {
        LOGGER.info(String.format("文件开始下载"));
        JSONObject json = null;
        json = new JSONObject(data);
        String  range = json.getString("range");
        String  downFile = json.getString("downFile");
        String misTypeId=json.getString("misTypeId");
        String misRecordId=json.getString("misRecordId");
        String versionId=json.getString("versionId");
        boolean isDisabled=json.getBoolean("isDisabled");
        String fileSize=json.getString("fileSize");
        int index = downFile.indexOf("/");
        int endIndex=downFile.lastIndexOf("/");
        String dwonFileName=downFile.substring(endIndex+1);
        //根据第一个点的位置 获得第二个点的位置
        index = downFile.indexOf("/", index + 1);
        String createFile ="";
        if(range.equals("bytes=0-1")){
            File localFile = new File("src\\main\\resources\\downDir\\"+ dwonFileName);
            if(localFile.exists()){
                createFile ="src\\main\\resources\\downDir\\"+ dwonFileName;
            }else{
                createFile =new JSchUtil().localCreateFile(downFile,dwonFileName);
            }
        }else{
            createFile ="src\\main\\resources\\downDir\\"+ dwonFileName;
        }
        File file = new File(createFile);
        //开始下载位置
        long startByte = 0;
        //结束下载位置
        long endByte = file.length() - 1;
        //有range的话
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String[] ranges = range.split("-");
            try {
                //判断range的类型
                if (ranges.length == 1) {
                    //类型一：bytes=-2343
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //类型二：bytes=2343-
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //类型三：bytes=22-2343
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }

            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = file.length() - 1;
                LOGGER.error("Range Occur Error,Message:{}", e.getLocalizedMessage());
            }
        }
        //要下载的长度
        long contentLength = endByte - startByte + 1;
        //文件名
        String fileName = file.getName();
        //文件类型
        String contentType = request.getServletContext().getMimeType(fileName);
        //解决下载文件时文件名乱码问题
        String downloadFileName = URLEncoder.encode(fileName, "UTF-8");
        //各种响应头设置
        response.setHeader("Accept-Ranges", "bytes");
        //http状态码要为206
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setContentType(contentType);
        response.setHeader("Content-Type", contentType+"; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        response.setHeader("Access-Control-Expose-Headers","Content-Disposition");
        response.setHeader("Content-Length", String.valueOf(contentLength));
        //Content-Range，格式为  [要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());
        //已传送数据大小
        long transmitted = 0;
        try (BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
             RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");) {
            byte[] buff = new byte[4096];
            int len = 0;
            randomAccessFile.seek(startByte);
            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
                transmitted += len;
            }
            //处理不足buff.length部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                outputStream.write(buff, 0, len);
                transmitted += len;
            }
            if((Long.valueOf(fileSize)==Long.valueOf(endByte))||(Long.valueOf(fileSize)-1==Long.valueOf(endByte))){
                //File localFile = new File(createFile);
               // localFile.delete();
                if(isDisabled){
                    contentService.updateVersionData(versionId,misTypeId,misRecordId);
                }
            }
            LOGGER.info("下载完毕：" + startByte + "-" + endByte + "：" + transmitted);
        } catch (ClientAbortException e) {
            LOGGER.warn("用户停止下载：" + startByte + "-" + endByte + "：" + transmitted);
            //捕获此异常表示拥护停止下载
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("用户下载IO异常，Message：{}", e.getLocalizedMessage());
        }
    }

    /**
     * 文件byte[]类型转File
     *
     * @param bytes     bytes
     * @param outPath   输出目录
     * @param fileName  文件名
     * @return
     */
    public static File bytesToFile(byte[] bytes, String outPath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(outPath);
            if (!dir.exists() && dir.isDirectory()) { //判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(outPath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }
}


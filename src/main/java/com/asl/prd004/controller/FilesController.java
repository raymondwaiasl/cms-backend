package com.asl.prd004.controller;

import cn.hutool.core.io.FileUtil;
import com.asl.prd004.config.ResultGenerator;
import com.asl.prd004.constant.FileDirConstant;
import com.asl.prd004.dto.FileInfoDTO;
import com.asl.prd004.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
//@CrossOrigin("http://localhost:8081")
@RequestMapping("/files")
public class FilesController {

  @Autowired
  FilesStorageService storageService;
//  @PostMapping("/upload")
//  public ResultGenerator uploadFile(@RequestParam("file") MultipartFile file) {
//    String message = "";
//    try {
//      storageService.save(file);
//
//      message = "Uploaded the file successfully: " + file.getOriginalFilename();
//      return ResultGenerator.getSuccessResult();
////      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
//    } catch (Exception e) {
//      message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
//      return ResultGenerator.getFailResult(message);
////      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
//    }
//  }

//  @GetMapping("/list")
//  public ResultGenerator<List<FileInfoDTO>> getListFiles() {
//    List<FileInfoDTO> fileInfos = storageService.loadAll().map(path -> {
//      String filename = path.getFileName().toString();
//      String url = MvcUriComponentsBuilder
//          .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();
//
//      return new FileInfoDTO(filename, url);
//    }).collect(Collectors.toList());
//
//    return ResultGenerator.getSuccessResult(fileInfos);
//  }

  @GetMapping("/{dir}/{filename:.+}")
  public void getFile(@PathVariable String dir, @PathVariable String filename, HttpServletResponse response) throws IOException {
    Resource file = storageService.load(FileDirConstant.getPath(dir),filename);
    FileUtil.writeToStream(file.getFile(),response.getOutputStream());
  }

//  @DeleteMapping("/files/{filename:.+}")
//  public ResultGenerator deleteFile(@PathVariable String filename) {
//    String message = "";
//
//    try {
//      boolean existed = storageService.delete(filename);
//
//      if (existed) {
//        message = "Delete the file successfully: " + filename;
////        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
//        return ResultGenerator.getSuccessResult(message);
//      }
//
//      message = "The file does not exist!";
////      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(message));
//      return ResultGenerator.getSuccessResult(message);
//    } catch (Exception e) {
//      message = "Could not delete the file: " + filename + ". Error: " + e.getMessage();
////      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(message));
//      return ResultGenerator.getFailResult(message);
//    }
//  }
}

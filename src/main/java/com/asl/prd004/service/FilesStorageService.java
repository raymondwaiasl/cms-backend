package com.asl.prd004.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {
//  public void init();

  public void save(Path path,MultipartFile file);

  public Resource load(Path path,String filename);
  
  public boolean delete(Path path,String filename);

  public void deleteAll(Path path);

  public Stream<Path> loadAll(Path path);
}

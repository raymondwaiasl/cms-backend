package com.asl.prd004.service.impl;

import com.asl.prd004.service.FilesStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

//  @Override
//  public void init() {
//    try {
//      Files.createDirectories(root);
//    } catch (IOException e) {
//      throw new RuntimeException("Could not initialize folder for upload!");
//    }
//  }

  @Override
  public void save(Path path,MultipartFile file) {
    try {
      Path targetPath = path.resolve(file.getOriginalFilename());
      if(Files.exists(targetPath)){
        Files.deleteIfExists(targetPath);
      }
      Files.copy(file.getInputStream(), targetPath);
    } catch (Exception e) {
      if (e instanceof FileAlreadyExistsException) {
        throw new RuntimeException("A file of that name already exists.");
      }

      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public Resource load(Path path,String filename) {
    try {

      Path file = path.resolve(filename);
      Resource resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("Could not read the file!");
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  @Override
  public boolean delete(Path path,String filename) {
    try {
      Path file = path.resolve(filename);
      return Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  @Override
  public void deleteAll(Path path) {
    FileSystemUtils.deleteRecursively(path.toFile());
  }

  @Override
  public Stream<Path> loadAll(Path path) {
    try {
//      return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
      return Files.walk(path).filter(p -> !p.equals(path)).map(path::relativize);
    } catch (IOException e) {
      throw new RuntimeException("Could not load the files!");
    }
  }

}

package com.asl.prd004.constant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileDirConstant {
    public static final Path ROOT = Paths.get("resource");
    public static final Path SYS_CONFIG = Paths.get("resource" + File.separator + "sysconfig");
    private static final Map<String,Path> STRING_2_PATH = new HashMap<>();
    public static Path getPath(String string){
        return STRING_2_PATH.get(string);
    }
    static {
        STRING_2_PATH.put("root",ROOT);
        STRING_2_PATH.put("sysconfig",SYS_CONFIG);
        try {
            Files.createDirectories(ROOT);
            Files.createDirectories(SYS_CONFIG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

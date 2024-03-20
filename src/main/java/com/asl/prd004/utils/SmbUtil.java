package com.asl.prd004.utils;

import cn.hutool.core.io.FileUtil;
import jcifs.smb.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class SmbUtil {

    public static void main(String[] args) {
        getRemoteFile();
    }

    public static void getRemoteFile() {
        InputStream in = null;
        try {
            // 创建远程文件对象
            // smb://ip地址/共享的路径/...
            // smb://用户名:密码@ip地址/共享的路径/...
            String remoteUrl = "smb://TestShare:z1019288@192.168.xxx.xxx/share/";
            SmbFile remoteFile = new SmbFile(remoteUrl);
            remoteFile.connect();//尝试连接
            if (remoteFile.exists()) {
                // 获取共享文件夹中文件列表
                SmbFile[] smbFiles = remoteFile.listFiles();
                for (SmbFile smbFile : smbFiles) {
                    createFile(smbFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void createFile(SmbFile remoteFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File localFile = new File("D:/file/" + remoteFile.getName());
            in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            byte[] buffer = new byte[4096];
            //读取长度
            int len = 0;
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 共享文件夹所在服务器ip
    private static String USER_DOMAIN = "192.168.xxx.xxx";
    //访问用户
    private static String USER_ACCOUNT = "userxx";
    //访问密码
    private static String USER_PWS = "xxx";
    //共享文件夹地址
    private static final String shareDirectory = "smb://192.168.xxx.xxx/dir";
    //字节长度
    private static final int byteLen = 1024;

    /**
     * @Title smbPut
     * @Description 向共享目录上传文件
     * @Param shareDirectory 共享目录
     * @Param localFilePath 本地目录中的文件路径
     * @date 2019-01-10 20:16
     */
    public static void smbPut(String shareDirectory, MultipartFile localFile,String fileName) {
        InputStream in = null;
        OutputStream out = null;
        try {
//            String fileName = localFile.getOriginalFilename();

            // 域服务器验证
//            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(USER_DOMAIN, USER_ACCOUNT,
//                    USER_PWS);
//            SmbFile remoteFile = new SmbFile(shareDirectory + "/" + fileName, auth);
            SmbFile remoteFile = new SmbFile("smb://" + shareDirectory + "/" + fileName);

            File file = null;
            // 创建临时文件
            file = File.createTempFile("temp", null);
            // 把multipartFile写入临时文件
            localFile.transferTo(file);
            // 使用文件创建 inputStream 流
            in = new FileInputStream(file);
            out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
            byte[] buffer = new byte[byteLen];
            while (in.read(buffer) != -1) {
                out.write(buffer);
                buffer = new byte[byteLen];
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // MultipartFile转换为InputStream
    private static InputStream multipartToInputStream(MultipartFile multipartFile) throws IOException {
        InputStream inputStream = null;
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile("temp", null);
            // 把multipartFile写入临时文件
            multipartFile.transferTo(file);
            // 使用文件创建 inputStream 流
            inputStream = new FileInputStream(file);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 最后记得删除文件
            file.deleteOnExit();
            // 关闭流
            inputStream.close();
        }
        return inputStream;
    }

    public static void deleteFile(String shareDirectory,String fileName){
        SmbFile smbFile;
        try {
            smbFile = new SmbFile("smb://" + shareDirectory + "/" + fileName);
            if(smbFile.exists()){
                smbFile.delete();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e){
            e.printStackTrace();
        }
    }
    /**
    * @description:fileName进行变更公共方法
    * @author: billy
    * @date: 2023/1/13 15:32
    * @param: [multipartFile, newFileName]
    * @return: org.springframework.web.multipart.MultipartFile
    **/
    public static MultipartFile fileRename(MultipartFile multipartFile,String newFileName) throws Exception{

        String originalFilename = multipartFile.getOriginalFilename();
        String[] filename = originalFilename.split("\\.");
        //临时文件
        File file=File.createTempFile(filename[0], "."+filename[1]);
        multipartFile.transferTo(file);
        //重命名
        file = FileUtil.rename(file, newFileName, true, true);
        InputStream inputStream = new FileInputStream(file);
        //File转换成MultipartFile
        multipartFile = new MockMultipartFile(file.getName(), inputStream);
        //程序退出后删除临时文件
        file.deleteOnExit();
        return multipartFile;
    }
    public  String getWebInfPath(){
        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
        String path = url.toString();
        int index = path.indexOf("WEB-INF");

        if(index == -1){
            index = path.indexOf("classes");
        }

        if(index == -1){
            index = path.indexOf("bin");
        }

        path = path.substring(0, index);

        if(path.startsWith("zip")){//当class文件在war中时，此时返回zip:D:/...这样的路径
            path = path.substring(4);
        }else if(path.startsWith("file")){//当class文件在class文件中时，此时返回file:/D:/...这样的路径
            path = path.substring(6);
        }else if(path.startsWith("jar")){//当class文件在jar文件里面时，此时返回jar:file:/D:/...这样的路径
            path = path.substring(10);
        }
        try {
            path =  URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return path;
    }
}



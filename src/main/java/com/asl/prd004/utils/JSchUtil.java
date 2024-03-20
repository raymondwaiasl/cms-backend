package com.asl.prd004.utils;

import com.jcraft.jsch.*;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;


import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * @author billy
 * @version 1.0
 * @description:对linux服务器进行文件上传、下载、删除、查看目录下文件及创建文件夹等功能的工具类
 * @date 2022/11/14 9:33
 */
public class JSchUtil {
    private String charset = "UTF-8"; // 设置编码格式,可以根据服务器的编码设置相应的编码格式
    private JSch jSch;
    private Session jSchSession;
    Channel channel = null;
    ChannelSftp chSftp = null;
    String username = PropertiesUtil.getProperty("linux.userName");
    String password = PropertiesUtil.getProperty("linux.password");
    String host = PropertiesUtil.getProperty("linux.host");
    int port = Integer.valueOf(PropertiesUtil.getProperty("linux.port"));
    public static void main(String[] args) throws Exception {
        //JSchUtil jSchUtil=new JSchUtil();
        //jSchUtil.chSftp.get("D:\\test","/abc/0028000000000074-0200000000000499-0432000000000752.mp4");
        //String filename = "/abc/0028000000000074-0200000000000500-0432000000000753.mp4";
        //String abc=jSchUtil.localCreateFile("/abc/0028000000000074-0200000000000500-0432000000000753.mp4","0028000000000074-0200000000000500-0432000000000753.mp4");
        //System.out.println("abc11==="+abc);
        //jSchUtil.viewLs("/root/Desktop/abc");
        //jSchUtil.put("C:\\Users\\billy\\Desktop\\临时文件\\group.jpg","/abc");
        //File abc=jSchUtil.downloadFile("/abc/0028000000000074-0200000000000498-0432000000000751.mp4","/abc");
        //System.out.println("======="+jSchUtil.viewLs("/abc"));
        //jSchUtil.createDir("/root/ykf");
        //jSchUtil.deleteFile("/testFile/AzureExam.pdf");
        }
    /**
    * @description:linux连接公共方法
    * @author: billy
    * @date: 2022/11/15 9:25
    * @param: []
    * @return: void
    **/
   public void linuxConnect(){

       // 创建JSch对象
       jSch = new JSch();
       try {

           // 根据主机账号、ip、端口获取一个Session对象
           jSchSession = jSch.getSession(username, host, port);

           // 存放主机密码
           jSchSession.setPassword(password);

           // 去掉首次连接确认
           Properties config = new Properties();
           config.put("StrictHostKeyChecking", "no");
           jSchSession.setConfig(config);

           // 超时连接时间为3秒
           jSchSession.setTimeout(3000);

           // 进行连接
           jSchSession.connect();

           // 打开SFTP通道
           chSftp = (ChannelSftp)jSchSession.openChannel("sftp");

           // 建立SFTP通道的连接
           chSftp.connect();
       } catch (Exception e) {
           System.out.println(e.getMessage());
       }
   }
   /**
   * @description:linux指定目录上传文件
   * @author: billy
   * @date: 2022/11/16 14:44
   * @param: [uploadFile 上传的文件流, linuxDir 上传文件目录,fileName 文件名]
   * @return: java.lang.String
   **/
   public String uploadFile(String linuxDir, InputStream uploadFile, String fileName){
       try {
           linuxConnect();
           chSftp.cd(linuxDir);
           chSftp.put(uploadFile, fileName);
       } catch (SftpException e) {
           return "Fail";
       } finally {
           // 关闭jschSesson流
           if (jSchSession != null && jSchSession.isConnected()) {
               jSchSession.disconnect();
           }
       }
       return "Success";
   }
    /**
     * @description:linux指定目录上传文件
     * @author: billy
     * @date: 2022/11/16 14:44
     * @param: [uploadFile 上传的文件流, linuxDir 上传文件目录,fileName 文件名]
     * @return: java.lang.String
     **/
    public String put(String linuxDir,  String fileName){
        try {
            linuxConnect();
            chSftp.put(linuxDir, fileName);
        } catch (SftpException e) {
            return "Fail";
        } finally {
            // 关闭jschSesson流
            if (jSchSession != null && jSchSession.isConnected()) {
                jSchSession.disconnect();
            }
        }
        return "Success";
    }
   /**
   * @description:linux服务器下载文件
   * @author: billy
   * @date: 2022/11/16 14:47
   * @param: [directory 下载放到本地文件夹 , downloadFile 服务器下载的路径文件]
   * @return: java.lang.String
   **/
    public byte[] download( String downloadFile,String directory) {
        byte[] buffer=null;
       try{
           linuxConnect();
            //chSftp.get( downloadFile,directory);
           chSftp.cd(directory);
           InputStream is = chSftp.get(downloadFile);
           is.close();
           return buffer;
        } catch (Exception e) {
           e.printStackTrace();
           //throw new RuntimeException(e);
           //return "Fail";
        }  finally {
           // 关闭jschSesson流
           if (jSchSession != null && jSchSession.isConnected()) {
               jSchSession.disconnect();
           }
        }
       return buffer;
    }
    /**
     * InputStream转换为byte[]
     * @param is
     * @return byte[]
     * @throws IOException
     */
    private byte [] inputStreamToByte(InputStream is) throws IOException {
        byte data [] = new byte[0];
        try {
            ByteArrayOutputStream bAOutputStream = new ByteArrayOutputStream();
            int ch;
            while((ch = is.read() ) != -1){
                bAOutputStream.write(ch);
            }
            data = bAOutputStream.toByteArray();
            bAOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        return data;
    }

    /**
    * @description:查看服务器对应文件夹下所有文件
    * @author: billy
    * @date: 2022/11/16 14:54
    * @param: [linuxDir]
    * @return: java.util.Vector<java.util.List>
    **/
    public Vector<List> viewLs(String linuxDir){
        Vector<List> vector= null;
        try {
            linuxConnect();
            vector = chSftp.ls(linuxDir);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }finally {
            // 关闭jschSesson流
            if (jSchSession != null && jSchSession.isConnected()) {
                jSchSession.disconnect();
            }
        }
        return vector;
    }
    /**
    * @description:服务器创建文件夹
    * @author: billy
    * @date: 2022/11/16 14:58
    * @param: [linuxDir]
    * @return: java.lang.String
    **/
    public String createDir(String linuxDir){
        try {
            linuxConnect();
            chSftp.mkdir(linuxDir);
        } catch (SftpException e) {
            return "Fail";
        }finally {
            // 关闭jschSesson流
            if (jSchSession != null && jSchSession.isConnected()) {
                jSchSession.disconnect();
            }
        }
        return "Success";
    }
    /**
    * @description:服务器删除指定文件
    * @author: billy
    * @date: 2022/11/16 15:00
    * @param: [delFile]
    * @return: java.lang.String
    **/
    public String deleteFile(String delFile){
        try {
            linuxConnect();
            chSftp.rm(delFile);
        } catch (SftpException e) {
            return "Fail";
        }finally {
            // 关闭jschSesson流
            if (jSchSession != null && jSchSession.isConnected()) {
                jSchSession.disconnect();
            }
        }
        return "Success";
    }
    /**
    * @description: linux上传文件名进行重命名
    * @author: billy
    * @date: 2023/1/13 17:39
    * @param: [oldPath, newPath]
    * @return: java.lang.String
    **/
    public String reName(String oldPath, String newPath){
        try {
            linuxConnect();
            chSftp.rename(oldPath,newPath);
        } catch (SftpException e) {
            return "Fail";
        } finally {
            // 关闭jschSesson流
            if (jSchSession != null && jSchSession.isConnected()) {
                jSchSession.disconnect();
            }
        }
        return "Success";
    }

    public String localCreateFile(String filename,String localFileName) throws Exception{
        linuxConnect();
        SftpATTRS attr = chSftp.stat(filename);
        long ab=attr.getSize();
        String createFileName="src\\main\\resources\\downDir\\"+ localFileName;
        OutputStream out = new FileOutputStream(createFileName);
        try {
            File localFile = new File(createFileName);
            if (!localFile.exists()||localFile.length()==0) {
                localFile.createNewFile();
                chSftp.get(filename, createFileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chSftp.quit();

        }
        return createFileName;
    }

}



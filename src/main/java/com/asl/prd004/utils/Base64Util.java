package com.asl.prd004.utils;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;

public class Base64Util {
    public static boolean generateImage(String imgStr,String imageName) {
        if (imgStr == null) //图像数据为空
            return false;
        //imgStr=imgStr.replace("data:image/png;base64,", "");
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            //Base64解码
            byte[] b = decoder.decode(imgStr.replaceAll(" ", ""));
            for(int i=0;i<b.length;++i) {
                if(b[i]<0) {//调整异常数据
                    b[i]+=256;
                }
            }
            OutputStream out = new FileOutputStream(imageName);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}

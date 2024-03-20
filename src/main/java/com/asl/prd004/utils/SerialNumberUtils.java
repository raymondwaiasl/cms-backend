package com.asl.prd004.utils;

import java.text.DecimalFormat;


public class SerialNumberUtils {



    /**
     * 获取前缀序号
     * @return
     */
    public static String getPrefixSequence(){
        SerialNumber serial = new DbEveryDaySerialNumber(4, "PREFIX_SEQ");
        return serial.getSerialNumber();
    }

    /**
     * 获取相应table的seq
     * @param tableName
     * @return
     */
    public static String getTableSequence(String tableName){
        SerialNumber serial = new DbEveryDaySerialNumber(12, tableName);
        return DbSeqUtil.getDbPrefix(tableName) + serial.getSerialNumber();
    }



}


abstract class SerialNumber {

    public synchronized String getSerialNumber() {
        return process();
    }
    protected abstract String process();
}


abstract class EveryDaySerialNumber extends SerialNumber {

    protected DecimalFormat df = null;

    public EveryDaySerialNumber(int width) {
        if(width < 1) {
            throw new IllegalArgumentException("Parameter length must be great than 1!");
        }
        char[] chs = new char[width];
        for(int i = 0; i < width; i++) {
            chs[i] = '0';
        }
        df = new DecimalFormat(new String(chs));
    }

    @Override
    protected String process() {
        int n = getOrUpdateNumber(1);
        return format(n);
    }

    protected String format(int num) {
        return df.format(num);
    }

    /**
     * 获得序列号，同时更新持久化存储中的序列
     * @param start   初始化的序号
     * @return 所获得新的序列号
     */
    protected abstract int getOrUpdateNumber(int start);
}

class DbEveryDaySerialNumber extends EveryDaySerialNumber {


    /**
     * 持久化存储的文件
     */
    private String tableName;



    public DbEveryDaySerialNumber(int width, String tableName) {
        super(width);
        this.tableName = tableName;
    }

    @Override
    protected int getOrUpdateNumber(int start) {
        int num = start;
        if(DbSeqUtil.getDbSeq(tableName) != -1){
            num = DbSeqUtil.getDbSeq(tableName);
        }

        DbSeqUtil.updateDbSeq(tableName, (num + 1));
        return num;
    }
}





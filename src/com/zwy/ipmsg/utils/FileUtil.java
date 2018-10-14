package com.zwy.ipmsg.utils;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FileUtil {
    public static long getTotalSize(File file){
        if(file.isFile()){
            return file.length();
        }
        File[] fileList=file.listFiles();
        long total=0;
        if(fileList!=null){
            for(File child:fileList){
                total+= getTotalSize(child);
            }
        }
        return total;
    }

    private static DecimalFormat df = null;
    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }
    public static String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if(size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if(size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if(size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }
}

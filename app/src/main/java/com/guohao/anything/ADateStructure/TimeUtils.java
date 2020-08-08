package com.guohao.anything.ADateStructure;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static void main(String[] args){
        System.out.println(getDate());
        System.out.println(getDateSimple());
        System.out.println("" + Integer.MAX_VALUE);
        System.out.println("" + Integer.MIN_VALUE);
    }

    /**
     * 获取本地时间
     * @return
     */
    public static String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日-HH时mm分ss秒", Locale.US);
        String date = df.format(new Date());
        return date;
    }

    public static String getDateSimple() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.US);
        String date = df.format(new Date());
        return date;
    }

}

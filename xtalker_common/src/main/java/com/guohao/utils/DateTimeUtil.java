package com.guohao.utils;

import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class DateTimeUtil {
    private static final SimpleDateFormat FORMAT_DAY = new SimpleDateFormat("yy-MM-dd", Locale.ENGLISH);
    private static final SimpleDateFormat FORMAT_SECOND = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
    private static final SimpleDateFormat FORMAT_ALL = new SimpleDateFormat("yy-MM-dd hh:mm:ss", Locale.ENGLISH);

    /**
     * 获取一个简单的时间字符串
     *
     * @param date Date
     * @return 时间字符串
     */
    public static String getSampleDate(Date date) {

        // 获取今天的日期
        Date today = new Date(System.currentTimeMillis());
        String todayStr = FORMAT_DAY.format(today);
        String msgStr = FORMAT_DAY.format(date);
        Log.i("guohao-DateTimeUtil","今天的日期是" + FORMAT_ALL.format(today));
        Log.i("guohao-DateTimeUtil","消息的日期是" + FORMAT_ALL.format(date));

        // 今天的消息，显示时间；昨天及之前的消息，显示日期
        return todayStr.equals(msgStr)?FORMAT_SECOND.format(date):msgStr;
    }
}

package com.guohao.anything;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Description
 * 日志工具类，用于控制日志的显示
 *
 * @author yangyi
 * @version 1.0.0
 * @date 17-11-17
 */

public class LogUtil {

    private static final String DEFAULT_TAG = LogUtil.class.getSimpleName();
    private static final int VERBOSE = 1;
    private static final int DEBUG = VERBOSE + 1;
    private static final int INFO = DEBUG + 1;
    private static final int WARN = INFO + 1;
    private static final int ERROR = WARN + 1;
    private static final int NOTHING = ERROR + 1;
    private static int level = VERBOSE;

    private static final boolean WRITETTOFILE = true;
    private static SimpleDateFormat mLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    private static final int MAX_SIZE = 300 * 1024 * 1024;

    public static void v( String msg) {
        String tag=getTag();
        msg=buildMessage(msg);

        if (level <= VERBOSE) {
            Log.v(DEFAULT_TAG + " " + tag + " ", msg);
        }
        writeLog(tag, msg);
    }

    public static void d( String msg) {
        String tag=getTag();
        msg=buildMessage(msg);
        if (level <= DEBUG) {
            Log.d(DEFAULT_TAG + " " + tag + " ", msg);
        }
        writeLog(tag, msg);
    }

    public static void i( String msg) {
        String tag=getTag();
        msg=buildMessage(msg);
        if (level <= INFO) {
            Log.i(DEFAULT_TAG + " " + tag + " ", msg);
        }
        //writeLog(tag, msg);
    }

    public static void w( String msg) {
        String tag=getTag();
        msg=buildMessage(msg);
        if (level <= WARN) {
            Log.w(DEFAULT_TAG + " " + tag + " ", msg);
        }
        writeLog(tag, msg);
    }

    public static void e( String msg) {
        String tag=getTag();
        msg=buildMessage(msg);
        if (level <= ERROR) {
            Log.e(DEFAULT_TAG + " " + tag + " ", msg);
        }
        writeLog(tag, msg);
    }

    /**
     * 将日志记录到文件中
     *
     * @param tag
     * @param msg
     */
    private static void writeLog(String tag, String msg) {

//        if (!WRITETTOFILE) {
//            return;
//        }
//
//        File logFile = getLogFile();
//        long length;
//        length = logFile.length();
//        if (length > MAX_SIZE) {
//            try {
//                mLogFileStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            logFile.delete();
//            mLogFileStream = getmLogFileStream();
//        }
//        Date nowTime = new Date();
//        String needWriteMessage = mLogSdf.format(nowTime) + "-" + tag + ": ---" + msg + "\n";
//
//        try {
//            if (mLogFileStream != null) {
//                mLogFileStream.write(needWriteMessage.getBytes());
//                mLogFileStream.flush();
//            } else {
//                mLogFileStream = getmLogFileStream();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    /**
     * 方法名和输出信息
     *
     * @param msg 原本输出的信息
     * @return 对原本的信息进行包装加上ｉd和方法名
     */
    private static String buildMessage(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String caller = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                caller = trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, " %s: %s", caller, msg);
    }

    /**
     * 获取到调用者的类名
     *
     * @return 调用者的类名
     */
    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass;
    }
}

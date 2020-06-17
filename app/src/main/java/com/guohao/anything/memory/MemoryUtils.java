package com.guohao.anything.memory;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.text.format.Formatter;
import com.guohao.anything.LogUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 这里列出了五种获取内存信息的方法
 * 并标明了它们使用的方式，供后续参考修改
 *
 */
public class MemoryUtils {

    // 方法一
    // 获取内存分配情况，
    // 方式：ActivityManager + Runtime.getRuntime()
    public static void getMemory1(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        //最大分配内存
        int memory = activityManager.getMemoryClass();
        LogUtil.e("memory: "+memory);
        //最大分配内存获取方法2
        float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0/ (1024 * 1024));
        //当前分配的总内存
        float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0/ (1024 * 1024));

        // 当 totalMemory 超过 maxMemory，就会发生 oom

        //剩余内存
        float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0/ (1024 * 1024));
        LogUtil.e("maxMemory: "+maxMemory);
        LogUtil.e("totalMemory: "+totalMemory);
        LogUtil.e("freeMemory: "+freeMemory);

        // 2020-06-17 22:36:13.949 27060-27060/com.guohao.anything E/LogUtil Main3Activity:  getMemoryofApp: memory: 384
        // 2020-06-17 22:36:13.950 27060-27060/com.guohao.anything E/LogUtil Main3Activity:  getMemoryofApp: maxMemory: 384.0
        // 2020-06-17 22:36:13.950 27060-27060/com.guohao.anything E/LogUtil Main3Activity:  getMemoryofApp: totalMemory: 3.4178467
        // 2020-06-17 22:36:13.950 27060-27060/com.guohao.anything E/LogUtil Main3Activity:  getMemoryofApp: freeMemory: 1.5314331
        // 说明我这个app在当前手机的最大分配内存是384m,现在已经分配了3.4m,这3.4m中有1.5m是空闲的
        // 这里的信息对应 dumpsys meminfo 信息中的
        // Dalvik Heap 的 Heap Size，Heap Alloc，Heap Free
    }

    //  ------

    // 方法二
    // 其中拿到信息对应 adb shell + dumpsys meminfo 的 App Summary 区域的内容！
    // 方式：Debug.MemoryInfo + activityManager
    // 细节：Debug.MemoryInfo.getMemoryStat(), 可以获取 App Summary 区域的每一项内容
    // 来源：[Android获取当前应用的运行内存](https://www.jianshu.com/p/9a7a65f12c01)
    @TargetApi(Build.VERSION_CODES.M)
    public static double getMemory2(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        double mem = 0.0D;
        try {
            // 统计进程的内存信息 totalPss
            final Debug.MemoryInfo[] memInfo = activityManager.getProcessMemoryInfo(new int[]{android.os.Process.myPid()});
            if (memInfo.length > 0) {

                /**
                 * 读取内存信息,跟 Android Profiler 分析一致
                 * 即对应  App Summary 的那些项
                 */
                String java_mem = memInfo[0].getMemoryStat("summary.java-heap");

                String native_mem = memInfo[0].getMemoryStat("summary.native-heap");

                String graphics_mem = memInfo[0].getMemoryStat("summary.graphics");

                String stack_mem = memInfo[0].getMemoryStat("summary.stack");

                String code_mem = memInfo[0].getMemoryStat("summary.code");

                String others_mem = memInfo[0].getMemoryStat("summary.system");

                final int dalvikPss = convertToInt(java_mem,0)
                        + convertToInt(native_mem,0)
                        + convertToInt(graphics_mem,0)
                        + convertToInt(stack_mem,0)
                        + convertToInt(code_mem,0)
                        + convertToInt(others_mem,0);

                if (dalvikPss >= 0) {
                    // Mem in MB
                    mem = dalvikPss / 1024.0D;
                    LogUtil.e("mem " + mem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mem;
    }

    //  ------

    // 方法三
    // 来源：https://blog.csdn.net/afei__/article/details/57986538
    // 方式：纯 Debug.MemoryInfo
    // 细节：Debug.MemoryInfo.getTotalXxx()，可以获取表格区域的每一列的总和
    // 拿到的是 dumpsys meminfo 信息中的表格横轴的几个项的和
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void getMemory3() {
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();

        Debug.getMemoryInfo(memoryInfo);

        // dalvikPrivateClean + nativePrivateClean + otherPrivateClean;
        int totalPrivateClean = memoryInfo.getTotalPrivateClean();
        // dalvikPrivateDirty + nativePrivateDirty + otherPrivateDirty;
        int totalPrivateDirty = memoryInfo.getTotalPrivateDirty();
        // dalvikPss + nativePss + otherPss;
        int totalPss = memoryInfo.getTotalPss();
        // dalvikSharedClean + nativeSharedClean + otherSharedClean;
        int totalSharedClean = memoryInfo.getTotalSharedClean();
        // dalvikSharedDirty + nativeSharedDirty + otherSharedDirty;
        int totalSharedDirty = memoryInfo.getTotalSharedDirty();
        // dalvikSwappablePss + nativeSwappablePss + otherSwappablePss;
        int totalSwappablePss = memoryInfo.getTotalSwappablePss();


        int total = totalPrivateClean + totalPrivateDirty + totalPss + totalSharedClean + totalSharedDirty + totalSwappablePss;

        String s = memoryInfo.getMemoryStat("summary.java-heap");
        LogUtil.e("summary.java-heap" + s);
        LogUtil.e("totalPss " + totalPss);
        LogUtil.e("totalPrivateDirty " + totalPrivateDirty);
        LogUtil.e("totalPrivateClean " + totalPrivateClean);

    }

    // ------

    // 方法四
    // 获得系统可用内存信息。注意！这是拿到的内存是系统的！
    // 方式：纯 ActivityManager
    // 来源：[Android中获取系统内存信息以及进程信息-----ActivityManager的使用(一)](https://blog.csdn.net/qinjuning/article/details/6978560?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2)
    public static void getMemory4(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        //获得MemoryInfo对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;
        //获得系统可用内存，保存在MemoryInfo对象上
        activityManager.getMemoryInfo(memoryInfo) ;
        long memSize = memoryInfo.availMem ;

        //字符类型转换
        String availMemStr = formateFileSize(memSize,context);

        LogUtil.e("availMemStr: "+ availMemStr);
    }

    // ------

    // 方法五
    // 获取的信息是很长的列表，但貌似跟其他数据对不上
    // 方式：读取 /proc/meminfo 节点
    // 来源：https://blog.csdn.net/MyArrow/article/details/8092227
    public static void getMemory5() {

        String str1 = "/proc/meminfo";
        String str2="";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                LogUtil.e( "---" + str2);
            }
        } catch (IOException e) {
        }
    }

    /**
     * 转化为int
     * @param value 传入对象
     * @param defaultValue 发生异常时，返回默认值
     * @return
     */
    private final static int convertToInt(Object value, int defaultValue){

        if (value == null || "".equals(value.toString().trim())){
            return defaultValue;
        }

        try {
            return Integer.valueOf(value.toString());
        }catch (Exception e){

            try {
                return Integer.valueOf(String.valueOf(value));
            }catch (Exception e1) {

                try {
                    return Double.valueOf(value.toString()).intValue();
                }catch (Exception e2){
                    return defaultValue;
                }
            }
        }
    }

    //调用系统函数，字符串转换 long -String KB/MB
    private static String formateFileSize(long size,Context context){
        return Formatter.formatFileSize(context, size);
    }
}

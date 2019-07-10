package com.anything.guohao.anything.FileOptUtil;

import android.content.Context;

import com.anything.guohao.anything.LogUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * 操作文件读写的工具类
 */
public class FileOptUtil {

    // 伪代码

    // 读文件时，如果文件过大，就需要分块循环的读

    // 读到文件的内容，三至少三个用途
    // 1、可以转字符输出；2、可以保存到另一个文件；3、可以返回其字节

    //String 写入到 文件
    public static void writeStringToFileInData(String content, String fileName, Context context) {


        try {

            FileOutputStream fop = context.openFileOutput(fileName, MODE_PRIVATE);

            fop.write(content.getBytes());
            fop.flush();
            fop.close();



            LogUtil.e("Done");

        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("IOException = " + e.toString());
        }

    }



}

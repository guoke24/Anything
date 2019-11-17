package com.guohao.anything;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class AssetsUtils {

    static String TAG = "guohao";

    /**
     * 把 assets 目录的 fileName文件 写入到 /data/data/{包名}/files
     * @param fileName
     * @param context
     * @return
     */
    public static String fileOpt(String fileName, Context context){

        int l = assetsFile2dataFile(fileName,context);
        LogUtil.e("fileOpt len = " + l);

        LogUtil.e("getFilesDir() = " + context.getFilesDir() );
        // 输出：/data/user/0/com.anything.guohao.anything/files
        // 等价与 /data/data/com.anything.guohao.anything/files

        //Log.d(TAG,"getCacheDir() = " + getCacheDir() );
        // /data/user/0/demo.elecsign/cache

        if(l>0){
            return context.getFilesDir().getPath() +"/" + fileName;
        }

        return "";
    }



    /**
     * 向指定的文件中写入指定的数据
     * 创建的文件保存在/data/data/<package name>/files目录
     * @param filename
     * @param context
     * @return
     */
    private static int assetsFile2dataFile(String filename, Context context){
        InputStream in = null;
        try {
            //读取 assets 目录下的文件
            in = context.getAssets().open(filename);


            FileOutputStream fos = context.openFileOutput(filename, MODE_PRIVATE);//获得FileOutputStream

            byte[]  bytes = new byte[in.available()];//将要写入的字符串转换为byte数组
            in.read(bytes);
            fos.write(bytes);//将byte数组写入文件，这个写文件的实现不够标准，待优化
            fos.close();//关闭文件输出流

            return readFileData(filename,context);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"assetsFile2dataFile Exception = " + e.toString());
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }



    /**
     * 打开指定文件，读取其数据，返回字符串对象
     * @param fileName
     * @param context
     * @return
     */
    private static int readFileData(String fileName, Context context){

        int len = -1;
        FileInputStream fis = null;
        try{

            fis = context.openFileInput(fileName);

            //获取文件长度
            int lenght = fis.available();

            byte[] buffer = new byte[lenght];

            fis.read(buffer);

            len = lenght;

            //将byte数组转换成指定格式的字符串
            //result = new String(buffer, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("readFileData Exception = " + e.toString());
        }finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return  len;
    }

    /**
     * 从 assets 打开文件的流并返回
     * @param filename
     * @param context
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(String filename, Context context) throws IOException {
        return context.getAssets().open(filename);
    }

    // 获取byte，从应用的本地数据路径下的文件中
    public static byte[] getByteFromAssetsAndCopyToData(String filename, Context context) {
        //showMessage("加载文件：" + filename);
        // 把 assets 的 xxx.apk，copy 到 context.getFilesDir() 的路径下
        // 即 /data/user/0/com.anything.guohao.anything/files
        String path = AssetsUtils.fileOpt(filename, context);

        if (!path.equals("")) {
            Toast.makeText(context, "加载完成", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT).show();
            return null;
        }


        InputStream in = null;
        byte[] buffer;

        try {
            in = context.openFileInput(filename);
            int lenght = in.available();
            buffer = new byte[lenght];
            in.read(buffer);
            return buffer;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}

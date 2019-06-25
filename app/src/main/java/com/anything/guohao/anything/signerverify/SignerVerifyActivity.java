package com.anything.guohao.anything.signerverify;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anything.guohao.anything.AssetsUtils;
import com.anything.guohao.anything.BaseTestActivity;
import com.anything.guohao.anything.ConvertUtil;
import com.anything.guohao.anything.LogUtil;
import com.anything.guohao.anything.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 验签demo
 * 该demo的第一阶段测试目的：从一大段字节中，找出一段字节的开头和结尾，然后把该段字节提取出来
 * 第一步，找到开头和结尾
 * 第二部，提取字节
 * 学到的知识点：
 * 1,字节可以直接用=比较，转字符在比较会很慢
 * 2,文件读取有很多比inputstream更高效的办法，如：RandomAccessFile，FileChannel和MappedByteBuffer等，有空总结
 */
public class SignerVerifyActivity extends BaseTestActivity {

    String jxnx_acquire_apk = "jxnx_acquire_22_2_9_03_release_20190520.apk";
    String SmartPhonePos_apk = "SmartPhonePos_new_20190612_2_0_3_release.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_signer_verify);
        super.onCreate(savedInstanceState);
    }

    String apkPath = "";

    /**
     * 从 assets 加载文件到 /data/data/{包名}/files
     * @param v
     */
    public void test_1(View v) {
        showMessage("SignerVerifyActivity test1");
        // 把 assets 的 xxx.apk，copy 到 context.getFilesDir() 的路径下
        // 即 /data/user/0/com.anything.guohao.anything/files
        String path = AssetsUtils.fileOpt(SmartPhonePos_apk, this);
        apkPath = path;
        if (path != null) {
            Toast.makeText(this, "加载完成", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 遍历 apk 内的文件，用 JarFile
     * @param v
     */
    public void test_2(View v) {

        try {
            if(apkPath.equals("")){
                Toast.makeText(this, "apk文件不存在", Toast.LENGTH_SHORT).show();
                return;
            }

            JarFile apkFile = new JarFile(apkPath);

            LogUtil.e("" + apkFile.getName());

            Enumeration<JarEntry> enumerations = apkFile.entries();

            while(enumerations.hasMoreElements()){
                String name = enumerations.nextElement().getName();
                if(name.startsWith("res")) continue;
                LogUtil.e("" + "Element = " + name);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出apk的字节的16进制字符，通过StringBuilder，报错，说明StringBuilder是有大小限制的
     * @param v
     */
    public void test_3(View v){
        if(apkPath.equals("")){
            Toast.makeText(this, "apk文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream in = this.openFileInput(SmartPhonePos_apk);

            //获取文件长度
            int lenght = in.available();

            byte[] buffer = new byte[lenght];

            in.read(buffer);

            Log.e("guohao","hexString = " + ConvertUtil.bytesToHexString(buffer));//报错：
            //  Caused by: java.lang.OutOfMemoryError: Failed to allocate a 51592120 byte allocation with 4194304 free bytes and 14MB until OOM
            //  at java.lang.StringBuilder.toString(StringBuilder.java:408)

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //从流中逐个读字节速度也很慢，12m 需要 4分钟
    public void test_5_fail(View view){
        if(apkPath.equals("")){
            Toast.makeText(this, "apk文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        InputStream in = null;
        try {
            in = this.openFileInput(SmartPhonePos_apk);

            //获取文件长度
            int lenght = in.available();

            byte[] buffer = new byte[lenght];

            int index = 0;
            Log.e("guohao","begin " );
            while((index = in.read())!=-1){
                //Log.e("guohao","index " + index);
            }
            Log.e("guohao","finish " );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // StringBuilder 的简单测试，可以掐头
    public void testString(){
        StringBuilder result = new StringBuilder(2);
        result.append("1");
        result.append("2");
        result.append("3");

        result.deleteCharAt(0);
        LogUtil.e("result = " + result.toString() + ",len = " + result.length());
    }


    public void test_4(View v){
        if(apkPath.equals("")){
            Toast.makeText(this, "apk文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream in = this.openFileInput(SmartPhonePos_apk);

            //获取文件长度
            int lenght = in.available();

            byte[] buffer = new byte[lenght];

            in.read(buffer);

            pullSignBlockHead(buffer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
    }

    // 匹配因子
    byte[] beginByte = new byte[]{0x13,0x11,0x41,0x43,0x51,0x55,0x49,0x52,0x45,0x52,0x2d,0x53,0x47,0x4e,0x2d,0x49,0x4e,0x46,0x4f};
    byte[] endByteV1 = new byte[]{0x58,0x47,0x44,0x20,0x53,0x69,0x67,0x20,0x42,0x6C,0x6F,0x63,0x6B,0x20,0x34,0x32};
    byte[] endByteV2 = new byte[]{0x41,0x50,0x4B,0x20,0x53,0x69,0x67,0x20,0x42,0x6C,0x6F,0x63,0x6B,0x20,0x34,0x32};


    /**
     * 匹配尾巴
     * @param bytes
     */
    public void pullSignBlockRear(byte[] bytes){
        int len = bytes.length;
        boolean flagCheck = false;
        int checkIndex = 15;

        for(int i = len - 1; i>=0 ; --i){//倒序遍历

            //Log.e("guohao","bytes[" + i + "] = " + byteToHexString(bytes[i]) );

            if(bytes[i] == endByteV2[checkIndex] && flagCheck == false){ // 最后一位匹配
                //Log.e("guohao","match 15");
                flagCheck = true;
                checkIndex--;
                continue;
            }
            else if(bytes[i] == endByteV2[checkIndex] && flagCheck == true){ // // 其他位匹配
                //Log.e("guohao","match other " + checkIndex);
                checkIndex--;
            }else{
                //Log.e("guohao","match fail again");
                flagCheck = false;
                checkIndex = 15;
            }

            if(checkIndex < 0){
                Log.e("guohao","match! position = " + i);
                break;
            }
        }
    }

    /**
     * 匹配开头,加log会多出5秒
     * @param bytes
     */
    public void pullSignBlockHead(byte[] bytes){
        int len = bytes.length;
        boolean flagCheck = false;
        int checkIndex = 18;

        for(int i = len - 1; i>=0 ; --i){//倒序遍历

            Log.e("guohao","bytes[" + i + "] = " + ConvertUtil.loopLogSingleByteToHexString(bytes[i]) );

            if(bytes[i] == beginByte[checkIndex] && flagCheck == false){ // 最后一位匹配
                Log.e("guohao","match 18");
                flagCheck = true;
                checkIndex--;
                continue;
            }
            else if(bytes[i] == beginByte[checkIndex] && flagCheck == true){ // 其他位匹配
                Log.e("guohao","match other " + checkIndex);
                checkIndex--;
            }else{
                Log.e("guohao","match fail again");
                flagCheck = false;
                checkIndex = 18;
            }

            if(checkIndex < 0){
                Log.e("guohao","match! position = " + i);
                break;
            }
        }
    }


}




















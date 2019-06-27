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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
        showMessage("SignerVerifyActivity");
        // 把 assets 的 xxx.apk，copy 到 context.getFilesDir() 的路径下
        // 即 /data/user/0/com.anything.guohao.anything/files
        String path = AssetsUtils.fileOpt(SmartPhonePos_apk, this);
        apkPath = path;
        if (!path.equals("")) {
            Toast.makeText(this, "加载完成", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
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

            SignerVerifyUtils.pullSignBlockHead(buffer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
    }


    public void test_5(View v){
        if (apkPath.equals("")) {
            Toast.makeText(this, "apk文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        InputStream in = null;
        try {
            in = this.openFileInput(SmartPhonePos_apk);
            int lenght = in.available();
            byte[] buffer = new byte[lenght];
            in.read(buffer);
            int magicOffset = SignerVerifyUtils.pullSignBlockRear(buffer);// 魔数块的偏移量
            // magicOffset + 16 就是 中央目录的偏移量 centralDirOffset
            LogUtil.e("magicOffset = " + magicOffset);

            // 读取apk 到 RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(getFilesDir().getPath() + File.separator + SmartPhonePos_apk, "r");

            int apkSigBlockSizeInFooter =  magicOffset - 8 ;



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //String hexStrWorkCert = "96 cb 04 8a 65 90 cc 98 b0 5b 9f 66 74 cc 6d 63 96 4c 39 78 d5 2f b3 e4 48 7a d5 b9 10 51 eb 42 d0 4a dc b1 2b ca 7d fb 93 5d ce 2a 0d 22 bb 6d 41 5e 54 17 6b e0 b9 28 06 4a 1f e6 9c 63 fc f4 aa 8c 4e f1 7d 18 3c 71 c2 98 20 e3 1a e6 e2 f5 c6 41 23 4e 14 0e 0b 13 e2 f2 dc 2c 4a 5f 03 76 2d c3 b9 43 4b 25 21 1b ee d4 c9 ee 79 93 73 ab 44 40 20 eb 63 4a 52 3d 6d ed c4 94 84 75 5c a0 db 6f 84 16 29 41 11 77 4b c4 1b 6a 52 4d 7f f3 45 f0 61 e4 e0 71 f0 7a f3 66 95 a5 6f c9 ad 4c 7f 9a 5d 15 b0 dd 68 4a 7b 96 71 7a 5a 5a 25 9b 31 ca 23 b6 03 52 5d 6b 45 f9 4b a1 be 81 f3 85 52 c8 78 b5 b2 0a 7c 78 34 bb 25 d6 85 d3 75 89 9d d1 e1 e9 6d 65 9e 3f 8a ed ad a8 59 5c 74 bf 62 85 62 a0 73 7c 16 54 93 84 43 5e 6f 2f f8 f6 c2 a9 6e cb a3 01 86 52 7c 04 66 63 7d c2 b3 25"
    //String hexStrWorkCert = "96 cb 04 8a 65 90 cc 98 b0 5b 9f 66 74 cc 6d 63 96 4c 39 78 d5 2f b3 e4 48 7a d5 b9 10 51 eb 42 d0 4a dc b1 2b ca 7d fb 93 5d ce 2a 0d 22 bb 6d 41 5e 54 17 6b e0 b9 28 06 4a 1f e6 9c 63 fc f4 aa 8c 4e f1 7d 18 3c 71 c2 98 20 e3 1a e6 e2 f5 c6 41 23 4e 14 0e 0b 13 e2 f2 dc 2c 4a 5f 03 76 2d c3 b9 43 4b 25 21 1b ee d4 c9 ee 79 93 73 ab 44 40 20 eb 63 4a 52 3d 6d ed c4 94 84 75 5c a0 db 6f 84 16 29 41 11 77 4b c4 1b 6a 52 4d 7f f3 45 f0 61 e4 e0 71 f0 7a f3 66 95 a5 6f c9 ad 4c 7f 9a 5d 15 b0 dd 68 4a 7b 96 71 7a 5a 5a 25 9b 31 ca 23 b6 03 52 5d 6b 45 f9 4b a1 be 81 f3 85 52 c8 78 b5 b2 0a 7c 78 34 bb 25 d6 85 d3 75 89 9d d1 e1 e9 6d 65 9e 3f 8a ed ad a8 59 5c 74 bf 62 85 62 a0 73 7c 16 54 93 84 43 5e 6f 2f f8 f6 c2 a9 6e cb a3 01 86 52 7c 04 66 63 7d c2 b3 25"
    //String hexStrWorkCert = "03 4b 20 30 82 03 46 30 82 02 2e 02 01 01 30 0d 06 09 2a 86 48 86 f7 0d 01 01 0b 05 20 30 71 31 0b 30 09 06 03 55 04 06 13 02 43 4e 31 18 30 16 06 03 55 04 08 13 0f 4a 69 61 6e 67 58 69 4e 61 6e 43 68 61 6e 67 31 11 30 0f 06 03 55 04 07 13 08 6e 61 6e 63 68 61 6e 67 31 12 30 10 06 03 55 04 0a 0c 09 6a 78 6e 78 5f 62 61 6e 6b 31 12 30 10 06 03 55 04 0b 0c 09 6a 78 6e 78 5f 62 61 6e 6b 31 0d 30 0b 06 03 55 04 03 13 04 6a 78 6e 78 30 20 17 0d 31 38 30 35 31 37 30 33 35 34 35 35 5a 18 0f 32 31 31 38 30 34 32 33 30 33 35 34 35 35 5a 30 5f 31 0b 30 09 06 03 55 04 06 13 02 43 4e 31 10 30 0e 06 03 55 04 08 0c 07 4a 69 61 6e 67 58 69 31 11 30 0f 06 03 55 04 07 0c 08 4e 61 6e 43 68 61 6e 67 31 0d 30 0b 06 03 55 04 0a 0c 04 6a 78 6e 78 31 0d 30 0b 06 03 55 04 0b 0c 04 6a 78 6e 78 31 0d 30 0b 06 03 55 04 03 0c 04 6a 78 6e 78 30 82 01 22 30 0d 06 09 2a 86 48 86 f7 0d 01 01 01 05 20 03 82 01 0f 20 30 82 01 0a 02 82 01 01 20 ea d3 1d 28 98 31 ee ee c3 5b 37 0a 36 62 4d e8 c6 50 cd 9b 74 03 8a fa 39 19 8d 2e ee be dd 09 55 9e 96 38 83 ec eb 4e e0 f2 ed 53 42 39 29 b7 e3 91 65 41 55 50 98 1f c4 56 b6 42 99 0f 0c c5 04 ab 6e e6 0d 55 bb 64 52 b6 d0 7f a4 18 1c a9 72 59 fa ce a5 93 5b 0f d4 98 72 de b3 65 46 b6 04 ce ee 0d 0f 2a 25 b3 cd b1 31 93 b7 87 92 2b e5 b2 8b 60 6e da 41 2d 38 45 5d 41 79 ee 9d b5 f8 04 7d b4 f2 9e df 46 e1 61 49 cc f0 80 45 96 5e 87 2a d1 49 4c 2a dc b1 cd 9f 2b 9c 8c 4b a7 0d 44 07 34 16 af 45 fe f4 e1 94 6c 9c 57 a0 5d 33 89 b5 ed 43 9c fa 98 f7 b3 ff b4 c0 b6 a2 eb 21 de 50 24 66 8f 4b f8 0a 80 17 8c 3a 5b 2a f4 fb 99 4e b7 ad 48 81 6f 77 89 88 1d e2 24 13 36 d6 d9 e7 5b 54 dc 01 e0 78 7f e4 20 d6 2c fe 23 e7 99 39 73 3b bf b8 8b c6 48 f8 45 7a 92 ea 35 02 03 01 20 01 30 0d 06 09 2a 86 48 86 f7 0d 01 01 0b 05 20 03 82 01 01 20 96 cb 04 8a 65 90 cc 98 b0 5b 9f 66 74 cc 6d 63 96 4c 39 78 d5 2f b3 e4 48 7a d5 b9 10 51 eb 42 d0 4a dc b1 2b ca 7d fb 93 5d ce 2a 0d 22 bb 6d 41 5e 54 17 6b e0 b9 28 06 4a 1f e6 9c 63 fc f4 aa 8c 4e f1 7d 18 3c 71 c2 98 20 e3 1a e6 e2 f5 c6 41 23 4e 14 0e 0b 13 e2 f2 dc 2c 4a 5f 03 76 2d c3 b9 43 4b 25 21 1b ee d4 c9 ee 79 93 73 ab 44 40 20 eb 63 4a 52 3d 6d ed c4 94 84 75 5c a0 db 6f 84 16 29 41 11 77 4b c4 1b 6a 52 4d 7f f3 45 f0 61 e4 e0 71 f0 7a f3 66 95 a5 6f c9 ad 4c 7f 9a 5d 15 b0 dd 68 4a 7b 96 71 7a 5a 5a 25 9b 31 ca 23 b6 03 52 5d 6b 45 f9 4b a1 be 81 f3 85 52 c8 78 b5 b2 0a 7c 78 34 bb 25 d6 85 d3 75 89 9d d1 e1 e9 6d 65 9e 3f 8a ed ad a8 59 5c 74 bf 62 85 62 a0 73 7c 16 54 93 84 43 5e 6f 2f f8 f6 c2 a9 6e cb a3 01 86 52 7c 04 66 63 7d c2 b3 25"
    String hexStrWorkCert = "03 82 03 4b 20 30 82 03 46 30 82 02 2e 02 01 01 30 0d 06 09 2a 86 48 86 f7 0d 01 01 0b 05 20 30 71 31 0b 30 09 06 03 55 04 06 13 02 43 4e 31 18 30 16 06 03 55 04 08 13 0f 4a 69 61 6e 67 58 69 4e 61 6e 43 68 61 6e 67 31 11 30 0f 06 03 55 04 07 13 08 6e 61 6e 63 68 61 6e 67 31 12 30 10 06 03 55 04 0a 0c 09 6a 78 6e 78 5f 62 61 6e 6b 31 12 30 10 06 03 55 04 0b 0c 09 6a 78 6e 78 5f 62 61 6e 6b 31 0d 30 0b 06 03 55 04 03 13 04 6a 78 6e 78 30 20 17 0d 31 38 30 35 31 37 30 33 35 34 35 35 5a 18 0f 32 31 31 38 30 34 32 33 30 33 35 34 35 35 5a 30 5f 31 0b 30 09 06 03 55 04 06 13 02 43 4e 31 10 30 0e 06 03 55 04 08 0c 07 4a 69 61 6e 67 58 69 31 11 30 0f 06 03 55 04 07 0c 08 4e 61 6e 43 68 61 6e 67 31 0d 30 0b 06 03 55 04 0a 0c 04 6a 78 6e 78 31 0d 30 0b 06 03 55 04 0b 0c 04 6a 78 6e 78 31 0d 30 0b 06 03 55 04 03 0c 04 6a 78 6e 78 30 82 01 22 30 0d 06 09 2a 86 48 86 f7 0d 01 01 01 05 20 03 82 01 0f 20 30 82 01 0a 02 82 01 01 20 ea d3 1d 28 98 31 ee ee c3 5b 37 0a 36 62 4d e8 c6 50 cd 9b 74 03 8a fa 39 19 8d 2e ee be dd 09 55 9e 96 38 83 ec eb 4e e0 f2 ed 53 42 39 29 b7 e3 91 65 41 55 50 98 1f c4 56 b6 42 99 0f 0c c5 04 ab 6e e6 0d 55 bb 64 52 b6 d0 7f a4 18 1c a9 72 59 fa ce a5 93 5b 0f d4 98 72 de b3 65 46 b6 04 ce ee 0d 0f 2a 25 b3 cd b1 31 93 b7 87 92 2b e5 b2 8b 60 6e da 41 2d 38 45 5d 41 79 ee 9d b5 f8 04 7d b4 f2 9e df 46 e1 61 49 cc f0 80 45 96 5e 87 2a d1 49 4c 2a dc b1 cd 9f 2b 9c 8c 4b a7 0d 44 07 34 16 af 45 fe f4 e1 94 6c 9c 57 a0 5d 33 89 b5 ed 43 9c fa 98 f7 b3 ff b4 c0 b6 a2 eb 21 de 50 24 66 8f 4b f8 0a 80 17 8c 3a 5b 2a f4 fb 99 4e b7 ad 48 81 6f 77 89 88 1d e2 24 13 36 d6 d9 e7 5b 54 dc 01 e0 78 7f e4 20 d6 2c fe 23 e7 99 39 73 3b bf b8 8b c6 48 f8 45 7a 92 ea 35 02 03 01 20 01 30 0d 06 09 2a 86 48 86 f7 0d 01 01 0b 05 20 03 82 01 01 20 96 cb 04 8a 65 90 cc 98 b0 5b 9f 66 74 cc 6d 63 96 4c 39 78 d5 2f b3 e4 48 7a d5 b9 10 51 eb 42 d0 4a dc b1 2b ca 7d fb 93 5d ce 2a 0d 22 bb 6d 41 5e 54 17 6b e0 b9 28 06 4a 1f e6 9c 63 fc f4 aa 8c 4e f1 7d 18 3c 71 c2 98 20 e3 1a e6 e2 f5 c6 41 23 4e 14 0e 0b 13 e2 f2 dc 2c 4a 5f 03 76 2d c3 b9 43 4b 25 21 1b ee d4 c9 ee 79 93 73 ab 44 40 20 eb 63 4a 52 3d 6d ed c4 94 84 75 5c a0 db 6f 84 16 29 41 11 77 4b c4 1b 6a 52 4d 7f f3 45 f0 61 e4 e0 71 f0 7a f3 66 95 a5 6f c9 ad 4c 7f 9a 5d 15 b0 dd 68 4a 7b 96 71 7a 5a 5a 25 9b 31 ca 23 b6 03 52 5d 6b 45 f9 4b a1 be 81 f3 85 52 c8 78 b5 b2 0a 7c 78 34 bb 25 d6 85 d3 75 89 9d d1 e1 e9 6d 65 9e 3f 8a ed ad a8 59 5c 74 bf 62 85 62 a0 73 7c 16 54 93 84 43 5e 6f 2f f8 f6 c2 a9 6e cb a3 01 86 52 7c 04 66 63 7d c2 b3 25"
            .replace(" ","");

    public void test_6(View v){
//        byte[] bytes = ConvertUtil.hexStringToBytes(hexStrWorkCert);
//        LogUtil.e(ConvertUtil.bytesToHexString(bytes));
        // 字节转换没问题

        //byte[] bytes = new byte[257];

        X509Certificate work_cert = null;
        CertificateFactory certFactory;
        try {
            certFactory = CertificateFactory.getInstance("X.509"); // 获取根证书

            InputStream input = new ByteArrayInputStream(ConvertUtil.hexStringToBytes(hexStrWorkCert));

            //从工作公钥证书的流中提取出工作公钥
            work_cert = (X509Certificate) certFactory.generateCertificate(input);

            showMessage("ok");
        } catch (CertificateException e) {
            throw new RuntimeException("Failed to obtain X.509 CertificateFactory", e);
        }
    }

}




















package com.anything.guohao.anything.FileOptUtil;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anything.guohao.anything.AssetsUtils;
import com.anything.guohao.anything.BaseTestActivity;
import com.anything.guohao.anything.ConvertUtil;
import com.anything.guohao.anything.LogUtil;
import com.anything.guohao.anything.R;
import com.anything.guohao.anything.signerverify.SignerVerifyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.util.Pair;

/**
 * RandomAccessFile是Java输入/输出流体系中功能最丰富的文件内容访问类，既可以读取文件内容，也可以向文件输出数据。与普通的输入/输出流不同的是，RandomAccessFile支持跳到文件任意位置读写数据，RandomAccessFile对象包含一个记录指针，用以标识当前读写处的位置，当程序创建一个新的RandomAccessFile对象时，该对象的文件记录指针对于文件头（也就是0处），当读写n个字节后，文件记录指针将会向后移动n个字节。除此之外，RandomAccessFile可以自由移动该记录指针
 * <p>
 * RandomAccessFile包含两个方法来操作文件记录指针：
 * <p>
 * long getFilePointer()：返回文件记录指针的当前位置
 * void seek(long pos)：将文件记录指针定位到pos位置
 * <p>
 * RandomAccessFile类在创建对象时，除了指定文件本身，还需要指定一个mode参数，该参数指定RandomAccessFile的访问模式，该参数有如下四个值：
 * <p>
 * r：以只读方式打开指定文件。如果试图对该RandomAccessFile指定的文件执行写入方法则会抛出IOException
 * rw：以读取、写入方式打开指定文件。如果该文件不存在，则尝试创建文件
 * rws：以读取、写入方式打开指定文件。相对于rw模式，还要求对文件的内容或元数据的每个更新都同步写入到底层存储设备，默认情形下(rw模式下),是使用buffer的,只有cache满的或者使用RandomAccessFile.close()关闭流的时候儿才真正的写到文件
 * rwd：与rws类似，只是仅对文件的内容同步更新到磁盘，而不修改文件的元数据
 */

/**
 * 代码中获取到的手机路径：
 *
 * getDataDirectory = /data
 * getRootDirectory = /system
 * getDownloadCacheDirectory = /data/cache
 * getExternalStorageDirectory = /storage/emulated/0
 * /storage/emulated/0 对应手机文件管理器中的 sdacrd 的路径， 一些常见的文件夹 /sdcard/Music /sdcard/Pictures /sdcard/Download
 * 如果要在代码中 获取文件管理器 的/sdcard/Download 的路径，代码这样写：
 * Environment.getExternalStorageDirectory().getPath() + "/Download"
 *
 * data/data/{包名}/files 这个路径的第一个data，是跟 sdcard 平级的
 *
 */

public class FileOptActivity extends BaseTestActivity {

    String SmartPhonePos_apk = "SmartPhonePos_new_20190612_2_0_3_release.apk";
    //String SmartPhonePos_apk = "SmartPhonePos_new_20190612_2_0_3_release.apk";
    String apkPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_file_opt);
        super.onCreate(savedInstanceState);

    }

    public void test_1(View v) {
        showMessage("FileOptActivity");
        // 把 assets 的 xxx.apk，copy 到 context.getFilesDir() 的路径下
        // 即 /data/user/0/com.anything.guohao.anything/files
        //String path = AssetsUtils.fileOpt(SmartPhonePos_apk, this);
        String path = AssetsUtils.fileOpt(SmartPhonePos_apk, this);
        apkPath = path;
        if (!path.equals("")) {
            Toast.makeText(this, "加载完成", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param v
     */
    public void test_2(View v) {
        File file = new File(apkPath);
        showMessage(file.exists() + "");
    }


    public void test_3(View v) {
        File file = new File(apkPath);
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            LogUtil.e("RandomAccessFile的文件指针初始位置:" + raf.getFilePointer());

            raf.seek(100);// 特点1 移动指针100个字节
            byte[] bbuf = new byte[1024];
            int hasRead = 0;
//            while ((hasRead = raf.read(bbuf)) > 0) {// 循环的读，每次读取1024个字节，即填满bbuf大小
//                System.out.print("hasRead = " + hasRead);
//            }
            hasRead = raf.read(bbuf);//特点2 读取指定大小的字节数，即填满bbuf大小
            LogUtil.e("hasRead = " + hasRead);
            LogUtil.e("RandomAccessFile的文件指针读后位置:" + raf.getFilePointer());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // 速度测试：不加log，读完一遍12M的字节要45秒左右
    public void test_4(View v) {
        File file = new File(apkPath);
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            LogUtil.e("RandomAccessFile的文件指针初始位置:" + raf.getFilePointer());

            //raf.seek(100);// 移动指针100个字节
            byte[] bbuf = new byte[1];
            int hasRead = 0;
            while ((hasRead = raf.read(bbuf)) > 0) {// 循环的读，每次读取1个字节，即填满bbuf大小
                //LogUtil.e(ConvertUtil.bytesToHexString(bbuf));
            }
            // 不加log，读完一遍12M的字节要45秒左右
            LogUtil.e("RandomAccessFile的文件指针读完位置:" + raf.getFilePointer());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // 测试插入内容
    public void test_5(View v) {
        try {
            // 一个汉字 3个字节！utf-8
            insert("testfile", 3, "插入的内容");
            showMessage("finish");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileName 被插入的文件
     * @param pos      插入的位置
     * @param content  插入的内容
     * @throws IOException
     */
    private void insert(String fileName, long pos, String content) throws IOException {
        //无效
//        File tempFile = File.createTempFile(getFilesDir().getPath()+File.separator+"temp",null);//创建临时空文件

        // 该创建方式有效
        File file = new File(getFilesDir().getPath() + File.separator + "temp");
//        if(!file.exists()){
//            file.createNewFile();
//        }

        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = openFileOutput("temp", MODE_PRIVATE);//获得FileOutputStream


        //tempFile.deleteOnExit();//在虚拟机终止时，请求删除此抽象路径名表示的文件或目录
        //FileOutputStream fos = new FileOutputStream(tempFile);//打开临时文件的输出流

        //1 备份插入点后的内容
        RandomAccessFile raf = new RandomAccessFile(getFilesDir().getPath() + File.separator + fileName, "rw");
        raf.seek(pos);//移动指针，从插入点开始读文件
        byte[] buffer = new byte[4];
        int num = 0;
        while (-1 != (num = raf.read(buffer))) {// 插入点之后的内容 ，读取到临时文件
            fos.write(buffer, 0, num);
        }

        //2 从插入点开始覆盖写入插入的内容
        raf.seek(pos);//回到插入点
        raf.write(content.getBytes());//从插入点开始写，覆盖写

        //3 把备份的内容，写回到原文件
        FileInputStream fis = openFileInput("temp");//临时文件的输入流
        while (-1 != (num = fis.read(buffer))) {// 临时文件的内容，追加的读到原文件
            raf.write(buffer, 0, num);
        }


    }


    // 以下为ByteBuffer 测试
    public void test_6(View view) {
        if (apkPath.equals("")) {
            Toast.makeText(this, "apk文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        InputStream in = null;
        try {
            in = this.openFileInput(SmartPhonePos_apk);
            //获取文件长度
            int lenght = in.available();
            byte[] buffer = new byte[lenght];
            in.read(buffer);
            int magicOffset = SignerVerifyUtils.pullSignBlockRear(buffer);// 魔数块的偏移量
            // magicOffset + 16 就是 中央目录的偏移量 centralDirOffset
            LogUtil.e("offset = " + magicOffset);

            RandomAccessFile raf = new RandomAccessFile(getFilesDir().getPath() + File.separator + SmartPhonePos_apk, "r");


            // 直接读到bytes，ok的
//            raf.seek(offset - 8);
//            byte[] bytes = new byte[8];
//            raf.read(bytes);
//            LogUtil.e(ConvertUtil.bytesToHexString(bytes));


            //
            ByteBuffer rearBytes = ByteBuffer.allocate(8); // 分配8个字节的空间
            rearBytes.order(ByteOrder.LITTLE_ENDIAN); // 设置为 小端排序
            raf.seek(magicOffset - 8);
            // 8个字节的签名块大小 读到 rearBytes
            raf.readFully(rearBytes.array()/*获取底层byte[]*/, rearBytes.arrayOffset()/*0*/, rearBytes.capacity());
            LogUtil.e(ConvertUtil.bytesToHexString(rearBytes.array()));

            LogUtil.e("小端序");
            LogUtil.e(rearBytes.getInt() + "");// 1个int，4个字节，每getInt()一次，指针移动四个字节
            LogUtil.e(rearBytes.getInt() + "");
            //LogUtil.e(footer.getInt() + "");// 此处超过8个字节,报错：java.nio.BufferUnderflowException


            byte[] smallbytes = rearBytes.array();//不受指针移动的影响
            byte[] bigbytes = BytesOptUtil.changeBytes(smallbytes);//小端序转大端序
            ByteBuffer bigBuffer = ByteBuffer.allocate(8);
            bigBuffer.put(bigbytes);//指针会移动到末尾
            LogUtil.e(ConvertUtil.bytesToHexString(bigBuffer.array()));
            bigBuffer.rewind();//重置指针到开头
            LogUtil.e("大端序");
            LogUtil.e(bigBuffer.getInt() + "");
            LogUtil.e(bigBuffer.getInt() + "");

            // 大小端序，只会影响字节的顺序，不会影响每个字节的值也不会影响字节组合起来的值

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




    // 安装 jxnx 测试ok
    public void test_7(View v){
        AssetsUtils.fileOpt(SmartPhonePos_apk,this);
        install(getApplication(),getFilesDir().getPath() +"/" +SmartPhonePos_apk);
        //install(getApplication(),s);

        LogUtil.e("getExternalStorageDirectory = " + Environment.getExternalStorageDirectory().getPath());//这个会取到sdcard的路径
        LogUtil.e("getDataDirectory = " +Environment.getDataDirectory().getPath());
        LogUtil.e("getRootDirectory = " +Environment.getRootDirectory().getPath());
        LogUtil.e("getDownloadCacheDirectory = " +Environment.getDownloadCacheDirectory().getPath());
        //LogUtil.e(Environment.getExternalStoragePublicDirectory().getPath());


    }

    String TAG = "guohao";

    // 等于 文件管理器的 /sdcard/Download/weixin_1400.apk
    String s = Environment.getExternalStorageDirectory().getPath() + "/Download/weixin_1400.apk";

    public static boolean install(Context con, String filePath) {
        try {
            if(TextUtils.isEmpty(filePath))
                return false;
            File file = new File(filePath);
            if(!file.exists()){
                Toast.makeText(con, "不存在", Toast.LENGTH_LONG).show();
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//增加读写权限
            }
            intent.setDataAndType(getPathUri(con, filePath), "application/vnd.android.package-archive");
            con.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(con, "安装失败，请重新下载", Toast.LENGTH_LONG).show();
            return false;
        } catch (Error error) {
            error.printStackTrace();
            Toast.makeText(con, "安装失败，请重新下载", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // /sdcard/Download/translator_union7.apk
    public static Uri getPathUri(Context context, String filePath) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String packageName = context.getPackageName();
            uri = FileProvider.getUriForFile(context, packageName + ".fileProvider", new File(filePath));
        } else {
            uri = Uri.fromFile(new File(filePath));
        }
        return uri;
    }

    static byte[] endByteV2 = new byte[]{0x41, 0x50, 0x4B, 0x20, 0x53, 0x69, 0x67, 0x20, 0x42, 0x6C, 0x6F, 0x63, 0x6B, 0x20, 0x34, 0x32};
    // 去处签名块
    public void test_8(View view){
        String apkPath = AssetsUtils.fileOpt(SmartPhonePos_apk,this);

        try {
            byte[] apkBytes = AssetsUtils.getByteFromAssetsAndCopyToData(SmartPhonePos_apk,this);
            // 计算偏移量
            int magicOffSet = BytesOptUtil.matchBytesBySelect(apkBytes, endByteV2, 1);
            int sizeInFooter = magicOffSet - 8;

            // 尾长度
            ByteBuffer sizeInFooterBuffer = BytesOptUtil.getByteBufferFormFile(apkPath,sizeInFooter,8);
            LogUtil.e("sizeInFooterBytes = " + ConvertUtil.bytesToHexString(sizeInFooterBuffer.array()));
            //LogUtil.e("sizeInFooter = " + sizeInFooterBuffer.getInt());

            // begin 转大端序，读出后四个字节的int数值，即整签名块的长度
            byte[] bigSizeInFooter = BytesOptUtil.changeBytes(sizeInFooterBuffer.array());
            LogUtil.e("bigSizeInFooter = " + ConvertUtil.bytesToHexString(bigSizeInFooter));
            ByteBuffer sizeInFooterBufferBig = BytesOptUtil.byteToBuffer(bigSizeInFooter);
            int sizeOfSigBlock = sizeInFooterBufferBig.getInt(4);
            LogUtil.e("getInt = " + sizeOfSigBlock);//3201
            // end 转大端序，读出后四个字节的int数值，即整签名块的长度

            // 头长度的偏移量
            int sizeInHead = magicOffSet + 16 - sizeOfSigBlock - 8;
            ByteBuffer sizeInHeadBuffer = BytesOptUtil.getByteBufferFormFile(apkPath,sizeInHead,8);
            LogUtil.e("sizeInHeadBuffer = " + ConvertUtil.bytesToHexString(sizeInHeadBuffer.array()));


            // begin 获取 签名块之前的部分
            int firstPartSize = sizeInHead; // 签名块之前的部分的大小
            ByteBuffer firstPartBuffer = BytesOptUtil.getByteBufferFormFile(apkPath,0,firstPartSize);
            // end 获取 签名块之前的部分
            // begin 头验证 签名块前的部分
            LogUtil.e("签名块前的部分size = " + firstPartBuffer.array().length);
            // 输出前4个字节
            LogUtil.e("输出前4个字节");
            byte[] Bytes4InHead = BytesOptUtil.getSubBytes(firstPartBuffer.array(),0,4);
            LogUtil.e(ConvertUtil.bytesToHexString(Bytes4InHead));
            LogUtil.e("=====");
            // 输出末尾4个字节
            LogUtil.e("输出末尾4个字节");
            byte[] Bytes4InFoot = BytesOptUtil.getSubBytes(firstPartBuffer.array(),firstPartBuffer.array().length - 4,4);
            LogUtil.e(ConvertUtil.bytesToHexString(Bytes4InFoot));
            // end 头验证 签名块前的部分

            // begin 获取 签名块之后的部分
            int secondPartOfApkOffSet = magicOffSet + 16; // 签名块之后的部分的大小
            int secondPartSize = apkBytes.length - secondPartOfApkOffSet  ;
            ByteBuffer secondPartBuffer = BytesOptUtil.getByteBufferFormFile(apkPath,secondPartOfApkOffSet,secondPartSize);
            // end 获取 签名块之后的部分
            // begin 验证 签名块之后的部分
            LogUtil.e("签名块后的部分size = " + secondPartBuffer.array().length);
            byte[] Bytes4InHeadOfSecPart = BytesOptUtil.getSubBytes(secondPartBuffer.array(),0,4);
            LogUtil.e(ConvertUtil.bytesToHexString(Bytes4InHeadOfSecPart));
            LogUtil.e("=====");
            byte[] Bytes4InFootOfSecPart = BytesOptUtil.getSubBytes(secondPartBuffer.array(),secondPartBuffer.array().length - 4,4);
            LogUtil.e(ConvertUtil.bytesToHexString(Bytes4InFootOfSecPart));
            // end 验证 签名块之后的部分


            // 现在取到了 签名块前 和 签名块后 的字节
            // 合并字节
            byte[] oriApkByte = BytesOptUtil.mergeBytes(firstPartBuffer.array(),secondPartBuffer.array());
            LogUtil.e("merge finish " );
            //需要验证一下数据对不对
            byte[] oriApkByteHead4 = BytesOptUtil.getSubBytes(oriApkByte,0,4);
            byte[] oriApkByteFoot4 = BytesOptUtil.getSubBytes(oriApkByte,oriApkByte.length - 4,4);
            LogUtil.e(ConvertUtil.bytesToHexString(oriApkByteHead4));
            LogUtil.e(ConvertUtil.bytesToHexString(oriApkByteFoot4));

            // 计算hash值对比一下
            byte[] oriHash = SignerVerifyUtils.calHash(oriApkByte,0,oriApkByte.length);
            LogUtil.e("oriHash = " + ConvertUtil.bytesToHexString(oriHash));

            // 中央尾曲的偏移量，要重新计算

        }  catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("Exception = " + e.toString());
        }
    }


    public void test_9(View v){
        LogUtil.e("" + "1");
        testByteBuffer();
        LogUtil.e("" + "2");
    }

    // 测试 bytebuffer
    private void testByteBuffer(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putInt(1);
        LogUtil.e("" + ConvertUtil.bytesToHexString(byteBuffer.array()));
        // 结果：0000000100000000
        // 说明写入 4个字节的int类型的数据

        ByteBuffer byteBuffer2 = ByteBuffer.allocate(8);
        byteBuffer2.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer2.putInt(1);
        LogUtil.e("" + ConvertUtil.bytesToHexString(byteBuffer2.array()));
        // 结果 0100000000000000
        // 说明，默认大端序，跟人类的直觉一致

        ByteBuffer byteBuffer3 = ByteBuffer.allocate(4);
        byteBuffer3.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer3.putInt(256 + 16 + 1);
        LogUtil.e("" + ConvertUtil.bytesToHexString(byteBuffer3.array()));
        // 结果 11010000
        // 转换成大端序： 00 00 01 11 （符合直觉）


        byte[] byte4 = new byte[]{0x1a,0x02,0x03,0x04};
        LogUtil.e("byte4 = " + byte4);
        // 直接输出字节数组的结果：[B@e442d66
        LogUtil.e("byte4 [0] = " + byte4[0]);
        // 单个字节输出,就会输出int数值

        // 如果想输出 字节数组的 int数值，用 bytebuffer
        ByteBuffer buffer = ByteBuffer.allocate(byte4.length);
        buffer.put(byte4);
        buffer.rewind();
        LogUtil.e("buffer.getInt() = " + buffer.getInt());//结果：436339460
        LogUtil.e("buffer = " + ConvertUtil.bytesToHexString(buffer.array()));
        

        buffer.clear();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        LogUtil.e("buffer.getInt() = " + buffer.getInt());//结果：67306010
        LogUtil.e("buffer = " + ConvertUtil.bytesToHexString(buffer.array()));
        // 同样的字节端，不同的端序，不同的数值

        // 测试 slice
        byte[] byte5 = new byte[]{0x01,0x02,0x03,0x04,0x05};
        ByteBuffer buffer5 = ByteBuffer.allocate(byte5.length);
        buffer5.put(byte5);
        buffer5.position(2);
        buffer5.limit(3);
        LogUtil.e("buffer5 position " + buffer5.position());
        LogUtil.e("buffer5 limit " + buffer5.limit());
        LogUtil.e("buffer5 capacity " + buffer5.capacity());
        LogUtil.e("buffer5 remaining " + buffer5.remaining());// limit - position
                                                                   // clear后，limit = capacity
                                                                   // limit = 1 时，表示还剩一个字节，就是当前的字节

        LogUtil.e("   ");

        ByteBuffer buffer5_new = buffer5.slice();// 把 position 开始的 limit 长度的字节，复制出来
                                               // buffer5_new 的 limit，capacity等是独立的
        LogUtil.e("buffer5_new position " + buffer5_new.position());// position 从0开始
        LogUtil.e("buffer5_new limit " + buffer5_new.limit());
        LogUtil.e("buffer5_new capacity " + buffer5_new.capacity());
        LogUtil.e("buffer5_new remaining " + buffer5_new.remaining());

        LogUtil.e("buffer5_new array " + ConvertUtil.bytesToHexString(buffer5_new.array()));
        // buffer5_new.array() 跟 buffer5.array() 一样
        // buffer5_new 是 read-only的

        // 小结：slice 就是 复制一段只读的子串出去

        LogUtil.e("   ");

        // 测试 duplicate
        byte[] byte6 = new byte[]{0x01,0x02,0x03,0x04,0x05};
        ByteBuffer buffer6 = ByteBuffer.allocate(byte6.length);
        buffer6.put(byte6);
        buffer6.position(2);
        buffer6.limit(3);
        LogUtil.e("buffer6 position " + buffer6.position());
        LogUtil.e("buffer6 limit " + buffer6.limit());
        LogUtil.e("buffer6 capacity " + buffer6.capacity());
        LogUtil.e("buffer6 remaining " + buffer6.remaining());// limit - position


        LogUtil.e("   ");

        ByteBuffer buffer6_new = buffer6.duplicate();
        LogUtil.e("buffer6_new position " + buffer6_new.position()); // 同上
        LogUtil.e("buffer6_new limit " + buffer6_new.limit()); // 同上
        LogUtil.e("buffer6_new capacity " + buffer6_new.capacity()); // 同上
        LogUtil.e("buffer6_new remaining " + buffer6_new.remaining()); // 同上

        LogUtil.e("buffer6_new array " + ConvertUtil.bytesToHexString(buffer6_new.array()));

        // 小结：duplicate 就是复制一个只读的全串，position，limit等全部一致

        LogUtil.e("   ");

        // 测试 put
        byte[] byte7 = new byte[]{0x01,0x02,0x03,0x04,0x05};
        ByteBuffer buffer7 = ByteBuffer.allocate(byte7.length);
        buffer7.put(byte7);
        buffer7.position(1);
        buffer7.put((byte) 0x10);
        LogUtil.e("buffer7 " + ConvertUtil.bytesToHexString(buffer7.array()));

        buffer7.position(1);
        byte[] byte7_2 = new byte[]{0x30,0x30};
        buffer7.put(byte7_2);
        LogUtil.e("buffer7 " + ConvertUtil.bytesToHexString(buffer7.array()));


        // 小结：put 一个字节，就会在 position 的位置上覆盖写入 一个字节
        // put n个字节，就会在 position 的位置开始覆盖写入 n个字节
        // 不会影响到后续n+1的字节

        //注意： put(byte[] src, int offset, int length) 和 get(byte[] src, int offset, int length)
        // 其中的入参 offset 和 length，是针对 src 来说的
        // 并不是针对 buffer 的 array()

        bufferToHexString(buffer7);

        bufferToHexString(buffer7,1,3);
    }


    public void bufferToHexString(ByteBuffer byteBuffer){
        StringBuilder stringBuilder = new StringBuilder(byteBuffer.capacity()*2);
        byteBuffer.clear();
        while(byteBuffer.remaining()>0){
            String s = ConvertUtil.loopLogSingleByteToHexString(byteBuffer.get());
            stringBuilder.append(s + " ");
        }
        LogUtil.e("" + stringBuilder.toString());
    }

    public void bufferToHexString(ByteBuffer byteBuffer,int pos,int len){
        if(pos + len > byteBuffer.capacity()){
            LogUtil.e(" pos + len > byteBuffer.capacity() ");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder(byteBuffer.capacity()*2);
        byteBuffer.clear();
        byteBuffer.position(pos);
        byteBuffer.limit(pos + len);
        while(byteBuffer.remaining()>0){
            String s = ConvertUtil.loopLogSingleByteToHexString(byteBuffer.get());
            stringBuilder.append(s + " ");
        }
        LogUtil.e("" + stringBuilder.toString());
    }

    public void test_10(View v){
        String xmlpath = AssetsUtils.fileOpt("updatemanifest.xml",this);
        String imgpath = AssetsUtils.fileOpt("V2.1.1_V2.4.1_update.img",this);
        new XmlOptUtil().parseXMLFileSimple(xmlpath,imgpath);
    }


}

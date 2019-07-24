package com.anything.guohao.anything.signerverify;


import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.anything.guohao.anything.AssetsUtils;
import com.anything.guohao.anything.BaseTestActivity;
import com.anything.guohao.anything.ConvertUtil;
import com.anything.guohao.anything.FileOptUtil.BytesOptUtil;
import com.anything.guohao.anything.FileOptUtil.FileOptUtil;
import com.anything.guohao.anything.LogUtil;
import com.anything.guohao.anything.R;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1SequenceParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 验签demo
 * 该demo的第一阶段测试目的：从一大段字节中，找出一段字节的开头和结尾，然后把该段字节提取出来
 * 第一步，找到开头和结尾
 * 第二部，提取字节
 * 学到的知识点：
 * 1,字节可以直接用=比较，转字符在比较会很慢
 * 2,文件读取有很多比inputstream更高效的办法，如：RandomAccessFile，FileChannel和 MappedByteBuffer 等，有空总结
 */

/**
 * 2019-7-25
 * 总结一下，江西农商行验签项目能提炼出来的几点收获：
 * 第一，代码中查看apk的方法：JarFIle类遍历，Zip格式截取四个分区（文件区，签名块，目录区，目录结尾区）
 * 第二，签名块的结构，以及如何去掉去掉签名块后还原apk（目录结尾区的目录区要修改）
 * 第三，可以延伸以下；自己插入签名块怎么做（同上，目录结尾区的目录区要修改）
 * 第四，前三条本质上就是需要对ZIP文件的格式了解清楚
 * 第五，RandomAccessFile 和 ByteBuffer 类的使用
 * 第六，了解了RSA算法加密解密，签名验签的流程
 * 第七，总结一份 V2签名 和 验签 的源码文档，发表。
 * 第八，并把该项目的测试内容整理一遍，相关函数放入Util类中，便于以后复用。
 *
 */
public class SignerVerifyActivity extends BaseTestActivity {

    String jxnx_acquire_apk = "jxnx_acquire_22_2_9_03_release_20190520.apk";
    //String SmartPhonePos_apk = "SmartPhonePos_new_20190612_2_0_3_release.apk";
    String SmartPhonePos_apk = "SmartPhonePos_new_20190612_2_0_3_release.apk";
    String verify_apk = "jxnx_acquire_22_2_9_03_release_20190520.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_signer_verify);
        super.onCreate(savedInstanceState);
    }

    String apkPath = "";

    // 获取byte，从应用的本地数据路径下的文件中
    private byte[] getByteFromAssetsAndCopyToData(String filename) {
        showMessage("加载文件：" + filename);
        // 把 assets 的 xxx.apk，copy 到 context.getFilesDir() 的路径下
        // 即 /data/user/0/com.anything.guohao.anything/files
        String path = AssetsUtils.fileOpt(SmartPhonePos_apk, this);
        apkPath = path;
        if (!path.equals("")) {
            Toast.makeText(this, "加载完成", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
        }

        if (apkPath.equals("")) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            return null;
        }

        InputStream in = null;
        byte[] buffer;

        try {
            in = this.openFileInput(filename);
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

    // 获取byte，从应用的本地数据路径下的文件中，可指定开头和长度
    private byte[] getByteFromFile(String fileName, int offset, int len) throws IOException {
        File file = new File(getFilesDir().getPath() + File.separator + fileName);
        if (!file.exists()) {
            throw new IOException("file not exists");
        }

        ByteBuffer mBytes = ByteBuffer.allocate(len);
        RandomAccessFile rdacFile = new RandomAccessFile(getFilesDir().getPath() + File.separator + fileName, "r");
        rdacFile.seek(offset);
        rdacFile.readFully(mBytes.array(), 0, len);
        //LogUtil.e("getByteFromFile = " + ConvertUtil.bytesToHexString(mBytes.array()));
        return mBytes.array();
    }


    //从 assets 加载文件到 /data/data/{包名}/files
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


    //遍历 apk 内的文件，用 JarFile
    public void test_2(View v) {
        apkPath = AssetsUtils.fileOpt(SmartPhonePos_apk, this);
        readApkWithJarFile(apkPath);
    }

    private void readApkWithJarFile(String apkPath) {
        try {
            if (apkPath.equals("")) {
                Toast.makeText(this, "apk文件不存在", Toast.LENGTH_SHORT).show();
                return;
            }

            JarFile apkFile = new JarFile(apkPath);

            LogUtil.e("" + apkFile.getName());

            Enumeration<JarEntry> enumerations = apkFile.entries();

            while (enumerations.hasMoreElements()) {
                String name = enumerations.nextElement().getName();
                if (name.startsWith("res")) continue;
                LogUtil.e("" + "Element = " + name);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //输出apk的字节的16进制字符，通过StringBuilder，报错，说明StringBuilder是有大小限制的
    public void test_3(View v) {
        if (apkPath.equals("")) {
            Toast.makeText(this, "apk文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream in = this.openFileInput(SmartPhonePos_apk);

            //获取文件长度
            int lenght = in.available();

            byte[] buffer = new byte[lenght];

            in.read(buffer);

            Log.e("guohao", "hexString = " + ConvertUtil.bytesToHexString(buffer));//报错：
            //  Caused by: java.lang.OutOfMemoryError: Failed to allocate a 51592120 byte allocation with 4194304 free bytes and 14MB until OOM
            //  at java.lang.StringBuilder.toString(StringBuilder.java:408)

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //从流中逐个读字节速度也很慢，12m 需要 4分钟
    public void test_5_fail(View view) {
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

            int index = 0;
            Log.e("guohao", "begin ");
            while ((index = in.read()) != -1) {
                //Log.e("guohao","index " + index);
            }
            Log.e("guohao", "finish ");
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
    }


    // StringBuilder 的简单测试，可以掐头
    public void testString() {
        StringBuilder result = new StringBuilder(2);
        result.append("1");
        result.append("2");
        result.append("3");

        result.deleteCharAt(0);
        LogUtil.e("result = " + result.toString() + ",len = " + result.length());
    }

    // 匹配开头的测试，输出匹配的下标
    public void test_4(View v) {
        if (apkPath.equals("")) {
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
        } finally {

        }
    }

    // 读取整个签名块，3201的字节那种
    public void test_5(View v) {
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

            ByteBuffer magicBytes = ByteBuffer.allocate(16);
            magicBytes.order(ByteOrder.LITTLE_ENDIAN); // 设置为 小端排序
            raf.seek(magicOffset);
            raf.readFully(magicBytes.array()/*获取底层byte[]*/, magicBytes.arrayOffset()/*0*/, magicBytes.capacity());
            LogUtil.e("magicOffset:" + ConvertUtil.bytesToHexString(magicBytes.array()));

            int apkSigBlockSizeInFooter = magicOffset - 8;// 尾块长度的偏移量

            ByteBuffer rearBytes = ByteBuffer.allocate(8); // 分配8个字节的空间
            rearBytes.order(ByteOrder.LITTLE_ENDIAN); // 设置为 小端排序
            raf.seek(apkSigBlockSizeInFooter);

            // 8个字节的签名块大小 读到 rearBytes
            raf.readFully(rearBytes.array()/*获取底层byte[]*/, rearBytes.arrayOffset()/*0*/, rearBytes.capacity());
            LogUtil.e("BlockSizeInFooter:" + ConvertUtil.bytesToHexString(rearBytes.array()));

            LogUtil.e("小端序");
            int sigBlockSize = rearBytes.getInt();// 1个int，4个字节，每getInt()一次，指针移动四个字节
            LogUtil.e("签名块大小：" + sigBlockSize + "");

            // 开始读取签名块

            int sigBlockOffset = apkSigBlockSizeInFooter + 24 - sigBlockSize - 8;//签名块的偏移量，包括块头的8字节长度

            ByteBuffer sigBlockBytes = ByteBuffer.allocate(sigBlockSize + 8); // 分配 sigBlock + 头8字节 的空间
            sigBlockBytes.order(ByteOrder.LITTLE_ENDIAN); // 设置为 小端排序
            raf.seek(sigBlockOffset);
            LogUtil.e("sigBlockBytes.capacity()：" + sigBlockBytes.capacity());
            raf.readFully(sigBlockBytes.array()/*获取底层byte[]*/, sigBlockBytes.arrayOffset()/*0*/, sigBlockBytes.capacity());

            //LogUtil.e("签名块内容：" + ConvertUtil.bytesToHexString(sigBlockBytes.array())); // string 全输出 3209*2 个字符会少内容！！

            // 这样输出ok start 匹配魔数的后五个字节
//            LogUtil.e("签名块内容：" + ConvertUtil.loopLogSingleByteToHexString(sigBlockBytes.array()[3204]));
//            LogUtil.e("签名块内容：" + ConvertUtil.loopLogSingleByteToHexString(sigBlockBytes.array()[3205]));
//            LogUtil.e("签名块内容：" + ConvertUtil.loopLogSingleByteToHexString(sigBlockBytes.array()[3206]));
//            LogUtil.e("签名块内容：" + ConvertUtil.loopLogSingleByteToHexString(sigBlockBytes.array()[3207]));
//            LogUtil.e("签名块内容：" + ConvertUtil.loopLogSingleByteToHexString(sigBlockBytes.array()[3208]));
            // 这样输出ok end


            StringBuilder sizeInHeaderBuilder = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                sizeInHeaderBuilder.append(ConvertUtil.loopLogSingleByteToHexString(sigBlockBytes.get(i)));
                sizeInHeaderBuilder.append(" ");
            }
            LogUtil.e("sizeInHeader：" + sizeInHeaderBuilder.toString());

            StringBuilder sizeInFooterBuilder = new StringBuilder(8);
            for (int i = 3209 - 16 - 8; i < (3209 - 16); i++) {
                sizeInFooterBuilder.append(ConvertUtil.loopLogSingleByteToHexString(sigBlockBytes.get(i)));
                sizeInFooterBuilder.append(" ");
            }

            LogUtil.e("sizeInFooter：" + sizeInFooterBuilder.toString());


            StringBuilder magicStrBuilder = new StringBuilder(16);
            for (int i = 3209 - 16; i < 3209; i++) {
                magicStrBuilder.append(ConvertUtil.loopLogSingleByteToHexString(sigBlockBytes.get(i)));
                magicStrBuilder.append(" ");
            }
            LogUtil.e("magicStr：" + magicStrBuilder.toString());

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

    static byte[] endByteV2 = new byte[]{0x41, 0x50, 0x4B, 0x20, 0x53, 0x69, 0x67, 0x20, 0x42, 0x6C, 0x6F, 0x63, 0x6B, 0x20, 0x34, 0x32};
    static byte[] beginByte = new byte[]{0x13, 0x11, 0x41, 0x43, 0x51, 0x55, 0x49, 0x52, 0x45, 0x52, 0x2d, 0x53, 0x47, 0x4e, 0x2d, 0x49, 0x4e, 0x46, 0x4f};

    // 匹配一段字节
    public void test_7(View v) {
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

            int pos = SignerVerifyUtils.matchBytes(buffer, endByteV2);
            LogUtil.e("pos:" + pos);

            int pos2 = SignerVerifyUtils.matchBytes(buffer, beginByte);
            LogUtil.e("pos2:" + pos2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 匹配一段字节，并且可以选取第n次的匹配
    public void test_8(View v) {
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

            int pos = BytesOptUtil.matchBytesBySelect(buffer, ConvertUtil.hexStringToBytes("810C000000000000"), 1);
            LogUtil.e("pos: " + pos);

            int pos2 = BytesOptUtil.matchBytesBySelect(buffer, ConvertUtil.hexStringToBytes("810C000000000000"), 2);
            LogUtil.e("pos2: " + pos2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("Exception: " + e.toString());
        }
    }

    // 静态的工作证书，偏移5个字节 : 03 82 03 4B 00
    String hex = "308203463082022E020101300D06092A864886F70D01010B05003071310B300906035504061302434E311830160603550408130F4A69616E6758694E616E4368616E673111300F060355040713086E616E6368616E6731123010060355040A0C096A786E785F62616E6B31123010060355040B0C096A786E785F62616E6B310D300B060355040313046A786E783020170D3138303531373033353435355A180F32313138303432333033353435355A305F310B300906035504061302434E3110300E06035504080C074A69616E6758693111300F06035504070C084E616E4368616E67310D300B060355040A0C046A786E78310D300B060355040B0C046A786E78310D300B06035504030C046A786E7830820122300D06092A864886F70D01010105000382010F003082010A0282010100EAD31D289831EEEEC35B370A36624DE8C650CD9B74038AFA39198D2EEEBEDD09559E963883ECEB4EE0F2ED53423929B7E39165415550981FC456B642990F0CC504AB6EE60D55BB6452B6D07FA4181CA97259FACEA5935B0FD49872DEB36546B604CEEE0D0F2A25B3CDB13193B787922BE5B28B606EDA412D38455D4179EE9DB5F8047DB4F29EDF46E16149CCF08045965E872AD1494C2ADCB1CD9F2B9C8C4BA70D44073416AF45FEF4E1946C9C57A05D3389B5ED439CFA98F7B3FFB4C0B6A2EB21DE5024668F4BF80A80178C3A5B2AF4FB994EB7AD48816F7789881DE2241336D6D9E75B54DC01E0787FE420D62CFE23E79939733BBFB88BC648F8457A92EA350203010001300D06092A864886F70D01010B0500038201010096CB048A6590CC98B05B9F6674CC6D63964C3978D52FB3E4487AD5B91051EB42D04ADCB12BCA7DFB935DCE2A0D22BB6D415E54176BE0B928064A1FE69C63FCF4AA8C4EF17D183C71C29820E31AE6E2F5C641234E140E0B13E2F2DC2C4A5F03762DC3B9434B25211BEED4C9EE799373AB444000EB634A523D6DEDC49484755CA0DB6F8416294111774BC41B6A524D7FF345F061E4E071F07AF36695A56FC9AD4C7F9A5D15B0DD684A7B96717A5A5A259B31CA23B603525D6B45F94BA1BE81F38552C878B5B20A7C7834BB25D685D375899DD1E1E96D659E3F8AEDADA8595C74BF628562A0737C16549384435E6F2FF8F6C2A96ECBA30186527C0466637DC2B325";

    // 静态的工作证书验证ok
    public void test_6(View v) {
        //byte[] bytes = ConvertUtil.hexStringToBytes(hexStrWorkCert);
        //LogUtil.e(ConvertUtil.bytesToHexString(bytes));
        // 字节转换没问题

        X509Certificate work_cert = null;
        X509Certificate root_cert = null;
        CertificateFactory certFactory;
        try {
            certFactory = CertificateFactory.getInstance("X.509"); // 获取根证书

            InputStream input = new ByteArrayInputStream(ConvertUtil.hexStringToBytes(hex));

            //从工作公钥证书的流中提取出工作公钥
            work_cert = (X509Certificate) certFactory.generateCertificate(input);
            root_cert = get_RootCert_fromFile();

            work_cert.verify(root_cert.getPublicKey());// 使用 终端保存的根证书 验证 工作证书 的合法性
            work_cert.checkValidity();

            // 这样验证不行
//            root_cert.verify(work_cert.getPublicKey());
//            root_cert.checkValidity();

            showMessage("ok");
            LogUtil.e("verify ok");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.toString());
        }
    }

    static String rootCert = "jxnx_root_smartpos.x509.pem";

    private X509Certificate get_RootCert_fromFile()
            throws IOException, GeneralSecurityException {

        InputStream in = getAssets().open(rootCert);
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(in);
        } finally {
            in.close();
        }
    }


    // 倒序去读,匹配出证书内容,并提取验证ok
    public void test_9(View v) {
        //
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

            // 以 0382034B 片段匹配，临时的
            int certOffSet = BytesOptUtil.matchBytesBySelect(buffer, ConvertUtil.hexStringToBytes("0382034B"), 1);
            LogUtil.e("certOffSet: " + certOffSet);

            int certSizeOffset = certOffSet + 2;

            // 读取apk 到 RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(getFilesDir().getPath() + File.separator + SmartPhonePos_apk, "r");

            //先读取证书的大小 2个字节
            ByteBuffer certSizeBytes = ByteBuffer.allocate(4);
            certSizeBytes.put((byte) 0x00); // 不影响 certSizeBytes.arrayOffset()
            certSizeBytes.put((byte) 0x00);
            LogUtil.e("certSizeBytes Offset:" + certSizeBytes.arrayOffset());
            raf.seek(certSizeOffset);
            raf.readFully(certSizeBytes.array(), 2, 2);
            LogUtil.e("certSizeBytes:" + ConvertUtil.bytesToHexString(certSizeBytes.array()));
            certSizeBytes.rewind();//倒带
            int certSize = certSizeBytes.getInt();
            LogUtil.e("certSize:" + certSize);


            //再读取证书的字节
            ByteBuffer certBytes = ByteBuffer.allocate(certSize);
            //certBytes.order(ByteOrder.LITTLE_ENDIAN); // 设置为 小端排序
            raf.seek(certSizeOffset + 2);
            raf.readFully(certBytes.array()/*获取底层byte[]*/, certBytes.arrayOffset()/*0*/, certBytes.capacity());
            LogUtil.e("certBytes:" + ConvertUtil.bytesToHexString(certBytes.array()));


            verifyWorkCert(certBytes.array(), 1, certSizeBytes.array().length - 1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("Exception: " + e.toString());
        }
    }

    public PublicKey verifyWorkCert(byte[] bytes, int offset, int len) {
        byte[] workCertBytes = bytes;

        X509Certificate work_cert = null;
        X509Certificate root_cert = null;
        CertificateFactory certFactory;
        try {
            certFactory = CertificateFactory.getInstance("X.509"); // 获取根证书

            InputStream input = new ByteArrayInputStream(workCertBytes, offset, len);

            //从工作公钥证书的流中提取出工作公钥
            work_cert = (X509Certificate) certFactory.generateCertificate(input);
            root_cert = get_RootCert_fromFile();

            work_cert.verify(root_cert.getPublicKey());// 使用 终端保存的根证书 验证 工作证书 的合法性
            work_cert.checkValidity();

            LogUtil.e("getPublicKey str " + work_cert.getPublicKey().toString());
            LogUtil.e("getPublicKey Algorithm " + work_cert.getPublicKey().getAlgorithm());
            LogUtil.e("getPublicKey Format " + work_cert.getPublicKey().getFormat());
            LogUtil.e("getPublicKey byte " + ConvertUtil.bytesToHexString(work_cert.getPublicKey().getEncoded()));

            LogUtil.e("getSignature byte " + ConvertUtil.bytesToHexString(work_cert.getSignature()));


            // 这样验证不行
//            root_cert.verify(work_cert.getPublicKey());
//            root_cert.checkValidity();

            showMessage("证书验证 ok");
            LogUtil.e("verify cert ok");

            //work_cert.get

            return work_cert.getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.toString());
        }

        return null;
    }


    //农信的签名ID：78 67 64 32 （以此为开头）
    static byte[] jxnxID = new byte[]{0x78, 0x67, 0x64, 0x32};

    // 提取出 jxnx签名块的 三部分：主体，签名数据，证书；且证书验签成功
    public void test_10(View v) {
        byte[] apkBytes = AssetsUtils.getByteFromAssetsAndCopyToData(SmartPhonePos_apk, this);

        if (apkBytes == null) {
            return;
        }

        try {
            // 计算偏移量
            int jxnxIDOffSet = BytesOptUtil.matchBytesBySelect(apkBytes, jxnxID, 1);
            int magicOffSet = BytesOptUtil.matchBytesBySelect(apkBytes, endByteV2, 1);

            LogUtil.e("jxnxIDOffSet:" + jxnxIDOffSet);
            LogUtil.e("magicOffSet:" + magicOffSet);

            // 取 江西农信的签名块ID之后 到 尾块长度之前 的字节
            // 即 ID-value 块
            int begin = jxnxIDOffSet + 4;/*开始下标移动到农信的签名块ID的结尾*/
            int len = (magicOffSet - 8) - begin;
            byte[] jxnxBytes = getByteFromFile(SmartPhonePos_apk, begin, len);
            LogUtil.e("jxnjSigBlocBytes = " + ConvertUtil.bytesToHexString(jxnxBytes));
            LogUtil.e("jxnjSigBlocBytes.size = " + jxnxBytes.length);

            // ID-value块 转 ASN1流
            InputStream in = new ByteArrayInputStream(jxnxBytes);
            ASN1InputStream asn1InputStream = new ASN1InputStream(in);

            //
            ASN1Object asn1Primitive = null;

            byte[] firstPartBytes = null;
            byte[] orighash = new byte[32];
            byte[] sigData = null;
            byte[] cert = null;

            while ((asn1Primitive = asn1InputStream.readObject()) != null) {
                if (asn1Primitive instanceof ASN1Sequence) {
                    ASN1Sequence asn1Sequence = (ASN1Sequence) asn1Primitive;
                    ASN1SequenceParser asn1SequenceParser = asn1Sequence.parser();
                    ASN1Encodable asn1Encodable = null;
                    int n = 0;
                    while ((asn1Encodable = asn1SequenceParser.readObject()) != null) {
                        LogUtil.e("-------------parse result -------------- n" + n);
                        asn1Primitive = asn1Encodable.toASN1Primitive();
                        LogUtil.e("asn1String: " + ConvertUtil.bytesToHexString(asn1Primitive.getEncoded()));
                        if (n == 0) {
                            firstPartBytes = asn1Primitive.getEncoded();
                            LogUtil.e("firstPartBytes size = " + firstPartBytes.length);
                        } else if (n == 1) {
                            sigData = asn1Primitive.getEncoded();
                            LogUtil.e("sigData size = " + sigData.length);
                        } else if (n == 2) {
                            cert = asn1Primitive.getEncoded();
                            LogUtil.e("cert size = " + cert.length);
                        }
                        n++;
                    }
                }
            }
            // 把原始apk的hash值
            byte[] hashId = new byte[]{0x02, 0x20};
            int hashOffset = BytesOptUtil.matchBytesBySelect(firstPartBytes, hashId, 1);
            test_10_2(firstPartBytes);//解析出原始apk的hash

            // 拷贝数组
            System.arraycopy(firstPartBytes, hashOffset + 2, orighash, 0, 32);

            LogUtil.e("orighash = " + ConvertUtil.bytesToHexString(orighash));

            LogUtil.e("sigData = " + ConvertUtil.bytesToHexString(sigData));

            LogUtil.e("cert = " + ConvertUtil.bytesToHexString(cert));

            // 第一步 验证书
            PublicKey publicKey = verifyWorkCert(cert, 5, cert.length - 5);

            // 第二步 验签名
            byte[] subSigdata = BytesOptUtil.getSubBytes(sigData, 4, sigData.length - 4);

            byte[] hash = SignerVerifyUtils.calHash(firstPartBytes, 0, firstPartBytes.length); // SHA256
            LogUtil.e("hash = " + ConvertUtil.bytesToHexString(hash));
            LogUtil.e("hash len = " + hash.length);


            // 公钥：publicKey
            // 签名数据：sigData
            // 原数据：hash 32

            byte[] hash32;
            if (hash.length > 32) {
                hash32 = new byte[32];
                System.arraycopy(hash, 0, hash32, 0, 32);
            } else {
                hash32 = hash;
            }
            boolean res = RSAUtils.verify(hash32, publicKey, subSigdata); // SHA256withRSA
            LogUtil.e("verify sign res = " + res);

            if (res) {
                showMessage("签名验证 ok");
            } else {
                showMessage("签名验证 fail");
            }

            // 第三步

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 去读第一部分的
    public void test_10_2(byte[] bytes) throws IOException {
        InputStream in = new ByteArrayInputStream(bytes);
        ASN1InputStream asn1InputStream = new ASN1InputStream(in);

        //
        ASN1Object asn1Primitive = null;

        byte[] firstPartBytes = null;
        byte[] orighash = new byte[32];
        byte[] sigData = null;
        byte[] cert = null;

        while ((asn1Primitive = asn1InputStream.readObject()) != null) {
            if (asn1Primitive instanceof ASN1Sequence) {
                ASN1Sequence asn1Sequence = (ASN1Sequence) asn1Primitive;
                ASN1SequenceParser asn1SequenceParser = asn1Sequence.parser();
                ASN1Encodable asn1Encodable = null;
                int n = 0;
                while ((asn1Encodable = asn1SequenceParser.readObject()) != null) {
                    LogUtil.e("-------------parse result -------------- n" + n);
                    asn1Primitive = asn1Encodable.toASN1Primitive();
                    LogUtil.e("asn1String: " + ConvertUtil.bytesToHexString(asn1Primitive.getEncoded()));
                    n++;
                }
            }
        }
    }

    // 测试 RSAEncrypt类 和 RSASignature类
    public void test_11(View v) throws Exception {
        String filepath = getFilesDir().getPath() + File.separator;

        //生成公钥和私钥文件
        RSAEncrypt.genKeyPair(filepath);

        System.out.println("--------------公钥加密私钥解密过程-------------------");
        String plainText = "这是一行待用公钥加密的文字";
        //公钥加密过程
        byte[] cipherData = RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(RSAEncrypt.loadPublicKeyByFile(filepath)), plainText.getBytes());
        String cipher = Base64.encode(cipherData);
        //私钥解密过程
        byte[] res = RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile(filepath)), Base64.decode(cipher));
        String restr = new String(res);
        System.out.println("原文：" + plainText);
        System.out.println("加密：" + cipher);
        System.out.println("解密：" + restr);
        System.out.println();

        System.out.println("--------------私钥加密公钥解密过程-------------------");
        plainText = "这是一行待用私钥加密的文字";
        //私钥加密过程
        cipherData = RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile(filepath)), plainText.getBytes());
        cipher = Base64.encode(cipherData);
        //公钥解密过程
        res = RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr(RSAEncrypt.loadPublicKeyByFile(filepath)), Base64.decode(cipher));
        restr = new String(res);
        System.out.println("原文：" + plainText);
        System.out.println("加密：" + cipher);
        System.out.println("解密：" + restr);
        System.out.println();

        System.out.println("---------------私钥【签名】过程------------------");
        String content = "这是一行待签名的文字";
        String signstr = RSASignature.sign(content, RSAEncrypt.loadPrivateKeyByFile(filepath));
        System.out.println("签名原串：" + content);
        System.out.println("签名串：" + signstr);
        System.out.println();

        System.out.println("---------------公钥【校验签名】------------------");
        System.out.println("签名原串：" + content);
        System.out.println("签名串：" + signstr);

        System.out.println("验签结果：" +
                RSASignature.doCheck(
                        content, // 原内容
                        signstr, // 签名数据
                        RSAEncrypt.loadPublicKeyByFile(filepath)) // 公钥
        );
        System.out.println();

    }

    // 测试 RSAUtils 类
    public void test_12(View v) throws Exception {
        test();
        testSign();
        testHttpSign();//此处类似 jxnx 的验签
    }

    static String publicKey;
    static String privateKey;

    static {
        try {
            Map<String, Object> keyMap = RSAUtils.genKeyPair();
            publicKey = RSAUtils.getPublicKey(keyMap);
            privateKey = RSAUtils.getPrivateKey(keyMap);
            System.err.println("公钥: \n\r" + publicKey);
            System.err.println("私钥： \n\r" + privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void test() throws Exception {
        System.err.println("公钥加密——私钥解密");
        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗？";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKey);
        System.out.println("加密后文字：\r\n" + new String(encodedData));
        byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, privateKey);
        String target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);
    }

    static void testSign() throws Exception {
        System.err.println("私钥加密——公钥解密");
        String source = "这是一行测试RSA数字签名的无意义文字";
        System.out.println("原文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
        System.out.println("加密后：\r\n" + new String(encodedData));
        byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, publicKey);
        String target = new String(decodedData);
        System.out.println("解密后: \r\n" + target);
        System.err.println("私钥签名——公钥验证签名");
        String sign = RSAUtils.sign(encodedData, privateKey);
        System.err.println("签名:\r" + sign);
        boolean status = RSAUtils.verify(encodedData, publicKey, sign);
        System.err.println("验证结果:\r" + status);
    }

    static void testHttpSign() {

        try {
            // A公司的员工，用私钥加密，后用私钥签名
            String param = "一个hash值";
            byte[] encodedData = RSAUtils.encryptByPrivateKey(param.getBytes(), privateKey);
            System.out.println("加密后：" + encodedData);

            String sign = null;
            sign = RSAUtils.sign(encodedData, privateKey);
            System.err.println("签名：" + sign);

            // A公司向B公司提供：签名数据：sign；公钥：publicKey；加密后的数据：encodedData

            // 公钥 可以放到工作证书里，工作证书到公证处做公正，产生 根公钥

            // 这时候，A公司向B公司提供的内容变为：
            // 签名数据：sign；公钥：【工作证书 + 根公钥】加密后的数据：encodedData

            // B公司的员工，先用 根公钥 验证 工作证书，通过后，提取 工作证书 中的 公钥

            // B公司的员工，用公钥验签，后用公钥解密
            boolean status = RSAUtils.verify(encodedData, publicKey, sign);
            System.err.println("签名验证结果：" + status);

            byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, publicKey);
            System.out.println("解密后：" + new String(decodedData));


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception：" + e.toString());
        }

    }

    // 查找 eocd 的 ID  0x50,0x4b,0x05,0x06
    public void test_13(View v) {
        byte[] apkBytes = AssetsUtils.getByteFromAssetsAndCopyToData(SmartPhonePos_apk, this);
        //byte[] eocdID = new byte[]{0x06,0x05,0x4b,0x50};
        byte[] eocdID = new byte[]{0x50, 0x4b, 0x05, 0x06};
        try {
            int eocdOffSet = BytesOptUtil.matchBytesBySelect(apkBytes, eocdID, 1);
            //int eocdOffSet = BytesOptUtil.matchBytesBySelect(apkBytes,jxnxID,1);
            LogUtil.e("eocdOffSet = " + eocdOffSet);

            int magicOffSet = BytesOptUtil.matchBytesBySelect(apkBytes, endByteV2, 1);

            // 中央区的偏移量
            int cdOffSet = magicOffSet + 16;
            LogUtil.e("cdOffSet = " + cdOffSet);
            byte[] test = new byte[]{0x00, (byte) 0xc3, (byte) 0xda, 0x0f};
            int test2 = BytesOptUtil.matchBytesBySelect(apkBytes, test, 1);
            LogUtil.e("test2 = " + test2);// 找不到记录


        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("Exception " + e.toString());
        }
    }

    // string 写入文件
    public void test_14(View v) {
        byte[] apkBytes = AssetsUtils.getByteFromAssetsAndCopyToData(SmartPhonePos_apk, this);
        String apkString = ConvertUtil.bytesToHexString(apkBytes);

        FileOptUtil.writeStringToFileInData(apkString, "apkHex.txt", this);
    }

    // 分离出原生v2 签名的apk ，并验证成功
    public void test_15(View v) {
        byte[] apkBytes = AssetsUtils.getByteFromAssetsAndCopyToData(SmartPhonePos_apk, this);

        try {
            // 第一步，魔数的偏移量
            int magicOffSet = BytesOptUtil.matchBytesBySelect(apkBytes, endByteV2, 1);
            LogUtil.e("magicOffSet = " + magicOffSet);

            // 第二步，尾巴块大小的偏移量
            int sizeInFooterOffset = magicOffSet - 8;
            LogUtil.e("sizeInFooterOffset = " + sizeInFooterOffset);

            // 第三步，计算 V2签名块 的大小
            byte[] sigBlockSizeByte = BytesOptUtil.getSubBytes(apkBytes, sizeInFooterOffset, 8);
            byte[] sigBlockSizeByteBig = BytesOptUtil.changeBytes(sigBlockSizeByte);
            int sigBlockSizeFromFooter = BytesOptUtil.byteToBuffer(sigBlockSizeByteBig).getInt(4);
            LogUtil.e("sigBlockSizeByte = " + ConvertUtil.bytesToHexString(sigBlockSizeByte));
            LogUtil.e("sigBlockSizeByteBig = " + ConvertUtil.bytesToHexString(sigBlockSizeByteBig));//0000000000000C81
            LogUtil.e("sigBlockSize = " + sigBlockSizeFromFooter);

            // 第四步，V2签名块的块头偏移量
            int allSigBlockHeadOffset = magicOffSet + 16 - sigBlockSizeFromFooter;
            LogUtil.e("sigBlockHeadOffset = " + allSigBlockHeadOffset);

            // 第五步，头部大小的偏移量
            int sizeInHeadOffset = allSigBlockHeadOffset - 8;
            LogUtil.e("sizeInHeadOffset = " + sizeInHeadOffset);

            // 第六步，用头部数据再次计算 V2签名块 的大小，对比第三步，验证数据是否一致
            byte[] sigBlockSizeByteFromHead = BytesOptUtil.getSubBytes(apkBytes, sizeInHeadOffset, 8);
            byte[] sigBlockSizeByteFromHeadBig = BytesOptUtil.changeBytes(sigBlockSizeByteFromHead);
            int sigBlockSizeFromHead = BytesOptUtil.byteToBuffer(sigBlockSizeByteFromHeadBig).getInt(4);
            LogUtil.e("sigBlockSizeByteFromHead = " + ConvertUtil.bytesToHexString(sigBlockSizeByteFromHead));
            LogUtil.e("sigBlockSizeByteFromHeadBig = " + ConvertUtil.bytesToHexString(sigBlockSizeByteFromHeadBig));//0000000000000C81
            LogUtil.e("sigBlockSizeFromHead = " + sigBlockSizeFromHead);

            if (sigBlockSizeFromHead == sigBlockSizeFromFooter) {
                LogUtil.e("头尾大小一致，验证ok");
            } else {
                showMessage("头尾大小不一致，验证失败");
                return;
            }

            // 第七步，jxnx签名块的ID
            int jxnxSigIDOffset = BytesOptUtil.matchBytesBySelect(apkBytes, jxnxID, 1);
            LogUtil.e("jxnxSigIDOffset = " + jxnxSigIDOffset);
            byte[] jxnxSigID = BytesOptUtil.getSubBytes(apkBytes, jxnxSigIDOffset, 4);
            LogUtil.e("jxnxSigID = " + ConvertUtil.bytesToHexString(jxnxSigID));
            int jxnxWholeSigBlockOffset = jxnxSigIDOffset - 8;

            // 第八步，eocd 的 ID
            byte[] eocdID = new byte[]{0x50, 0x4b, 0x05, 0x06,};
            int eocdIDOffset = BytesOptUtil.matchBytesBySelect(apkBytes, eocdID, 1);
            byte[] eocdBytes = BytesOptUtil.getSubBytes(apkBytes, eocdIDOffset, apkBytes.length - eocdIDOffset);
            LogUtil.e("eocdIDOffset = " + eocdIDOffset);
            LogUtil.e("eocdBytes = " + ConvertUtil.bytesToHexString(eocdBytes));


            int cdOffset = magicOffSet + 16;

            LogUtil.e("开始重新构建");
            // 开始重新构建 原生v2签名apk的字节块

            // 第一部分：coze ： content of zip entry
            ByteBuffer coznBytes = BytesOptUtil.getSubBytesBuffer(apkBytes, 0, sizeInHeadOffset);
            byte[] coznBytes4BytesInFooter = BytesOptUtil.getSubBytes(coznBytes.array(), coznBytes.array().length - 4, 4);
            LogUtil.e("coznBytes 4 bytes in footer = " + ConvertUtil.bytesToHexString(coznBytes4BytesInFooter));


            // 原生v2签名部分：origV2sigBlock
            ByteBuffer origV2sigBlockBytes =   //
                    BytesOptUtil.getSubBytesBuffer(apkBytes, allSigBlockHeadOffset, jxnxWholeSigBlockOffset - allSigBlockHeadOffset);
            byte[] origV2sigBlock4BytesInFooter = BytesOptUtil.getSubBytes(origV2sigBlockBytes.array(), origV2sigBlockBytes.array().length - 4, 4);
            LogUtil.e("origV2sigBlock 4 bytes in footer = " + ConvertUtil.bytesToHexString(origV2sigBlock4BytesInFooter));


            // 魔数块
            ByteBuffer magicBytes = BytesOptUtil.getSubBytesBuffer(apkBytes, magicOffSet, 16);
            LogUtil.e("magicBytes = " + ConvertUtil.bytesToHexString(magicBytes.array()));


            // cd
            ByteBuffer cdBytes = BytesOptUtil.getSubBytesBuffer(apkBytes, cdOffset, eocdIDOffset - cdOffset);
            byte[] cd4BytesInFooter = BytesOptUtil.getSubBytes(cdBytes.array(), cdBytes.array().length - 4, 4);
            LogUtil.e("cd4BytesInFooter = " + ConvertUtil.bytesToHexString(cd4BytesInFooter));

            // eocd
            ByteBuffer eocdBytesBuffer = BytesOptUtil.byteToBuffer(eocdBytes);
            LogUtil.e("eocdBytes = " + ConvertUtil.bytesToHexString(eocdBytesBuffer.array()));

            // 构建完成 原生v2签名apk的字节块

            // 重新计算 原生v2签名块的长度

            int origV2SigBlockSize = origV2sigBlockBytes.array().length;
            LogUtil.e("origV2SigBlockSize = " + origV2SigBlockSize);

            int origV2SigBlockAndRearSize = origV2sigBlockBytes.array().length + 8 + 16;
            LogUtil.e("origV2SigBlockAndRearSize = " + origV2SigBlockAndRearSize);

            ByteBuffer origV2SigBlockAndRearSizeBuffer = ByteBuffer.allocate(8);
            origV2SigBlockAndRearSizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
            origV2SigBlockAndRearSizeBuffer.putInt(origV2SigBlockAndRearSize);
            origV2SigBlockAndRearSizeBuffer.rewind();
            LogUtil.e("wholeSigBlockSizeBuffer = " + ConvertUtil.bytesToHexString(origV2SigBlockAndRearSizeBuffer.array()));

            byte[] sizeInHeadNew = origV2SigBlockAndRearSizeBuffer.array();
            byte[] sizeInFooterNew = origV2SigBlockAndRearSizeBuffer.array();

            // cd的新偏移量的字节
            int cdOffsetNew = sizeInHeadOffset + 8 + origV2SigBlockAndRearSize;
            ByteBuffer cdOffsetNewBuffer = ByteBuffer.allocate(6);
            cdOffsetNewBuffer.order(ByteOrder.LITTLE_ENDIAN);
            cdOffsetNewBuffer.putInt(cdOffsetNew);
            LogUtil.e("cdOffsetNewBuffer = " + ConvertUtil.bytesToHexString(cdOffsetNewBuffer.array()));

            // 原来的eocd的字节
            LogUtil.e("eocdBytesBuffer = " + ConvertUtil.bytesToHexString(eocdBytesBuffer.array()));

            // 将偏移量插入 eocd 的尾部，字节块内 偏移16个字节开始，替换6个字节。原生代码确认一下
            byte[] eocdNocdOffset = BytesOptUtil.getSubBytes(eocdBytesBuffer.array(), 0, eocdBytesBuffer.array().length - 6);
            LogUtil.e("eocdNocdOffset = " + ConvertUtil.bytesToHexString(eocdNocdOffset));

            byte[] eocdNewBytes = BytesOptUtil.mergeBytes(eocdNocdOffset, cdOffsetNewBuffer.array());
            LogUtil.e("eocdNewBytes = " + ConvertUtil.bytesToHexString(eocdNewBytes));

            // 计算新apk的大小
            int origApkSize = coznBytes.array().length
                    + origV2SigBlockAndRearSizeBuffer.array().length
                    + origV2sigBlockBytes.array().length
                    + origV2SigBlockAndRearSizeBuffer.array().length
                    + 16
                    + cdBytes.array().length
                    + eocdNewBytes.length;


            ByteBuffer origApkBytes = ByteBuffer.allocate(origApkSize);

            origApkBytes.put(coznBytes.array())
                    .put(origV2SigBlockAndRearSizeBuffer.array())
                    .put(origV2sigBlockBytes.array())
                    .put(origV2SigBlockAndRearSizeBuffer.array())
                    .put(endByteV2)
                    .put(cdBytes.array())
                    .put(eocdNewBytes);

            byte[] hash = SignerVerifyUtils.calHash(origApkBytes.array(), 0, origApkBytes.array().length);

            LogUtil.e("hash = " + ConvertUtil.bytesToHexString(hash));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("Exception = " + e.toString());
        }

    }


    String ruipak = "ruipay_smartpos_app_v0.0.72_2018-09-10_sign.apk";

    public void test_16(View v) {
        apkPath = AssetsUtils.fileOpt(ruipak, this);
        readApkWithJarFile(apkPath);
    }


    // 参照源码，重新实现签名块的提取
    public void test_17(View v) {
        //gotoVerify();

        String apkPath = AssetsUtils.fileOpt(SmartPhonePos_apk, this);
        try {
            RandomAccessFile apk = new RandomAccessFile(apkPath, "r");

            SignatureInfoForJxnx signatureInfo = findSignatureForJxnx(apk);

            LogUtil.e("ok");

            verifyApkOriHash(signatureInfo);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(" Exception = " + e.toString());
        }

    }

    private void verifyApkOriHash(SignatureInfoForJxnx signatureInfo) {
        ByteBuffer cozn = signatureInfo.cozn;
        byte[] coznBytes = new byte[8];
        cozn.get(coznBytes, 0, 8);
        LogUtil.e("coznBytes " + ConvertUtil.bytesToHexString(coznBytes));

        // v2签名的子块，不带头size
        ByteBuffer sigBlock = signatureInfo.signatureV2Block;

        // 输出头，尾 验证
        byte[] sigBlockBytes = new byte[8];
        sigBlock.get(sigBlockBytes, 0, 8);
        LogUtil.e("sigBlock " + ConvertUtil.bytesToHexString(sigBlockBytes));

        byte[] sigBlockBytes4Last = new byte[8];
        sigBlock.position(sigBlock.capacity() - 8);
        sigBlock.get(sigBlockBytes4Last, 0, 8);
        LogUtil.e("sigBlock 4Last" + ConvertUtil.bytesToHexString(sigBlockBytes4Last));

        // 计算 v2签名块的大小 + 8尾size + 16魔数
        int newSize_InHead_And_Foot = sigBlock.array().length + 8 + 16;
        ByteBuffer newSize_InHead_And_Foot_Buffer = ByteBuffer.allocate(8);
        newSize_InHead_And_Foot_Buffer.order(ByteOrder.LITTLE_ENDIAN);
        newSize_InHead_And_Foot_Buffer.putInt(newSize_InHead_And_Foot);
        newSize_InHead_And_Foot_Buffer.clear();

        //
        ByteBuffer sigJxnxBlock = signatureInfo.signatureJxnxBlock;
        byte[] sigJxnxBlockBytes = new byte[8];
        sigJxnxBlock.get(sigJxnxBlockBytes, 0, 8);
        LogUtil.e("sigJxnxBlock " + ConvertUtil.bytesToHexString(sigJxnxBlockBytes));

        // cd 的 前8字节
        ByteBuffer centralDir = signatureInfo.centralDir;
        byte[] centralDirBytes = new byte[8];
        centralDir.get(centralDirBytes, 0, 8);
        LogUtil.e("centralDir " + ConvertUtil.bytesToHexString(centralDirBytes));

        // eocd 的字节段
        ByteBuffer eocd = signatureInfo.eocd;
        byte[] eocdDirBytes = new byte[8];
        eocd.get(eocdDirBytes, 0, 8);
        LogUtil.e("eocd " + ConvertUtil.bytesToHexString(eocdDirBytes));
        eocd.clear();

        ByteBuffer newEocd = makeNewEocd(signatureInfo);


        ByteBuffer oriApkBytes = ByteBuffer.allocate(
                cozn.array().length
                        + newSize_InHead_And_Foot_Buffer.array().length
                        + sigBlock.array().length
                        + newSize_InHead_And_Foot_Buffer.array().length
                        + endByteV2.length
                        + centralDir.array().length
                        + newEocd.array().length);

        oriApkBytes.order(ByteOrder.LITTLE_ENDIAN);

        oriApkBytes.put(cozn.array())
                .put(newSize_InHead_And_Foot_Buffer.array())
                .put(sigBlock.array())
                .put(newSize_InHead_And_Foot_Buffer.array())
                .put(endByteV2)
                .put(centralDir.array())
                .put(newEocd.array());

        byte[] hash;
        try {
            hash = SignerVerifyUtils.calHash(oriApkBytes.array(), 0, oriApkBytes.array().length);
            LogUtil.e("hash = " + ConvertUtil.bytesToHexString(hash));
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("calHash IOException = " + e.toString());
        }


    }

    private ByteBuffer makeNewEocd(SignatureInfoForJxnx signatureInfo) {
        ByteBuffer oldEocd = signatureInfo.eocd;


        LogUtil.e("11");

        ByteBuffer part1 = getNewByteBuffer(oldEocd, 0, 16);
        ByteBuffer part2 = getNewByteBuffer(oldEocd, 16, 4);
        ByteBuffer part3 = getNewByteBuffer(oldEocd, 20, 2);

        LogUtil.e("22");

        // cd 的 偏移量 = cozn的长度 + 8 + v2签名块的长度 + 8 + 16
        ByteBuffer sigV2Block = signatureInfo.signatureV2Block;
        int sigV2BlockSize = sigV2Block.array().length;

        ByteBuffer cozn = signatureInfo.cozn;
        int coznSize = cozn.array().length;

        int cdNewOffset = coznSize + 8 + sigV2BlockSize + 8 + 16;

        ByteBuffer part2New = ByteBuffer.allocate(4);
        part2New.order(ByteOrder.LITTLE_ENDIAN);
        part2New.putInt(cdNewOffset);
        part2New.clear();

        LogUtil.e("33 " + ConvertUtil.bytesToHexString(part2New.array()));

        ByteBuffer newEocd = ByteBuffer.allocate(oldEocd.array().length);
        newEocd.put(part1.array());
        newEocd.put(part2New.array());
        newEocd.put(part3.array());
        newEocd.clear();

        LogUtil.e("" + ConvertUtil.bytesToHexString(newEocd.array()));

        return newEocd;
    }

    private static class SignatureInfoForJxnx {
        private final ByteBuffer cozn;

        /**
         * （V2 签名块的字节buffer）
         */
        private final ByteBuffer signatureV2Block;

        /**
         * （V2 签名块的偏移量）
         */
        private final long apkSigningV2BlockOffset;

        /**
         * （Jxnx 签名块的字节buffer）
         */
        private final ByteBuffer signatureJxnxBlock;

        /**
         * （Jxnx 签名块的偏移量）
         */
        private final long signatureJxnxBlockOffset;


        /**
         * Position of the ZIP Central Directory in the file.（中央目录的偏移量）
         */
        private final long centralDirOffset;

        private final ByteBuffer centralDir;

        /**
         * Position of the ZIP End of Central Directory (EoCD) in the file. （中央区尾的偏移量）
         */
        private final long eocdOffset;

        /**
         * Contents of ZIP End of Central Directory (EoCD) of the file. （中央区尾的字节buffer）
         */
        private final ByteBuffer eocd;

        private SignatureInfoForJxnx(
                ByteBuffer cozn,
                long apkSigningBlockOffset,
                ByteBuffer signatureBlock,
                long signatureJxnxBlockOffset,
                ByteBuffer signatureJxnxBlock,
                long centralDirOffset,
                ByteBuffer centralDir,
                long eocdOffset,
                ByteBuffer eocd) {
            this.cozn = cozn;
            this.apkSigningV2BlockOffset = apkSigningBlockOffset;
            this.signatureV2Block = signatureBlock;
            this.signatureJxnxBlockOffset = signatureJxnxBlockOffset;
            this.signatureJxnxBlock = signatureJxnxBlock;
            this.centralDirOffset = centralDirOffset;
            this.centralDir = centralDir;
            this.eocdOffset = eocdOffset;
            this.eocd = eocd;

        }
    }


    public void gotoVerify(String apkFile) throws Exception {
        RandomAccessFile apk = new RandomAccessFile(apkFile, "r");
        SignatureInfoForJxnx signatureInfo = findSignatureForJxnx(apk);
        //verify(signatureInfo);
    }

    // 尽量不要用遍历字节数组的方法！！！
    // 通过字节数组转int的方式，获得某一块的size，再结合偏移量，找到每一块的偏移量！！
    // 源码中，都是确定好偏移量，从RandomAccessFile 读取到 ByteBuffer内的

    /**
     * 读取apk的四大块：文件区，签名块，中央区，中央结尾区，放入一个集合
     * @param apk
     * @return
     * @throws Exception
     */
    private SignatureInfoForJxnx findSignatureForJxnx(RandomAccessFile apk) throws Exception {

        // 找到 eocd

        byte[] apkBytes = AssetsUtils.getByteFromAssetsAndCopyToData(SmartPhonePos_apk, this);
        byte[] eocdID = new byte[]{0x50, 0x4b, 0x05, 0x06,};

        long eocdOffset = BytesOptUtil.matchBytesBySelect(apkBytes, eocdID, 1);
        ByteBuffer eocd = BytesOptUtil.getSubBytesBuffer(apkBytes, (int) eocdOffset, apkBytes.length - (int) eocdOffset);

        // 输出验证是否找对
        LogUtil.e("eocd = " + ConvertUtil.bytesToHexString(eocd.array()));

        // 读取 cd的 偏移量
        long cdOffsetInEocd = eocdOffset + 16;
        apk.seek(cdOffsetInEocd);
        ByteBuffer cdOffsetInBytes = ByteBuffer.allocate(4);
        cdOffsetInBytes.order(ByteOrder.LITTLE_ENDIAN);
        apk.readFully(cdOffsetInBytes.array(), cdOffsetInBytes.arrayOffset(), cdOffsetInBytes.capacity());
        cdOffsetInBytes.rewind();
        int cdOffset = cdOffsetInBytes.getInt(0);

        // 输出验证是否找对
        LogUtil.e("cd偏移量的字节 cdOffsetInBytes = " + ConvertUtil.bytesToHexString(cdOffsetInBytes.array()));// 0FDAC300
        LogUtil.e("cd偏移量的数值 cdOffset = " + cdOffset);// 12835343 = 0xc3da0f
        LogUtil.e("对比 apk.length() = " + apk.length());

        // 获取 中央区字节
        long centralDirOffset = cdOffset; // 源码另有逻辑取到值
        ByteBuffer centralDir = getSubBytes(apk, (int) centralDirOffset, (int) eocdOffset - (int) centralDirOffset);

        checkByteBuffer(centralDir);//输出检出
        LogUtil.e("centralDirOffset = " + cdOffset);

        LogUtil.e("33333333");//

        Pair<ByteBuffer, Long> all_SignBlock_And_Offset_InFile =
                findApkSigningBlock(apk, centralDirOffset); //3-1 根据中央区偏移量，找出签名块和其偏移量，构成键值对，一起返回

        ByteBuffer all_SignBlock = all_SignBlock_And_Offset_InFile.first;  // 签名块的字节,整个签名块
        long all_SignBlock_Offset = all_SignBlock_And_Offset_InFile.second; // 签名块的偏移量,包括头size

        LogUtil.e("check all_SignBlock ");
        checkByteBuffer(all_SignBlock);//输出检出 ,810c开头，34 32魔数结尾
        LogUtil.e("all_SignBlock_Offset = " + all_SignBlock_Offset); // 整个签名块的偏移量
        // 12832134 = 0xc3cd86


        // all_SignBlock_Offset 该偏移量，是810c的开头
        LogUtil.e("44444444");//

        // v2 签名子块
        Pair<ByteBuffer, Long> v2SigBlock_HeadSize_And_Offset_InFile
                = findApkSubSignBlock(all_SignBlock_Offset, all_SignBlock, APK_SIGNATURE_SCHEME_V2_BLOCK_ID, true); // 返回的是原声签名块里的ID-Value块


        ByteBuffer v2SigBlock_HeadSize = v2SigBlock_HeadSize_And_Offset_InFile.first;
        Long v2Block_HeadSize_Offset = v2SigBlock_HeadSize_And_Offset_InFile.second;

        LogUtil.e("check v2SigBlock_HeadSize ");
        checkByteBuffer(v2SigBlock_HeadSize);//输出检出
        //head = D0050000
        //rear = 03010001
        LogUtil.e("v2Block_HeadSize_Offset = " + v2Block_HeadSize_Offset);

        LogUtil.e("55555555");

        // jxnx 签名子块
        Pair<ByteBuffer, Long> jxnxSigBlock_And_Offset_InFile = findApkSubSignBlock(all_SignBlock_Offset, all_SignBlock, APK_SIGNATURE_JXNX_BLOCK_ID, false);// 返回的是JXNX签名块里的ID-Value块
        ByteBuffer jxnxSigBlock = jxnxSigBlock_And_Offset_InFile.first;
        long jxnxSigBlock_Offset = jxnxSigBlock_And_Offset_InFile.second;
        LogUtil.e("6");

        LogUtil.e("check jxnxSigBlock ");
        checkByteBuffer(jxnxSigBlock);//输出检出
        //head = D0050000
        //rear = 03010001
        LogUtil.e("jxnxSigBlock_Offset = " + jxnxSigBlock_Offset);

        //head = 13114143
        //rear = 7DC2B325

        // 第一部分的字节
        ByteBuffer cozn = getSubBytes(apk, 0, (int) all_SignBlock_Offset);

        LogUtil.e("check cozn begin");
        checkByteBuffer(cozn);//输出检出
        //504B0304
        //rear = 2B050000
        LogUtil.e("check cozn finish" );


        return new SignatureInfoForJxnx(
                cozn,
                v2Block_HeadSize_Offset,
                v2SigBlock_HeadSize,
                jxnxSigBlock_Offset,
                jxnxSigBlock,// ID-Value块
                centralDirOffset,
                centralDir,
                eocdOffset,
                eocd);

        //return null;
    }



    /**
     * 输出bytebuffer的头位和长度，用于验证
     * @param byteBuffer
     */
    private void checkByteBuffer(ByteBuffer byteBuffer) {
        int pos = byteBuffer.position();
        int limit = byteBuffer.limit();
        int capacity = byteBuffer.capacity();
        LogUtil.e("pos " + pos);
        LogUtil.e("limit " + limit);
        LogUtil.e("capacity " + capacity);

        byte[] all = byteBuffer.array();

        if (capacity > 20) {
            // 输出头尾
            try {
                byte[] head = BytesOptUtil.getSubBytes(all, 0, 4);
                byte[] rear = BytesOptUtil.getSubBytes(all, capacity - 4, 4);

                LogUtil.e("head = " + ConvertUtil.bytesToHexString(head));
                LogUtil.e("rear = " + ConvertUtil.bytesToHexString(rear));

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("checkByteBuffer Exception " + e.toString());
            }

        } else {

            LogUtil.e("all " + ConvertUtil.bytesToHexString(all));

        }

    }

    /**
     * 从文件中提取子字符串，返回byteBuffer
     * @param zip
     * @param Offset
     * @param len
     * @return
     */
    private ByteBuffer getSubBytes(RandomAccessFile zip, int Offset, int len) {

        ByteBuffer buf = ByteBuffer.allocate(len);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        try {
            zip.seek(Offset);
            zip.readFully(buf.array(), buf.arrayOffset(), buf.capacity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        buf.clear();

        return buf;
    }


    private static Pair<ByteBuffer, Long> findApkSigningBlock(
            RandomAccessFile apk, long centralDirOffset)
            throws IOException {

        // 签名块的结构：前块大小 + 签名信息 + 后块大小 + 魔数


        // Read the magic and offset in file from the footer section of the block:
        // * uint64:   size of block
        // * 16 bytes: magic
        ByteBuffer footer = ByteBuffer.allocate(24); // 分配24个字节的空间
        footer.order(ByteOrder.LITTLE_ENDIAN); // 设置为 小端排序
        apk.seek(centralDirOffset - footer.capacity());//移动指针到 （后面的块大小+魔数）的起始位置
        apk.readFully(footer.array()/*获取底层byte[]*/, footer.arrayOffset()/*0*/, footer.capacity()); // 读取（后面的块大小+魔数）到 footer


        // Read and compare size fields
        long apkSigBlockSizeInFooter = footer.getLong(0);// 尾size，前8个字节
        // 该数值，即签名块的长度值


        // apkSigBlockSizeInFooter 该数值包括： 签名整块长度 + 后8字节的块长度 + 魔数长度
        int totalSize = (int) (apkSigBlockSizeInFooter + 8);
        long apkSigBlockOffset = centralDirOffset - totalSize;// 签名整块的偏移量

        ByteBuffer apkSigBlock = ByteBuffer.allocate(totalSize);// 签名整块的字节
        apkSigBlock.order(ByteOrder.LITTLE_ENDIAN);
        apk.seek(apkSigBlockOffset);
        apk.readFully(apkSigBlock.array(), apkSigBlock.arrayOffset(), apkSigBlock.capacity());// 签名整块，读到 apkSigBlock
        long apkSigBlockSizeInHeader = apkSigBlock.getLong(0);


        if (apkSigBlockSizeInFooter == apkSigBlockSizeInHeader) {
            LogUtil.e("头size 和 尾size 相等"); // 这里相等
        }

        ByteBuffer sizeByteBuffer = ByteBuffer.allocate(8);
        sizeByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        sizeByteBuffer.putInt((int) apkSigBlockSizeInFooter);
        LogUtil.e("sizeByteBuffer = " + ConvertUtil.bytesToHexString(sizeByteBuffer.array()));//输出：810C000000000000

        return Pair.create(apkSigBlock/*签名整块*/, apkSigBlockOffset);// 最后构造键值对
    }

    private static final int APK_SIGNATURE_SCHEME_V2_BLOCK_ID = 0x7109871a;
    //private static final int APK_SIGNATURE_JXNX_BLOCK_ID = 0x78676432;
    private static final int APK_SIGNATURE_JXNX_BLOCK_ID = 0x32646778;

    /**
     * 从整个签名块中，读取子签名块，同时返回子签名块在文件中的偏移量
     *
     * @param all_SignBlock_Offset
     * @param all_SignBlock
     * @param sigBlockID
     * @param withHeadAndSize
     * @return
     */
    private Pair<ByteBuffer, Long> findApkSubSignBlock(long all_SignBlock_Offset, ByteBuffer all_SignBlock, int sigBlockID, boolean withHeadAndSize) {
        //checkByteOrderLittleEndian(apkSigningBlock);
        LogUtil.e("1");

        // 入参 apkSigningBlockOffset 是整个签名块（包括头部）在文件中的偏移量

        //ByteBuffer pairs = sliceFromToCus(apkSigningBlock, 8, apkSigningBlock.capacity() - 24);
        ByteBuffer pairs = null;
        try {
            pairs = BytesOptUtil.getSubBytesBuffer(all_SignBlock.array(), 8, all_SignBlock.capacity() - 8/*头size*/ - 24/*尾size，魔数*/);
            pairs.order(ByteOrder.LITTLE_ENDIAN);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("get pairs Exception" + e.toString());
        }

        LogUtil.e("check pairs begin");
        checkByteBuffer(pairs);
        // capacity 3177
        // head = D0050000
        // rear = 7DC2B325
        LogUtil.e("check pairs finish");

        //此时的pairs的结构是 8字节的长度 + 4字节标识 + n字节的签名内容块（也称ID-Value块）

        int entryCount = 0;
        pairs.rewind();
        while (pairs.hasRemaining()) {
            LogUtil.e("2111");
            entryCount++;

            long lenLong = pairs.getLong();// 取8个字节的long数值，position 移动了8位
            // 该值是原生签名块的长度（该长度包括 4个字节的标识 + n个字节的原生签名数据）

            LogUtil.e("2222");

            int len = (int) lenLong;// 获取长度，即原生v2签名块的长度

            int nextEntryPos = pairs.position() + len;// 下一个元素的下标,下一个子签名块

            LogUtil.e("2333");

            int id = pairs.getInt();// 取4个字节的int数值，position 移动了4位
            // 该值是原生签名块的标识

            if (id == sigBlockID/*APK_SIGNATURE_SCHEME_V2_BLOCK_ID*/) { // 根据标识判断是否是 原生v2签名
                LogUtil.e("2");
                if (withHeadAndSize) {
                    LogUtil.e("3");
                    pairs.position(0);
                    ByteBuffer tmp = getNewByteBuffer(pairs, 0, len + 8);// 加上标识

                    LogUtil.e("tmp = " + ConvertUtil.bytesToHexString(tmp.array()));

                    return Pair.create(tmp, all_SignBlock_Offset + 8);/*在文件中的偏移量*/
                } else {
                    LogUtil.e("4");
                    //return getByteBuffer(pairs, len - 4);// 去掉标识，只返回ID-Value块
                    return Pair.create(getNewByteBuffer(pairs, pairs.position(),len - 4),
                            all_SignBlock_Offset + 8 + (long) pairs.position());// 去掉标识，只返回ID-Value块
                }

            }
            pairs.position(nextEntryPos);//定位到下一个元素，下一次循环，处理子签名块
        }

        return null;

    }

    // 从 bytebuffer中截取子bytebuffer
    private ByteBuffer getNewByteBuffer(ByteBuffer source, int offset, int size) {
        byte[] bytes = source.array();
        try {
            byte[] subBytes = BytesOptUtil.getSubBytes(bytes,offset,size);
            ByteBuffer tmp = ByteBuffer.allocate(size);
            tmp.order(ByteOrder.LITTLE_ENDIAN);
            tmp.put(subBytes);
            tmp.rewind();


            return tmp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


}




















package com.guohao.anything.FileOptUtil;


import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;

import com.guohao.anything.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class XmlOptUtil {
    private static final String TAG = "guohao_XmlOptUtil";

    private final static int FILE_NOT_EXIST = -1;
    private final static int UNZIP_FAILED = -2;
    private final static int PARSE_XML_FAILED = -3;
    private static final int UPDATE_SUCCESS = 0;
    private static final int UPDATEING = 1;
    private static final int UPDATE_FAILURE = 2;
    private static final int CHECK_SIGN_FAILURE = 3;
    private static final int TRY_TIMES = 3;
    private static final int UPDATE_NOT_SUPPORT = 4;


    private final static String unzipFilePath = "tpw/";



    String mPath;
    int mType;

    String mTargetTerminal;
    private String fmd;
    String mRomVersion;
    String decryptResult;



    boolean lKLUSDKSPECVersionPass = false;
    boolean targetTerminalPass = false;
    boolean vendorPass = false;
    boolean firmwareManifestPass = false;
    boolean firmwareListPass = false;
    boolean appPass = false;
    boolean versionPass = false;
    boolean emd5Pass = false;
    boolean fireWarePass = false;
    int fileCount;
    boolean isBJI = false;
    int index;
    int failureCount;
    private File jsonFile;
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;
    private String dialogMsg = "";


    List<UpdateFile> fileList = null;

    UpdateFile myFile;

    // 简单的解析xmk文件函数
    public int parseXMLFileSimple(String xmlFile,String imgFile) {
        Log.d(TAG, "parseXMLFile , xmlFile : " + xmlFile);
        int result = PARSE_XML_FAILED;
        InputStream xmlStream = null;
        if (fileList == null) {
            fileList = new ArrayList<UpdateFile>();
        }
        try {
            File source = new File(xmlFile);
            if (source == null || !source.exists()) {
                return PARSE_XML_FAILED;
            }
            xmlStream = new FileInputStream(source);

            XmlPullParser parser = Xml.newPullParser();
            if (parser == null) {
                return PARSE_XML_FAILED;
            }
            parser.setInput(xmlStream, "utf-8");
            int type = parser.getEventType();
            LogUtil.e( "type : " + type);
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        LogUtil.e( "name : " + name);
                        int count = parser.getAttributeCount();
                        LogUtil.e( "count : " + count);
                        for(int i = 0;i<count;i++){
                            String arrt = parser.getAttributeName(i);
                            LogUtil.e( "arrt " + i + " :" + arrt + "=" + parser.getAttributeValue(i));
                        }
                        LogUtil.e( "        ");
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (xmlStream != null) {
                try {
                    xmlStream.close();
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        }
        return result;
    }


    public int parseXMLFile(String xmlFile,String imgFile) {
        Log.d(TAG, "parseXMLFile , xmlFile : " + xmlFile);
        int result = PARSE_XML_FAILED;
        InputStream xmlStream = null;
        if (fileList == null) {
            fileList = new ArrayList<UpdateFile>();
        }
        try {
            File source = new File(xmlFile);
            if (source == null || !source.exists()) {
                return PARSE_XML_FAILED;
            }
            xmlStream = new FileInputStream(source);

            XmlPullParser parser = Xml.newPullParser();
            if (parser == null) {
                return PARSE_XML_FAILED;
            }
            parser.setInput(xmlStream, "utf-8");
            int type = parser.getEventType();
            LogUtil.e( "type : " + type);
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("firmwareManifest".equals(parser.getName())) {
                            LogUtil.e("parse firmwareManifest");
                            String xmlSpecVersion = null;
                            String currentSpecVersion = null;
                            String typeInXML = null;

                            try {
                                xmlSpecVersion = parser.getAttributeValue(1);
                                currentSpecVersion = "";//mSpecVersion
                                typeInXML = parser.getAttributeValue(0);
                            } catch (Exception re) {
                                re.printStackTrace();
                                return UPDATE_NOT_SUPPORT;
                            }

                            LogUtil.e("xmlSpecVersion : " + xmlSpecVersion + " , currentSpecVersion : " + currentSpecVersion + " , typeInXML : " + typeInXML);

//                            if (xmlSpecVersion == null || currentSpecVersion == null || typeInXML == null) {
//                                return UPDATE_NOT_SUPPORT;
//                            }

                            //start add by tuhao,topwise for bug#39801,2017.12.22
//                            if (!""/*mSpecVersion*/.equals(typeInXML) && !""/*DRIVER_UPDATE*/.equals(typeInXML)) {
//                                return UPDATE_NOT_SUPPORT;
//                            }
                            //end add by tuhao,topwise for bug#39801,2017.12.22

//                            if ((typeInXML.equals("Driver") && mType == 0) || (typeInXML.equals("OS") && mType == 1)) {
//                                Log.d(TAG, "typeInXML string is not match");
//                                return UPDATE_NOT_SUPPORT;
//                            }
//
//                            if (!validSpec(currentSpecVersion, xmlSpecVersion)) {
//                                Log.d(TAG, "SPEC Version is illegal");
//                                return UPDATE_NOT_SUPPORT;
//                            }

                            firmwareManifestPass = true;
                        } else if ("TargetTerminal".equals(parser.getName())) {
                            LogUtil.e("parse TargetTerminal");
                            String target = parser.nextText();

//                            if (target == null || mTargetTerminal == null || !mTargetTerminal.equals(target)) {
//                                Log.d(TAG, "TargetTerminal is illegal");
//                                return UPDATE_NOT_SUPPORT;
//                            }

                            LogUtil.e( "TargetTerminal target = " + target);
                            //targetTerminalPass = true;
                        } else if ("Vendor".equals(parser.getName())) {
                            LogUtil.e( "parse Vendor");
                            String vendor = parser.nextText();
                            LogUtil.e( "vendor : " + vendor);

//                            if (vendor == null || !"DZ".equals(vendor)) {
//                                Log.d(TAG, "Vendor is illegal");
//                                return UPDATE_NOT_SUPPORT;
//                            }

                            //Log.d(TAG, "Vendor pass");
                            //vendorPass = true;
                        } else if ("firmwareList".equals(parser.getName())) {
                            LogUtil.e( "parse firmwareList");
                            try {
                                fileCount = Integer.parseInt(parser.getAttributeValue(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                                return UPDATE_NOT_SUPPORT;
                            }
                            LogUtil.e("fileCount = " + fileCount);

//                            if (fileCount <= 0) {
//                                return UPDATE_NOT_SUPPORT;
//                            }

                            //firmwareListPass = true;
                            //Log.d(TAG, "fileCount pass");
                        } else if ("firmware".equals(parser.getName())) {
                            LogUtil.e("parse firmware");
                            String fileName = null;
                            String doneAction = null;
                            String fileType = null;
                            try {
                                fileName = parser.getAttributeValue(0);
                                doneAction = parser.getAttributeValue(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return UPDATE_NOT_SUPPORT;
                            }

                            LogUtil.e("fileName : " + fileName + " , doneAction : " + doneAction);

//                            if (fileName == null || "".equals(fileName)) {
//                                return UPDATE_NOT_SUPPORT;
//                            }
//
//                            if (doneAction == null || (!"reboot".equals(doneAction) && !"none".equals(doneAction))) {
//                                return UPDATE_NOT_SUPPORT;
//                            }
//
//                            if ((fileName.contains(".jar") || fileName.contains(".bin")) && mType == 0) {
//                                Log.d(TAG, "jar and bin file must mType is 1");
//                                return UPDATE_NOT_SUPPORT;
//                            }

                            if (fileName.contains(".jar")) {
                                fileType = "jar";
                            } else if (fileName.contains(".img")) {
                                fileType = "img";
                            } else if (fileName.contains(".bin")) {
                                fileType = "bin";
                            }

                            if (fileName.contains(".jar") || fileName.contains(".img") || fileName.contains(".bin")) {
                                isBJI = true;
                            } else {
                                isBJI = false;
                            }

                            if (fileName.contains(".img") && mType == 1) {
                                Log.d(TAG, "img file must mType is 0");
                                return UPDATE_NOT_SUPPORT;
                            }

                            //File file = new File(/*mPath + */unzipFilePath + fileName);
                            File file = new File(imgFile);
                            LogUtil.e("file : " + file);

                            if (file == null || !file.exists() || file.isDirectory()) {
                                Log.i(TAG, "file not exists or ");
                                if (file != null) {
                                    Log.i(TAG, "file exists : " + file.exists() + " , isDirectory : " + file.isDirectory());
                                } else {
                                    Log.i(TAG, "file is null");
                                }
                                return UPDATE_NOT_SUPPORT;
                            }

                            fmd = getFileMD5(file);
                            myFile = new UpdateFile(fileName, doneAction, fileType);
                            LogUtil.e("fmd : " + fmd + " , fireWarePass : " + fireWarePass);
                            fireWarePass = true;
                        } else if ("APP".equals(parser.getName())) {
                            Log.d(TAG, "parse APP");
                            String fileName = null;
                            String doneAction = null;

                            try {
                                fileName = parser.getAttributeValue(0);
                                doneAction = parser.getAttributeValue(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return UPDATE_NOT_SUPPORT;
                            }

                            LogUtil.e( "fileName : " + fileName + " , doneAction : " + doneAction);

                            if (fileName == null || "".equals(fileName)) {
                                return UPDATE_NOT_SUPPORT;
                            }

                            if (doneAction == null || (!"reboot".equals(doneAction) && !"none".equals(doneAction))) {
                                return UPDATE_NOT_SUPPORT;
                            }

                            if (fileName.contains(".apk") && mType == 0) {
                                LogUtil.e( "apk file must mType 1");
                                return UPDATE_NOT_SUPPORT;
                            }

                            File file = new File(mPath + unzipFilePath + fileName);
                            LogUtil.e("file : " + file);

                            if (file == null || !file.exists() || file.isDirectory()) {
                                return UPDATE_NOT_SUPPORT;
                            }
                            LogUtil.e("APP pass");
                            myFile = new UpdateFile(fileName, doneAction, "apk");
                            appPass = true;
                        } else if ("version".equals(parser.getName())) {
                            LogUtil.e( "parse version");
                            try {
                                String xmlVersion = parser.nextText();
                                String currentVersion = mRomVersion;
                                String intCurVersion = null;
                                String intxmlVersion = null;
                                LogUtil.e( "xmlVersion : " + xmlVersion + " , currentVersion : " + currentVersion);
                                if (myFile != null) {
                                    if (!myFile.getFileType().equals("apk")) {
                                        LogUtil.e("isBJI");
                                        if (xmlVersion != null && currentVersion != null) {
                                            intCurVersion = currentVersion.toLowerCase().replace("v", "").replace(".", "");
                                            intxmlVersion = xmlVersion.toLowerCase().replace("v", "").replace(".", "");
                                            if (Integer.parseInt(intxmlVersion) <= Integer.parseInt(intCurVersion)) {
                                                LogUtil.e("not new version");
                                                return UPDATE_NOT_SUPPORT;
                                            }
                                        }
                                    } else {
                                        LogUtil.e("isAPK");
//                                        if (!checkVersion(myFile.getName())) {
//                                            Log.d(TAG, "not new version");
//                                            return UPDATE_NOT_SUPPORT;
//                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                return UPDATE_NOT_SUPPORT;
                            }

                            LogUtil.e("version pass");
                            if (myFile != null && "apk".equals(myFile.getFileType())) {
                                LogUtil.e("add " + myFile.getName());
                                myFile.setName(mPath + unzipFilePath + myFile.getName());
                                fileList.add(myFile);
                            }
                            versionPass = true;
                        } else if ("EMD5".equals(parser.getName())) {
                            LogUtil.e("parse EMD5");
                            //if (SystemProperties.getBoolean("ro.support.verity.emd5", true))
                            {
                                String emd5 = null;
                                try {
                                    emd5 = parser.nextText();
                                    LogUtil.e("EMD5 : " + emd5);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return CHECK_SIGN_FAILURE;
                                }

                                byte[] m = null;

                                try {
                                    m = Base64.decode(emd5, Base64.DEFAULT);// 解码后
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return CHECK_SIGN_FAILURE;
                                }

//                                try {
//                                    byte[] b = decrypt(getPublicKey(), m);
//                                    if (b == null) {
//                                        LogUtil.e("b is null");
//                                        return CHECK_SIGN_FAILURE;
//                                    }
//                                    decryptResult = new String(b);
//                                    LogUtil.e("decryptResult : " + decryptResult + " , m : " + m + " , b : " + b);
//
//                                    if (decryptResult != null && !"".equals(decryptResult)) {
//                                        if (decryptResult.equals(fmd)) {
//                                            LogUtil.e("verify success");
//                                        } else {
//                                            LogUtil.e("verify failure, not equal");
//                                            return CHECK_SIGN_FAILURE;
//                                        }
//                                    } else {
//                                        Log.d(TAG, "verify failure");
//                                        return CHECK_SIGN_FAILURE;
//                                    }
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    return CHECK_SIGN_FAILURE;
//                                }
                            }
                            LogUtil.e( "add " + myFile.getName());
                            myFile.setName(mPath + unzipFilePath + myFile.getName());
                            fileList.add(myFile);
                            emd5Pass = true;
                            LogUtil.e( "EMD5 pass");
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                type = parser.next();
            }

//            if (fileList == null || fileList.size() == 0 || fileList.size() != fileCount) {
//                result = PARSE_XML_FAILED;
//            } else {
//                Log.d(TAG, "fileCount : " + fileCount + " , list size : " + fileList.size());
//            }

            LogUtil.e( "firmwareManifestPass : " + firmwareManifestPass + " , firmwareListPass : " + firmwareListPass + " , targetTerminalPass : " + targetTerminalPass);

//            if (firmwareManifestPass && firmwareListPass && targetTerminalPass && vendorPass) {
//                if (appPass || versionPass) {
//                    result = PARSE_XML_SUCCESS;
//                } else {
//                    result = PARSE_XML_FAILED;
//                }
//            } else {
//                result = PARSE_XML_FAILED;
//            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (xmlStream != null) {
                try {
                    xmlStream.close();
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        }
        return result;
    }

    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp5Fwk01tlmO92WEbQObx04jRH3kfezsmJgYXNPGbO5fee4Yc6B46p+cXeAklkTiv32HyKVkfTwu0hiUigT5AXosemO7BgK6OUvSKBTuNWfIBCChP8Qp9ADQ9fiFbnvRLzT/N7VJB3nBdcuI2rA8E1C5G+SZ5TNeQMtlNtMjtT9hM++dpqpLzKI4vYQXE7pDktBtBFeXllbdfIsE6bAYZPCRUNFC9x74NR4FxrwiwvIHiDRja3Otaic1mXnYBdjN3W9ShRav7efCWerycB9eytVVLM06j5lFLZiK8OLaZyf3s6M8FbwF5vJUq5lYg7kHFEv0QUjWvXc3/GYhR4uqVrwIDAQAB";



    private PublicKey getPublicKey() {
        X509EncodedKeySpec spec = null;

        try {
            String publicKey = null;

//            if(android.os.TopwiseProp.getDefaultSettingBoolean("default_test_publickey")){
//                publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsznS+EeQFA84GiFxuO8bfnGXPYKxHKJvd6OCgDJ3zIFnRhJ9Uk534VSu7/NEQUj/eILfN/+et0dFHPohON1WuQQWFbxQ2IJpEd1EDtWWufzfIO2ud/TDoWJ2PHpLJzeubq19sKAMHHdjsHMnT3cD7jZOYN8Q1cYgiGfhi/VUOdvIGvQsOSbDv2/h04TeAGB+G/OtRynZjlB+NlXnYywaoLdN0E5YGF3YyxMtub79HY/397V8bM1R583fpVic6AmvNMNGGCUO292IfN0B5LeUgkAM4+z4tnm64DNKCy2j4RqOqtFUxkwo+hcTKOhj8NdWxc9f89OzlX2G+Uu1y9nsBwIDAQAB";
//            }else{
//                publicKey = Settings.Global.getString(getContentResolver(), "lakala_pulic_key");
//            }

            publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsznS+EeQFA84GiFxuO8bfnGXPYKxHKJvd6OCgDJ3zIFnRhJ9Uk534VSu7/NEQUj/eILfN/+et0dFHPohON1WuQQWFbxQ2IJpEd1EDtWWufzfIO2ud/TDoWJ2PHpLJzeubq19sKAMHHdjsHMnT3cD7jZOYN8Q1cYgiGfhi/VUOdvIGvQsOSbDv2/h04TeAGB+G/OtRynZjlB+NlXnYywaoLdN0E5YGF3YyxMtub79HY/397V8bM1R583fpVic6AmvNMNGGCUO292IfN0B5LeUgkAM4+z4tnm64DNKCy2j4RqOqtFUxkwo+hcTKOhj8NdWxc9f89OzlX2G+Uu1y9nsBwIDAQAB";


            Log.d(TAG, "getPublicKey publicKey : " + publicKey);
            if (null == publicKey || "".equals(publicKey) || "null".equals(publicKey)) {
                publicKey = PUBLIC_KEY;
            }
            byte[] raw = publicKey.getBytes();
            byte[] key = MyBase64.decodeBase64(raw);
            spec = new X509EncodedKeySpec(key);
        } catch (Exception e) {
            Log.e(TAG, "getPublicKey->error:" + e);
            return null;
        }


        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        PublicKey publicKey = null;
        try {
            publicKey = factory.generatePublic(spec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
        return publicKey;
    }

    //使用公钥解密
    public static byte[] decrypt(PublicKey publicKey, byte[] cipherData) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            Log.d(TAG, "NoSuchPaddingException");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Log.d(TAG, "解密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.d(TAG, "密文长度非法");
        } catch (BadPaddingException e) {
            e.printStackTrace();
            Log.d(TAG, "密文数据已损坏");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Exception");
        }
        return null;
    }

    public boolean validSpec(String currentVersion, String firewareVersion) {
        String[] currentArray = currentVersion.split("\\.");
        String[] firewareArray = firewareVersion.split("\\.");
        boolean isValid = true;
        try {
            if (currentArray != null && firewareArray != null) {
                int currentLength = currentArray.length;
                int firewareLength = firewareArray.length;
                int compareLength = Math.min(currentLength, firewareLength);
                for (int i = 0; i < compareLength; i++) {
                    int currentNumb = Integer.parseInt(currentArray[i]);
                    int firewareNumb = Integer.parseInt(firewareArray[i]);
                    if (currentNumb < firewareNumb) {
                        isValid = false;
                    }
                }
            } else {
                isValid = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isValid = false;
        }
        return isValid;
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        if (bigInt == null) {
            return null;
        }
        String md5 = bigInt.toString(16);
        Log.d(TAG, "md5 : " + md5);
        if (md5 != null && md5.length() == 31) {
            return "0" + md5;
        } else {
            return md5;
        }
    }

    // 内部类
    public class UpdateFile {
        private String name;
        private String doneAction;
        private String fileType;

        public UpdateFile(String name, String doneAction, String fileType) {
            this.name = name;
            this.doneAction = doneAction;
            this.fileType = fileType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDoneAction() {
            return doneAction;
        }

        public void setDoneAction(String doneAction) {
            this.doneAction = doneAction;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        @Override
        public String toString() {
            return "name : " + name + " , doneAction : " + doneAction + " , fileType : " + fileType;
        }
    }

}

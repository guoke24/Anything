package com.anything.guohao.anything.signerverify;

import android.util.Log;

import com.anything.guohao.anything.ConvertUtil;

public class SignerVerifyUtils {
    // 匹配因子
    static byte[] beginByte = new byte[]{0x13,0x11,0x41,0x43,0x51,0x55,0x49,0x52,0x45,0x52,0x2d,0x53,0x47,0x4e,0x2d,0x49,0x4e,0x46,0x4f};
    static byte[] endByteV1 = new byte[]{0x58,0x47,0x44,0x20,0x53,0x69,0x67,0x20,0x42,0x6C,0x6F,0x63,0x6B,0x20,0x34,0x32};
    static byte[] endByteV2 = new byte[]{0x41,0x50,0x4B,0x20,0x53,0x69,0x67,0x20,0x42,0x6C,0x6F,0x63,0x6B,0x20,0x34,0x32};


    /**
     * 匹配尾巴
     * @param bytes
     */
    public static int pullSignBlockRear(byte[] bytes){
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
                return i;
            }
        }

        return -2;
    }

    /**
     * 匹配开头,加log会多出5秒
     * @param bytes
     */
    public static void pullSignBlockHead(byte[] bytes){
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

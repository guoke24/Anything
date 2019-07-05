package com.anything.guohao.anything.FileOptUtil;

import com.anything.guohao.anything.LogUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 字节操作的工具类
 */
public class BytesOptUtil {


    /**
     * 源字节段中，匹配目标字节，可取第matchSelect次的匹配
     * @param src  源字节
     * @param des  目标字节
     * @param matchSelect 取第几次的匹配
     * @return
     */
    public static int matchBytesBySelect(byte[] src, byte[] des,int matchSelect) throws Exception {
        LogUtil.e("取第" + matchSelect + "次的匹配");
        int srclen = src.length;
        int deslen = des.length;
        int checkIndex = deslen - 1;
        int matchCount = 0;

        if(matchSelect == 0){
            throw new Exception("matchSelect must greater than 0");
        }

        if(des.length > src.length){
            throw new Exception("des.length must less than or equal to src.length");
        }

        //Arrays.equals()比较字节串

        for (int i = srclen - 1; i >= deslen - 1; --i) {//倒序遍历

            if( src[i] == des[checkIndex] ){
                //LogUtil.e("匹配 " + " i = " + i + " checkIndex =  " + checkIndex + " byte =" + ConvertUtil.loopLogSingleByteToHexString(des[checkIndex]) );
                for(int j = checkIndex -1 , ii = i -1 ; j >= 0 ; j--,ii--){
                    if(src[ii] == des[j]){
                        // 匹配则继续
                        //LogUtil.e("匹配 " + " ii = " + ii + " j =  " + j + " byte =" +  ConvertUtil.loopLogSingleByteToHexString(des[j]) );
                    }else{
                        //
                        break;
                    }

                    if(j == 0){
                        // 到这里说明匹配完
                        matchCount++;
                        if(matchCount == matchSelect){
                            LogUtil.e("匹配 " + matchCount + "次,命中 matchSelect：" + matchSelect);
                            return ii;
                        }else{
                            LogUtil.e("匹配 " + matchCount + "次,没有命中 matchSelect：" + matchSelect);
                        }
                    }

                }
            }


        }

        return -2;
    }

    /**
     * 合并两段字节
     * @param bytes1
     * @param bytes2
     * @return
     */
    public static byte[] mergeBytes(byte[] bytes1,byte[] bytes2){
        int comlen = bytes1.length + bytes2.length ;
        byte[] comBytes = new byte[comlen];
        System.arraycopy(bytes1,0,comBytes,0,bytes1.length);
        System.arraycopy(bytes2,0,comBytes,bytes1.length,bytes2.length);
        return comBytes;
    }

    /**
     * 返回子字节串串，根据偏移量和字串
     * @param bytes
     * @param offset
     * @param len
     * @return
     * @throws Exception
     */
    public static byte[] getSubBytes(byte[] bytes , int offset , int len) throws Exception {

        if(bytes.length < offset + len){
            throw new Exception(" bytes.length must greater than (offset + len) ");
        }

        byte[] subBytes = new byte[len];
        System.arraycopy(bytes,offset,subBytes,0,len);
        return subBytes;
    }

    public static ByteBuffer getSubBytesBuffer(byte[] bytes , int offset , int len) throws Exception {

        if(bytes.length < offset + len){
            throw new Exception(" bytes.length must greater than (offset + len) ");
        }

        byte[] subBytes = new byte[len];
        System.arraycopy(bytes,offset,subBytes,0,len);

        ByteBuffer byteBuffer = ByteBuffer.allocate(subBytes.length);
        byteBuffer.put(subBytes);
        byteBuffer.rewind();
        return byteBuffer;
    }

    /**
     * byte字节 转 byteBuffere
     * @param bytes
     * @return
     */
    public static ByteBuffer byteToBuffer(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.rewind();
        return byteBuffer;

    }

    /**
     * 从文件中提取字节
     * @param fileName
     * @param offset
     * @param len
     * @return
     * @throws IOException
     */
    public static ByteBuffer getByteBufferFormFile(String fileName,int offset ,int len) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName,"r");
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        randomAccessFile.seek(offset);
        randomAccessFile.readFully(byteBuffer.array(),0,len);
        byteBuffer.rewind();
        return byteBuffer;
    }

    /**
     * 大小端序转换
     * @param a
     * @return
     */
    public static byte[] changeBytes(byte[] a) {
        byte[] b = new byte[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[b.length - i - 1];
        }
        return b;
    }

}

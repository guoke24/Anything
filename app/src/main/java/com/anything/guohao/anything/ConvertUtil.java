package com.anything.guohao.anything;

import java.io.UnsupportedEncodingException;
import java.util.IllegalFormatCodePointException;

/**
 * Description
 * 转换工具类，负责编码、进制等转换
 *
 * @author yangyi
 * @version 1.0.0
 * @date 17-11-8
 */

public class ConvertUtil {


    private static final String DEFAULT_ENCODE = "GBK";

    /**
     * byte 数组转为十六进制字符串，基于 ASCII 编码转换，十进制转二进制再转十六进制
     *
     * @param srcBytes 待转换 byte 数组
     * @return 转换后的字符串
     */
    public static String bytesToHexString(byte[] srcBytes) {
        if (srcBytes == null) {
            return null;
        }

        //基于 ASCII 转换所以长度是两倍
        StringBuilder result = new StringBuilder(2 * srcBytes.length);

        for (byte aByte : srcBytes) {
            int fourBits;

            fourBits = 0x0f & (aByte >> 4);
            //根据四位二进制对应的十进制数转换成相应的十六进制表示
            result.append("0123456789ABCDEF".charAt(fourBits));

            fourBits = 0x0f & aByte;
            result.append("0123456789ABCDEF".charAt(fourBits));
        }

        return result.toString();
    }

    /**
     * 单个字节 转 16进制的字符
     * @param aByte
     */
    public static String loopLogSingleByteToHexString(byte aByte) {
        int fourBits;
        StringBuilder result = new StringBuilder(2);

        fourBits = 0x0f & (aByte >> 4);
        //根据四位二进制对应的十进制数转换成相应的十六进制表示
        result.append("0123456789ABCDEF".charAt(fourBits));

        fourBits = 0x0f & aByte;
        result.append("0123456789ABCDEF".charAt(fourBits));

        return result.toString();

    }

    /**
     * 大小端序互换
     * @param a
     * @return
     */
    public static byte[] changeBytesOrder(byte[] a) {
        byte[] b = new byte[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[b.length - i - 1];
        }
        return b;
    }

    /**
     * 十六进制字符串转换成 byte 数组，十六进制转二进制再转十进制
     *
     * @param srcHexString 待转换的十六进制字符串
     * @return 转换后的 byte 数组
     */
    public static byte[] hexStringToBytes(String srcHexString) {
        if (srcHexString == null) {
            return null;
        }
        //过滤掉字符串中不正确的字符
        srcHexString = srcHexString.replaceAll("[^0-9a-fA-F]", "");
        int len = (srcHexString.length() / 2);
        byte[] result = new byte[len];
        char[] hexCharArray = srcHexString.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(hexCharArray[pos]) << 4
                    | toByte(hexCharArray[pos + 1]));
        }
        return result;
    }


    /**字符串转byte数组，一个字符占一位
     *
     * @param str 要转换的字符串
     * @return  转换后的byte数组
     */
    public static byte[] StringToByte(String str) {
        int len = str.length();
        byte[] result = new byte[len];
        char[] achar = str.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) achar[i];
        }
        return result;
    }

    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * ASCII 编码转 BCD 编码
     *
     * @param asciiSrcBytes 待转换的 byte 数组
     * @return 转换后用 BCD 编码的 byte 数组
     */
    public static byte[] fromAsciiToBcd(byte[] asciiSrcBytes) {
        if (asciiSrcBytes == null) {
            return null;
        }
        String asciiSrcStr = bytesToString(asciiSrcBytes);
        if ((asciiSrcStr.length() % 2) != 0) {
            asciiSrcStr = '0' + asciiSrcStr;
        }
        byte[] bcdOut = new byte[asciiSrcStr.length() / 2];
        byte highFourBits = 0;
        byte lowFourBits;
        int index = 0;

        try {
            for (byte asciiByte : asciiSrcStr.getBytes(DEFAULT_ENCODE)) {
                byte fourBits;
                if ((asciiByte >= 97) && (asciiByte <= 122)) {
                    //字符a-z
                    fourBits = (byte) (asciiByte - 97 + 10);
                } else if ((asciiByte >= 65) && (asciiByte <= 90)) {
                    //字符A-Z
                    fourBits = (byte) (asciiByte - 65 + 10);
                } else {
                    //数字0-9
                    fourBits = (byte) (asciiByte - 48);
                }
                //每两个字节进行一次合并形成一个 BCD 码
                if (index % 2 != 0) {
                    lowFourBits = fourBits;
                    bcdOut[(index - 1) / 2] = (byte) (highFourBits << 4 | lowFourBits);
                } else {
                    highFourBits = fourBits;
                }
                index++;
            }
        } catch (UnsupportedEncodingException e) {
            LogUtil.e("fromAsciiToBcd: " + e.getMessage());
        }
        return bcdOut;
    }


    /**
     * BCD 编码转 ASCII 编码
     *
     * @param bcdSrcBytes 待转换的 byte 数组
     * @return 转换后用 ASCII 编码字符串
     */
    public static String fromBcdToAscii(byte[] bcdSrcBytes) {
        if (bcdSrcBytes == null) {
            return null;
        }
        //字符串的长度为原来的2倍
        StringBuilder builder = new StringBuilder(bcdSrcBytes.length * 2);
        for (byte bcdByte : bcdSrcBytes) {
            //得到高4位二进制转换后的十进制值
            byte highBitByte = (byte) ((bcdByte & 0xF0) >>> 4);
            //得到低4位二进制转换后的十进制值
            byte lowBitByte = (byte) (bcdByte & 0x0F);
            //根据ASCII码表转成str表示的数字
            builder.append(String.format("%c", highBitByte + 48));
            builder.append(String.format("%c", lowBitByte + 48));
        }
        return builder.toString();
    }

    /**
     * byte 数组 转换成 String
     *
     * @param srcBytes 待转换的 byte 数组
     * @return 转换后的字符串
     */
    public static String bytesToString(byte[] srcBytes) {
        if (srcBytes == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        //String 不可变，还是不要使用 new 创建 String 对象吧
        try {
            for (byte currentByte : srcBytes) {
                builder.append(String.format("%c", currentByte));
            }
        } catch (IllegalFormatCodePointException e) {
            LogUtil.e("bytesToString: " + e.getMessage());
        }
        return builder.toString();
    }

    /**
     * 获取byte数据bit的值
     *
     * @param value byte数据
     * @param index bit位置
     * @return bit的值
     */
    public static int getBitValueFromByte(byte[] value, int index) {
        int length = value.length * 8;
        if (index > length) {
            LogUtil.e("getBitValueFromByte : error index out of length");
            return 0;
        }
        int dataIndex = index / 8;
        int bitIndex = index % 8;
        byte data = value[dataIndex];
        int bitValues = data & (1 << (8 - bitIndex));
        return bitValues == 0 ? 0 : 1;
    }

    /**
     * 字符串指定位置插入字符串
     *
     * @param source 原字符串
     * @param target 插入字符串
     * @param index  插入字符串坐标
     * @return 插入字符串后新的字符串
     */
    public static String insertString(String source, String target, int index) {
        int length = source.length();
        if (index < 0 || index > length) {
            LogUtil.e("insertString() index out of range: " + index);
            return source;
        }
        StringBuilder stringBuilder = new StringBuilder(source);
        stringBuilder.insert(index, target);
        return stringBuilder.toString();
    }

}

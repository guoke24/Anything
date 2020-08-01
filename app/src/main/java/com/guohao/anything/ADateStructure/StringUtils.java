package com.guohao.anything.ADateStructure;

/**
 * 二进制，字节数组，十六进制，这三者的相互转换，之需要如下功能：
 *
 * BnryStr <-> byte[]；
 * byte[] <-> HexStr；
 *
 * 最终就是：
 * BnryStr <-> byte[] <-> HexStr；
 *
 * 而 BnryStr <-> HexStr 不是必须。
 */
public class StringUtils {

    /**
     * byte[] -> 二进制字符串
     *
     * 跟转十六进制的思路一样，利用 位移运算 和 位运算
     *
     * @param bytes 待转换 byte 数组
     * @return 转换后的字符串
     */
    public static String bytesToBnryStr(byte[] bytes){

        if (bytes == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder(bytes.length * 8);//一个字节有8位
        for (byte b:bytes) {
            for(int n = 1;n <= 8;n++){
                stringBuilder.append(getBitN(b,n));
                if( n % 4 == 0 ) stringBuilder.append(" ");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    /**
     * 取一个 byte 中的第 n 位的值
     *
     * @param b 待取值待字节
     * @param n 取值为 1-8，从高到低
     * @return 第 n 位的 int 值
     */
    private static int getBitN(byte b,int n){

        if(n<1||n>8) return -1;

        int bit = 0x1 & (b >> ( 8 - n ));
        return bit;
    }

    /**
     * byte[] -> 十六进制字符串
     *
     * @param srcBytes 待转换 byte 数组
     * @return 转换后的字符串
     */
    public static String bytesToHexStr(byte[] srcBytes) {
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

            result.append(" ");
        }

        result.deleteCharAt(result.length()-1);
        return result.toString();
    }

    /**
     * 十六进制字符串 -> byte[]
     *
     * @param srcHexString 待转换的十六进制字符串
     * @return 转换后的 byte 数组
     */
    public static byte[] hexStrToBytes(String srcHexString) {
        if (srcHexString == null || srcHexString.length() == 0 || srcHexString.contains("[^0-9a-fA-F]")) {
            return null;
        }
        //过滤掉字符串中不正确的字符
        //srcHexString = srcHexString.replaceAll("[^0-9a-fA-F]", "");

        // 如果不是偶数位，则需补一个0
        if(srcHexString.length()%2!=0) srcHexString = "0" + srcHexString;

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

    /**
     * 十六进制字符 -> byte
     *
     * 巧妙的利用了 indexOf 返回下标为 int，
     * 这个 c 在哪个位置，其返回的下标就有多大
     *
     * @param c 一个十六进制字符
     * @return 一个字节 byte，即 c 代表的实际值
     */
    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 二进制字符串 -> byte[]
     * @param bnryStr
     * @return
     */
    public static byte[] bnry2Bytes(String bnryStr){

        if(bnryStr == null || bnryStr.length() == 0 ) return null; // 包含 0、1 以外的字符

        bnryStr = bnryStr.replace(" ","");

        if(bnryStr.contains("[^0-1]")) return null;

        int leftbit;
        StringBuilder leftZore;
        int moreZero = bnryStr.length() % 8;
        if( moreZero != 0) { // 不是 8 位的整数

            leftbit = 8 - moreZero; // 还差 leftbit 个 0
            leftZore = new StringBuilder(leftbit);
            for(int i = 0; i < leftbit; i++){
                leftZore.append(0);
            }
            bnryStr = leftZore.toString() + bnryStr; // 补齐到 8 位的整数
        }

        int bytesNum = bnryStr.length() / 8;

        byte[] bytes = new byte[bytesNum];

        for(int i = 0,k = 0; i < bnryStr.length(); i += 8 , k++){ // 每 8 位，一个 byte

            int powNum = 0;
            int cur8BitVar = 0;
            for(int j = i + 7; j >= i; j-- , powNum++){ // 一个 byte 的 8 位从右到左处理
                char curChar = bnryStr.charAt(j);
                if( curChar == '1' ){
                    cur8BitVar += (int)Math.pow(2,powNum); // 累加 2 的 n 次方
                }

            }

            bytes[k] = (byte) cur8BitVar; // int 转 byte，直接转即可
        }

        return bytes;
    }

    /**
     * 十六进制字符串 -> 二进制字符串
     *
     * @param hexStr
     * @return
     */
    public static String hex2BnryStr(String hexStr){

        if(hexStr == null || hexStr.length() == 0 || hexStr.contains("[^0-9a-fA-F]")) return null;

        if(hexStr.length() % 2 != 0) hexStr = "0" + hexStr;

        hexStr = hexStr.toUpperCase();

        StringBuilder sb = new StringBuilder(hexStr.length() * 4);

        for(int i = 0; i < hexStr.length(); i++){
            char curChar = hexStr.charAt(i);
            int curVal = "0123456789ABCDEF".indexOf(curChar);
            for(int j = 3;j >= 0; j--){
                int bit =  0x1 & (curVal >> j);
                sb.append(bit);
            }
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    /**
     * 二进制字符串 -> 十六进制字符串
     *
     * @param bnryStr
     * @return
     */
    public static String bnry2HexStr(String bnryStr){

        if(bnryStr == null || bnryStr.length() == 0 ) return null; // 包含 0、1 以外的字符

        bnryStr = bnryStr.replace(" ","");

        if(bnryStr.contains("[^0-1]")) return null;

        int leftbit;
        StringBuilder leftZore;
        int moreZero = bnryStr.length() % 8;
        if( moreZero != 0) { // 不是 8 位的整数

            leftbit = 8 - moreZero; // 还差 leftbit 个 0
            leftZore = new StringBuilder(leftbit);
            for(int i = 0; i < leftbit; i++){
                leftZore.append(0);
            }
            bnryStr = leftZore.toString() + bnryStr; // 补齐到 8 位的整数
        }

        StringBuilder sb = new StringBuilder(bnryStr.length()/4);

        for(int i = 0;i < bnryStr.length(); i += 4){

            int pow = 3;
            int cur4BitVar = 0;

            for(int j = i; j < i + 4; j++){
                char curChar = bnryStr.charAt(j);// 0或1
                if(curChar == '1'){
                    cur4BitVar += (int)Math.pow(2,pow);
                }
                pow--;
            }
            // 此处 cur4BitVar 是一个十六进制数的值，但要转换成字符
            char hexChar = "0123456789ABCDEF".charAt(cur4BitVar);
            sb.append(hexChar);
        }

        return sb.toString();
    }

}

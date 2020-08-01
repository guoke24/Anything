package com.guohao.anything.ADateStructure;

import java.nio.charset.Charset;

import static com.guohao.anything.ConvertUtil.bytesToBnryString;
import static com.guohao.anything.ConvertUtil.bytesToHexString;

public class StringTes {

    public static void main(String[] args){
        test5();
    }

    // String 的函数操作返回的字符串时
    // 返回的都是一个新的字符串
    public static void test1(){
        String s1 = "1231";
        String s2 = "123";

        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());

        String s3 = s1.substring(1); // 调用 native 函数 fastSubstring()
        System.out.println(s3);

        String s4 = s1.replace("1","s");// 两个下标记录匹配下标，填字符到 StringBuilder
        // 内部持有到数据结构是 new char[capacity]
        // append 函数内部调用 System.arraycopy 函数来拷贝字符串
        System.out.println(s1);
        System.out.println(s4);

    }

    // 测试 final 修饰的类型
    // 它的实例的成员变量能够被修改吗？
    // 结果：可以的！
    // 除非加上 final 修饰其成员变量
    public static void test2(){
        User user = new User();
        user.age = 1;
        user.name = "hahaha";

        System.out.println(user.age + user.name);

        user.age = 2;
        user.name = "vvvv";

        System.out.println(user.age + user.name);

        User user1 = new User();
        user = user1;
        System.out.println(user.age + user.name);
    }

    // char 占用的内存大小
    public static void test3(){

    }

    // 编码测试
    public static void test4(){
        byte[] bytes = "严".getBytes();
        // 打印十进制
        prtDecimal(bytes);
        // 打印十六进制
        System.out.println(bytesToHexString(bytes));
        // 打印二进制
        System.out.println(bytesToBnryString("严".getBytes()));
        // "严" 的 Unicode 编码是 4E25
        // "严".getBytes()，默认是 utf-8 编码格式，E4B8A5
        // 在这个类 StandardCharsets 中，有6中编码格式

    }

    // 循环的打印一个字节数组的每个字节的值，十进制
    private static void prtDecimal(byte[] bytes){
        System.out.println("十进制：");
        for (byte b:bytes) {
            System.out.println(b);
        }
    }

    // 进制转化测试
    public static void test5(){

        byte[] bytes = "严".getBytes();// E4B8A5

        System.out.println(StringUtils.bytesToHexStr(bytes));
        System.out.println(StringUtils.bytesToBnryStr(bytes));
        System.out.println(" ");

        System.out.println(StringUtils.bytesToBnryStr(StringUtils.bnry2Bytes("1010 1110")));
        System.out.println(StringUtils.bytesToHexStr(StringUtils.hexStrToBytes("E4B8A5")));
        System.out.println(" ");

        System.out.println(StringUtils.bnry2HexStr("1010 1110"));
        System.out.println(StringUtils.hex2BnryStr("E4B8A5"));


    }

}

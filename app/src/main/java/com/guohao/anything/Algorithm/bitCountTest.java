package com.guohao.anything.Algorithm;

public class bitCountTest {

    public static void main(String[] args){
        testAnd();
        testOr();
        testXor();
    }

    // 与运算，and
    // 算法原则：全1才1
    private static void testAnd(){
        int a = 5;       // 101
        int b = 6;       // 110
        int tmp = a & b; // 100 = 4
        System.out.println(tmp);
    }

    // 或运算，or
    // 算法原则：有1就1
    private static void testOr(){
        int a = 5;       // 101
        int b = 6;       // 110
        int tmp = a | b; // 111 = 7
        System.out.println(tmp);
    }

    // 异或运算 xclusive or，简称 xor
    // 算法原则：相异才或
    private static void testXor(){
        int a = 5;       // 101
        int b = 6;       // 110
        int tmp = a ^ b; // 011 = 3
        System.out.println(tmp);
    }

    // ---

    // 位移运算
    // << , <<< , >>> , >>>


    // 补码，反码，原码


    // 基数类型数据的边界值

}

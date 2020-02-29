package com.guohao.anything.viewtest;

/**
 * 如类名，不需要安装到模拟器，有 main 函数，直接运行
 */
public class RunWithoutUI {

    public static void main(String[] args){

        test_MeasureSpace();

    }

    private static void test_MeasureSpace(){

        int count_bit = 3;
        System.out.println("1 = " + Integer.toBinaryString(1));
        // 1

        int a = 1 << count_bit; // 左移 3 位

        System.out.println("a = " + Integer.toBinaryString(a));
        // 1000


        System.out.println("1 = " + Integer.toBinaryString(-1));
        // 11111111111111111111111111111111
        // int 类型的负数，非数值的高位全为1

        int b = -1 << count_bit; // 左移 3 位，即左边去掉 3 位，右边补 3 个 0。
        System.out.println("b = " + Integer.toBinaryString(b));
        // 11111111111111111111111111111000



    }

    private static void test_FLAG_DISALLOW_INTERCEPT(){

        // View 事件分发流程中，FLAG_DISALLOW_INTERCEPT 变量的位运算
        int FLAG_DISALLOW_INTERCEPT = 0x80000; // 0x10000 * 8
        int FLAG_DISALLOW_INTERCEPT3 = 16 * 16 * 16 * 16 * 8;// 能化成2的n次方的，其二进制数就是一个1和n个0组成
        // 4 + 4 + 4 + 4 + 3 = 19（个0）
        // 即 mGroupFlags 第二十位的值，为 FLAG_DISALLOW_INTERCEPT 的值
        // 0x80000 = 1000 0000 0000 0000 0000，一位十六进制数 = 四位二进制数

        System.out.println("" + FLAG_DISALLOW_INTERCEPT);
        System.out.println("" + FLAG_DISALLOW_INTERCEPT3);
        System.out.println("" + ~FLAG_DISALLOW_INTERCEPT3);

        int mGroupFlags = 524288;// 0x80000
        // 重置 FLAG_DISALLOW_INTERCEPT 标识位的时候，执行：
        mGroupFlags &= ~FLAG_DISALLOW_INTERCEPT;
        // 相当于 mGroupFlags = mGroupFlags & (~FLAG_DISALLOW_INTERCEPT);
        // 位运算中重置的方式，标志位全部取反，mGroupFlags 再与其做与运算，
        // 可以确保 mGroupFlags 中只有第20位的数为置为0，不影响其他位的指
        // 因为 mGroupFlags 的每一位的数字都对应着一种标志位的值

        System.out.println("" + mGroupFlags);
    }

}

package com.guohao.anything.viewtest;

/**
 * 如类名，不需要安装到模拟器，有 main 函数，直接运行
 */
public class RunWithoutUI {

    public static void main(String[] args){

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

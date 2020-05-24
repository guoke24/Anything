package com.LagouAndroidShare.class2;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * 测试新生代、老年代的分配和清理策略
 *
 * VM agrs: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails
 * -XX:SurvivorRatio=8 -XX:+UseSerialGC
 *
 * 终端执行：java -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+UseSerialGC MinorGCTest
 */

public class MinorGCTest {
    private static final int _1MB = 1024 * 1024;

    public static void testAllocation() {
        byte[] allocation1, allocation2, allocation3, allocation4;
        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];// eden 中 8M 的空间累计了 6M 的对象
        allocation4 = new byte[4 * _1MB];// 此时再给该实例分配内存之前会触发GC
    }


    public static void main1(String[] agrs) {
        testAllocation();
    }

    public static void printMemory() {
        System.out.print("free is " + Runtime.getRuntime().freeMemory() + " B, ");
        System.out.println("total is " + Runtime.getRuntime().totalMemory() + " B, ");
    }
}

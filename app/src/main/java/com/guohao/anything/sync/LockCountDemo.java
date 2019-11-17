package com.guohao.anything.sync;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 该demo的目的是：sum函数要在add执行两次之后才会被唤醒，然后计算总和
 */
public class LockCountDemo {
    public int start = 10;
    public int middle = 90;
    public int end = 200;

    public volatile int tmpAns1 = 0;
    public volatile int tmpAns2 = 0;

    public Lock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();
    public AtomicInteger count = new AtomicInteger(0);


    public int add(int i, int j,String threadName) {

        try {
            System.out.println(threadName + " 尝试获得锁");
            lock.lock();
            System.out.println(threadName + " 成功获得锁");
            System.out.println(threadName + " add 函数开始");
            int sum = 0;
            for (int tmp = i; tmp < j; tmp++) {
                sum += tmp;
            }
            return sum;
        } finally {
            atomic(threadName);
            System.out.println(threadName + " add 函数结束 释放锁");
            lock.unlock();
        }
    }


    public int sum(String threadName) throws InterruptedException {
        System.out.println(threadName + " sum 函数开始");
        try {
            lock.lock();
            condition.await();
            System.out.println(threadName + " after 被唤醒");
            return tmpAns1 + tmpAns2;
        } finally {
            lock.unlock();
            System.out.println(threadName + " sum 函数结束");
        }
    }

    public void atomic(String threadName) {
        int x = count.addAndGet(1);
        System.out.println(threadName + " 尝试唤醒 atomic x = " + x);
        if (2 == x) {
            condition.signal();
            System.out.println(threadName + " 唤醒");
        }
    }

}

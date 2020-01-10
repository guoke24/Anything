package com.guohao.anything.sync;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试：累加次数唤醒
 *
 * 该demo的具体做法：sum 函数要在 add 执行两次之后才会被唤醒，然后计算总和
 * add 函数将要被 放到两个线程执行，并得到 tmpAns1 和 tmpAns2
 * sum 函数要等待 两个线程的 add 函数执行完分别唤醒一次，累计唤醒两次，才会被唤醒，并计算 tmpAns1 + tmpAns2
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


    /**
     * 累计 从 i 累加到 j 的和
     * 执行完调用 atomic 进行一次累加
     *
     * @param i
     * @param j
     * @param threadName
     * @return
     */
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


    /**
     * 一开始执行就阻塞，等待 condition 这个条件对象的唤醒
     *
     * @param threadName
     * @return
     * @throws InterruptedException
     */
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

    /**
     * 累计两次，再唤醒等待 condition 这个条件对象的线程
     *
     * @param threadName
     */
    public void atomic(String threadName) {
        int x = count.addAndGet(1);
        System.out.println(threadName + " 尝试唤醒 atomic x = " + x);
        if (2 == x) {
            condition.signal();
            System.out.println(threadName + " 唤醒");
        }
    }

}

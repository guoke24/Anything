package com.guohao.anything.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 关于 ReentrantLock 的使用
 * 参考：
 * [Java并发学习之ReentrantLock的工作原理及使用姿势](https://cloud.tencent.com/developer/article/1038499)
 *
 * 本demo内容概括：简单的上锁和解锁的例子，确保只有一个人上班
 */
public class LockDemo {

    private Lock lock = new ReentrantLock();

    private void workOn() {
        System.out.println(Thread.currentThread().getName() + ":上班!");

    }

    private void workOff() {
        System.out.println(Thread.currentThread().getName() + ":下班");
        System.out.println("            ");
    }


    public void work() {
        try {
            lock.lock();
            workOn();
            System.out.println(Thread.currentThread().getName()
                    + "工作中!!!!");
            Thread.sleep(100);
            workOff();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void execTest(){
        LockDemo lockDemo = new LockDemo();

        int i = 0;
        List<Thread> list = new ArrayList<>(30);
        do {
            Thread a = new Thread(new Runnable() {
                @Override
                public void run() {
                    lockDemo.work();
                }
            }, "小A_" + i);

            Thread b = new Thread(new Runnable() {
                @Override
                public void run() {
                    lockDemo.work();
                }
            }, "小B_" + i);


            list.add(a);
            list.add(b);
        } while (i++ < 10);//循环新建10对线程，放入list

        list.parallelStream().forEach(Thread::start);//执行list内的线程

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main over!");
    }
}

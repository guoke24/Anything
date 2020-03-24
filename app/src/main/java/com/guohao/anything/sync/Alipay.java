package com.guohao.anything.sync;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 参考：刘望舒的《Android进阶之光，P173，4.2小节》
 *
 * 当多个线程，同时调用 Alipay 的同一个实例的 transfer 函数时候，
 * 会遇到 数据交叉混乱的 问题，这个时候就需要给 transfer 函数加一把锁，
 * 保证同一个时刻，只能有一个线程在执行 Alipay 实例的 transfer 函数，其他尝试执行 transfer 函数的线程，
 * 会因为尝试取得 Alipay 实例内的锁，而被放入等待集，
 * 只有持有锁的那个线程执行完 transfer 函数，释放锁并唤醒等待集的线程之后，
 * 等待集的线程之中又会有一个线程可以抢到锁，从而重复上述的流程：上锁 -- 执行任务 -- 唤醒其他线程 -- 释放锁
 *
 * 关于锁的重要概念之一，就是：多个线程，竞争同一个实例的锁，这个锁才有意义。
 *
 * 本 Demo 的内容概括：
 * 采用了三种方法，实现同步操作：
 * 1，ReentrantLock
 * 2，同步函数
 * 3，同步代码块的方式
 *
 */
public class Alipay {
    private double[] accounts;
    private Lock alipaylock;
    private Condition condition;

    public Alipay(int n, double money) {
        accounts = new double[n];
        alipaylock = new ReentrantLock();
        // 得到条件锁对象
        condition = alipaylock.newCondition();
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = money;
        }
    }

    // 依赖 ReentrantLock 实现原子性
    public void transfer(int from, int to, int amount) throws InterruptedException {
        alipaylock.lock();// 竞争锁
        // 若没争到锁，则当前线程 blocked ，等锁unlock之后再去竞争
        // 若争到锁，则继续执行...

        try {
            while (accounts[from] < amount) {
                // 释放锁，线程进入 wait 状态
                condition.await();
                // 内部会调用 unlock 函数,此函数会抛出 InterruptedException 异常
                // 当阻塞方法收到中断请求的时候就会抛出InterruptedException异常
                // 一个中断的例子就是，杀毒软件正在全盘查杀病毒，此时我们不想让他杀毒，
                // 这时候点击取消，那么就是正在中断一个运行的线程。
                // 关于 InterruptedException 和线程阻塞的更多内容，
                // 参考：https://www.jianshu.com/p/a8abe097d4ed
            }

            // 转账的操作
            accounts[from] = accounts[from] - amount;
            accounts[to] = accounts[to] + amount;

            // 通知 wait 状态的线程，进入 blocked 状态，蓄势待发竞争准备释放的锁
            condition.signalAll();

        } finally {
            alipaylock.unlock();// 释放锁
        }

    }

    // 依赖 同步函数，实现原子性
    // 会简化代码，效果等同于上述同名函数
    // 同步函数，synchronized 关键字锁住的是当前 Alipay 类实例的对象锁
    // 所在直接调用 wait，notify 等函数
    public synchronized void transfer(int from, int to, int amount,int nothing) throws InterruptedException {
        //alipaylock.lock();//进入函数后会自动上锁

        //try {
            while (accounts[from] < amount) {
                // 阻塞当前线程，并放弃锁
                //condition.await();// 内部会调用 unlock 函数
                // 替换为：
                wait();
            }

            // 转账的操作
            accounts[from] = accounts[from] - amount;
            accounts[to] = accounts[to] + amount;

            //condition.signalAll();
            // 替换为：
            notifyAll();

        //} finally {
        //    alipaylock.unlock();//退出函数时会自动解锁
        //}


    }

    // 依赖 同步代码块，实现原子性
    // 此时锁住的是 Alipay 类实例的成员变量 sth 的对象锁
    private Object sth = new Object();
    public void transfer(int from, int to, int amount,int nothing,int nothing2) throws InterruptedException {

        synchronized (sth){
            while (accounts[from] < amount) {
                // 阻塞当前线程，并放弃锁
                //condition.await();// 内部会调用 unlock 函数
                // 替换为：
                sth.wait();
            }
            // 转账的操作
            accounts[from] = accounts[from] - amount;
            accounts[to] = accounts[to] + amount;
            sth.notifyAll();
        }

    }

}

package com.anything.guohao.anything.sync;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    public void transfer(int from, int to, int amount) throws InterruptedException {
        alipaylock.lock();// 阻塞当前线程，不放弃锁，等锁unlock之后再去竞争
        try {
            while (accounts[from] < amount) {
                // 阻塞当前线程，并放弃锁
                condition.await();// 内部会调用 unlock 函数,此函数会抛出 InterruptedException 异常
                // 当阻塞方法收到中断请求的时候就会抛出InterruptedException异常
                // 一个中断的例子就是，杀毒软件正在全盘查杀病毒，此时我们不想让他杀毒，
                // 这时候点击取消，那么就是正在中断一个运行的线程。
                // 关于 InterruptedException 和线程阻塞的更多内容，
                // 参考：https://www.jianshu.com/p/a8abe097d4ed
            }

            // 转账的操作
            accounts[from] = accounts[from] - amount;
            accounts[to] = accounts[to] + amount;
            condition.signalAll();

        } finally {
            alipaylock.unlock();
        }

    }

    // 同步方法：
    // 使用 synchronized 关键字修饰函数，会简化代码，效果等同于上述同名函数
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

    //还有一种写法，同步代码块
    private Object sth = new Object();
    public void transfer(int from, int to, int amount,int nothing,int nothing2) throws InterruptedException {

        synchronized (sth){
            while (accounts[from] < amount) {
                // 阻塞当前线程，并放弃锁
                //condition.await();// 内部会调用 unlock 函数
                // 替换为：
                wait();
            }
            // 转账的操作
            accounts[from] = accounts[from] - amount;
            accounts[to] = accounts[to] + amount;
        }

    }

}

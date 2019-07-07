package com.anything.guohao.anything.sync;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Alipay {
    private double[] accounts;
    private Lock alipaylock;
    private Condition condition;
    public Alipay(int n,double money){
        accounts = new double[n];
        alipaylock = new ReentrantLock();
        // 得到条件锁对象
        condition = alipaylock.newCondition();
        for(int i = 0;i<accounts.length;i++){
            accounts[i] = money;
        }
    }

    public void transfer(int from,int to,int amount) throws InterruptedException{
        alipaylock.lock();// 阻塞当前线程，不放弃锁，等锁unlock之后再去竞争
        try{
            while(accounts[from] < amount){
                // 阻塞当前线程，并放弃锁
                condition.await();// 内部会调用 unlock 函数
            }

            // 转账的操作
            accounts[from] = accounts[from] - amount ;
            accounts[to] = accounts[to] + amount;
            condition.signalAll();

        }finally {
            alipaylock.unlock();
        }

    }

}

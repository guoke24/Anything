package com.guohao.anything.sync;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

}

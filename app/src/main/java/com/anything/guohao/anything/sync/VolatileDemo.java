package com.anything.guohao.anything.sync;

import com.anything.guohao.anything.LogUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 参考：https://www.jb51.net/article/128240.htm
//
public class VolatileDemo {

    boolean flag = true;


    // 指令重排序,导致 t1 有一定的小概率无法停止
    public void test(){


        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while(flag){ // 有一定的小概率无法停止
                    LogUtil.e("i = " + i);
                    i++;
                    try {
                        Thread.sleep(10);//此处休眠是各自线程的休眠
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t.start();

        // 该写法等同于等价于上面的
        Thread t3 = new Thread(()->{
            try {
                Thread.sleep(3000);
                LogUtil.e("flag = false ");
                flag = false;

                while(!flag){
                    // 不让线程结束
                    Thread.sleep(1000);
                    LogUtil.e(" in while ");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t3.start();

    }


    // volatile 不能保证原子性
    static class Test {
        public volatile int inc = 0;

        public void increase() {
            inc++;
        }

        public void exec() {
            final Test test = new Test();
            for(int i=0;i<10;i++){ // 新建10个线程
                new Thread(){
                    public void run() {
                        for(int j=0;j<1000;j++)
                            test.increase();
                    };
                }.start();
            }

            while(Thread.activeCount()>2) //有子线程就让出资源，保证所有子线程都执行完
                Thread.yield();//当前线程，让出cpu，进入就绪状态
            LogUtil.e("" + test.inc); // 结果小于10000
        }
    }

    // synchronized 可以保证原子性
    static class Test2 {
        public int inc = 0;

        public synchronized void increase() {
            inc++;
        }

        public  void exec() {
            final Test2 test = new Test2();
            for(int i=0;i<10;i++){
                new Thread(){
                    public void run() {
                        for(int j=0;j<1000;j++)
                            test.increase();
                    };
                }.start();
            }

            while(Thread.activeCount()>2) //有子线程就让出资源，保证所有子线程都执行完
                Thread.yield();
            LogUtil.e("" + test.inc); // 结果=10000
        }
    }

    // Lock 实现，也可以保证原子性
    static class Test3 {
        public int inc = 0;
        Lock lock = new ReentrantLock();

        public void increase() {
            lock.lock();
            try {
                inc++;
            } finally{
                lock.unlock();
            }
        }

        public void exec() {
            final Test3 test = new Test3();
            for(int i=0;i<10;i++){
                new Thread(){
                    public void run() {
                        for(int j=0;j<1000;j++)
                            test.increase();
                    };
                }.start();
            }

            while(Thread.activeCount()>2)
                Thread.yield();
            LogUtil.e("" + test.inc); // 结果=10000
        }
    }

    static class Test4 {
        // 该类带有很多原子性操作
        public AtomicInteger inc = new AtomicInteger();

        public void increase() {
            inc.getAndIncrement();//具有原子性
        }

        public void exec() {
            final Test4 test = new Test4();
            for(int i=0;i<10;i++){
                new Thread(){
                    public void run() {
                        for(int j=0;j<1000;j++)
                            test.increase();
                    };
                }.start();
            }

            while(Thread.activeCount()>2)
                Thread.yield();
            LogUtil.e("" + test.inc); // 结果=10000
        }
    }

}

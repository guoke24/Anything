package com.guohao.anything.sync;

// 参考：无...
/**
 * 本demo内容概括：
 *
 * 测试线程所有状态的转换，锁的竞争，获取，释放，唤醒等操作
 *
 * 测试线程的中断
 *
 */
public class ObjectForLock {

    Object lock = new Object();

    /**
     * 测试线程状态的转换
     *
     * 通过两个线程，t0先获得锁，在释放锁，进入 wait 队列；
     * By object # wait 函数
     *
     * t1 再获得锁，并在释放锁前，通知 wait 该对象锁的线程 t0 进入 block 队列；
     * By object # notify 函数
     *
     * t0，t1 都执行完，再执行 mainThread；
     * By Thread # join 函数
     */
    public void testThreadState() {

        Thread mainThread = Thread.currentThread();

        Thread t0 = new Thread(new Runnable() {
            @Override
            public void run() {

                // lock.wait(); 会报错
                // 因为还没获得 lock 的对象锁

                synchronized (lock) { // 获得 objectForLock 的对象锁

                    try {
                        System.out.println(Thread.currentThread().getName() + ": 我拿到锁了...");

                        System.out.println(Thread.currentThread().getName() + ": 睡眠 1 秒，让出 cpu...");

                        Thread.sleep(2000); // 休眠2秒，让出cpu，没让出锁

                        System.out.println(Thread.currentThread().getName() + ": 我释放锁了...");

                        lock.wait();// 释放锁，进入 wait 状态，等待别的线程调用 objectForLock.notify();

                        // 当有别的线程调用 lock.notify()，
                        // 才有可能执行到这里
                        // 或者 wait 的 3 秒时间到了，其他线程释放了锁，本线程竞争到了锁，也能进来
                        // lock.wait(3000);

                        System.out.println(Thread.currentThread().getName() + ": 我又拿到了锁，进入 RUNNABLE 状态 ...");
                        System.out.println(Thread.currentThread().getName() + ": 我的状态 = " + Thread.currentThread().getState().name());


                    } catch (InterruptedException e) {// 捕捉 sleep 和 wait 时被中断的异常
                        e.printStackTrace();
                    } finally {
                        System.out.println(Thread.currentThread().getName() + ": 线程结束，我要释放锁了...");
                        System.out.println(Thread.currentThread().getName() + ": mainThread 的状态 = " + mainThread.getState().name());
                    }

                }
            }
        });

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    System.out.println(Thread.currentThread().getName() + ": t0 没释放锁了，所以我即将进入 BLOCKED ...");
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (lock) { // 获得 objectForLock 的对象锁
                    try {

                        System.out.println(Thread.currentThread().getName() + ": 我拿到锁了...");


                        System.out.println(Thread.currentThread().getName() + ": 睡眠 1 秒，让出 cpu...");

                        Thread.sleep(1000); // 模拟执行任务

                    } catch (InterruptedException e) { // 捕捉 sleep 时被中断的异常
                        e.printStackTrace();
                    } finally {

                        System.out.println(Thread.currentThread().getName() + ": t0 的状态 = " + t0.getState().name());

                        System.out.println(Thread.currentThread().getName() + ": notify...");
                        lock.notify();

                        System.out.println(Thread.currentThread().getName() + ": t0 的状态 = " + t0.getState().name());


                        System.out.println(Thread.currentThread().getName() + ": 线程结束，我要释放锁了...");
                        System.out.println(Thread.currentThread().getName() + ": mainThread 的状态 = " + mainThread.getState().name());
                    }
                } // end synchronized
                // synchronized 之外调用 notify 会报错，因为释放了锁
                //
                // lock.notify();

            }
        });

        System.out.println(Thread.currentThread().getName() + ": t0 的状态 = " + t0.getState().name());
        System.out.println(Thread.currentThread().getName() + ": t1 的状态 = " + t1.getState().name());

        System.out.println(Thread.currentThread().getName() + "启动 t0，t1 ...");

        t0.start();
        t1.start();

        try {
            System.out.println(Thread.currentThread().getName() + "挂起，进入 WAIT 状态，等待 t0，t1 执行完再通知我...");
            t0.join();
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + ": t0，t1 执行完毕，轮到我了...");
        System.out.println(Thread.currentThread().getName() + ": t0 的状态 = " + t0.getState().name());
        System.out.println(Thread.currentThread().getName() + ": t1 的状态 = " + t1.getState().name());

    }


    /**
     * 测试线程的中断
     *
     * 第一种是在有 sleep 的时候，在捕获异常的时候修改自定义的 boolean 标志位
     *
     * 第二种是借助 Thread.currentThread().isInterrupted() 函数实现
     *
     * 这两种可以结合起来用
     *
     * 还有一种，声明一个 Runnable 的子类，跟第一种类似，boolean 标志位用 volatile 修饰，保证可见性
     *
     */
    public void testThreadInterrupt() {


        /**
         * 有 sleep 的中断
         */
        Thread t0 = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean  iscatch = false;

                for(;;){
                    try {

                        if(iscatch) {
                            System.out.println(Thread.currentThread().getName() + ": 有抛出异常，跳出循环");
                            break;
                        }

                        System.out.println(Thread.currentThread().getName() + ": 循环中，中断标志 = "
                                + Thread.currentThread().isInterrupted());

                        Thread.sleep(1000);

                    }
                    catch (InterruptedException e) {
                        //e.printStackTrace();

                        // 捕获异常后，中断标志位已经被复位，置为 false
                        System.out.println(Thread.currentThread().getName() + ": 捕获 InterruptedException，中断标志已经被复位 = "
                                + Thread.currentThread().isInterrupted());

                        // 捕获异常后，会回到 try 中继续执行，循环会继续
                        iscatch = true;
                    }
                }
            }
        });

        /**
         * 没有 sleep 的中断
         */
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    System.out.println(Thread.currentThread().getName() + ": 循环中，中断标志 = "
                            + Thread.currentThread().isInterrupted());// false
                }

                System.out.println(Thread.currentThread().getName() + ": 已跳出循环，中断标志 = "
                        + Thread.currentThread().isInterrupted());// true

                System.out.println(Thread.currentThread().getName() + ": 执行复位中断标志 = "
                        + Thread.interrupted());// true

                System.out.println(Thread.currentThread().getName() + ": 复位中断标志后，中断标志 = "
                        + Thread.currentThread().isInterrupted());// false

            }
        });


        /**
         * 还有一种安全中断的方法，声明一个 Runnable 的子类
         *
         */
        CusRunnable cusRunnable = new CusRunnable();
        Thread t2 = new Thread(cusRunnable,"cusRunnable");

        //t0.start();
        //t1.start();
        t2.start();

        try {
            Thread.sleep(3000);
            //t0.interrupt();
            //t1.interrupt();
            cusRunnable.cancle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 还有一种安全中断的方法，声明一个 Runnable 的子类
     *
     */
    class CusRunnable implements Runnable{
        private long i = 0;
        private volatile boolean on = true;

        @Override
        public void run() {
            while(on){
                i++;
                System.out.println(i);
            }
            System.out.println("stop");

        }

        public void cancle(){
            on = false;
        }
    }
    // 测试线程的中断 end
}

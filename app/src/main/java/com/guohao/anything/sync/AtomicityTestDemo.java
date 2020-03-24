package com.guohao.anything.sync;

import com.guohao.anything.LogUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 参考：[Java中Volatile关键字详解及代码示例](https://www.jb51.net/article/128240.htm)
// 参考：刘望舒的《Android进阶之光，P180，4.2.2小节》

/**
 * 先补充一下概念：Java内存模型中的可见性、原子性和有序性。
 *
 * 可见性：
 * 可见性，是指线程之间的可见性，一个线程修改的状态对另一个线程是可见的。也就是一个线程修改的结果。另一个线程马上就能看到。
 * 比如：用 volatile 修饰的变量，就会具有可见性。volatile 修饰的变量不允许线程内部缓存和重排序，即直接修改内存。
 * 所以对其他线程是可见的。但是这里需要注意一个问题，volatile 只能让被他修饰内容具有可见性，但不能保证它具有原子性。
 * 比如 volatileinta = 0；之后有一个操作 a++；这个变量 a 具有可见性，但是 a++ 依然是一个非原子操作，也就是这个操作同样存在线程安全问题。
 *
 * 在 Java 中 volatile、synchronized 和 final 实现可见性。
 *
 * 原子性：
 * 原子是世界上的最小单位，具有不可分割性。
 * 比如 a=0；（ a 非 long 和 double 类型）这个操作是不可分割的，那么我们说这个操作时原子操作。
 * 再比如：a++；这个操作实际是 a=a+1；是可分割的，所以他不是一个原子操作。
 * 非原子操作都会存在线程安全问题，需要我们使用同步技术（sychronized）来让它变成一个原子操作。一个操作是原子操作，那么我们称它具有原子性。
 *
 * java 的 concurrent 包下提供了一些原子类，我们可以通过阅读API来了解这些原子类的用法。
 * 比如：AtomicInteger、AtomicLong、AtomicReference 等。
 *
 * 在 Java 中 synchronized 和在 lock、unlock 中操作保证原子性。
 *
 * 有序性：
 * Java 语言提供了 volatile 和 synchronized 两个关键字来保证线程之间操作的有序性，
 * volatile 是因为其本身包含 “禁止指令重排序” 的语义，
 * synchronized 是由 “一个变量在同一个时刻只允许一条线程对其进行 lock 操作” 这条规则获得的，此规则决定了持有同一个对象锁的两个同步块只能串行执行。
 *
 * 同时具有「可见性」和「原子性」，就认为是「有序性」了吗？
 *
 */

/**
 * 本demo内容概括：主要做：原子性测试，顺便做一个可见性测试
 * volatile，synchronized，Lock，AtomicInteger 的原子性测试
 *
 * 可见性测试思路：
 * 一个线程修改了某个非 volatile 修饰的共享变量，另一个线程没有立即可见
 *
 * 原子性测试思路：
 * 多个线程同时引用同一个对象，执行某个非原子性函数
 * 比如，i 从 0 开始
 * 10个线程，都执行 i++ 1000次
 * （i++，非原子操作，可分解为三步原子操作：
 *   第一：读取 inc 的值（读取到哪？cpu 的缓存中？）；
 *   第二：值 + 1 ；
 *   第三：写回工作内存。）
 * 不保证原子性的情况下，结果可能小于 10000
 * 保证原子性的情况下，结果等于 10000
 */
public class AtomicityTestDemo {

    boolean flag = true;


    // 测试可见性
    // 每个线程在运行过程中都有自己的工作内存，线程在运行的时候，会将 flag 变量的值拷贝一份放在自己的工作内存当中
    // 此处测试的思路：
    // 若 t3 线程修改了 flag 的值，有一定的概率没有即时刷新到主存，则对 t1 不可见，导致 t1 无法停止
    public void test(){

        Thread t1 =new Thread(new Runnable() {
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
        t1.start();

        // 该写法等同于等价于上面的
        Thread t3 = new Thread(()->{
            try {
                Thread.sleep(3000);
                LogUtil.e("flag = false ");
                flag = false;
                // 此处修改了 flag 值，然后就立刻去做别的事情了，此处用 while 循环模拟
                // flag 值不一定能即时写到主存中去，导致线程 t 无法停止。
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

    // --------------------- 分割线 ---------------------
    // 从此处开始都是在测试原子性，
    // 用同样的测试思路：
    // 同时启动10个线程，累加同一个数1000次，期望结果是 10000


    // volatile 只能保证可见性，不能保证原子性
    // 由于 原子性问题，此处可能会导致结果小于 10000
    static class TestVolatile {
        public volatile int inc = 0;

        public void increase() {
            inc++;
        }

        public static void exec() {
            final TestVolatile test = new TestVolatile();

            for(int i=0;i<10;i++){ // 新建10个线程，累加同一个数
                new Thread(){
                    public void run() {
                        for(int j=0;j<1000;j++)
                            //System.out.println("" + test.inc);
                            test.increase();
                    }
                }.start();
            }

            // 此时在主线程执行
            while(Thread.activeCount() > 2){ //至少有主线程和一个子线程，则让出资源，保证所有子线程先执行完
                //System.out.println("res = " + test.inc);
                // System.out.println("Thread.activeCount() = " + Thread.activeCount());
                Thread.yield();//当前线程，让出cpu，进入就绪状态
            }


            // 到此所有子线程执行完，只剩主线程
            //LogUtil.e("" + test.inc); // 结果可能小于10000
            System.out.println("final res = " + test.inc);
        }// end exec()
    }
    /**
     * 自增操作是不具备原子性的！
     * inc++ 这个操作，可分解为：
     * 第一：读取 inc 的值（读取到哪？cpu 的缓存中？）；
     * 第二：值 + 1 ；
     * 第三：写回工作内存 。
     *
     * 出问题的具体过程如下：
     * 某一个线程之刚执行完的第二步：值 + 1 ，没来执行第三步：写回工作内存；
     * 另一个线程同时开始第一步：读取 inc 的值，那么这个时候，读到的值是没有完成累加操作的；
     * 这就必然会出现，有很多次累加操作不算数。
     *
     * 所以，这个代码的结果，肯定是会小于 10000
     */

    // synchronized 可以保证原子性
    static class TestSynchronized {
        public int inc = 0;

        public synchronized void increase() {
            inc++;
        }

        public  void exec() {
            final TestSynchronized test = new TestSynchronized();
            for(int i=0;i<10;i++){
                new Thread(){
                    public void run() {
                        for(int j=0;j<1000;j++)
                            test.increase();
                    };
                }.start();
            }


            while(Thread.activeCount()>2)
                Thread.yield();//

            LogUtil.e("" + test.inc); // 结果=10000
        }
    }

    // Lock 实现，也可以保证原子性
    static class TestReentrantLock {
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
            final TestReentrantLock test = new TestReentrantLock();
            for(int i=0;i<10;i++){
                new Thread(){
                    public void run() {
                        for(int j=0;j<1000;j++)
                            test.increase();
                    };
                }.start();
            }

            // 线程数大于1，说明除主线程之外还有子线程
            // 有子线程就让出资源，保证所有子线程都执行完
            while(Thread.activeCount()>2)
                Thread.yield();// 此处是主线程执行的语句，作用就是让出资源给子线程执行

            LogUtil.e("" + test.inc); // 结果=10000
        }
    }

    // AtomicInteger 原子性操作
    static class TestAtomicInteger {
        // 该类带有很多原子性操作
        public AtomicInteger inc = new AtomicInteger();

        public void increase() {
            inc.getAndIncrement();//具有原子性
        }

        public void exec() {
            final TestAtomicInteger test = new TestAtomicInteger();
            for(int i=0;i<10;i++){
                new Thread(){
                    public void run() {
                        for(int j=0;j<1000;j++)
                            test.increase();
                    };
                }.start();
            }

            // 线程数大于1，说明除主线程之外还有子线程
            // 有子线程就让出资源，保证所有子线程都执行完
            while(Thread.activeCount()>2)
                Thread.yield();// 此处是主线程执行的语句，作用就是让出资源给子线程执行

            LogUtil.e("" + test.inc); // 结果=10000
        }
    }

}

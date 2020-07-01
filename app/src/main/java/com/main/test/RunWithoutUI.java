package com.main.test;

import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseArray;

import org.greenrobot.eventbus.EventBus;

import java.io.OutputStream;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.internal.Util;

/**
 * 如类名，不需要安装到模拟器，有 main 函数，直接运行
 */
public class RunWithoutUI {


    public static void main(String[] args){

        //System.out.println(5%2);// % 取整

        //testWhile(); // 测试循环的判断条件的执行顺序

        //testStatueValueForThreadPool(); // 测试线程池的状态

        //testThreadLocalHashCode(); // 测试 ThreadLocal 的 ThreadLocalHashCode 的生成

        //testRef(); // 测试四种引用类型

        OutputStream outputStream;

        EventBus eventBus;

        LocalBroadcastManager localBroadcastManager;

        ThreadPoolExecutor threadPoolExecutor;

        AsyncTask asyncTask;

        AtomicInteger atomicInteger;

        ReentrantLock reentrantLock;

        // 缓存线程池
        Executors.newCachedThreadPool();
        Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return null;
            }
        });

        // 固定数量4的线程池
        Executors.newFixedThreadPool(5);
        Executors.newFixedThreadPool(5, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return null;
            }
        });

        // 单线程的线程池
        Executors.newSingleThreadExecutor();
        Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return null;
            }
        });

        // 周期调度线程池
        Executors.newScheduledThreadPool(5);
        Executors.newScheduledThreadPool(5, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return null;
            }
        });

        // 周期调度的单线程 线程池
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {

            }
        },1000,TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return null;
            }
        });


//        HashMap hashMap ;
//        hashMap.put("","");
//        hashMap.get("");
//
//        SparseArray array;
//        array.put(1,"");
//        array.get(1);

        WeakHashMap weakHashMap;

        Looper looper;

        TestBuilder testBuilder = new TestBuilder()
                .setName(1)
                .setName2(2)
                .setName3(3);


    }


    public static void testHash(){
        //Dog i = new Dog();
        int h;
        //(h = i.hashCode()) ^ (h >>> 16);
    }

    class Dog{

    }

    // 测试四种引用类型
    private static void testRef(){
        // 强引用，绝对不会被回收
        Object o = new Object(); // 默认强引用
        String s = new String("hello");
        //System.out.println("强引用 = " + s);

        // 软引用，内存不足时，遇到 gc 就会被回收
        SoftReference<String> softReference = new SoftReference<String>(new String("hello"));

        // 弱引用，下一次 gc 就会被回收
        WeakReference<String> weakReferencer = new WeakReference<String>(new String("hello"));


        // 虚引用，随时都可能被 gc 回收
        ReferenceQueue<String> queue = new ReferenceQueue<String>();// 需要借助引用队列
        PhantomReference<String> phantomReference = new PhantomReference<String>(new String("hello"), queue);

        // 通知 JVM 的 gc 进行垃圾回收后
        System.out.println("强引用 = " + s);
        System.out.println("软引用 = " + softReference.get());
        System.out.println("弱引用 = " + weakReferencer.get());
        System.out.println("虚引用 = " + phantomReference.get());
        System.gc();
        System.out.println("垃圾回收后...");
        System.out.println("强引用 = " + s);
        System.out.println("软引用 = " + softReference.get());
        System.out.println("弱引用 = " + weakReferencer.get());
        System.out.println("虚引用 = " + phantomReference.get());

        //输出结果：
        //强引用 = hello
        //软引用 = hello
        //弱引用 = hello
        //虚引用 = null
        //垃圾回收后...
        //强引用 = hello
        //软引用 = hello
        //弱引用 = null
        //虚引用 = null

    }

    // 测试 threadLocalHashCode 的产生的相关变量和函数
    private static final int HASH_INCREMENT = 0x61c88647;

    private static AtomicInteger nextHashCode =
            new AtomicInteger();

    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    // 测试 threadLocalHashCode 的产生
    static void testThreadLocalHashCode(){
        // 模拟第一个 ThreadLocal<T> thrdLcl1 = new ThreadLocal<T>();// threadLocalHashCode1
        int threadLocalHashCode1 = nextHashCode();

        // 模拟第二个 ThreadLocal<T> thrdLcl2 = new ThreadLocal<T>();// threadLocalHashCode2
        int threadLocalHashCode2 = nextHashCode();

        // 模拟第三到第七个
        int threadLocalHashCode3 = nextHashCode();
        int threadLocalHashCode4 = nextHashCode();
        int threadLocalHashCode5 = nextHashCode();
        int threadLocalHashCode6 = nextHashCode();
        int threadLocalHashCode7 = nextHashCode();
        int threadLocalHashCode8 = nextHashCode();
        int threadLocalHashCode9 = nextHashCode();
        int threadLocalHashCode10 = nextHashCode();
        int threadLocalHashCode11 = nextHashCode();
        int threadLocalHashCode12 = nextHashCode();
        int threadLocalHashCode13 = nextHashCode();
        int threadLocalHashCode14 = nextHashCode();
        int threadLocalHashCode15 = nextHashCode();
        int threadLocalHashCode16 = nextHashCode();
        int threadLocalHashCode17 = nextHashCode();
        int threadLocalHashCode18 = nextHashCode();
        int threadLocalHashCode19 = nextHashCode();
        int threadLocalHashCode20 = nextHashCode();


        // 同一个线程，可以新建多个 ThreadLocal<T> 实例，但操作的 ThreadLocalMap 对象都是同一个
        // 同一个线程的多个 ThreadLocal<T> 实例，threadLocalHashCode 值不一样
        // 这个值，会影响其在 ThreadLocalMap 中保存数据时用到的 table 数组的下标。
        // 具体计算方式就是
        // 对于2的幂作为模数取模，可以用&(2^n-1)来替代%2^n，位运算比取模效率高很多
        int i1 = threadLocalHashCode1 & (16-1);
        int i2 = threadLocalHashCode2 & (16-1);
        int i3 = threadLocalHashCode3 & (16-1);
        int i4 = threadLocalHashCode4 & (16-1);
        int i5 = threadLocalHashCode5 & (16-1);
        int i6 = threadLocalHashCode6 & (16-1);
        int i7 = threadLocalHashCode7 & (16-1);
        int i8 = threadLocalHashCode8 & (16-1);
        int i9 = threadLocalHashCode9 & (16-1);
        int i10 = threadLocalHashCode10 & (16-1);
        int i11 = threadLocalHashCode11 & (16-1);
        int i12 = threadLocalHashCode12 & (16-1);
        int i13 = threadLocalHashCode13 & (16-1);
        int i14 = threadLocalHashCode14 & (16-1);
        int i15 = threadLocalHashCode15 & (16-1);
        int i16 = threadLocalHashCode16 & (16-1);
        int i17 = threadLocalHashCode17 & (16-1);
        int i18 = threadLocalHashCode18 & (16-1);
        int i19 = threadLocalHashCode19 & (16-1);
        int i20 = threadLocalHashCode20 & (16-1);

        System.out.println("threadLocalHashCode1 = " + threadLocalHashCode1);
        System.out.println("threadLocalHashCode2 = " + threadLocalHashCode2);
        System.out.println("threadLocalHashCode3 = " + threadLocalHashCode3);
        System.out.println("......");

        System.out.println("i1 = " + i1);
        System.out.println("i2 = " + i2);
        System.out.println("i3 = " + i3);
        System.out.println("i4 = " + i4);
        System.out.println("i5 = " + i5);
        System.out.println("i6 = " + i6);
        System.out.println("i7 = " + i7);
        System.out.println("i8 = " + i8);
        System.out.println("i9 = " + i9);
        System.out.println("i10 = " + i10);
        System.out.println("i11 = " + i11);
        System.out.println("i12 = " + i12);
        System.out.println("i13 = " + i13);
        System.out.println("i14 = " + i14);
        System.out.println("i15 = " + i15);
        System.out.println("i16 = " + i16);
        System.out.println("i17 = " + i17);
        System.out.println("i18 = " + i18);
        System.out.println("i19 = " + i19);
        System.out.println("i20 = " + i20);

    }

    // 测试 AtomicInteger 的一些常用接口
    static void testAtomicInteger(){

    }

    // ThreadPoolExecutor.java 中的一些变量
    private static final int COUNT_BITS = Integer.SIZE - 3; //29
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1; // 29 个 1
    // 1 << 29 = 0010 0000 ....(省略24个0)
    // (1 << 29) - 1 = 0001 1111 ....(省略24个1)，即一共 29 个 1

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS; // 1110 0000 ....(省略24个0)
    private static final int SHUTDOWN   =  0 << COUNT_BITS; // 0000 0000 ....(省略24个0)
    private static final int STOP       =  1 << COUNT_BITS; // 0010 0000 ....(省略24个0)
    private static final int TIDYING    =  2 << COUNT_BITS; // 0100 0000 ....(省略24个0)
    private static final int TERMINATED =  3 << COUNT_BITS; // 0110 0000 ....(省略24个0)

    // 这五个变量，二进制下都是 32 位，其中高三位当作「状态标识」，后 29 位表示「线程数」
    // 有点类似 View 框架里的 MeasureSpec

    /**
     * 将 rs 和 wc 通过位运算结合成一个数返回
     *
     * 前提是约定：
     * rs 仅占用一个 32 位 int 型数的高 3 位
     * wc 则占用一个 32 位 int 型数的低 29 位
     *
     * @param rs runState
     * @param wc workerCount
     * @return
     */
    static int ctlOf(int rs, int wc) { return rs | wc; }

    /**
     * CAPACITY 是一个二进制下低 29 位都是 1 的数
     * ~CAPACITY 就是一个 二进制下低 29 位都是 0 的数，高 3 位都是 1 的数
     * c & ~CAPACITY 就是把 c 的低 29 位都置为 0，高 3 位不变；
     *
     * 换句话说：就是取 c 的高 3 位
     *
     * @param c
     * @return
     */
    static int runStateOf(int c)     { return c & ~CAPACITY; }

    /**
     * CAPACITY 是一个二进制下低 29 位都是 1 的数
     * c & CAPACITY 就是把 c 的高 3 位都置为 0，低 29 位不变；
     *
     * 换句话说：就是取 c 的低 29 位
     *
     * @param c
     * @return
     */
    static int workerCountOf(int c)  { return c & CAPACITY; }

    // 打印 ThreadPoolExecutor.java 中的五个状态量
    static void testStatueValueForThreadPool(){
        System.out.println("RUNNING = " + RUNNING);
        System.out.println("RUNNING（二进制）= " + Integer.toBinaryString(RUNNING));

        System.out.println("SHUTDOWN = " + SHUTDOWN);
        System.out.println("SHUTDOWN（二进制）= " + Integer.toBinaryString(SHUTDOWN));

        System.out.println("STOP = " + STOP);
        System.out.println("STOP（二进制）= " + Integer.toBinaryString(STOP));

        System.out.println("TIDYING = " + TIDYING);
        System.out.println("TIDYING（二进制）= " + Integer.toBinaryString(TIDYING));

        System.out.println("TERMINATED = " + TERMINATED);
        System.out.println("TERMINATED（二进制）= " + Integer.toBinaryString(TERMINATED));

        System.out.println("ctlOf(RUNNING, 0) = " + ctlOf(RUNNING, 0));
        System.out.println("runStateOf(RUNNING) = " + runStateOf(RUNNING));
        System.out.println("workerCountOf(RUNNING) = " + workerCountOf(RUNNING));

        System.out.println("CAPACITY = " + CAPACITY);
        System.out.println("CAPACITY（二进制）= " + Integer.toBinaryString(CAPACITY));
    }

    // 测试线程池的使用
    static void testThreadPoolUse(){
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
//                new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
//
//        threadPoolExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("hello go to xizao");
//            }
//        });
    }

    // while 这种判断条件的小细节：
    // 满足第一个条件，则不会执行第二个条件的语句！
    static boolean testWhile(){

        int i = 1;

        while(i == 1 || (i = 0) < 1){// 满足第一个条件，则不会执行第二个条件的语句！

            System.out.println("i = " + i);

            if(i == 1){
                break;
            }
        }

        return false;
    }

    // ------------- 一个蛋的问题 begin -------------
    // 1分钱，3个鸡蛋
    // 3分钱，1个鸭蛋
    // 5分钱，1个鹅蛋
    // 1块钱，100个蛋，三种蛋怎么组合？

    public static void caculateEgg(){


        int cg ;
        int dg ;
        int eg ;


        for(cg = 1; cg < 100; cg ++ ){ // cg 从 1 循环到 99

            for(dg = 1; dg < 100; dg ++ ){ // dg 从 1 循环到 99

                for(eg = 1; eg < 100; eg ++ ){ // eg 从 1 循环到 99

                    //System.out.println("cg = " + cg + "," + "dg = " + dg + "," + "eg = " + eg);


                    if( (cg + dg + eg == 100)
                            && is100(cg,dg,eg)
                            //&& cg%2 != 0 && dg%2 != 0 && eg%2 != 0
                    ){
                        System.out.println("cg = " + cg + "," + "dg = " + dg + "," + "eg = " + eg);
                    }

                }

            }

        }

        System.out.println("hello");

    }

    private static boolean is100(int cg,int dg,int eg){
        if(cg/3 + dg*3 + eg*5 == 100 && cg%3 ==0 ){
            return true;
        }
        return false;
    }
    // ------------- 一个蛋的问题 end -------------
}




package com.guohao.anything.sync;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TransferQueue;

public class RunWithoutUI {


    public static void main(String[] args) {



        //testListCurThreadName(); // 输出当前线程


//        JDK7提供了7个阻塞队列，分别是：
//
//        ArrayBlockingQueue ：由数组结构组成的有界阻塞队列。
//        LinkedBlockingQueue ：由链表结构组成的有界阻塞队列。
//        PriorityBlockingQueue ：支持优先级排序的无界阻塞队列。
//        DelayQueue：使用优先级队列实现的无界阻塞队列。
//        SynchronousQueue：不存储元素的阻塞队列。
//        LinkedTransferQueue：由链表结构组成的无界阻塞队列。
//        LinkedBlockingDeque：由链表结构组成的双向阻塞队列。

        ArrayBlockingQueue arrayBlockingQueue;
        LinkedBlockingQueue linkedBlockingQueue;
        PriorityBlockingQueue priorityBlockingQueue;
        DelayQueue delayQueue;
        SynchronousQueue synchronousQueue;
        LinkedTransferQueue linkedTransferQueue;
        LinkedBlockingDeque linkedBlockingDeque;

        // 接口
        BlockingDeque blockingDeque;
        // 接口
        TransferQueue transferQueue;
        // DelayedWorkQueue，在 ScheduledThreadPoolExecutor 的静态内部类，默认缺省修饰符，此处访问不了
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    }

    // 原来我们可以列出：所有活跃的线程，和当前线程组的线程
    public static void testListCurThreadName(){

        // 列出当前所有活跃的线程，以及所在的组 start
        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        Set<Thread> threads = map.keySet();
        Iterator<Thread> iterator = threads.iterator();

        System.out.println("当前所有线程组活跃的线程：");
        for (int i = 1;iterator.hasNext();i++){
            Thread t = iterator.next();
            System.out.println(i + "，name = " + t.getName() + " ，isAlive = " + t.isAlive()
                            + "，group = " + t.getThreadGroup().getName());
        }

        //输出结果：
        //当前活跃的线程 = 2
        //1，Monitor Ctrl-Break ，isAlive = true，group = main
        //2，Signal Dispatcher ，isAlive = true，group = system
        //3，Reference Handler ，isAlive = true，group = system
        //4，main ，isAlive = true，group = main
        //5，Finalizer ，isAlive = true，group = system

        // 所以有三个线程没有算在 Thread.activeCount() 里面，
        // 因为不同组
        System.out.println("当前线程组活跃的线程数 = " + Thread.activeCount()); // 2

        System.out.println("当前线程组活跃的线程：");
        // 列出当前线程组的线程
        Thread.currentThread().getThreadGroup().list();
    }

}

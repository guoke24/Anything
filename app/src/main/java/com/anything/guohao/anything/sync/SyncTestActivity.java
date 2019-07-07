package com.anything.guohao.anything.sync;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.anything.guohao.anything.BaseTestActivity;
import com.anything.guohao.anything.R;

import java.util.ArrayList;
import java.util.List;

public class SyncTestActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sync_test);
        super.onCreate(savedInstanceState);

    }

    public void test_1(View view) throws InterruptedException {
        showMessage("SyncTestActivity");

        //testLockDemo();

        testLockCountDemo();
    }

    // 参考：https://cloud.tencent.com/developer/article/1038499
    private void testLockDemo() throws InterruptedException {
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

        Thread.sleep(3000);
        System.out.println("main over!");
    }

    private void testLockCountDemo() throws InterruptedException {
        LockCountDemo demo = new LockCountDemo();
        Thread thread1 = new Thread(() -> {
            //System.out.println(Thread.currentThread().getName() + " : 开始执行");
            demo.tmpAns1 = demo.add(demo.start, demo.middle,Thread.currentThread().getName());
            System.out.println(Thread.currentThread().getName() +
                    " : calculate ans: " + demo.tmpAns1);
        }, "count1");

        Thread thread2 = new Thread(() -> {
            //System.out.println(Thread.currentThread().getName() + " : 开始执行");
            demo.tmpAns2 = demo.add(demo.middle, demo.end + 1,Thread.currentThread().getName());
            System.out.println(Thread.currentThread().getName() +
                    " : calculate ans: " + demo.tmpAns2);
        }, "count2");

        Thread thread3 = new Thread(() -> {
            try {
                //System.out.println(Thread.currentThread().getName() + " : 开始执行");
                int ans = demo.sum(Thread.currentThread().getName());
                System.out.println(Thread.currentThread().getName() + "the total result: " + ans);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "sum");


        thread3.start();
        thread1.start();
        thread2.start();

        Thread.sleep(3000);
        System.out.println("over");
    }

}

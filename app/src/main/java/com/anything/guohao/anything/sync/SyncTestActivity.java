package com.anything.guohao.anything.sync;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.anything.guohao.anything.BaseTestActivity;
import com.anything.guohao.anything.LogUtil;
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

    // start 关于 Lock 和 Condition 的使用，
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
    // end 关于 Lock 和 Condition 的使用，

    public void test_2(View view){
        new VolatileDemo().test();// 重排序
    }

    public void test_3(View view){
        new VolatileDemo.Test().exec();// volatile 没有原子性
    }

    public void test_4(View view){
        new VolatileDemo.Test2().exec();// synchronized 有原子性
    }

    public void test_5(View view){
        new VolatileDemo.Test3().exec();// Lock 有原子性
    }

    public void test_6(View view){
        new VolatileDemo.Test4().exec();// AtomicInteger 有原子性
    }

    // AsyncTask 用法的简单示例 start
    IAsynTask iAsynTask = null;
    public void test_7(View v){
        // 还可以这么写
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("in AsyncTask run");
            }
        });

        // 也可以这么写
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("in Handler run");
            }
        });

        iAsynTask = new IAsynTask();
        iAsynTask.execute("test8");
    }

    public void test_8(View v){
        if(iAsynTask != null){
            if(!iAsynTask.isCancelled())
                iAsynTask.cancel(true);
        }

    }

    public class IAsynTask extends AsyncTask<String,Integer,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showMessage("onPreExecute");
            LogUtil.e("onPreExecute");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showMessage("onPostExecute" + s);
            LogUtil.e("onPostExecute " + s);
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            showMessage("onProgressUpdate " + values[0]);
        }

        @Override
        protected String doInBackground(String... strings) {
            publishProgress(100);
            LogUtil.e("doInBackground begin " + strings[0]);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LogUtil.e("doInBackground finish " + strings[0]);
            return "okkk";
        }

        // 两个 onCancelled 都会调用到
        @Override
        protected void onCancelled() {
            super.onCancelled();
            showMessage("onCancelled");
            LogUtil.e("onCancelled ");
        }

        @Override
        protected void onCancelled(String s) { // 此处的s 就是 doInBackground 函数返回的 string
            super.onCancelled(s);
            showMessage("onCancelled " + s);
            LogUtil.e("onCancelled " + s);
        }
    }// AsyncTask 用法的简单示例 end

}

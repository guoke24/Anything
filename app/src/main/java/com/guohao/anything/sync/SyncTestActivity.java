package com.guohao.anything.sync;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.guohao.anything.BaseTestActivity;
import com.guohao.anything.LogUtil;
import com.guohao.anything.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            demo.tmpAns1 = demo.add(demo.start, demo.middle, Thread.currentThread().getName());
            System.out.println(Thread.currentThread().getName() +
                    " : calculate ans: " + demo.tmpAns1);
        }, "count1");

        Thread thread2 = new Thread(() -> {
            //System.out.println(Thread.currentThread().getName() + " : 开始执行");
            demo.tmpAns2 = demo.add(demo.middle, demo.end + 1, Thread.currentThread().getName());
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

    public void test_2(View view) {
        new VolatileDemo().test();// 重排序
    }

    public void test_3(View view) {
        new VolatileDemo.Test().exec();// volatile 没有原子性
    }

    public void test_4(View view) {
        new VolatileDemo.Test2().exec();// synchronized 有原子性
    }

    public void test_5(View view) {
        new VolatileDemo.Test3().exec();// Lock 有原子性
    }

    public void test_6(View view) {
        new VolatileDemo.Test4().exec();// AtomicInteger 有原子性
    }

    // AsyncTask 用法的简单示例 start
    IAsynTask iAsynTask = null;

    public void test_7(View v) {
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

    public void test_8(View v) {
        if (iAsynTask != null) {
            if (!iAsynTask.isCancelled())
                iAsynTask.cancel(true);
        }

    }

    public class IAsynTask extends AsyncTask<String, Integer, String> {

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

    // 线程 测试 start
    public void test_9(View view) {
        test_threadPool();
        //test_sleep();
        //test_Thead_sleep();
    }

    // 测试：线程池的复用
    public void test_threadPool() {
        System.out.println("主线程：" + Thread.currentThread().getName());

        //创建一个可缓存线程池
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {//

            // 循环10次 执行线程
            cachedThreadPool.execute(new Runnable() {
                public void run() {
                    //打印正在执行的缓存线程信息
                    LogUtil.e("f子线程：" + Thread.currentThread().getName() + " 正在被执行");
                    try {
                        Thread.currentThread().sleep(1000);//睡眠一秒，再结束子线程
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 观察 每隔一秒 执行一次的任务，是否用的同一个线程？
                    // 不是同一个。因为每执行一个任务，前一个子线程还在睡眠没有退出，所以要用新的线程来执行任务
                }
            });

        }


        for (int i = 0; i < 10; i++) {//

            try {
                // 让主线程睡眠1秒，再去执行下一个线程，这样上一个线程就会执行完退出
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 循环10次 执行线程
            cachedThreadPool.execute(new Runnable() {
                public void run() {
                    //打印正在执行的缓存线程信息
                    LogUtil.e("s子线程：" + Thread.currentThread().getName() + " 正在被执行");
                    // 观察 每隔一秒 执行一次的任务，是否用的同一个线程？
                    // 是的。因为每执行一个任务，上一个线程已经结束退出（因主线程休眠1s再开始下一个任务），所以可以复用
                }
            });

        }
    }


    // 测试：主线程 睡眠，子线程是否会受影响
    public void test_sleep(){

        // 执行一个子线程，循环打印，睡眠一一秒，打印一次
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while(true){
                    i++;
                    try {
                        Thread.currentThread().sleep(1000);// 这里还是主线程
                        LogUtil.e("线程_Handler：" + Thread.currentThread().getName() + " " + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(i == 5) break;

                }
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while(true){
                    i++;
                    try {
                        Thread.currentThread().sleep(1000);// 这里子主线程
                        LogUtil.e("线程_Thread：" + Thread.currentThread().getName() + " " + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(i == 5) break;

                }
            }
        }).start();

        try {
            LogUtil.e("主线程 sleep begin");
            Thread.sleep(6000);
            LogUtil.e("主线程 sleep finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // 测试：子线程的Thead.sleep
    public void test_Thead_sleep(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while(true){
                    i++;
                    try {
                        Thread.sleep(1000);// 这里是让主线程还是子线程休眠呢?答：子线程
                        LogUtil.e("线程_Thread：" + Thread.currentThread().getName() + " " + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(i == 5) break;

                }
            }
        }).start();

        int i = 0;
        while(true){
            i++;
            LogUtil.e("线程_Main：" + Thread.currentThread().getName() + " " + i);
            if(i == 10) break;

        }

        // 观察那个线程被休眠！结果是：子线程，也就是说，在子线程执行 Thread.sleep ，休眠的是子线程
    }
    // 线程 测试 end

    // Thread.currentThread().sleep 和 Thread.sleep 都是让当前线程休眠，不让出锁
    //

    Object object = new Object();

    /*
     * 在主线程开启一个子线程，然后主线程阻塞，等待子线程耗时操作结束后，唤醒主线程
     *
     */
    public void test_10(View view){
        int a = getValue();
        LogUtil.e("返回值 a = " + a);
    }

    public int getValue(){
        synchronized (object){
            try {
                LogUtil.e(Thread.currentThread().getName() + " 启动子线程执行耗时任务");
                Thread t1 = new MyThread("子线程");
                t1.start();
                LogUtil.e(Thread.currentThread().getName() + " 阻塞开始！等待子线程唤醒。");
                object.wait();//阻塞，释放锁
                LogUtil.e(Thread.currentThread().getName() + " 被唤醒！阻塞结束！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return 24;
    }

    class MyThread extends Thread{
        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            //super.run();
            try {
                LogUtil.e( Thread.currentThread().getName() + " 开始耗时任务,约5秒");
                int j = 0;
                while(true){
                    Thread.currentThread().sleep(1000);
                    LogUtil.e("耗时 " + (++j) + "秒");
                    if(j == 5) break;
                }

                LogUtil.e( Thread.currentThread().getName() + " 结束耗时任务");
                synchronized (object){
                    LogUtil.e( Thread.currentThread().getName() + " 唤醒主线程");
                    object.notifyAll();
                    //LogUtil.e( Thread.currentThread().getName() + " 唤醒结束");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}












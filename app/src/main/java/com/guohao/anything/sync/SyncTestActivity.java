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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncTestActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sync_test);
        super.onCreate(savedInstanceState);

    }

    // --------------------- 分割线 ---------------------


    public void test_1(View view) throws InterruptedException {
        showMessage("SyncTestActivity");

        //LockDemo.execTest(); // 确保只有一个人上班

        //LockCountDemo.execTest(); // 累加2次后再唤醒输出结果

        //new ObjectForLock().testThreadState(); // 所有状态的转换

        //new ObjectForLock().testThreadInterrupt(); // 线程的中断
    }


    // --------------------- 分割线 ---------------------

    // start 可见性和原子性测试

    public void test_2(View view) { // 可见性
        new AtomicityTestDemo().test();
    }

    // volatile 没有原子性
    public void test_3(View view) {
        new AtomicityTestDemo.TestVolatile().exec();
    }

    // synchronized 有原子性
    public void test_4(View view) {
        new AtomicityTestDemo.TestSynchronized().exec();
    }

    // Lock 有原子性
    public void test_5(View view) {
        new AtomicityTestDemo.TestReentrantLock().exec();
    }

    // AtomicInteger 有原子性
    public void test_6(View view) {
        new AtomicityTestDemo.TestAtomicInteger().exec();
    }

    // end 可见性和原子性

    // --------------------- 分割线 ---------------------

    // AsyncTask 用法的简单示例 start
    IAsynTask iAsynTask = null;

    public void test_7(View v) {
        // 在子线程执行一个 Runnable
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("in AsyncTask run，currentThread = " + Thread.currentThread().getName());
            }
        });

        // 在主线程排队执行一个 Runnable
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("in Handler runcurrentThread = " + Thread.currentThread().getName());
            }
        });

        // 执行一个异步任务
        iAsynTask = new IAsynTask();
        iAsynTask.execute("test8");
    }

    // 取消异步任务 iAsynTask 的执行
    public void test_8(View v) {
        if (iAsynTask != null) {
            if (!iAsynTask.isCancelled())
                iAsynTask.cancel(true);
        }

    }

    /**
     * 继承 AsyncTask，复写几个关键的函数：
     *
     * 在 子线程中 执行
     * doInBackground
     *
     * 四个函数在 主线程中 执行
     * onPreExecute，调用时机在 doInBackground 之前
     * onPostExecute，调用时机在 doInBackground 之前
     * onProgressUpdate，调用时机在 doInBackground 函数中执行了 publishProgress 的时候，publishProgress 是 protected 修饰的，不可外部调用
     * onCancelled 内外部调用 cancel 函数时
     *
     */
    public class IAsynTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() { // 主线程执行
            super.onPreExecute();
            showMessage("onPreExecute");
            LogUtil.e("onPreExecute，currentThread = " + Thread.currentThread().getName());
        }

        @Override
        protected void onPostExecute(String s) { // 主线程执行
            super.onPostExecute(s);
            showMessage("onPostExecute" + s);
            LogUtil.e("onPostExecute " + s + "，currentThread = " + Thread.currentThread().getName());
        }


        @Override
        protected void onProgressUpdate(Integer... values) { // 主线程执行
            super.onProgressUpdate(values);
            showMessage("onProgressUpdate " + values[0] + "，currentThread = " + Thread.currentThread().getName());
            LogUtil.e(values[0] + "，currentThread = " + Thread.currentThread().getName());
        }

        @Override
        protected String doInBackground(String... strings) { // 子线程执行
            //publishProgress(100);
            LogUtil.e("begin " + strings[0] + "，currentThread = " + Thread.currentThread().getName());
            try {
                // 模拟更新进度
                Thread.sleep(2000);
                publishProgress(1);
                Thread.sleep(2000);
                publishProgress(2);
                Thread.sleep(2000);
                publishProgress(3);
                Thread.sleep(2000);
                if(strings[0].equals("test8")){
                    cancel(true);
                }
                publishProgress(4);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LogUtil.e("finish " + strings[0] + "，currentThread = " + Thread.currentThread().getName());
            return "okkk";
        }

        // 两个 onCancelled 都会调用到
        @Override
        protected void onCancelled() { // 主线程执行
            super.onCancelled();
            showMessage("onCancelled");
            LogUtil.e("currentThread = " + Thread.currentThread().getName());
        }

        @Override
        protected void onCancelled(String s) { // 主线程执行
            super.onCancelled(s);
            showMessage("onCancelled " + s); // 此处的s 就是 doInBackground 函数返回的 string
            LogUtil.e(" " + s + "，currentThread = " + Thread.currentThread().getName());
        }
    }
    // AsyncTask 用法的简单示例 end

    // --------------------- 分割线 ---------------------

    // 调用 线程池中的线程的复用 测试
    public void test_9(View view) {
        test_threadPool();
        //test_Thead_sleep();
    }

    // 测试：线程池中的线程的复用
    public void test_threadPool() {
        System.out.println("主线程：" + Thread.currentThread().getName());

        //创建一个可缓存线程池
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        // 没有复用线程的例子：
        for (int i = 0; i < 10; i++) {//

            // 循环10次 执行线程
            cachedThreadPool.execute(new Runnable() {
                public void run() {
                    //打印正在执行的缓存线程信息
                    System.out.println("f子线程：" + Thread.currentThread().getName() + " 正在被执行");
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

        // 复用了线程的例子：
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
                    System.out.println("s子线程：" + Thread.currentThread().getName() + " 正在被执行");
                    // 观察 每隔一秒 执行一次的任务，是否用的同一个线程？
                    // 是的。因为每执行一个任务，上一个线程已经结束退出（因主线程休眠1s再开始下一个任务），所以可以复用
                }
            });

        }
    }


    // --------------------- 分割线 ---------------------


    Object object = new Object();

    /**
     * 测试：子线程等待主线程的锁唤醒，notify 方式
     *
     * 在主线程开启一个子线程，然后主线程阻塞，等待主线程耗时操作结束后，唤醒子线程
     *
     * 此处测试跟 new ObjectForLock().testObjectWait() 重复，可以忽略
     *
     */
    public void test_10(View view){
        int a = getValue();
        LogUtil.e("返回值 a = " + a);
    }

    // 子线程，等待 object 锁唤醒再返回值
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

    // 主线程执行耗时操作5秒后
    // 唤醒等待 object 锁的子线程
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
                    LogUtil.e( Thread.currentThread().getName() + " 唤醒子线程");
                    object.notifyAll();
                    //LogUtil.e( Thread.currentThread().getName() + " 唤醒结束");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void test_11(View view){
        //MyAsyncTask myAsyncTask = new MyAsyncTask();
        //myAsyncTask.executeOnExecutor(myAsyncTask.getTHREAD_POOL_EXECUTOR(),"1");
        // 会在执行第二行的时候报错：Cannot execute task: the task is already running.
        //myAsyncTask.executeOnExecutor(myAsyncTask.getTHREAD_POOL_EXECUTOR(),"2");
        // 说明 AsyncTask 的重点在于单一异步任务加上 前后和过程进度的回调。

        //MyAsyncTask.THREAD_POOL_EXECUTOR.execute(runnable);
        //MyAsyncTask.THREAD_POOL_EXECUTOR.execute(runnable);
        //MyAsyncTask.THREAD_POOL_EXECUTOR.execute(runnable);

        // 可以通过类的静态函数提交任务
        MyAsyncTask.execute(runnable2);
        MyAsyncTask.execute(runnable2);
        MyAsyncTask.execute(runnable2);
        //MyAsyncTask.SERIAL_EXECUTOR.execute(runnable2);
    }

    class MyAsyncTask extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... strings) {

            int i = 0;

            while(true){

                try {
                    Thread.sleep(2000);
                    ++i;
                    System.out.println("name = " + Thread.currentThread().getName() + "我是动态的 MyAsyncTask 实例");
                    if( i == 5) return "-1";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(strings[0] == null) return "2";

            }

            //return "2";
        }

        public Executor getTHREAD_POOL_EXECUTOR(){
            return THREAD_POOL_EXECUTOR;
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            int i = 0;

            while(true){

                try {
                    Thread.sleep(2000);
                    ++i;
                    System.out.println("name = " + Thread.currentThread().getName() + " 我是静态 THREAD_POOL_EXECUTOR ");
                    if(i == 5) return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {

            int i = 0;

            while(true){

                try {
                    Thread.sleep(2000);
                    ++i;
                    System.out.println("name = " + Thread.currentThread().getName() + " 我是静态 SERIAL_EXECUTOR ");
                    if(i == 5) return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };
}












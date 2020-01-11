package com.guohao.anything.handler;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.guohao.anything.BaseTestActivity;
import com.guohao.anything.LogUtil;
import com.guohao.anything.R;
// 参考：https://blog.csdn.net/wsq_tomato/article/details/80301851
public class handlerTestActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_handler_test);
        super.onCreate(savedInstanceState);

        // 循环查看主线程的 loop 的 队列是否为空
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    try {
//                        Thread.sleep(1000);//休眠一秒的目的
//                        LogUtil.e(" 主线程队列是否空闲：" + Looper.getMainLooper().getQueue().isIdle());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
    }

    public void test_1(View view){
        showMessage("handlerTestActivity test_1:");
        testHandler();

    }

    private Handler mHandler = null;

    // 测试一个线程给另一个线程通信
    // kobeThread 线程实例化了 mHandler 对象，其 Looper 跟 kobeThread 绑定
    // mHandler 的 handleMessage 函数在 kobeThread 线程执行

    // tmacThread 想跟 kobeThread 线程通信，就通过 mHandler 来发消息，
    // 调用 sendMessage 或 post 函数实现
    public void testHandler(){

        // 收消息
        Thread kobeThread = new Thread(new Runnable() {
            @Override
            public void run() {

                //  Can't create handler inside thread that has not called Looper.prepare()
                Looper.prepare();

                // 在这执行为何下面的handler收不到信息？因为阻塞了？
                // 那主线程为何不阻塞呢？因为主线程一直消息发给主线程的queue?
                //Looper.loop();

                // 在该子线程，新建一个handler，在缺省参数的构造函数中，会调用 this(null, false);
                // 接着会调用 mLooper = Looper.myLooper(); 来获得所在线程的 looper 实例
                mHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        LogUtil.e(Thread.currentThread().getName() + " get msg = " + (String)msg.obj);
                    }
                };

                // 否则收不到消息
                Looper.loop();

                LogUtil.e("after Looper.loop();");
            }
        });

        kobeThread.setName("Kobe");
        kobeThread.start();

        // 发消息,延迟一秒
        Thread tmacThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);//休眠一秒的目的，就是等待Loop启动
                    if(mHandler == null) return;
                    Message message = new Message();
                    message.obj = "I am Tmac,trainning?";
                    mHandler.sendMessage(message);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.e("当前线程：" + Thread.currentThread().getName());
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        tmacThread.setName("Tmac");
        tmacThread.start();
    }

    public void test_2(View view){
        showMessage("handlerTestActivity test_2:");

        CusThread cusThread = new CusThread();
        cusThread.start(); // 执行 run 函数，阻塞在 Looper.loop();

        //cusThread.setMessage(); // 测试，此调用来自主线程，能否给子线程发消息
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cusThread.mHandler.sendEmptyMessage(234);
        // 在主线程操作子线程示例的成员变量，是可以的
        // 因为成员变量是一个对象，线程间共享
        // ActivityThread 线程被 Looper.loop() 的 for循环阻塞了，
        // ApplicationThread 线程 还可以委托 mH 发消息给 Looper 的 mQueue。
    }

    // 测试静态变量的访问
    public void testStaticV(){
        //Looper looper = new Looper(); 非public，不能新建，

        People p = new People();
        LogUtil.e("People.a = " + People.a);//静态变量，类名可以访问
        LogUtil.e("p.b = " + p.a);// 静态变量，对象也可以访问，因为在方法区就存了一份
        LogUtil.e("p.b = " + p.b);// 非静态变量，只能对象去访问
        //LogUtil.e("People.a = " + People.b);

    }

    /**
     * 自定义一个线程，带有一个成员变量 mHandler
     *
     * 线程内 run 函数执行到 Looper.loop(); 就会陷入死循环而阻塞后面的代码
     *
     * 可以通过线程外部引用成员变量 mHandler 来发消息到 线程内部的 Looper 实例持有的消息队列内，
     *
     * 这样消息就会被 Looper 实例取出并发送给 mHandler 的 dispatchMessage -- handleMessage 函数，
     * 进而在线程内部执行消息指定的内容了。
     *
     */
    class CusThread extends Thread{

        public Handler mHandler;

        public void setMessage(){
            if(mHandler != null){
                mHandler.sendEmptyMessage(24);
            }
        }

        @Override
        public void run() {
            super.run();

            Looper.prepare();


            mHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    LogUtil.e("CusThread " + " get msg = " + msg.what);
                }
            };

            // 否则收不到消息
            Looper.loop();

            LogUtil.e("CusThread after Looper.loop();");
        }
    }



}

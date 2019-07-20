package com.anything.guohao.anything.handler;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.anything.guohao.anything.BaseTestActivity;
import com.anything.guohao.anything.LogUtil;
import com.anything.guohao.anything.R;
// 参考：https://blog.csdn.net/wsq_tomato/article/details/80301851
public class handlerTestActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_handler_test);
        super.onCreate(savedInstanceState);
    }

    public void test_1(View view){
        showMessage("handlerTestActivity test_1:");
        testHandler();
    }

    private Handler mHandler = null;
    public void testHandler(){

        // 发消息,延迟一秒
        Thread tmacThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if(mHandler == null) return;
                    Message message = new Message();
                    message.obj = "I am Tmac,trainning?";
                    mHandler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        tmacThread.setName("Tmac");
        tmacThread.start();

        // 收消息
        Thread kobeThread = new Thread(new Runnable() {
            @Override
            public void run() {

                //  Can't create handler inside thread that has not called Looper.prepare()
                Looper.prepare();

                //在该子线程，新建一个handler
                mHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        LogUtil.e(Thread.currentThread().getName() + " get msg = " + (String)msg.obj);
                    }
                };

                // 否则收不到消息
                Looper.loop();
            }
        });
        kobeThread.setName("Kobe");
        kobeThread.start();
    }

    public void test_2(View view){
        showMessage("handlerTestActivity test_2:");

    }

    public void testLooper(){
        //Looper looper = new Looper(); 非public，不能新建，

        People p = new People();
        LogUtil.e("People.a = " + People.a);//静态变量，只能通过类名去访问，而不能通过对象名去访问
        LogUtil.e("p.b = " + p.b);

    }




}

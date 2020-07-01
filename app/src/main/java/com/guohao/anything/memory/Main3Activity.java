package com.guohao.anything.memory;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.guohao.anything.LogUtil;
import com.guohao.anything.R;

import java.io.IOException;

public class Main3Activity extends AppCompatActivity {

    // 若不是类引用这个 handler，而是 实例引用这个 handler，
    // 那么这个 handler 什么时候会被回收？
    static MyHandler myHandler = new MyHandler();

    private int _10MB = 10 * 1024 * 1024;
    private byte[] memory ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }

    public void add(View v){
        // 分配内存！
        memory = new byte[39 * _10MB];
        // Caused by: java.lang.OutOfMemoryError:
        // Failed to allocate a 838860816 byte allocation
        // with 1572864 free bytes and 382MB until OOM,
        // max allowed footprint 2785872, growth limit 402653184
    }

    public void check(View v){

        // 获取内存情况

        MemoryUtils.getMemory1(this);
        //MemoryUtils.getMemory2(this);
        //MemoryUtils.getMemory3();
    }

    public void jump(View v){
        // 跳转到，测试内存泄漏
//        Intent intent = new Intent(this, MemoryLeakActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

        // 调用 handler 发消息，测试何时被回收
//        myHandler.sendEmptyMessage(1);
    }

    static class MyHandler extends Handler{

        // 循环的发消息，
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.i("MyHandler","guohao MyHandler");
            myHandler.sendEmptyMessageDelayed(1,1000);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        try {
            Debug.dumpHprofData("fileName");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

}

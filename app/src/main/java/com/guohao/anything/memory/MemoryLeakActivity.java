package com.guohao.anything.memory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.guohao.anything.R;
import com.guohao.anything.RootApplication;


/**
 * 模拟内存泄露的Activity
 */
public class MemoryLeakActivity extends AppCompatActivity implements CallBack{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memoryleak);
        ImageView imageView = findViewById(R.id.iv_memoryleak);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.splash);
        imageView.setImageBitmap(bitmap);

        CallBackManager.addCallBack(this);
        SingleInstance.Holder.newInstance(this);

        LeakThread leakThread = new LeakThread();
        leakThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //CallBackManager.removeCallBack(this);
        //RootApplication.getRefWatcher(this).watch(this);
    }

    @Override
    public void dpOperate() {
        // do sth
    }

    class LeakThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(6 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

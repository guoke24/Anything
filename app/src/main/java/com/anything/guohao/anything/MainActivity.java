package com.anything.guohao.anything;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anything.guohao.anything.hotfix.BugTest;
import com.anything.guohao.anything.hotfix.FixDexUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void test_1(View v){
        new BugTest().getBug(MainActivity.this);
    }



    public void test_2(View v){
        // 热修复
        if (FixDexUtil.isGoingToFix(MainActivity.this)) {
            FixDexUtil.loadFixedDex(MainActivity.this, Environment.getExternalStorageDirectory());
        }
    }

}

package com.guohao.anything.ARouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.guohao.anything.R;

public class ARouterTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arouter_test);
    }

    public void test_1(View v){
        String className = "com.guohao.stockchart.MainActivity";
    }
}

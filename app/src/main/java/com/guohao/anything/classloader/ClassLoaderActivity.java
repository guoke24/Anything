package com.guohao.anything.classloader;


import android.os.Bundle;
import android.view.View;

import com.guohao.anything.BaseTestActivity;
import com.anything.guohao.anything.R;

public class ClassLoaderActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_class_loader);
        super.onCreate(savedInstanceState);
    }

    public void test_1(View v){
        showMessage("test1");
    }

}

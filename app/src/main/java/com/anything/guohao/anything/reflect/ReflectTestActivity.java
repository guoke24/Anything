package com.anything.guohao.anything.reflect;


import android.os.Bundle;
import android.view.View;

import com.anything.guohao.anything.BaseTestActivity;
import com.anything.guohao.anything.R;

public class ReflectTestActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_reflect_test);
        super.onCreate(savedInstanceState);

    }

    public void test_1(View v){
        showMessage("test1");
    }
}

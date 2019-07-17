package com.anything.guohao.anything;

import android.os.Bundle;
import android.view.View;

public class initActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_init);
        super.onCreate(savedInstanceState);
    }

    public void test_1(View view){
        showMessage("initActivity test_1:");
    }
}

package com.guohao.anything;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.guohao.anything.R;
import com.guohao.factory.presenter.group.GroupCreateContract;

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

package com.guohao.xtalker;


import android.os.Bundle;

import com.guohao.common.app.Activity;
import com.guohao.common.frags.PermissionsFragment;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_launch);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            MainXActivity.show(this);
            finish();
        }

    }
}

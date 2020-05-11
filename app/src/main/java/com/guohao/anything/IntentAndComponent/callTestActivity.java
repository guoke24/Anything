package com.guohao.anything.IntentAndComponent;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.View;

import com.guohao.anything.BaseTestActivity;
import com.guohao.anything.LogUtil;
import com.guohao.anything.R;
import com.guohao.anything.net.RetrofitActivity;

public class callTestActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_call_test);
        super.onCreate(savedInstanceState);

        HandlerThread handlerThread;
    }


    public void test_1(View view){
        showMessage("callTestActivity test_1:");

        String pkgName = getPackageName();
        String clsName = RetrofitActivity.class.getName();
        ComponentName componentName = new ComponentName(pkgName,clsName);
        Intent intent = new Intent();
        intent.setComponent(componentName);

        LogUtil.e(" " + pkgName + "/" + clsName);
        // com.guohao.anything/com.guohao.anything.net.RetrofitActivity

        startActivity(intent);
    }

    // 隐式启动再测试几个案例

}

package com.guohao.anything;

import android.app.Application;
import android.content.Context;

import com.guohao.anything.Hook.HookHelper;



public class RootApplication extends Application {

    // 该函数的生命周期在所有组件之前
    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // 反射的方式，替换掉一些组件
//        try {
//            HookHelper.hookAMS(); // 替换 IActivityManager
//
//            HookHelper.hookHandler(); // 替换 mH 的 callback
//
//            //HookHelper.hookAMSForService(); // 替换 IActivityManager
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    // leakcanary 初始化,2.0 版本前才需要这么做

    @Override
    public void onCreate() {
        super.onCreate();
        //setupLeakCanary();
    }

//    RefWatcher refWatcher;
//    protected void setupLeakCanary() {
//
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//
//        refWatcher = LeakCanary.install(this);
//    }
//
//    public static RefWatcher getRefWatcher(Context context){
//
//        return ((RootApplication)context.getApplicationContext()).refWatcher;
//    }

}

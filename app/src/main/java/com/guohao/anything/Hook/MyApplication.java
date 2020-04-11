package com.guohao.anything.Hook;

import android.app.Application;
import android.content.Context;

/**
 * 需要在清单文件中指明该 Application：
 *
 * <application
 *         android:name=".Hook.MyApplication"
 *
 */
public class MyApplication extends Application {

    // 该函数的生命周期在所有组件之前
    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // 反射的方式，替换掉一些组件
        try {
            HookHelper.hookAMS(); // 替换 IActivityManager

            HookHelper.hookHandler(); // 替换 mH 的 callback

            //HookHelper.hookAMSForService(); // 替换 IActivityManager
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


package com.guohao.common.app;

import android.app.Activity;
import android.os.Bundle;

import com.guohao.factory.Factory;
import com.guohao.xtalker.AppMessageReceiverService;
import com.guohao.xtalker.AppPushService;
import com.igexin.sdk.PushManager;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 调用Factory进行初始化
        Factory.setup();

        // 注册生命周期
        registerActivityLifecycleCallbacks(new PushInitializeLifecycle());

    }


    /**
     * 个推服务在部分手机上极易容易回收，可放Resumed中唤起
     */
    private class PushInitializeLifecycle implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(android.app.Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(android.app.Activity activity) {

        }

        @Override
        public void onActivityResumed(android.app.Activity activity) {
            // 推送进行初始化
            PushManager.getInstance().initialize(App.this, AppPushService.class);
            // 推送注册消息接收服务
            PushManager.getInstance().registerPushIntentService(App.this, AppMessageReceiverService.class);
        }

        @Override
        public void onActivityPaused(android.app.Activity activity) {

        }

        @Override
        public void onActivityStopped(android.app.Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(android.app.Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

}

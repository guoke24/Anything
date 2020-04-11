package com.guohao.anything.Hook;

import android.content.Intent;

import com.guohao.anything.Hook.HookActivity.StubActivity;
import com.guohao.anything.Hook.HookService.ProxyService;
import com.guohao.anything.LogUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class IActivityManagerProxy implements InvocationHandler {
    private Object mActivityManager;
    private static final String TAG = "IActivityManagerProxy";
    public IActivityManagerProxy(Object activityManager) {
        this.mActivityManager = activityManager;
    }
    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        // 拦截 startActivity 函数
        if ("startActivity".equals(method.getName())) {//1
            Intent intent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            intent = (Intent) args[index];
            Intent subIntent = new Intent();//2
            String packageName = "com.guohao.anything";
            String clsName = StubActivity.class.getName();// com.guohao.anything.Hook.HookActivity.StubActivity
            subIntent.setClassName(packageName,clsName);//3
            subIntent.putExtra(HookHelper.TARGET_INTENT, intent);//4 TargetActivity的Intent保存到subIntent中，便于以后还原TargetActivity
            args[index] = subIntent;//5
            LogUtil.e("invoke startActivity");
        }

        // 拦截 startService 函数
        if("startService".equals(method.getName())){
            Intent intent = null;
            int index = 0;
            for(int i = 0; i < args.length; i++){
                if(args[i] instanceof Intent){
                    index = i;
                    break;
                }
            }

            intent = (Intent) args[index];
            Intent proxyIntent = new Intent();
            String packageName = "com.guohao.anything";
            String clsName = ProxyService.class.getName();
            proxyIntent.setClassName(packageName,clsName);
            proxyIntent.putExtra(ProxyService.TARGET_SERVICE,intent.getComponent().getClassName());
            args[index] = proxyIntent;//5
            LogUtil.e("invoke startService");
        }

        // 继续原来逻辑
        return method.invoke(mActivityManager, args);
    }
}


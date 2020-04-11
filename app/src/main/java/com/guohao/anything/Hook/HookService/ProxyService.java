package com.guohao.anything.Hook.HookService;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.guohao.anything.Hook.FieldUtil;
import com.guohao.anything.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ProxyService extends Service {

    public static final String TARGET_SERVICE = "target_service";

    public ProxyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 代理分发
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e("");

        if( null == intent || !intent.hasExtra(TARGET_SERVICE)){
            return START_STICKY;
        }

        String serviceName = intent.getStringExtra(TARGET_SERVICE);
        if( null == serviceName){
            return START_STICKY;
        }

        Service targetService = null;

        try {
            // 得到类型：ActivityThread
            Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");

            // 得到 ActivityThread 实例，也是 ActivityThread 类的静态的具体字段
            Object activityThread = FieldUtil.getField(activityThreadClazz,null,"sCurrentActivityThread");

            // 得到方法字段：getActivityThreadMethod()
            Method getActivityThreadMethod = activityThreadClazz.getDeclaredMethod("getApplicationThread");

            // 允许访问
            getActivityThreadMethod.setAccessible(true);

            // 得到 ApplicationThread 实例，by 反射调用
            Object applicationThread = getActivityThreadMethod.invoke(activityThread);

            // ApplicationThread extends IApplicationThread.Stub，实现 IInterface 接口

            // 得到类型：IInterface
            Class iInterfaceClass = Class.forName("android.os.IInterface");

            // 得到方法字段： asBinder
            Method asBinderMethod = iInterfaceClass.getDeclaredMethod("asBinder");

            // 允许访问
            asBinderMethod.setAccessible(true);

            // 得到 token，by 反射调用
            Object token = asBinderMethod.invoke(applicationThread);

            // 得到类型： Service
            Class serviceClazz = Class.forName("android.app.Service");

            // 得到方法字段：attach
            Method attachMethod = serviceClazz.getDeclaredMethod("attach",
                    Context.class,activityThreadClazz,String.class,IBinder.class,
                    Application.class,Object.class);

            // 允许访问
            attachMethod.setAccessible(true);

            Object defaultSingleton = null;

            if (Build.VERSION.SDK_INT >= 26) {

                Class<?> activityManageClazz = Class.forName("android.app.ActivityManager");

                // 获取 activityManager 中的 IActivityManagerSingleton 字段
                defaultSingleton=  FieldUtil.getField(activityManageClazz ,null,"IActivityManagerSingleton");

            } else {

                Class<?> activityManagerNativeClazz = Class.forName("android.app.ActivityManagerNative");

                //获取 ActivityManagerNative 中的 gDefault 字段
                defaultSingleton=  FieldUtil.getField(activityManagerNativeClazz,null,"gDefault");

            }

            // 得到类型 Singleton -- defaultSingleton
            Class<?> singletonClazz = Class.forName("android.util.Singleton");

            // 得到抽象字段
            Field mInstanceField= FieldUtil.getField(singletonClazz ,"mInstance");

            // 获取具体字段 iActivityManager
            Object iActivityManager = mInstanceField.get(defaultSingleton);

            // 新建目标 service
            targetService = (Service) Class.forName(serviceName).newInstance();

            // 反射调用，初始化目标 service
            attachMethod.invoke(targetService,this,activityThread,intent.getComponent().getClassName(),
                    token,getApplication());

            // 开启生命周期
            targetService.onCreate();


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        targetService.onStartCommand(intent,flags,startId);

        LogUtil.e(" return START_STICKY ");
        return START_STICKY;
    }
}

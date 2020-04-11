package com.guohao.anything.Hook.HookSimpleDemo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.guohao.anything.R;

import java.lang.reflect.Field;

public class HookSimpleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_test);

//        replaceInstrumentation(this);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("https://guoke24.top"));
//        startActivity(intent);

        replaceContextInstrumentation();
        Intent intent2 = new Intent(Intent.ACTION_VIEW);
        intent2.setData(Uri.parse("https://guoke24.top"));
        //intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent2);

    }

    // 替换 Activity 中的 Instrumentation 实例
    public void replaceInstrumentation(Activity activity){
        try {
            // 拿到字段
            Field field = Activity.class.getDeclaredField("mInstrumentation");

            // --- 小知识点：字段还不是具体的成员变量，因为没有指定某个具体的实例

            // 取消权限控制
            field.setAccessible(true);

            // 反射拿到原来的实例
            Instrumentation instrumentation = (Instrumentation) field.get(activity);

            // 新建代理实例，并持有原来的实例；
            // 代理类执行完自己的逻辑后，调用原来的实例继续原来的流程
            Instrumentation instrumentationProxy = new InstrumentationProxy(instrumentation);

            // 完成替换
            field.set(activity,instrumentationProxy);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // 替换 Context 中的的 Instrumentation 实例
    public void replaceContextInstrumentation(){

        try {
            // 拿到类型，即 ActivityThread 类型的引用
            Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");

            // 拿到字段
            Field activityThreadField = activityThreadClazz.getDeclaredField("sCurrentActivityThread");

            // 允许访问
            activityThreadField.setAccessible(true);

            // 拿到具体的成员变量，即 ActivityThread 实例
            Object currentActivityThread = activityThreadField.get(null);// 静态成员变量，所以传参为 null
            // 此时拿到了当前的 Activity 线程实例

            // --- 小知识点：静态成员变量，是依附类存在的，一个类只有一个
            // 动态成员变量，是依附具体的实例存在的，一个实例只有一个 ---

            // 拿到字段
            Field instrumentationField = activityThreadClazz.getDeclaredField("mInstrumentation");

            // 允许访问
            instrumentationField.setAccessible(true);

            // 反射，拿到 ActivityThread 实例的成员变量，即 Instrumentation 的实例
            Instrumentation instrumentation = (Instrumentation) instrumentationField.get(currentActivityThread);

            // 新建代理实例，并持有原来的实例；
            // 代理类执行完自己的逻辑后，调用原来的实例继续原来的流程
            Instrumentation instrumentationProxy = new InstrumentationProxy(instrumentation);

            // 完成替换
            instrumentationField.set(currentActivityThread,instrumentationProxy);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

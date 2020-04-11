package com.guohao.anything.Hook.HookSimpleDemo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.guohao.anything.LogUtil;

import java.lang.reflect.Method;


public class InstrumentationProxy extends Instrumentation {
    private Instrumentation mInstrumentation;

    public InstrumentationProxy(Instrumentation instrumentation) {
        mInstrumentation = instrumentation;

    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {

        LogUtil.e("Hook 成功" + "---who：" + who);

        try {

            Method execMethod = Instrumentation.class.getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);

            return (ActivityResult) execMethod.invoke(mInstrumentation, who, contextThread, token,
                    target, intent, requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
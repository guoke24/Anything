package com.guohao.anything.Hook.HookService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.guohao.anything.LogUtil;

public class TargetService extends Service {
    public TargetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtil.e("");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e("");
        return super.onStartCommand(intent, flags, startId);
    }
}

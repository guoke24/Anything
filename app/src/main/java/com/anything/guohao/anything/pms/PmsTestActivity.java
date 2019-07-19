package com.anything.guohao.anything.pms;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.anything.guohao.anything.AssetsUtils;
import com.anything.guohao.anything.BaseTestActivity;
import com.anything.guohao.anything.LogUtil;
import com.anything.guohao.anything.R;

import java.util.ArrayList;
import java.util.List;

public class PmsTestActivity extends BaseTestActivity {

    String SmartPhonePos_apk = "jxnx_acquire_22_2_9_03_release_20190520.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pms_test);
        super.onCreate(savedInstanceState);

    }

    // 参考 https://blog.csdn.net/qiaowe/article/details/79100241
    public void test_1(View view){
        showMessage("PmsTestActivity test_1:");
        String path = AssetsUtils.fileOpt(SmartPhonePos_apk, this);

        PackageManager pm = getPackageManager();
        PackageInfo appInfo = pm.getPackageArchiveInfo(path,
                PackageManager.GET_ACTIVITIES);

        String packageName = appInfo.packageName;
        String label = (String) pm.getApplicationLabel(appInfo.applicationInfo);
        //注意，这里是不能直接从apk中获取到正确的label，

        //从已安装的apk里，才能获取到正确的label
        //获取手机内所有应用
        List<PackageInfo> paklist = pm.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                LogUtil.e(" 已安装apk： " + (String) pm.getApplicationLabel(pak.applicationInfo));
            }
        }


        LogUtil.d("packageName = " + packageName);
        LogUtil.d("label = " + label);

    }



}

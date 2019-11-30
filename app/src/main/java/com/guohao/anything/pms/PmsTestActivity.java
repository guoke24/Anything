package com.guohao.anything.pms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.guohao.anything.AssetsUtils;
import com.guohao.anything.BaseTestActivity;
import com.guohao.anything.LogUtil;
import com.guohao.anything.R;

import java.util.List;

public class PmsTestActivity extends BaseTestActivity {

    String SmartPhonePos_apk = "jxnx_acquire_22_2_9_03_release_20190520.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pms_test);
        super.onCreate(savedInstanceState);

    }

    // 参考 https://blog.csdn.net/qiaowe/article/details/79100241
    public void test_1(View view) {
        showMessage("PmsTestActivity test_1:");
        String path = AssetsUtils.fileOpt(SmartPhonePos_apk, this);

        PackageManager pm = getPackageManager();
        PackageInfo appInfo = pm.getPackageArchiveInfo(path,
                PackageManager.GET_ACTIVITIES);

        String packageName = appInfo.packageName;
        String label = (String) pm.getApplicationLabel(appInfo.applicationInfo);
        //注意，这里是不能直接从apk中获取到正确的label，
        LogUtil.d("packageName = " + packageName);
        LogUtil.d("label = " + label);


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


    }

    public void test_2(View view) {
        addShortCutByCustom("com.android.settings",R.drawable.app_charityapp_smttx,true);
    }

    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    // 添加快捷方式 示例
    public void addShortcutBelowAndroidN(Context context) {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);
        // 不允许重复创建，不是根据快捷方式的名字判断重复的
        addShortcutIntent.putExtra("duplicate", true);
        // 名字
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Shortcut Name");
        // 图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.app_charityapp_smttx));
        // 设置关联程序
        Intent launcherIntent = new Intent();
        launcherIntent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
        //launcherIntent.setComponent(new ComponentName("com.charityapp", "com.charityapp.activity.LoginActivity"));
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
        // 发送广播
        sendBroadcast(addShortcutIntent);
    }

    // 根据包名添加快捷方式，根据包名解析出app名，自定义图标
    // 入参：
    // 包名，据此解析出app名字
    // 图标id，使用自定义的图标
    public void addShortCutByCustom(String pkName, int resID, boolean canDuplicate) {

        //根据包名，获取该app的图标，app名
        PackageManager mPm = getPackageManager();
//        String pkName = "com.android.settings";
//        int resID = R.mipmap.ic_launcher;
        try {
            PackageInfo charityapp = mPm.getPackageInfo(pkName, 0);//没有系统权限，会导致获取不到系统app

            String appName = (String) mPm.getApplicationLabel(charityapp.applicationInfo);
            //String appName = "yidi";
            LogUtil.e("appName = " + appName);

            Intent addShortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            // 不允许重复创建，不是根据快捷方式的名字判断重复的
            addShortcutIntent.putExtra("duplicate", !canDuplicate);
            // 名字
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
            // 图标
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, resID));

            // 设置关联程序
            //Intent launcherIntent = new Intent();
            //launcherIntent.setComponent(new ComponentName("com.charityapp", "com.charityapp.activity.LoginActivity"));

            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mPm.getLaunchIntentForPackage(pkName));

            // 发送广播
            sendBroadcast(addShortcutIntent);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}

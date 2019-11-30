package com.guohao.anything;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.guohao.anything.R;
import com.guohao.anything.hotfix.BugTest;
import com.guohao.anything.hotfix.FixDexUtil;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void test_1(View v) {
        new BugTest().getBug(MainActivity.this);
    }


    public void test_2(View v) {
        // 热修复
        // 先检测是否有补丁dex，检测到，就先add到loadedDex
        if (FixDexUtil.isGoingToFix(MainActivity.this, getFilesDir() /*new File(getFilesDir(), "odex")*/)) {
            // 从loadedDex集合取出补丁dex，加载
            FixDexUtil.loadFixedDex(MainActivity.this);
        } else {
            Toast.makeText(this, "没有补丁", Toast.LENGTH_SHORT).show();
        }
    }

    public void test_3(View v) {
        // 把 assets 的 xxx.apk，copy 到 context.getFilesDir() 的路径下
        // 即 /data/user/0/com.anything.guohao.anything/files
        String path = AssetsUtils.fileOpt("classes.dex", this);
        if (path != null) {
            Toast.makeText(this, "加载补丁完成", Toast.LENGTH_SHORT).show();
        }
    }

    // 参考：https://blog.csdn.net/u013082948/article/details/71473594
    // 测试：Settings.System.putInt
    public void test_4(View v) {
        //ContentResolver resolver = getContentResolver(); //mContext为所在应用的上下文
        Settings.Global.putInt(getContentResolver(), "inputFlag", 1);// 正常运行

        // 报错：You cannot keep your settings in the secure settings
        //Settings.System.putInt(getContentResolver(), "inputFlag", 2);

        Settings.Secure.putInt(getContentResolver(), "inputFlag", 2);// 正常运行
    }

    public void test_5(View v) {
        int i = Settings.Global.getInt(getContentResolver(), "inputFlag", 3);
        //int j = Settings.System.getInt(getContentResolver(), "inputFlag", 4);
        int j = Settings.Secure.getInt(getContentResolver(), "inputFlag", 4);
        LogUtil.e("i = " + i);
        LogUtil.e("j = " + j);
    }
    // end 测试：Settings.System.putInt

    public void test_6(View c) {
        // 尝试重启：com.android.systemui，貌似无效
        restartApp(this,"com.android.systemui");
    }

    /**
     * 重启app
     *
     * @param context
     */
    public static void restartApp(Context context,String packageName) {
        PackageManager packageManager = context.getPackageManager();
        if (null == packageManager) {
            LogUtil.e("null == packageManager");
            return;
        }
        final Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }else{
            Toast.makeText(context,"无应用",Toast.LENGTH_SHORT).show();
        }
    }


}

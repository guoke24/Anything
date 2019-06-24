package com.anything.guohao.anything;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anything.guohao.anything.hotfix.BugTest;
import com.anything.guohao.anything.hotfix.FixDexUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void test_1(View v){
        new BugTest().getBug(MainActivity.this);
    }



    public void test_2(View v){
        // 热修复
        // 先检测是否有补丁dex，检测到，就先add到loadedDex
        if (FixDexUtil.isGoingToFix(MainActivity.this, getFilesDir() /*new File(getFilesDir(), "odex")*/)) {
            // 从loadedDex集合取出补丁dex，加载
            FixDexUtil.loadFixedDex(MainActivity.this);
        }else{
            Toast.makeText(this,"没有补丁",Toast.LENGTH_SHORT).show();
        }
    }

    public void test_3(View v){
        // 把 assets 的 sougou.apk，copy 到 context.getFilesDir() 的路径下
        // 即 /data/user/0/com.anything.guohao.anything/files
        String path = AssetsUtils.fileOpt("classes.dex",this);
        if(path != null){
            Toast.makeText(this,"加载补丁完成",Toast.LENGTH_SHORT).show();
        }
    }

}

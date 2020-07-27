package com.guohao.anything.shellTest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.guohao.anything.R;


// 参考：https://www.cnblogs.com/zhujiabin/p/10495214.html
// 有权限问题，所以几乎不能用...
public class Main5Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test_1(View view){
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("sudo ls",false);
        Log.i("guohao-shell","successMsg " + commandResult.successMsg);
        Log.i("guohao-shell","errorMsg " + commandResult.errorMsg);
        Log.i("guohao-shell","result " + commandResult.result);
    }


}

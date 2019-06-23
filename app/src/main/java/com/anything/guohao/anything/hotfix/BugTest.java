package com.anything.guohao.anything.hotfix;

import android.content.Context;
import android.widget.Toast;

public class BugTest {
    public void getBug(Context context) {
        //模拟一个bug
        int i = 10;
        int a = 1;
        Toast.makeText(context, "Hello,Minuit:" + i / a, Toast.LENGTH_SHORT).show();
    }
}

package com.guohao.anything.viewtest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guohao.anything.R;

/**
 * 已知 一个Activity 的根视图是 DecorView，那么 DecorView 内的视图结构是怎样的呢？
 *
 * 结构如下：
 *
 * 第0层：DecorView
 * -第1层，第1个View：LinearLayout （screen_simple.xml）
 * --第2层，第1个View：ViewStub
 * --第2层，第2个View：FrameLayout（装载 activity_my.xml 的父容器，id 为 content）
 * ---第3层，第1个View：LinearLayout（activity_my.xml ）
 * ----第4层，第1个View：TextView
 */
public class MyActivity extends Activity {

    FrameLayout frameLayout;
    LinearLayout linearLayout;
    TextView textView;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ViewUtils.listViewTree(getWindow().getDecorView());
    }
}

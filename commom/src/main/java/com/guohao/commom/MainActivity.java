package com.guohao.commom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;


public class MainActivity extends AppCompatActivity {

    // for butterknife module ，参考：https://github.com/JakeWharton/butterknife
    @BindView(R2.id.tx2)//注意此处需要用R2
    TextView tx2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

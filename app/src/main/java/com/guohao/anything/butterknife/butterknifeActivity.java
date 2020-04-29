package com.guohao.anything.butterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.guohao.anything.R;


import butterknife.BindView;
import butterknife.ButterKnife;
// 参考：[Android Butterknife使用方法总结](https://www.jianshu.com/p/3678aafdabc7)
public class butterknifeActivity extends AppCompatActivity {

    @BindView(R.id.tx1)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_butterknife);
        // 必须在setContentView之后
        ButterKnife.bind(this);

    }
}

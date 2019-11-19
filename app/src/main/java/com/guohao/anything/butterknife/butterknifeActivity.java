package com.guohao.anything.butterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.anything.guohao.anything.R;


import butterknife.BindView;

public class butterknifeActivity extends AppCompatActivity {

    @BindView(R.id.tx1)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_butterknife);

    }
}

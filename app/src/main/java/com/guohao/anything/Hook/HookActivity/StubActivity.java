package com.guohao.anything.Hook.HookActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.guohao.anything.R;

public class StubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
    }
}

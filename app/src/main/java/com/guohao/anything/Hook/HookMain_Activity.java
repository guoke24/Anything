package com.guohao.anything.Hook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.guohao.anything.Hook.HookActivity.TargetActivity;
import com.guohao.anything.Hook.HookService.TargetService;
import com.guohao.anything.R;

public class HookMain_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_);
    }

    public void startTargetActivity(View view){
        //LogUtil.e( SubActivity.class.getName() );// com.guohao.anything.Hook.HookActivity.SubActivity
        Intent intent = new Intent(HookMain_Activity.this, TargetActivity.class);
        startActivity(intent);
    }

    public void startTargetSerivce(View view){
        Intent intent = new Intent(HookMain_Activity.this, TargetService.class);
        startService(intent);
    }
}

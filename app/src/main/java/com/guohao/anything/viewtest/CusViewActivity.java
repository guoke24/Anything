package com.guohao.anything.viewtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;



import com.guohao.anything.PackagesListActivity;
import com.guohao.anything.R;



public class CusViewActivity extends Activity {

    CusView cusView1;
    CusView cusView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_view);
        //initView();

        ViewUtils.listViewTree(getWindow().getDecorView());
    }

    // 给两个控件设置回调
    public void initView(){
        cusView1 = findViewById(R.id.customView);
        cusView1.setCallBack(new CusView.CallBack() {
            @Override
            public void onMove(MotionEvent event) {
                checkIfSamePosition();
            }
        });

        cusView2 = findViewById(R.id.customView2);
        cusView2.setCallBack(new CusView.CallBack() {
            @Override
            public void onMove(MotionEvent event) {
                checkIfSamePosition();
            }
        });
    }


    // 检查是否位置重合
    private void checkIfSamePosition(){
        if(cusView1.getX() > cusView2.getX()){
            //
            //Toast.makeText(this,"SamePosition!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,PackagesListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    // 点击响应
    public void btClick(View v){
        ViewUtils.listViewTree(getWindow().getDecorView());
    }
}

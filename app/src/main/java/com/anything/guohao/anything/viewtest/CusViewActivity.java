package com.anything.guohao.anything.viewtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.anything.guohao.anything.PackagesListActivity;
import com.anything.guohao.anything.R;

public class CusViewActivity extends AppCompatActivity {

    CusView cusView1;
    CusView cusView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_view);
        initView();
    }

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



    public void checkIfSamePosition(){
        if(cusView1.getX() > cusView2.getX()){
            //
            //Toast.makeText(this,"SamePosition!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,PackagesListActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

}

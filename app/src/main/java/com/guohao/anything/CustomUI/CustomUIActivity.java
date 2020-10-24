package com.guohao.anything.CustomUI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dalong.marqueeview.MarqueeView;
import com.guohao.anything.R;

public class CustomUIActivity extends AppCompatActivity {

    SeekBar seekBar;
    MarqueeView marqueeView;
    TextView textView;
    MyTextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_ui);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("guohao"," " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("guohao","onStartTrackingTouch");

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("guohao","onStopTrackingTouch");
            }
        });

        marqueeView = findViewById(R.id.mMarqueeView);
        marqueeView.setText("124545464575658");
        myTextView = findViewById(R.id.TextViewNotice);
    }

    int i = 0;

    public void test1(View c){
        marqueeView.setText("124545464575658");
        marqueeView.startScroll();

//        myTextView.setText("哈哈哈哈哈哈哈");
//        myTextView.init(getWindowManager());
//        myTextView.startScroll();//启动
        myTextView.setText( String.valueOf(++i) + myTextView.getText());

    }

}

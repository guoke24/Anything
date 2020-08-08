package com.guohao.anything.ui_sample;

import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.guohao.anything.R;

public class PicPublicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_public);

        initView();

    }

    private void initView(){
        TabLayout tabLayout = findViewById(R.id.tab1);
        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("图片"));
        tabLayout.addTab(tabLayout.newTab().setText("视频"));

        //tabLayout.setSelectedTabIndicatorHeight(1);

        Toolbar toolbar = findViewById(R.id.toolbar);

        //setTitleCenter(toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        initToolbar(toolbar);
    }

    public void initToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        initTitleNeedBack();
    }

    protected void initTitleNeedBack() {
        // 设置左上角的返回按钮为实际的返回效果
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    // title 居中
    public void setTitleCenter(Toolbar toolbar) {
        String title = "title";
        final CharSequence originalTitle = toolbar.getTitle();
        toolbar.setTitle(title);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (title.equals(textView.getText())) {
                    textView.setGravity(Gravity.CENTER);
                    Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
                    params.gravity = Gravity.CENTER;
                    textView.setLayoutParams(params);
                }
            }
            toolbar.setTitle(originalTitle);
        }
    }
}

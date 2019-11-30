package com.guohao.anything.FragmentDemo;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AnticipateOvershootInterpolator;

import com.guohao.anything.R;
import com.guohao.xtalker.frags.main.ActiveFragment;
import com.guohao.xtalker.frags.main.ContactFragment;
import com.guohao.xtalker.frags.main.GroupFragment;

import net.qiujuer.genius.ui.animation.AnimatorListener;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Home2Activity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        ButterKnife.bind(this);

        // 添加对底部按钮点击的监听
        mNavigation.setOnNavigationItemSelectedListener(this);

        // 获取fragment的事务
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    }

    // 加上此注解，该按钮才可以被点击
    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        Log.d("guohao", "click search");
    }

    //点击底部导航栏按钮触发的函数
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // 切换fragment
        switchFragment(item);

        // 浮动按钮的动画效果小demo
        //testAnimation();
        return true;
    }

    private void switchFragment(MenuItem item) {

        Log.d("guohaox", "click ItemTitle = " + item.getTitle());
        Log.d("guohaox", "click ItemId = " + item.getItemId());//R.id.action_home

        // 获取fragment的事务
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();


        if (item.getTitle().equals(getString(R.string.action_home))) {
            Log.d("guohaox", "click home");
            ft.replace(R.id.lay_container,new ActiveFragment(),"");
        } else if (item.getTitle().equals(getString(R.string.action_group))) {
            Log.d("guohaox", "click group");
            ft.replace(R.id.lay_container,new GroupFragment(),"");
        } else {
            Log.d("guohaox", "click contact");
            ft.replace(R.id.lay_container,new ContactFragment(),"");
        }

        // 提交事务
        ft.commit();

    }

    // 浮动按钮的动画效果小demo
    private void testAnimation() {

        Log.d("guohaox", "x = " + mAction.getX() + "transX = " + mAction.getTranslationX());


        // 此时的坐标是以控件初始位置为基准的
        float transX = -884;
        float transY = -333;
        float rotation = 180;

        if (mAction.getTranslationX() < 0) {//向左移动来
            transX = 0;// 回到原位
            transY = 0;
            rotation = 0;
        }

        // 开始动画
        // 旋转，X/Y轴位移，弹性差值器（弹一弹的效果，数值越大，弹动幅度越大），时间
        mAction.animate()
                .rotation(rotation)
                .translationX(transX)// 表示移动到的x轴的位置
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(2000)
                .setListener(new AnimatorListener() {// 监听动画结束
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (mAction.getTranslationX() < 0) {
                            testAnimation();
                        }
                    }
                })
                .start();
    }
}

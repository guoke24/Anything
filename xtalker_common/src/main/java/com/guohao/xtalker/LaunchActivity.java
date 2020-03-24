package com.guohao.xtalker;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;

import com.guohao.common.app.Activity;
import com.guohao.common.frags.PermissionsFragment;
import com.guohao.factory.persistence.Account;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

/**
 * 此类逻辑概述：
 *
 * startAnim 函数，动画播放一半，触发 waitPushReceiverId 函数
 *
 * waitPushReceiverId 函数，判断是否有 pushID
 * 一直循环等待，知道拿到 pushID，触发 waitPushReceiverIdDone 函数
 *
 * waitPushReceiverIdDone 函数，调用 startAnim 函数
 * startAnim 函数，将动画播放完毕，并触发 reallySkip 函数
 *
 * reallySkip 函数，判断权限
 * 授权之后，就可以跳转到下一个界面：登录界面 或 主界面
 *
 */
public class LaunchActivity extends Activity {

    // Drawable
    private ColorDrawable mBgDrawable;
    // 是否已经得到PushId
    private boolean mAlreadyGotPushReceiverId = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_launch);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 拿到跟布局
        View root = findViewById(R.id.activity_launch);
        // 获取颜色
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        // 创建一个Drawable
        ColorDrawable drawable = new ColorDrawable(color);
        // 设置给背景
        root.setBackground(drawable);
        mBgDrawable = drawable;

    }

    @Override
    protected void initData() {
        super.initData();

        // 动画进入到50%等待PushId获取到
        // 检查等待状态
        startAnim(0.5f, this::waitPushReceiverId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 判断是否已经得到推送Id，如果已经得到则进行跳转操作，
        // 在操作中检测权限状态
        if (mAlreadyGotPushReceiverId) {
            reallySkip();
        }
    }

    /**
     * 等待个推框架对我们的PushId设置好值
     */
    private void waitPushReceiverId() {
        if (Account.isLogin()) {
            // 已经登录情况下，判断是否绑定，已经绑定说明后台已经拿到 PushId
            // 如果没有绑定则等待广播接收器进行绑定
            if (Account.isBind()) {
                waitPushReceiverIdDone();
                return;
            }
        } else {
            // 没有登录
            // 如果拿到了PushId，也可以下一步
            // 登录时绑定PushId的
            if (!TextUtils.isEmpty(Account.getPushId())) {
                // 跳转
                waitPushReceiverIdDone();
                return;
            }
        }

        // 循环等待
        getWindow().getDecorView()
                .postDelayed(this::waitPushReceiverId, 500);
    }


    /**
     * 在跳转之前需要把剩下的50%进行完成
     */
    private void waitPushReceiverIdDone() {
        // 标志已经得到PushId
        mAlreadyGotPushReceiverId = true;
        startAnim(1f, this::reallySkip);
    }

    /**
     * 真实的跳转
     */
    private void reallySkip() {
        // 权限检测，跳转
        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            // 检查跳转到主页还是登录
            if (Account.isLogin()) {
                MainXActivity.show(this);
            } else {
                AccountActivity.show(this);
            }
            finish();
        }
    }

    /**
     * 给背景设置一个动画
     *
     * @param endProgress 动画的结束进度
     * @param endCallback 动画结束时触发
     */
    private void startAnim(float endProgress, final Runnable endCallback) {
        // 获取一个最终的颜色
        int finalColor = Resource.Color.WHITE; // UiCompat.getColor(getResources(), R.color.white);
        // 运算当前进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        // 构建一个属性动画
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        valueAnimator.setDuration(1500); // 时间
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor); // 开始结束值
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 结束时触发
                endCallback.run();
            }
        });
        valueAnimator.start();
    }


    private final Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }

        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }
    };
}

package com.anything.guohao.anything.viewtest;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.anything.guohao.anything.LogUtil;

public class CusView extends View {

    private int lastX;
    private int lastY;

    CallBack mCallBack;

    interface CallBack{
        void onMove(MotionEvent event);
    }

    public void setCallBack(CallBack cb){
        mCallBack = cb;
    }

    public CusView(Context context) {
        super(context);
    }

    public CusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        LogUtil.e("" + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.e(""+ event.getAction());
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - lastX;
                int offsetY = y - lastY;
                layout(getLeft()+offsetX,getTop()+offsetY,
                        getRight()+offsetX,getBottom()+offsetY);
                break;
        }

        if(mCallBack != null){
            mCallBack.onMove(event);
        }

        return true;
    }


    // 测量 test


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w_mode = MeasureSpec.getMode(widthMeasureSpec) >> 30;
        LogUtil.e("w_mode = " + w_mode);

        LogUtil.e("WRAP_CONTENT = " + ViewGroup.LayoutParams.WRAP_CONTENT);


        int h_mode = MeasureSpec.getMode(heightMeasureSpec) >> 30;
        LogUtil.e("h_mode = " + h_mode);

        LogUtil.e("\n");

        // 1 代表 exactly ，2 代表 at_most
        // 该 view 的父view 的 mode应该是 exactly
        // 该 view 的 w，h 都是 wrap_content，即 ViewGroup.LayoutParams.WRAP_CONTENT = -2

        // 该函数被回调类8次，而且 传进来的 w 和 h 的 mode 会变化，开始是-2，后面会变为1，为什么会这样？

    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);

    }
}

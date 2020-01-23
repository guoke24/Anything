package com.guohao.anything;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 这是一个可滑动的view
 *
 * 通过重写 onTouchEvent 函数来实现滑动，并解决和其父容器滑动冲突
 *
 * 调用链：
 * dispatchTouchEvent
 *   onInterceptTouchEvent
 *   child.dispatchTouchEvent
 *   onTouchEvent
 *
 */
public class CusLinearLayout extends LinearLayout {
    public CusLinearLayout(Context context) {
        super(context);
    }

    public CusLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CusLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int lastX;
    private int lastY;

    private int initLeft; // 用于恢复初始位置
    private int initRight;


    // onInterceptTouchEvent 函数的调用时机
    // 只有两种情况会调用 onInterceptTouchEvent 函数
    // 第一种情况：down 事件
    // 第二种情况：move/up 事件 且有子View消费事件（即 mFirstTouchTarget != null）
    // 满足这两种情况之后，还要受 flag disallowIntercept 的影响
    // 子view 可以通过调用 getParent().requestDisallowInterceptTouchEvent 来设置该值
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // 判断滑动的距离是否超过
                int offsetX = x - lastX;
                int offsetY = y - lastY;
                if(Math.abs(offsetX) > 0){
                    getParent().requestDisallowInterceptTouchEvent(true);//后续事件都分发下来
                    // 在此不生效的原因：在于 onInterceptTouchEvent 函数的调用时机
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                initLeft = getLeft();
                initRight = getRight();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - lastX;
                int offsetY = y - lastY;
                // 实现滑动
                layout(getLeft()+offsetX,getTop(),
                        getRight()+offsetX,getBottom());
                if( Math.abs(offsetX) > Math.abs(offsetY)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    //父容器不再拦截，后续事件都分发下来，解决和其父容器滑动冲突
                }
                break;
            case MotionEvent.ACTION_UP:
                // 恢复原样，可加上动画来优化
                layout(initLeft,getTop(),
                        initRight,getBottom());
                break;
        }

        // 走的是 View#onTouchEvent
        return super.onTouchEvent(event);
    }

}

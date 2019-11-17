package com.guohao.anything;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

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

    private int initX;
    private int initX2;

    boolean flag = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        if(!flag){
            initX = getLeft();
            initX2 = getRight();
            flag = true;
        }

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - lastX;
                int offsetY = y - lastY;
                layout(getLeft()+offsetX,getTop(),
                        getRight()+offsetX,getBottom());
                break;
            case MotionEvent.ACTION_UP:
                // 恢复原样,逻辑待优化
                layout(initX,getTop(),
                        initX2,getBottom());
                break;
        }

        return super.onTouchEvent(event);
    }

}

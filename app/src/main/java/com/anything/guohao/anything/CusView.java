package com.anything.guohao.anything;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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
    public boolean onTouchEvent(MotionEvent event) {

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
}

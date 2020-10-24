package com.guohao.anything.CustomUI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;


public class CustomSeekBar extends android.support.v7.widget.AppCompatSeekBar {

    Paint paint = new Paint();

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 初始化...

    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画出4个点
        int x = getWidth();
        int y = getHeight();

        Log.i("guohao","x " + x);
        Log.i("guohao","y " + y);

        Log.i("guohao","getMatrix " + canvas.getMatrix().toString());

        canvas.save();
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.translate(0,0);// 移动了，不是移动到
        Log.i("guohao","getMatrix2 " + canvas.getMatrix().toString());

        canvas.drawText("hello",x/2,y/3,paint);
        canvas.restore();

        canvas.save();
        //canvas.translate(0,0);
        //canvas.drawText("hello",x/3,0,paint);

        canvas.restore();
    }
}

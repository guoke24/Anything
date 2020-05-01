package com.guohao.anything.viewtest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.guohao.anything.R;

// 自定义view
// [刘望舒 Android View体系（九）自定义View ](http://liuwangshu.cn/application/view/9-custom-view.html)
public class RectView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //private int mColor = Color.RED;
    private int mColor ;
    public RectView(Context context) {
        super(context);
        initDraw();
    }

    public RectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray=context.obtainStyledAttributes(attrs, R.styleable.RectView);
        //提取RectView属性集合的rect_color属性，如果没设置默认值为Color.RED
        mColor=mTypedArray.getColor(R.styleable.RectView_rect_color,Color.RED);
        //获取资源后要及时回收
        mTypedArray.recycle();
        initDraw();
    }

    public RectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDraw();
    }

    private void initDraw() {
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth((float) 1.5);
    }


    // 指定一个默认的宽和高，在设置wrap_content属性时设置此默认的宽和高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        // 再经过父容器的测量后，该view本身的宽高的规格会有三种可能：
        // UNSPECIFIED：未指定模式，View想多大就多大，父容器不做限制，一般用于系统内部的测量。
        // AT_MOST：最大模式，对应于wrap_comtent属性，只要尺寸不超过父控件允许的最大尺寸就行。
        // EXACTLY：精确模式，对应于match_parent属性和具体的数值，父容器测量出View所需要的大小，也就是specSize的值。

        if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){

            // setMeasuredDimension()方法接收的参数的单位是px
            setMeasuredDimension(400,400);

        }else if(widthSpecMode == MeasureSpec.AT_MOST){

            setMeasuredDimension(400,heightSpecSize);

        }else if(heightSpecMode == MeasureSpec.AT_MOST){

            setMeasuredDimension(widthSpecSize,400);

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 经过 measure 和 layout 流程，大小个位置固定了
        // 只能在 width 和 height 这块面积内作画
        // 即便 drawRect 指定的位置超过 width 和 height，也不会显示
        int width = getWidth();
        int height = getHeight();


        // 获取边距
        int paddingLeft=getPaddingLeft();
        int paddingRight=getPaddingRight();
        int paddingTop=getPaddingTop();
        int paddingBottom=getPaddingBottom();

        canvas.drawRect(0+paddingLeft, 0+paddingTop, width-paddingRight, height-paddingBottom, mPaint);

    }
}

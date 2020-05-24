package com.LagouAndroidShare.class17;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.IntRange;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Danny 姜
 */
public class PieImageView extends ImageView {

    private static final int MAX_PROGRESS = 100;
    private Paint mArcPaint;
    private RectF mBound;
    private Paint mCirclePaint;
    private int mProgress = 0;

    // 三个默认的构造函数
    public PieImageView(Context context) {
        this(context, null, 0);
    }

    public PieImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 设置进度，即扇形的大小（或者说扇形的胖瘦）
    public void setProgress(@IntRange(from = 0, to = MAX_PROGRESS) int mProgress) {
        this.mProgress = mProgress; // onDraw 根据此变量去绘制
        ViewCompat.postInvalidateOnAnimation(this);
    }

    // 初始化
    private void init() {
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mArcPaint.setStrokeWidth(dpToPixel(0.1f, getContext()));
        mArcPaint.setColor(Color.RED);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(dpToPixel(2, getContext()));
        mCirclePaint.setColor(Color.argb(120, 0xff, 0xff, 0xff));
        mBound = new RectF();
    }

    // 处理 wrap_content 模式
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // 判断是wrap_content的测量模式
        if (MeasureSpec.AT_MOST == widthMode || MeasureSpec.AT_MOST == heightMode) {
            int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
            int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
            // 将宽高设置为传入宽高的最小的那一边，作一个正方形大小
            int size = measuredWidth > measuredHeight ? measuredHeight : measuredWidth;
            // 调用setMeasuredDimension设置View实际大小
            setMeasuredDimension(size, size);
        }
    }

    // 尺寸变化时的处理逻辑
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int min = Math.min(w, h);
        int max = w + h - min;
        int r = Math.min(w, h) / 3;
        mBound.set((max >> 1) - r, (min >> 1) - r, (max >> 1) + r, (min >> 1) + r);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制扇形区域
        if (mProgress != MAX_PROGRESS && mProgress != 0) {
            float mAngle = mProgress * 360f / MAX_PROGRESS;
            canvas.drawArc(mBound, 270, mAngle, true, mArcPaint);
            canvas.drawCircle(mBound.centerX(), mBound.centerY(), mBound.height() / 2, mCirclePaint);
        }
    }

    private float scale = 0;

    private int dpToPixel(float dp, Context context) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * scale);
    }
}

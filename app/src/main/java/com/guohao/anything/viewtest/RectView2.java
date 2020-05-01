package com.guohao.anything.viewtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.guohao.anything.R;

public class RectView2 extends View {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public RectView2(Context context) {
        super(context);
        initDraw(context);
    }

    public RectView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDraw(context);
    }

    public RectView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDraw(context);
    }

    private void initDraw(Context context) {
        mPaint.setStrokeWidth((float) 1.5);

        //setBackgroundColor(Color.WHITE);
    }

    int spec = 400;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        testDraw1(canvas);

        testDraw2(canvas);

        testDraw3(canvas);
        testDraw4(canvas);
        testDraw5(canvas);

    }


    // 不带剪裁的画图，画出的内容会放到「帧缓存」中。插曲：cpu 负责处理 canvas 画的内容；提交后，再由 gpu 进行栅格化合成；最后由 display 拿来显示到屏幕上
    public void testDraw1(Canvas canvas){

        mPaint.setColor(Color.WHITE);

        int y1 = 100;
        int y2 = y1 + spec;

        canvas.drawRect(100, y1, 100 + spec, y2, mPaint);

        // 往右移动 100 像素，只改动 x 轴的两个值
        canvas.drawRect(200, y1, 200 + spec, y2, mPaint);

        // 再往右移动 100 像素
        canvas.drawRect(300, y1, 300 + spec, y2, mPaint);

    }

    // 对比 testDraw1 函数
    // 带剪裁的画图，避免不可见的部分也被绘制
    // clipRect 对 drawRect 不起作用
    public void testDraw2(Canvas canvas){

        mPaint.setColor(Color.WHITE);

        int x1 = 800;
        int x2 = x1 + spec;
        int y1 = 100;
        int y2 = y1 + spec;

        canvas.save();// 保存和重置，剪裁不会基于前面的操作
        canvas.clipRect(x1, y1, x1 + spec, y2);// 剪裁一个矩形区域，默认用 Region.Op.INTERSECT，但只有连续剪裁才会发挥作用
        canvas.drawRect(x1, y1, x1 + spec, y2, mPaint);
        canvas.restore();// 恢复之前的状态

        canvas.save();
        canvas.clipRect(x1 + 100, y1, x1 + 100 + spec, y2);
        canvas.drawRect(x1 + 100, y1, x1 + 100 + spec, y2, mPaint);
        canvas.restore();

        //canvas.save();
        //canvas.clipRect(300, y1, 300 + spec, y2);
        canvas.drawRect(x1 + 200, y1, x1 + 200 + spec, y2, mPaint);
        //canvas.restore();

        //invalidate();
    }

    // save：用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作。
    // restore：用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响。
    //
    //对canvas中特定元素的旋转平移等操作实际上是对整个画布进行了操作，
    // 所以如果不对canvas进行save以及restore，那么每一次绘图都会在上一次的基础上进行操作，最后导致错位。
    // save是入栈，restore是出栈。


    // 绘制三张 Bitmap 的图
    public void testDraw3(Canvas canvas){

        Bitmap bm = getBitmap(getContext(), R.mipmap.chris);// R.mipmap.ic_launcher will make nullpoint

        if(bm == null) return;

        Bitmap bm2 = Bitmap.createBitmap(bm);
        canvas.save();
        canvas.translate(100, 600); // 移动到 x,y = 100,1300 位置
        for (int i = 1; i <= 3;i++) {

            if(i > 1)
            canvas.translate(200, 0); // 每次左移 200 像素，

            canvas.drawBitmap(bm2, 0, 0, null);
        }
        canvas.restore();
    }

    // 对比 testDraw3 ，clipRect 起作用
    // 对比 testDraw3 ，clipRect 对 drawBitmap 起作用
    public void testDraw4(Canvas canvas){

        Bitmap bm = getBitmap(getContext(), R.mipmap.chris);

        if(bm == null) return;

        canvas.save();
        canvas.translate(100, 1300);
        for (int i = 1;i <= 3;i++) {

            if(i > 1)
            canvas.translate(200, 0);

            canvas.save();
            if (i < 3) {
                canvas.clipRect(0, 0, 200, bm.getHeight());
            }
            canvas.drawBitmap(bm, 0, 0, null);
            canvas.restore();
        }
        canvas.restore();
    }

    public void testDraw5(Canvas canvas){

        Bitmap bm = getBitmap(getContext(), R.mipmap.chris);

        if(bm == null) return;

        canvas.save();
        canvas.translate(100, 2000);
        for (int i = 1;i <= 3;i++) {

            if(i > 1)
                canvas.translate(200, 0);

            canvas.save();

            // 对比 testDraw4，若第三章也是剪裁再绘制
            // 实际结果是仅仅绘制跟第二种张中重合的部分
            canvas.clipRect(0, 0, 200, bm.getHeight());
            canvas.drawBitmap(bm, 0, 0, null);

            canvas.restore();
        }
        canvas.restore();
    }


    public static Bitmap getBitmap(Context context, int resId) {
        // 参考：[使用BitmapFactory的decodeResource方法加载图片的坑--被缩放问题](https://blog.csdn.net/qiantanlong/article/details/87712906)

        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        context.getResources().openRawResource(resId, value);
        options.inTargetDensity = value.density;
        options.inScaled=false;//不缩放
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }
}


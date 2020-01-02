package com.guohao.anything.viewtest;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.guohao.anything.LogUtil;
import static com.guohao.anything.viewtest.ViewUtils.Ms2Str;

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

    public CusLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        LogUtil.e("-----");
        LogUtil.e("start");
        Ms2Str(widthMeasureSpec,"宽");// 单位为px
        Ms2Str(heightMeasureSpec,"高");
        LogUtil.e("end");

    }
}

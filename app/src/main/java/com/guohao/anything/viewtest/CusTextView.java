package com.guohao.anything.viewtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.guohao.anything.LogUtil;
import static com.guohao.anything.viewtest.ViewUtils.Ms2Str;

@SuppressLint("AppCompatCustomView")
public class CusTextView extends TextView {
    public CusTextView(Context context) {
        super(context);
    }

    public CusTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        LogUtil.e("-----");
        LogUtil.e("start");
        Ms2Str(widthMeasureSpec,"CusTextView 宽");// 单位为px
        Ms2Str(heightMeasureSpec,"CusTextView 高");
        LogUtil.e("end");
    }
}

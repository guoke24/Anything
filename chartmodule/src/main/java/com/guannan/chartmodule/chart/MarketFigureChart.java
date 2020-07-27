package com.guannan.chartmodule.chart;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.guannan.chartmodule.helper.ChartTouchHelper;
import com.guannan.chartmodule.inter.IChartGestureListener;
import com.guannan.chartmodule.inter.IPressChangeListener;
import com.guannan.chartmodule.utils.DisplayUtils;

/**
 * @author guannan
 * @date on 2020-04-21 11:54
 * @des 行情图容器
 * #######################（1）动态添加主图和副图 ########################
 * #######################（2）处理主图和副图的手势 ######################
 */
public class MarketFigureChart extends LinearLayout implements IChartGestureListener {

  private Context mContext;

  private IPressChangeListener mPressChangeListener;

  public MarketFigureChart(Context context) {
    this(context, null);
  }

  public MarketFigureChart(Context context,
      @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MarketFigureChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.mContext = context;
    // 垂直
    setOrientation(VERTICAL);
    // 自身布局参数：横向填满，纵向填充
    setLayoutParams(
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    // 设置容器的手势监听
    ChartTouchHelper chartTouchHelper = new ChartTouchHelper(this);
    chartTouchHelper.setChartGestureListener(this);
    // 注意，ChartTouchHelper 内的 view 持有该 MarketFigureChart 实例的引用；
    // 通过设置 OnTouchListener 来拿到触摸事件
    // view.setOnTouchListener(this);

    // 触摸事件会分发整个 ViewTree，
    // 每个 View 的 onTouch、onTouchEvent 函数先后都能得到触摸事件

  }

  /**
   * 向容器中添加主图、附图视图
   */
  public void addChildChart(BaseChartView childView, float height) {
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            DisplayUtils.dip2px(mContext, height));
    addView(childView, params);
  }

  public void setPressChangeListener(IPressChangeListener listener) {
    this.mPressChangeListener = listener;
  }

  @Override
  public void onChartGestureStart(MotionEvent me,
      ChartTouchHelper.ChartGesture lastPerformedGesture) {

  }

  @Override
  public void onChartGestureEnd(MotionEvent me,
      ChartTouchHelper.ChartGesture lastPerformedGesture) {
    for (int i = 0; i < getChildCount(); i++) {
      BaseChartView baseChartView = (BaseChartView) getChildAt(i);
      if (baseChartView != null) {
        baseChartView.onChartGestureEnd(me, lastPerformedGesture);
      }
    }
  }

  @Override
  public void onChartLongPressed(MotionEvent me) {
    for (int i = 0; i < getChildCount(); i++) {
      BaseChartView baseChartView = (BaseChartView) getChildAt(i);
      if (baseChartView != null) {
        baseChartView.onChartLongPressed(me);
      }
    }
  }

  @Override
  public void onChartDoubleTapped(MotionEvent me) {

  }

  @Override
  public void onChartSingleTapped(MotionEvent me) {
    for (int i = 0; i < getChildCount(); i++) {
      BaseChartView baseChartView = (BaseChartView) getChildAt(i);
      if (baseChartView != null) {
        baseChartView.onChartSingleTapped(me);
      }
    }
  }

  @Override
  public void onChartFling(float distanceX) {
    if (mPressChangeListener != null) {
      mPressChangeListener.onChartFling(distanceX);
    }
  }

  @Override
  public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
    if (mPressChangeListener != null) {
      mPressChangeListener.onChartScale(me, scaleX, scaleY);
    }
  }

  @Override
  public void onChartTranslate(MotionEvent me, float dX) {
    if (mPressChangeListener != null) {
      mPressChangeListener.onChartTranslate(me, dX);
    }
  }
}

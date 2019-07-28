ViewGroup 有一个变量存放子View，即：mChildren，代码如下：
```
    // Child views of this ViewGroup
    private View[] mChildren;
```
追踪 mChildren，发现在传递焦点，点击事件分发等函数中，都会用到 mChildren。
我们这次的目标是理解测量过程，在 measureChildren 函数中，也找到了 mChildren，代码如下：

```
    /**
     * Ask all of the children of this view to measure themselves, taking into
     * account both the MeasureSpec requirements for this view and its padding.
     * We skip children that are in the GONE state The heavy lifting is done in
     * getChildMeasureSpec.
     *
     * @param widthMeasureSpec The width requirements for this view
     * @param heightMeasureSpec The height requirements for this view
     */
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = mChildrenCount;
        final View[] children = mChildren;
        for (int i = 0; i < size; ++i) {
            final View child = children[i];
            if ((child.mViewFlags & VISIBILITY_MASK) != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);//1-1
            }
        }
    }
```
//1-1

```
    /**
     * Ask one of the children of this view to measure itself, taking into
     * account both the MeasureSpec requirements for this view and its padding.
     * The heavy lifting is done in getChildMeasureSpec.
     *
     * @param child The child to measure
     * @param parentWidthMeasureSpec The width requirements for this view
     * @param parentHeightMeasureSpec The height requirements for this view
     */
    protected void measureChild(View child, int parentWidthMeasureSpec,
            int parentHeightMeasureSpec) {
        final LayoutParams lp = child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                mPaddingTop + mPaddingBottom, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);//2-1
    }
```

从Activity类找到其他类的线索
ActivityThread 类，Activity内有引用
Window 类，Activity内有引用

WindowManagerImpl 类，Window 类有引用
WindowManagerGlobal 类，WindowManagerImpl 类有引用
ViewRootImpl 类，WindowManagerGlobal 类的函数 addView 中有引用 ViewRootImpl
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

* 从Activity类找到其他类的线索
* ActivityThread 类，Activity内有引用
* Window 类，Activity内有引用
* WindowManagerImpl 类，Window 类有引用
* WindowManagerGlobal 类，WindowManagerImpl 类有引用
* ViewRootImpl 类，WindowManagerGlobal 类的函数 addView 中有引用 ViewRootImpl

### view的绘制是从函数 performTraversals 开始
追踪一个函数 performTraversals 的调用链，结果如下：


```
    void scheduleTraversals() {
        if (!mTraversalScheduled) {
            mTraversalScheduled = true;
            mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();//同步屏障
            mChoreographer.postCallback(
                    Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);//提交任务：mTraversalRunnable
            if (!mUnbufferedInputDispatch) {
                scheduleConsumeBatchedInput();
            }
            notifyRendererOfFramePending();
            pokeDrawLockIfNeeded();
        }
    }

    void unscheduleTraversals() {
        if (mTraversalScheduled) {
            mTraversalScheduled = false;
            mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);
            mChoreographer.removeCallbacks(
                    Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);//取消任务：mTraversalRunnable
        }
    }
```

```
final TraversalRunnable mTraversalRunnable = new TraversalRunnable();

final class TraversalRunnable implements Runnable {
        @Override
        public void run() {
            doTraversal();
        }
    }
```

```
    void doTraversal() {
        if (mTraversalScheduled) {
            mTraversalScheduled = false;
            mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);
            ......
            performTraversals(); // 此处调用
            ......
        }
    }
```

```
    private void performTraversals() {
        // 开启三大绘制流程，稍后分析
    }
```


```
    private void performMeasure(int childWidthMeasureSpec, int childHeightMeasureSpec) {
        if (mView == null) {
            return;
        }
        Trace.traceBegin(Trace.TRACE_TAG_VIEW, "measure");
        try {
            mView.measure(childWidthMeasureSpec, childHeightMeasureSpec);// 调用 view 的 measure 函数，所以是持有view的引用的
        } finally {
            Trace.traceEnd(Trace.TRACE_TAG_VIEW);
        }
    }
```

由上述代码可知，函数 scheduleTraversals，会开启绘制viewTree的任务；而函数 unscheduleTraversals，会结束绘制viewTree的任务。
同时由该链接文章：https://www.jianshu.com/p/aecff29d6751，可知：
>
在创建了 ViewRootImpl 后，需要将其与 DecorView 关联起来，会调用到 ViewRootImpl 中的 setView()。
setView() 中会调用到requestLayout()。requestLayout() 中调用 scheduleTraversals() 从而开始了 traversals 的过程。代码如下：
```
    public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
        synchronized (this) {
            if (mView == null) {
                mView = view;

                ......
                mSoftInputMode = attrs.softInputMode;
                mWindowAttributesChanged = true;
                mWindowAttributesChangesFlag = WindowManager.LayoutParams.EVERYTHING_CHANGED;
                mAttachInfo.mRootView = view;
                mAttachInfo.mScalingRequired = mTranslator != null;
                mAttachInfo.mApplicationScale =
                        mTranslator == null ? 1.0f : mTranslator.applicationScale;
                if (panelParentView != null) {
                    mAttachInfo.mPanelParentWindowToken
                            = panelParentView.getApplicationWindowToken();
                }
                mAdded = true;
                int res; /* = WindowManagerImpl.ADD_OKAY; */

                // Schedule the first layout -before- adding to the window
                // manager, to make sure we do the relayout before receiving
                // any other events from the system.
                requestLayout();
                ......
            }
        }
    }
```
```
    @Override
    public void requestLayout() {
        if (!mHandlingLayoutInLayoutRequest) {
            checkThread();
            mLayoutRequested = true;
            scheduleTraversals();
        }
    }
```

接下来的问题：new ViewRootImpl 只被创建一次吗？都有谁会调用 ViewRootImpl类 的 setView函数？

这篇链接：https://juejin.im/post/5ba0bec25188255c9e02c732
可知：一个 activity，对应一个 ViewRootImpl
>
总结：每个Activity对应一个PhoneWindow,window与decorView相关联。
设置布局的时候使用WindowManager addView,
每调用一次addView便创建一个ViewRootImpl,也就是创建一颗view树。
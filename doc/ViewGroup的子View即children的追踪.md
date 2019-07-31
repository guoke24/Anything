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

=========================================================

借个地方，分析 ViewGroup#dispatchTouchEvent函数的逻辑结构，一共分为三大逻辑：
第一，拦截判断
第二，遍历子view，分发事件
第三，处理 mFirstTouchTarget
最后返回拦截的结果。

ViewGroup 的函数： dispatchTouchEvent

```
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handled = false;
        if (onFilterTouchEventForSecurity(ev)) { // 99%逻辑都在里边
            final int action = ev.getAction();
            final int actionMasked = action & MotionEvent.ACTION_MASK;

            // Handle an initial down.
            if (actionMasked == MotionEvent.ACTION_DOWN) {
                // Throw away all previous state when starting a new touch gesture.
                // The framework may have dropped the up or cancel event for the previous gesture
                // due to an app switch, ANR, or some other state change.
                cancelAndClearTouchTargets(ev); // 清除 mFirstTouchTarget
                resetTouchState(); // 清除各种flag
            }

            // Check for interception.
            final boolean intercepted;
            if (actionMasked == MotionEvent.ACTION_DOWN
                    || mFirstTouchTarget != null) {
                 // ACTION_DOWN 说明是手指下落

                 // 补充说明：当一个点击产生，处于点击位置所有view，就会形成一个点击目标链条，
                 // 最顶层就是DecView，最底层一定是一个view，而不是viewGroup

                 // mFirstTouchTarget != null 说明点击的目标在本view的子view链里

                final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
                if (!disallowIntercept) {
                    intercepted = onInterceptTouchEvent(ev);
                    ev.setAction(action); // restore action in case it was changed
                } else {
                    intercepted = false;
                }
            } else {
                // There are no touch targets and this action is not an initial down
                // so this view group continues to intercept touches.
                intercepted = true;
                // 逻辑走到这里，说明点击目标没在子view链里，
                // 而逻辑会走到这里，暗含一个事实，本view处理点击目标链条，且父view没有拦截点击事件
                // 那就只剩一种可能，那就是本view要处理这个事件
                // 而且这个事件，绝不会是 down 事件，只可能是 move 或者 up 事件
            }
            // 在决定是否拦截后，接着去遍历子view


            // If intercepted, start normal event dispatch. Also if there is already
            // a view that is handling the gesture, do normal event dispatch.
            if (intercepted || mFirstTouchTarget != null) {
                ev.setTargetAccessibilityFocus(false);
            }

            // Check for cancelation.
            final boolean canceled = resetCancelNextUpFlag(this)
                    || actionMasked == MotionEvent.ACTION_CANCEL;

            // Update list of touch targets for pointer down, if needed.
            final boolean split = (mGroupFlags & FLAG_SPLIT_MOTION_EVENTS) != 0;
            TouchTarget newTouchTarget = null;
            boolean alreadyDispatchedToNewTouchTarget = false;

            // 不取消也不拦截，则遍历子view
            if (!canceled && !intercepted) {
                // 遍历子view
            }
            // 遍历子view结束,立马去处理 mFirstTouchTarget


            // Dispatch to touch targets.
            if (mFirstTouchTarget == null) { // 处理 mFirstTouchTarget
                // No touch targets so treat this as an ordinary view.
                handled = dispatchTransformedTouchEvent(ev, canceled, null,
                        TouchTarget.ALL_POINTER_IDS);
            } else {
                // Dispatch to touch targets, excluding the new touch target if we already
                // dispatched to it.  Cancel touch targets if necessary.
                TouchTarget predecessor = null;
                TouchTarget target = mFirstTouchTarget;
                while (target != null) {
                    final TouchTarget next = target.next;
                    if (alreadyDispatchedToNewTouchTarget && target == newTouchTarget) {
                        handled = true;
                    } else {
                        final boolean cancelChild = resetCancelNextUpFlag(target.child)
                                || intercepted;
                        if (dispatchTransformedTouchEvent(ev, cancelChild,
                                target.child, target.pointerIdBits)) {
                            handled = true;
                        }
                        if (cancelChild) {
                            if (predecessor == null) {
                                mFirstTouchTarget = next;
                            } else {
                                predecessor.next = next;
                            }
                            target.recycle();
                            target = next;
                            continue;
                        }
                    }
                    predecessor = target;
                    target = next;
                }
            }

            ......
        }
        ......
        return handled;
    }
```


























package com.guohao.factory.presenter;

/**
 * Presenter 的顶层基类
 *
 * 添加的功能：
 *
 * 实现了 BaseContract.Presenter 接口的函数 start 和 destroy，
 * 开放给 View 端调用；
 *
 * 实现了 setView 和 getView 两个函数，
 * 并在构造函数内绑定 View 端，
 * 然后调用 View 端绑定自己，
 * 从而实现双向绑定。
 *
 */
public class BasePresenter<T extends BaseContract.View> implements BaseContract.Presenter {
    private T mView;

    public BasePresenter(T view) {
        setView(view);
    }

    /**
     * 设置一个View，子类可以复写
     */
    @SuppressWarnings("unchecked")
    protected void setView(T view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }

    /**
     * 给子类使用的获取View的操作
     * 不允许复写
     *
     * @return View
     */
    protected final T getView() {
        return mView;
    }

    @Override
    public void start() {
        // 开始的时候进行Loading调用
        T view = mView;
        if (view != null) {
            view.showLoading();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void destroy() {
        T view = mView;
        mView = null;
        if (view != null) {
            // 把Presenter设置为NULL
            view.setPresenter(null);
        }
    }
}

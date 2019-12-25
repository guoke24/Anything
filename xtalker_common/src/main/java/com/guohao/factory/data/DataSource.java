package com.guohao.factory.data;

import android.support.annotation.StringRes;

/**
 * 数据源接口定义
 *
 * 该接口定义了两个子接口：
 * 成功回调接口，内含 onDataLoaded 函数，
 * 失败的回调接口，内含 onDataNotAvailable 函数，
 * 并用了 Callback 接口继承自上述两个接口，
 * 实现该接口的类，将会持有 SucceedCallback 和 FailedCallback 两个接口，可以回调出去，而不是回调进来
 *
 * 一个函数：
 * 销毁操作，开放给委托者调用进来
 *
 * 小节：
 * 接口内可含有接口，
 * 接口内的函数需要实现，
 * 但接口内的接口不需要实现。
 *
 * 实现一个接口，
 * 一来是需要实现所有的接口函数，
 * 二来是可以持有接口内的子接口的实现者的引用
 *
 */
public interface DataSource {

    /**
     * 同时包括了成功与失败的回调接口
     *
     * @param <T> 任意类型
     */
    interface Callback<T> extends SucceedCallback<T>, FailedCallback {

    }

    /**
     * 只关注成功的接口
     *
     * @param <T> 任意类型
     */
    interface SucceedCallback<T> {
        // 数据加载成功, 网络请求成功
        void onDataLoaded(T t);

    }

    /**
     * 只关注失败的接口
     */
    interface FailedCallback {
        // 数据加载失败, 网络请求失败
        void onDataNotAvailable(@StringRes int strRes);
    }


    /**
     * 销毁操作
     */
    void dispose();

}

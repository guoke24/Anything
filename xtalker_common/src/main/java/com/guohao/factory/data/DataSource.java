package com.guohao.factory.data;

import android.support.annotation.StringRes;

/**
 * 数据源接口定义
 *
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

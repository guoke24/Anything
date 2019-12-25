package com.guohao.factory.data;


import java.util.List;

/**
 * 追加一个 load 函数，开放给委托者调用进来
 *
 * @param <Data>
 */
public interface DbDataSource<Data> extends DataSource {
    /**
     * 对数据进行加载的一个职责
     *
     * @param callback 加载成功后返回的Callback
     */
    void load(DataSource.SucceedCallback<List<Data>> callback);

}

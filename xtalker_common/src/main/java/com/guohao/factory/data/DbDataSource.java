package com.guohao.factory.data;


import java.util.List;

public interface DbDataSource<Data> extends DataSource {
    /**
     * 对数据进行加载的一个职责
     *
     * @param callback 加载成功后返回的Callback
     */
    void load(DataSource.SucceedCallback<List<Data>> callback);

}

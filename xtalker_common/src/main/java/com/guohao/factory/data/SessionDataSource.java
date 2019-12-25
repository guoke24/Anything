package com.guohao.factory.data;

import com.guohao.factory.model.db.Session;
import com.guohao.factory.model.db.User;

import java.util.List;

/**
 * 会话的数据源接口
 *
 * 给父接口的泛型指定 Session 类型
 */
public interface SessionDataSource extends DbDataSource<Session> {
    /**
     * 对数据进行加载的一个职责
     *
     * @param callback 加载成功后返回的Callback
     */
    void load(DataSource.SucceedCallback<List<Session>> callback);
}

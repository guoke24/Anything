package com.guohao.factory.data.user;



import com.guohao.factory.data.DataSource;
import com.guohao.factory.data.DbDataSource;
import com.guohao.factory.model.db.User;

import java.util.List;

/**
 * 联系人数据源
 *
 * 给父接口的泛型指定 User 类型
 *
 */
public interface ContactDataSource extends DbDataSource<User> {
    /**
     * 对数据进行加载的一个职责
     *
     * @param callback 加载成功后返回的Callback
     */
    void load(DataSource.SucceedCallback<List<User>> callback);
}

package com.guohao.factory.data.user;



import com.guohao.factory.data.DataSource;
import com.guohao.factory.model.db.User;

import java.util.List;

/**
 * 联系人数据源
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public interface ContactDataSource {
    /**
     * 对数据进行加载的一个职责
     *
     * @param callback 加载成功后返回的Callback
     */
    void load(DataSource.SucceedCallback<List<User>> callback);

    /**
     * 销毁操作
     */
    void dispose();
}

package com.guohao.factory.data.session;

import android.support.annotation.NonNull;

import com.guohao.factory.data.BaseDbRepository;
import com.guohao.factory.data.DataSource;
import com.guohao.factory.data.SessionDataSource;
import com.guohao.factory.model.db.Session;
import com.guohao.factory.model.db.Session_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

public class SessionRepository extends BaseDbRepository<Session>
        implements SessionDataSource {

    private DataSource.SucceedCallback<List<Session>> callback;

    @Override
    public void load(SucceedCallback<List<Session>> callback) {
        super.load(callback);

        // 加载本地数据库的数据，结果回调到 presenter 的 onDataLoaded 函数
        SQLite.select()
                .from(Session.class)
                .orderBy(Session_Table.modifyAt, false) // false 是倒序
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();

    }


    @Override
    protected boolean isRequired(Session session) {
        // 所有的会话我都需要，不需要过滤
        return true;
    }

    @Override
    protected void insert(Session session) {
        // 复写方法，让新的数据加到头部
        dataList.addFirst(session);
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Session> tResult) {
        // 复写数据库回来的方法, 进行一次反转
        Collections.reverse(tResult);

        super.onListQueryResult(transaction, tResult);
    }
}

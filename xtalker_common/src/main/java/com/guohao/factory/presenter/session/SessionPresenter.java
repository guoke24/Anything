package com.guohao.factory.presenter.session;

import android.support.v7.util.DiffUtil;

import com.guohao.common.widget.recycler.RecyclerAdapter;
import com.guohao.factory.data.DataSource;
import com.guohao.factory.data.SessionDataSource;
import com.guohao.factory.data.session.SessionRepository;
import com.guohao.factory.model.db.Session;

import com.guohao.factory.presenter.BaseSourcePresenter;
import com.guohao.factory.presenter.session.SessionContract.Presenter;
import com.guohao.utils.DiffUiDataCallback;

import java.util.List;

public class SessionPresenter extends BaseSourcePresenter<Session,Session,
        SessionDataSource,
        SessionContract.View>
        implements Presenter,DataSource.SucceedCallback<List<Session>> {


    public SessionPresenter(SessionContract.View view) {
        super(new SessionRepository(), view);
    }


    @Override
    public void onDataLoaded(List<Session> sessions) {
        // 无论怎么操作，数据变更，最终都会通知到这里来
        final SessionContract.View view = getView();
        if (view == null)
            return;

        RecyclerAdapter<Session> adapter = view.getRecyclerAdapter();
        List<Session> old = adapter.getItems();


        // 进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(old, sessions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 调用基类方法进行界面刷新
        refreshData(result, sessions);
    }
}

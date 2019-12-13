package com.guohao.factory.presenter.contact;

import android.support.v7.util.DiffUtil;


import com.guohao.common.widget.recycler.RecyclerAdapter;
import com.guohao.factory.data.DataSource;
import com.guohao.factory.data.helper.UserHelper;
import com.guohao.factory.data.user.ContactDataSource;
import com.guohao.factory.data.user.ContactRepository;
import com.guohao.factory.model.db.User;
import com.guohao.factory.presenter.BaseRecyclerPresenter;
import com.guohao.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 联系人的Presenter实现
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class ContactPresenter extends BaseRecyclerPresenter<User, ContactContract.View>
        implements ContactContract.Presenter, DataSource.SucceedCallback<List<User>> {

    private ContactDataSource mSource;

    public ContactPresenter(ContactContract.View view) {
        super(view);
        mSource = new ContactRepository();
    }


    @Override
    public void start() {
        super.start();

        // 进行本地的数据加载，并添加监听
        mSource.load(this);

        // 加载网络数据
        UserHelper.refreshContacts();
    }

    // 运行到这里的时候是子线程
    @Override
    public void onDataLoaded(List<User> users) {
        // 无论怎么操作，数据变更，最终都会通知到这里来
        final ContactContract.View view = getView();
        if (view == null)
            return;

        RecyclerAdapter<User> adapter = view.getRecyclerAdapter();
        List<User> old = adapter.getItems();


        // 进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(old, users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 调用基类方法进行界面刷新
        refreshData(result, users);
    }

    @Override
    public void destroy() {
        super.destroy();
        // 当界面销毁的时候，我们应该把数据监听进行销毁
        mSource.dispose();
    }
}

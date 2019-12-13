package com.guohao.factory.presenter.search;

import com.guohao.factory.data.DataSource;
import com.guohao.factory.data.helper.UserHelper;
import com.guohao.factory.model.card.UserCard;
import com.guohao.factory.persistence.Account;
import com.guohao.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * 搜索人的实现
 *
 * 作为 p 端，主要业务是发起查询，
 * 委托了 UserHelper 类发起查询，
 * 得到回调后，再把结果回调给 v 端。
 */
public class SearchUserPresenter extends BasePresenter<SearchContract.UserView>
        implements SearchContract.Presenter, DataSource.Callback<List<UserCard>> {
    private Call searchCall;

    public SearchUserPresenter(SearchContract.UserView view) {
        super(view);
    }

    @Override
    public void search(String content) {
        start();

        Call call = searchCall;
        if (call != null && !call.isCanceled()) {
            // 如果有上一次的请求，并且没有取消，
            // 则调用取消请求操作
            call.cancel();
        }

        searchCall = UserHelper.search(content, this);
    }

    @Override
    public void onDataLoaded(final List<UserCard> userCards) {
        // 搜索成功
        final SearchContract.UserView view = getView();
        if(view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    // 过滤用户本身
                    List<UserCard> userCards2 = new ArrayList<>();
                    for(UserCard userCard: userCards){
                        if (userCard.getId().equals(Account.getUserId())){
                            continue;
                        }
                        userCards2.add(userCard);
                    }

                    view.onSearchDone(userCards2);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        // 搜索失败
        final SearchContract.UserView view = getView();
        if(view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(strRes);
                }
            });
        }
    }
}

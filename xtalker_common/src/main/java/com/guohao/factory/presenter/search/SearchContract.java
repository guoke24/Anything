package com.guohao.factory.presenter.search;

import com.guohao.factory.model.card.UserCard;
import com.guohao.factory.presenter.BaseContract;



import java.util.List;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public interface SearchContract {
    interface Presenter extends BaseContract.Presenter {
        // 搜索内容
        void search(String content);
    }

    // 搜索人的界面
    interface UserView extends BaseContract.View<Presenter> {
        void onSearchDone(List<UserCard> userCards);
    }

    // 搜索群的界面
//    interface GroupView extends BaseContract.View<Presenter> {
//        void onSearchDone(List<GroupCard> groupCards);
//    }

}

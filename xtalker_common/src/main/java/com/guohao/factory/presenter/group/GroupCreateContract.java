package com.guohao.factory.presenter.group;

import com.guohao.factory.model.Author;
import com.guohao.factory.presenter.BaseContract;

/**
 * 创建群的契约
 */
public interface GroupCreateContract {

    interface Presenter extends BaseContract.Presenter{
        // 创建群，需群名，群描述，群头像的url
        void create(String name, String desc, String picture);

        // 更改一个Model的选中状态
        void changeSelect(ViewModel model, boolean isSelected);

    }

    interface View extends BaseContract.RecyclerView<Presenter,ViewModel>{
        // 创建成功
        void createSucceed();
    }

    class ViewModel {
        // 用户信息
        Author author;
        // 是否选中
        boolean isSelected;
    }
}

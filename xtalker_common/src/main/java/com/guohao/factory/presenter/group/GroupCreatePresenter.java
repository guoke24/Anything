package com.guohao.factory.presenter.group;

import android.util.Log;

import com.guohao.factory.Factory;
import com.guohao.factory.data.helper.UserHelper;
import com.guohao.factory.model.db.view.UserSampleModel;
import com.guohao.factory.presenter.BaseRecyclerPresenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 只需要继承 BaseRecyclerPresenter，而不是继承 BaseSourcePresenter，因为不需要监听数据库
 *
 *
 *
 */
public class GroupCreatePresenter extends BaseRecyclerPresenter<GroupCreateContract.ViewModel, GroupCreateContract.View>
    implements GroupCreateContract.Presenter{

    // 维护一个选中用户的列表
    private Set<String> users = new HashSet<>();

    public GroupCreatePresenter(GroupCreateContract.View view) {
        super(view);
    }


    @Override
    public void start() {
        super.start();
        // 丢到线程池，加载数据
        Factory.runOnAsync(loader);
    }

    @Override
    public void create(String name, String desc, String picture) {

    }

    @Override
    public void changeSelect(GroupCreateContract.ViewModel model, boolean isSelected) {
        if (isSelected)
            users.add(model.author.getId());
        else
            users.remove(model.author.getId());

        Log.i("guohao-x","users.size() = " + users.size());
    }

    /**
     * 委托 UserHelper 查询到类型为 sampleModels 的简单的数据
     */
    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            List<UserSampleModel> sampleModels = UserHelper.getSampleContact();
            List<GroupCreateContract.ViewModel> models = new ArrayList<>();
            // UserSampleModel --> ViewModel
            for (UserSampleModel sampleModel : sampleModels) {
                GroupCreateContract.ViewModel viewModel = new GroupCreateContract.ViewModel();
                viewModel.author = sampleModel;
                models.add(viewModel);
            }
            // 转换类型后，再刷新会界面
            // GroupCreateContract.ViewModel 这个类型是在 GroupCreateContract 这个契约中定义的
            // View 端和 Presenter端都会遵守
            refreshData(models);
        }
    };
}

package com.guohao.factory.presenter.group;

import android.text.TextUtils;
import android.util.Log;

import com.guohao.factory.Factory;
import com.guohao.factory.data.DataSource;
import com.guohao.factory.data.helper.GroupHelper;
import com.guohao.factory.data.helper.UserHelper;
import com.guohao.factory.model.api.group.GroupCreateModel;
import com.guohao.factory.model.card.GroupCard;
import com.guohao.factory.model.db.view.UserSampleModel;
import com.guohao.factory.net.UploadHelper;
import com.guohao.factory.presenter.BaseRecyclerPresenter;
import com.guohao.xtalker.R;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

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
    implements GroupCreateContract.Presenter, DataSource.Callback<GroupCard>{

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
        // 调用 View 端显示加载中
        GroupCreateContract.View view = getView();
        view.showLoading();

        // 先判断参数是否合法
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) ||
                TextUtils.isEmpty(picture) || users.size() ==0 ){
            view.showError(R.string.label_group_create_invalid);
            return;
        }

        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                // 在异步线程里上传图片，成功后获得一个网络的url，再继续下一步
                String url = uploadPicture(picture);

                if (TextUtils.isEmpty(url))
                    return;

                // 再建一个 model 来封装几个参数
                GroupCreateModel createModel = new GroupCreateModel(name,desc,url,users);

                // 此处应该委托 GroupHelper 发起网络查询
                GroupHelper.create(createModel,GroupCreatePresenter.this);
            }
        });

    }

    // 同步上传操作
    private String uploadPicture(String path) {
        String url = UploadHelper.uploadPortrait(path);
        if (TextUtils.isEmpty(url)) {
            // 切换到UI线程 提示信息
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    GroupCreateContract.View view = getView();
                    if (view != null) {
                        view.showError(R.string.data_upload_error);
                    }
                }
            });
        }
        return url;
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
     *
     * 查询到数据后，通过父类的调用链：refreshData --> view.onAdapterDataChanged()
     * 来回调到 View 端
     *
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

    @Override
    public void onDataLoaded(GroupCard groupCard) {
        // 通知 View 更新
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = getView();
                if (view != null) {
                    view.onCreateSucceed();
                }
            }
        });
    }

    @Override
    public void onDataNotAvailable(int strRes) {
        // 失败情况
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = getView();
                if (view != null) {
                    view.showError(strRes);
                }
            }
        });
    }
}

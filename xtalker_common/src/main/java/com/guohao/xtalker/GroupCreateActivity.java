package com.guohao.xtalker;

import android.content.Context;
import android.content.Intent;
import com.guohao.common.app.PresenterToolbarActivity;
import com.guohao.common.widget.recycler.RecyclerAdapter;
import com.guohao.factory.presenter.group.GroupCreateContract;

/**
 * 创建群组的界面， MVP 模式
 *
 * 直接继承自 PresenterToolbarActivity，已带有 View 端的基本功能
 * 间接继承 ToolbarActivity，已带有 Toolbar 端的基本设置
 * 顶层基类 Activity，已封装好基本的初始化流程 ：
 * onCreate --> initWidows()
 *          --> int layId = getContentLayoutId();
 *              setContentView(layId);
 *              initBefore();
 *              initWidget();
 *              initData();
 *
 * 该类自身添加的功能：
 * 总体是做一个 MVP 模式的创建群界面的功能；
 *
 * 第零步：实现 MVP 契约
 * 第一步：实现界面布局
 *
 *
 */
public class GroupCreateActivity extends PresenterToolbarActivity<GroupCreateContract.Presenter>
    implements GroupCreateContract.View{


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_create;
    }

    /**
     * 外部入口
     * @param context
     */
    public static void show(Context context){
        Intent intent = new Intent(context,GroupCreateActivity.class);
        context.startActivity(intent);
    }

    // --- GroupCreateContract.View 职责 start ---
    @Override
    protected GroupCreateContract.Presenter initPresenter() {
        return null;
    }

    @Override
    public void createSucceed() {

    }

    @Override
    public RecyclerAdapter<GroupCreateContract.ViewModel> getRecyclerAdapter() {
        return null;
    }

    @Override
    public void onAdapterDataChanged() {

    }
    // --- GroupCreateContract.View 职责 end ---
}

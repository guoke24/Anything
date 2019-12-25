package com.guohao.xtalker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guohao.common.app.PresenterToolbarActivity;
import com.guohao.common.widget.PortraitView;
import com.guohao.common.widget.recycler.RecyclerAdapter;
import com.guohao.factory.presenter.group.GroupCreateContract;
import com.guohao.factory.presenter.group.GroupCreatePresenter;

import butterknife.BindView;
import butterknife.OnCheckedChanged;

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
 * 第二步：设置列表的布局
 * recyclerView：设置为线性布局
 * adapter：指定item布局文件，新建voewHolder并传入布局文件到构造函数
 * viewHolder：建一个 viewHolder 子类，重写bind函数
 *
 * 第三步：实例化presenter，并委托 presenter 加载数据
 *
 */
public class GroupCreateActivity extends PresenterToolbarActivity<GroupCreateContract.Presenter>
    implements GroupCreateContract.View{


    @BindView(R2.id.recycler)
    RecyclerView mRecycler;

    private Adapter mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_create;
    }

    /**
     * 初始化组件的时候，初始化 mRecycler
     */
    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new Adapter());
    }

    /**
     * 初始化数据的时候，加载数据
     */
    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
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
        return new GroupCreatePresenter(this);
    }

    @Override
    public void createSucceed() {

    }

    @Override
    public RecyclerAdapter<GroupCreateContract.ViewModel> getRecyclerAdapter() {
        return mAdapter;
    }

    /**
     * 在 BaseRecyclerPresenter 类中的 refreshDataOnUiThread 函数中回调过来
     *
     * 其子类发起函数调用：
     * refreshData --> view.onAdapterDataChanged()
     *
     * 或者，另一种参数，：
     * refreshData --> refreshDataOnUiThread --> view.onAdapterDataChanged()
     *
     */
    @Override
    public void onAdapterDataChanged() {
        hideLoading();
    }
    // --- GroupCreateContract.View 职责 end ---


    /**
     * RecyclerAdapter 中，item 使用的类型为 GroupCreateContract.ViewModel
     *
     */
    class Adapter extends RecyclerAdapter<GroupCreateContract.ViewModel>{

        @Override
        protected int getItemViewType(int position, GroupCreateContract.ViewModel viewModel) {
            return R.layout.cell_group_create_contact;
        }

        @Override
        protected ViewHolder<GroupCreateContract.ViewModel> onCreateViewHolder(View root, int viewType) {
            return new GroupCreateActivity.ViewHolder(root);
        }
    }


    /**
     * 泛型的具体类型要跟 Adapter 中的一致，为 GroupCreateContract.ViewModel
     *
     */
    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCreateContract.ViewModel>{

        @BindView(R2.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R2.id.txt_name)
        TextView mName;
        @BindView(R2.id.cb_select)
        CheckBox mSelect;


        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(GroupCreateContract.ViewModel viewModel) {
            mPortrait.setup(Glide.with(GroupCreateActivity.this), viewModel.author);
            mName.setText(viewModel.author.getName());
            mSelect.setChecked(viewModel.isSelected);
        }

        /**
         * 监听 选中控件
         */
        @OnCheckedChanged(R2.id.cb_select)
        void onCheckedChanged(boolean checked) {
            // 进行状态更改
            mPresenter.changeSelect(mData, checked);
        }
    }
}

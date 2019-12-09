package com.guohao.xtalker.frags.search;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guohao.common.app.PresenterFragment;
import com.guohao.common.widget.EmptyView;
import com.guohao.common.widget.PortraitView;
import com.guohao.common.widget.recycler.RecyclerAdapter;
import com.guohao.factory.model.card.UserCard;
import com.guohao.factory.presenter.search.SearchContract;
import com.guohao.factory.presenter.search.SearchUserPresenter;
import com.guohao.xtalker.R;
import com.guohao.xtalker.R2;
import com.guohao.xtalker.SearchActivity;


import java.util.List;

import butterknife.BindView;


/**
 * 搜索人的界面实现
 *
 * 该界面是一个fragment，同时也是mvp模式的v端
 * 该界面继承 PresenterFragment，带有初始化 present 和 show加载/错误的函数；
 *
 * 该类实现来一个垂直线性布局的 RecyclerView，
 * 并且完善来 RecyclerView 的 Adapter，
 * 指定了 item 的布局，viewHolder 的绑定逻辑等；
 *
 * 该类实现了 SearchActivity.SearchFragment 接口，
 * 向 SearchActivity 开放 search 函数入口；
 *
 * 该类作为v端，实现了 SearchContract.UserView 接口，
 * 向 p 端开放了 onSearchDone 函数接口；
 *
 * 该页面本身采用了布局：fragment_search_user
 * 有一个搜索按钮，点击调用 p 端发起查询。
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment, SearchContract.UserView {

    @BindView(R2.id.empty)
    EmptyView mEmptyView;

    @BindView(R2.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<UserCard> mAdapter;

    public SearchUserFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // 初始化Recycler
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<UserCard>() {
            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                // 返回cell的布局id
                return R.layout.cell_search_list;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                return new SearchUserFragment.ViewHolder(root);
            }
        });

        // 初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        // 发起首次搜索
        search("");
    }

    @Override
    public void search(String content) {
        // Activity->Fragment->Presenter->Net
        mPresenter.search(content);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {
        // 数据成功的情况下返回数据
        mAdapter.replace(userCards);
        // 如果有数据，则是OK，没有数据就显示空布局
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        // 初始化Presenter
        return new SearchUserPresenter(this);
    }

    /**
     * 每一个Cell的布局操作
     */
    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard> {
        @BindView(R2.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R2.id.txt_name)
        TextView mName;

        @BindView(R2.id.im_follow)
        ImageView mFollow;




        public ViewHolder(View itemView) {
            super(itemView);
            // 当前View和Presenter绑定
            //new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            mPortraitView.setup(Glide.with(SearchUserFragment.this), userCard);
            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }


//        @Override
//        public void showError(int str) {
//            // 更改当前界面状态
//            if (mFollow.getDrawable() instanceof LoadingDrawable) {
//                // 失败则停止动画，并且显示一个圆圈
//                LoadingDrawable drawable = (LoadingDrawable) mFollow.getDrawable();
//                drawable.setProgress(1);
//                drawable.stop();
//            }
//        }
//
//        @Override
//        public void showLoading() {
//            int minSize = (int) Ui.dipToPx(getResources(), 22);
//            int maxSize = (int) Ui.dipToPx(getResources(), 30);
//            // 初始化一个圆形的动画的Drawable
//            LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
//            drawable.setBackgroundColor(0);
//
//            int[] color = new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)};
//            drawable.setForegroundColor(color);
//            // 设置进去
//            mFollow.setImageDrawable(drawable);
//            // 启动动画
//            drawable.start();
//        }

//        @Override
//        public void setPresenter(FollowContract.Presenter presenter) {
//            mPresenter = presenter;
//        }

//        @Override
//        public void onFollowSucceed(UserCard userCard) {
//            // 更改当前界面状态
//            if (mFollow.getDrawable() instanceof LoadingDrawable) {
//                ((LoadingDrawable) mFollow.getDrawable()).stop();
//                // 设置为默认的
//                mFollow.setImageResource(R.drawable.sel_opt_done_add);
//            }
//            // 发起更新
//            updateData(userCard);
//        }
    }
}

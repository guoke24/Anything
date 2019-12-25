package com.guohao.xtalker.frags.main;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guohao.common.app.PresenterFragment;
import com.guohao.common.widget.PortraitView;
import com.guohao.common.widget.recycler.RecyclerAdapter;
import com.guohao.factory.model.db.Session;
import com.guohao.factory.presenter.session.SessionContract;
import com.guohao.xtalker.R;
import com.guohao.xtalker.R2;

import butterknife.BindView;

/**
 * 展示会话列表的界面，mvp 模式
 *
 * 设置 RecycleView
 * 设置 Adapter
 * 实现 ViewHolder
 *
 *
 *
 *
 */
public class ActiveFragment2 extends PresenterFragment<SessionContract.Presenter>
    implements SessionContract.View{

    // 绑定布局中的 RecyclerView，不用再实例化
    @BindView(R2.id.recycler)
    RecyclerView mRecycler;

    // 声明一个 Adapter 的引用
    private RecyclerAdapter<Session> mAdapter;

    public ActiveFragment2() {
        //
    }

    /**
     * 在父类的 onCreateView 的时候调用，即把 xml 布局转为 View 的时候;
     *
     * 设置布局为线性布局
     *
     * 实现 Adapter，创建 ViewHolder
     *
     * @param root
     */
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        // 线性布局
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        // 设置适配器
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Session>() {
            @Override
            protected int getItemViewType(int position, Session session) {
                return R.layout.cell_contact_list;
            }

            @Override
            protected ViewHolder<Session> onCreateViewHolder(View root, int viewType) {
                return new ActiveFragment2.ViewHolder(root);
            }
        });

    }

    /**
     * 在父类的 onViewCreated 的时候调用，即创建布局完成的时候;
     * 有 flag判断，仅调用一次
     */
    @Override
    protected void onFirstInit() {
        super.onFirstInit();

        // for test
//        Session session = new Session();
//        session.setName("1");
//        mAdapter.add(session);
    }

    /**
     * 创建 present，并传入自身，完成双向绑定
     * @return
     */
    @Override
    protected SessionContract.Presenter initPresenter() {

        return null;
    }

    /**
     * 返回 xml 布局id，在父类完成解析
     * @return
     */
    @Override
    protected int getContentLayoutId() {

        return R.layout.fragment_active2;
    }

    /**
     * 返回一个自建的 Adapter，指明 item 的布局，
     * 在父类中完成设置给 RecycleView。
     * @return
     */
    @Override
    public RecyclerAdapter<Session> getRecyclerAdapter() {
        return mAdapter;
    }

    /**
     * 有数据更新时，presenter 会回调该函数
     */
    @Override
    public void onAdapterDataChanged() {

    }

    /**
     * 自定义一个 Viewholder
     */
    class ViewHolder extends RecyclerAdapter.ViewHolder<Session>{

        @BindView(R2.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R2.id.txt_name)
        TextView mName;

        @BindView(R2.id.txt_desc)
        TextView mDesc;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        // 每个item刷新，都会由如下的调用链：
        // 框架内 --> RecycleView.onBindViewHolder 函数
        // --> RecyclerAdapter.ViewHolder.bind 函数
        // --> RecyclerAdapter.ViewHolder.onBind 函数，
        // 调用到此处。
        @Override
        protected void onBind(Session session) {
            //mPortraitView.setup(Glide.with(ActiveFragment2.this), session);
            mName.setText("hello");
            mDesc.setText("world");
        }
    }
}

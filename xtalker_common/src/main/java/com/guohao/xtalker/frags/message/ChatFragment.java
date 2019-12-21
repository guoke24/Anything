package com.guohao.xtalker.frags.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guohao.common.app.PresenterFragment;
import com.guohao.common.widget.PortraitView;
import com.guohao.common.widget.adapter.TextWatcherAdapter;
import com.guohao.common.widget.recycler.RecyclerAdapter;
import com.guohao.factory.model.db.Message;
import com.guohao.factory.model.db.User;
import com.guohao.factory.persistence.Account;
import com.guohao.factory.presenter.message.ChatContract;
import com.guohao.xtalker.MessageActivity;
import com.guohao.xtalker.R;
import com.guohao.xtalker.R2;

import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;


import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 单聊和群聊两个 Fragment 的基类
 *
 * 所做工作：
 *
 * 初始化顶部bar，留出 onOffsetChanged 函数监听顶部bar的折叠状态，让子类复写实现
 *
 * 设置 mRecyclerView 的布局
 *
 * 实现 Adapter
 *
 * 实现多个消息类型对应的 ViewHolder
 *
 * 监听输入框，更开发送按钮的点击状态
 *
 * 监听发送按钮：委托 P 端发送消息
 *
 */
public abstract class ChatFragment<InitModel>
        extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener,
        ChatContract.View<InitModel> {

    protected String mReceiverId;
    protected Adapter mAdapter;

    @BindView(R2.id.toolbar)
    Toolbar mToolbar;

    @BindView(R2.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R2.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R2.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingLayout;

    @BindView(R2.id.edit_content)
    EditText mContent;

    @BindView(R2.id.btn_submit)
    View mSubmit;


    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        initToolbar();
        initAppbar();
        initEditContent();

        // RecyclerView基本设置
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化数据，排在其他初始化操作之后
     */
    @Override
    protected void initData() {
        super.initData();
        // 开始进行初始化操作，
        // ChatUserPresenter 的 start 函数，加载头像和名字，
        // 其父类 ChatPresenter 的 start 函数没有复写这个函数，
        // 其父类 BaseSourcePresenter 的 start 函数，委托数据源接口的 load 函数，发起数据加载，
        // 并把 回调到自身的接口 传递给数据源接口的实现者。
        mPresenter.start();
    }

    // 初始化Toolbar
    protected void initToolbar() {
        Toolbar toolbar = mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    //  给界面的Appbar设置一个监听，得到关闭与打开的时候的进度
    private void initAppbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    // 初始化输入框监听
    private void initEditContent() {
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                // 设置状态，改变对应的Icon
                mSubmit.setActivated(needSendMsg);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
    }

    @OnClick(R2.id.btn_face)
    void onFaceClick() {
        // TODO
    }

    @OnClick(R2.id.btn_record)
    void onRecordClick() {
        // TODO
    }

    /**
     * 点击发送按钮
     * 委托 P 端进行发送
     */
    @OnClick(R2.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            // 发送
            String content = mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(content);
        } else {
            onMoreClick();
        }
    }

    private void onMoreClick() {
        // TODO
    }

    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // 界面没有占位布局，Recycler是一直显示的，所有不需要做任何事情
    }

    /**
     * 内容的适配器
     * 此处主要作两件事：
     * 第一，指明使用的 xml 布局文件；
     * 第二，创建 Holder，传入 View 类型的布局文件给 Holder（父类实现了 xml 布局转 View）。
     */
    private class Adapter extends RecyclerAdapter<Message> {

        /**
         * 根据消息类型，返回对应的布局
         * @param position 坐标
         * @param message
         * @return
         */
        @Override
        protected int getItemViewType(int position, Message message) {
            // 我发送的在右边，收到的在左边
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());

            switch (message.getType()) {
                // 文字内容
                case Message.TYPE_STR:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;

                // 语音内容
                case Message.TYPE_AUDIO:
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;

                // 图片内容
                case Message.TYPE_PIC:
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;

                // 其他内容：文件
                default:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
            }
        }

        /**
         * 为每条消息，创建一个 Holder，并把布局 root 传入构造函数
         * @param root     根布局，在父类中，已经从 xml 布局文件转为 View 类型
         * @param viewType 布局类型，其实就是 XML 的 ID
         * @return
         */
        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            // moduel 中 R.ID 不是常量,不能用switch语句

            if (viewType == R.layout.cell_chat_text_right || viewType == R.layout.cell_chat_text_left) {
                return new TextHolder(root);
            } else if (viewType == R.layout.cell_chat_audio_right || viewType == R.layout.cell_chat_audio_left) {
                return new AudioHolder(root);
            } else if (viewType == R.layout.cell_chat_pic_right || viewType == R.layout.cell_chat_pic_left) {
                return new PicHolder(root);
            }

            // 默认情况下，返回的就是Text类型的Holder进行处理
            // 文件的一些实现
            return new TextHolder(root);
        }
    }


    // Holder的基类
    // 负责头像Loading的状态刷新
    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {
        @BindView(R2.id.im_portrait)
        PortraitView mPortrait;

        // 允许为空，左边没有，右边有
        @Nullable
        @BindView(R2.id.loading)
        Loading mLoading;


        public BaseHolder(View itemView) {
            super(itemView);
        }

        /**
         * 每次刷新都会调用该函数
         * 在 Adapter 通过  函数获得 holder 之后，
         * 每次刷新 Adapter，都会调用 onBindViewHolder 函数，
         * 调用链为：
         * Adapter.onBindViewHolder --> holder.bind --> holder.onBind
         *
         * 在基类中，每次刷新只更新头像状态
         * 具体内容刷新，让子类实现
         *
         * @param message
         */
        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            // 进行数据加载
            sender.load();
            // 头像加载
            mPortrait.setup(Glide.with(ChatFragment.this), sender);

            if (mLoading != null) {
                // 当前布局应该是在右边
                int status = message.getStatus();
                if (status == Message.STATUS_DONE) {
                    // 正常状态, 隐藏Loading
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) {
                    // 正在发送中的状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {
                    // 发送失败状态, 允许重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                }

                // 当状态是错误状态时才允许点击
                mPortrait.setEnabled(status == Message.STATUS_FAILED);
            }
        }

        @OnClick(R2.id.im_portrait)
        void onRePushClick() {
            // 重新发送

            if (mLoading != null && mPresenter.rePush(mData)) {
                // 必须是右边的才有可能需要重新发送
                // 状态改变需要重新刷新界面当前的信息
                updateData(mData);
            }

        }
    }

    // 文字的Holder
    // 负责文字的绑定
    class TextHolder extends BaseHolder {
        @BindView(R2.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);

            // 把内容设置到布局上
            mContent.setText(message.getContent());

        }
    }

    // 语音的Holder
    class AudioHolder extends BaseHolder {

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // TODO
        }
    }

    // 图片的Holder
    class PicHolder extends BaseHolder {

        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // TODO
        }
    }


}

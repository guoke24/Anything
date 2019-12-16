package com.guohao.xtalker.frags.message;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;


import com.guohao.common.app.Fragment;
import com.guohao.common.widget.adapter.TextWatcherAdapter;
import com.guohao.xtalker.MessageActivity;
import com.guohao.xtalker.R;
import com.guohao.xtalker.R2;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public abstract class ChatFragment extends Fragment
        implements AppBarLayout.OnOffsetChangedListener {

    protected String mReceiverId;

    @BindView(R2.id.toolbar)
    Toolbar mToolbar;

    @BindView(R2.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R2.id.appbar)
    AppBarLayout mAppBarLayout;

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

    @OnClick(R2.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            // 发送
        } else {
            onMoreClick();
        }
    }

    private void onMoreClick() {
        // TODO
    }


}

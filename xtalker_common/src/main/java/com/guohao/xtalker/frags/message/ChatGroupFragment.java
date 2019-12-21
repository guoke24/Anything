package com.guohao.xtalker.frags.message;


import android.support.v4.app.Fragment;

import com.guohao.factory.model.db.Group;
import com.guohao.factory.presenter.message.ChatContract;
import com.guohao.xtalker.R;



/**
 * 群聊天界面实现
 */
public class ChatGroupFragment extends ChatFragment<Group>
        implements ChatContract.GroupView {


    public ChatGroupFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_group;
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return null;
    }

    @Override
    public void onInit(Group group) {

    }
}

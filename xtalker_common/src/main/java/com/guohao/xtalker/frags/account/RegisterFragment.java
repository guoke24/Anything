package com.guohao.xtalker.frags.account;


import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import com.guohao.common.app.PresenterFragment;
import com.guohao.factory.presenter.account.RegisterContract;
import com.guohao.factory.presenter.account.RegisterPresenter;
import com.guohao.xtalker.MainXActivity;
import com.guohao.xtalker.R;
import com.guohao.xtalker.R2;

import net.qiujuer.genius.ui.widget.Loading;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * 注册的界面
 */
public class RegisterFragment extends PresenterFragment<RegisterContract.Presenter>
        implements RegisterContract.View {
    private AccountTrigger mAccountTrigger;

    @BindView(R2.id.edit_phone)
    EditText mPhone;
    @BindView(R2.id.edit_name)
    EditText mName;
    @BindView(R2.id.edit_password)
    EditText mPassword;


    @BindView(R2.id.loading)
    Loading mLoading;

    @BindView(R2.id.btn_submit)
    Button mSubmit;


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 拿到我们的Activity的引用
        mAccountTrigger = (AccountTrigger) context;
    }

    @Override
    protected RegisterContract.Presenter initPresenter() {
        return new RegisterPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_register;
    }


    @OnClick(R2.id.btn_submit)
    void onSubmitClick() {
        String phone = mPhone.getText().toString();
        String name = mName.getText().toString();
        String password = mPassword.getText().toString();
        // 调用P层进行注册
        mPresenter.register(phone, name, password);
    }

    @OnClick(R2.id.txt_go_login)
    void onShowLoginClick() {
        // 让AccountActivity进行界面切换
        mAccountTrigger.triggerView();
    }


    // 开放行为，V端职责：外部需要展示错误，作为接口 BaseContract.View 的函数
    @Override
    public void showError(int str) {
        super.showError(str);
        // 当需要显示错误的时候触发，一定是结束了

        // 停止Loading
        mLoading.stop();
        // 让控件可以输入
        mPhone.setEnabled(true);
        mName.setEnabled(true);
        mPassword.setEnabled(true);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(true);
    }

    // 开放行为，V端职责：外部需要展示加载，作为接口 BaseContract.View 的函数
    @Override
    public void showLoading() {
        super.showLoading();

        // 正在进行时，正在进行注册，界面不可操作
        // 开始Loading
        mLoading.start();
        // 让控件不可以输入
        mPhone.setEnabled(false);
        mName.setEnabled(false);
        mPassword.setEnabled(false);
        // 提交按钮不可以继续点击
        mSubmit.setEnabled(false);

    }

    // 开放行为，V端职责：外部加载成功后调用，作为接口 RegisterContract.View 的函数
    @Override
    public void registerSuccess() {
        // 注册成功，这个时候账户已经登录
        // 我们需要进行跳转到MainActivity界面
        MainXActivity.show(getContext());
        // 关闭当前界面
        getActivity().finish();
    }
}

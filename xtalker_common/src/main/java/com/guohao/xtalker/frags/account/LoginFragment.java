package com.guohao.xtalker.frags.account;


import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import com.guohao.common.app.PresenterFragment;
import com.guohao.factory.presenter.account.LoginContract;
import com.guohao.factory.presenter.account.LoginPresenter;
import com.guohao.xtalker.MainXActivity;
import com.guohao.xtalker.R;
import com.guohao.xtalker.R2;

import net.qiujuer.genius.ui.widget.Loading;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录的界面
 */
public class LoginFragment extends PresenterFragment<LoginContract.Presenter>
        implements LoginContract.View {
    private AccountTrigger mAccountTrigger;

    @BindView(R2.id.edit_phone)
    EditText mPhone;
    @BindView(R2.id.edit_password)
    EditText mPassword;

    @BindView(R2.id.loading)
    Loading mLoading;

    @BindView(R2.id.btn_submit)
    Button mSubmit;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 拿到我们的Activity的引用
        mAccountTrigger = (AccountTrigger) context;
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_login;
    }


    @OnClick(R2.id.btn_submit)
    void onSubmitClick() {
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();
        // 调用P层进行注册
        mPresenter.login(phone, password);
    }

    @OnClick(R2.id.txt_go_register)
    void onShowRegisterClick() {
        // 让AccountActivity进行界面切换
        mAccountTrigger.triggerView();
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        // 当需要显示错误的时候触发，一定是结束了

        // 停止Loading
        mLoading.stop();
        // 让控件可以输入
        mPhone.setEnabled(true);
        mPassword.setEnabled(true);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();

        // 正在进行时，正在进行注册，界面不可操作
        // 开始Loading
        mLoading.start();
        // 让控件不可以输入
        mPhone.setEnabled(false);
        mPassword.setEnabled(false);
        // 提交按钮不可以继续点击
        mSubmit.setEnabled(false);
    }

    @Override
    public void loginSuccess() {
        MainXActivity.show(getContext());
        getActivity().finish();
    }
}

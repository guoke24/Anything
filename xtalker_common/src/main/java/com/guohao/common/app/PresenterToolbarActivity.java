package com.guohao.common.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.guohao.factory.presenter.BaseContract;
import com.guohao.xtalker.R;


/**
 * 该基类添加的功能
 *
 * 在继承带ToolBar的ToolbarActivity后，
 * 实现一些 MVP 模式中，View 端需要实现的职责：
 * 1.初始化和设置 Presenter
 * 2.显示和隐藏加载 by 占位布局 mPlaceHolderView
 * 3.显示错误 by 占位布局 mPlaceHolderView
 *
 */
public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter>
        extends ToolbarActivity implements BaseContract.View<Presenter> {
    protected Presenter mPresenter;
    protected ProgressDialog mLoadingDialog;

    @Override
    protected void initBefore() {
        super.initBefore();
        // 初始化Presenter
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 界面关闭时进行销毁的操作
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    /**
     * 初始化Presenter
     *
     * @return Presenter
     */
    protected abstract Presenter initPresenter();

    @Override
    public void showError(int str) {
        // 不管你怎么样，我先隐藏我
        hideDialogLoading();

        // 显示错误, 优先使用占位布局
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(str);
        } else {
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        } else {
            ProgressDialog dialog = mLoadingDialog;
            if (dialog == null) {
                dialog = new ProgressDialog(this, R.style.AppTheme_Dialog_Alert_Light);
                // 不可触摸取消
                dialog.setCanceledOnTouchOutside(false);
                // 强制取消关闭界面
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                mLoadingDialog = dialog;
            }

            dialog.setMessage(getText(R.string.prompt_loading));
            dialog.show();
        }
    }

    protected void hideDialogLoading() {
        ProgressDialog dialog = mLoadingDialog;
        if (dialog != null) {
            mLoadingDialog = null;
            dialog.dismiss();
        }
    }

    protected void hideLoading() {
        // 不管你怎么样，我先隐藏我
        hideDialogLoading();

        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        // View中赋值Presenter
        mPresenter = presenter;
    }
}

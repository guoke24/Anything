package com.guohao.factory.presenter.session;

import com.guohao.factory.model.db.Session;
import com.guohao.factory.presenter.BaseContract;

/**
 * 会话mvp的契约
 */
public interface SessionContract {

    /**
     * 继承基础契约接口的 presenter 接口
     *
     * presenter 需要开放给 view 两个接口，启动 和 销毁
     * 即 start 和 destory
     *
     */
    interface Presenter extends BaseContract.Presenter{
    };


    /**
     * 继承 BaseContract 契约接口的 RecyclerView 接口
     *
     * view 需要开放给 present 接口有：
     *
     * 基础view：显示加载，显示错误，设置 presenter;
     *
     * RecyclerView：获取Adapter，刷新数据
     */
     interface View extends BaseContract.RecyclerView<Presenter, Session>{

    }
}

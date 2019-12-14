package com.guohao.factory.presenter;

import com.guohao.factory.data.DataSource;
import com.guohao.factory.data.DbDataSource;

import java.util.List;

/**
 * 基础的仓库源的Presenter定义
 *
 * 泛型 Data，是查询数据表对应的实体类
 *
 * 泛型 viewModel，是 RecyclerView 的 item 对应的类型
 *
 * 泛型 Source，是仓库源的接口依赖
 *
 * 泛型 View，即绑定的 RecyclerView
 *
 */
public abstract class BaseSourcePresenter<Data, viewModel,
        Source extends DbDataSource<Data>,
        View extends BaseContract.RecyclerView>
        extends BaseRecyclerPresenter<viewModel, View>
        implements DataSource.SucceedCallback<List<Data>> {

    // 持有一个数据源的引用
    protected Source mSource;

    public BaseSourcePresenter(Source source,View view) {
        super(view);
        // 构造时获得数据源的接口引用，可使用其开放的 load 函数
        this.mSource = source;
    }

    // 积累默认在启动时，调用数据源的 load 函数
    @Override
    public void start() {
        super.start();
        if (mSource != null)
            mSource.load(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        mSource.dispose();
        mSource = null;
    }
}

package com.guohao.factory.data;

import android.support.annotation.NonNull;

import com.guohao.factory.data.helper.DbHelper;
import com.guohao.factory.model.db.BaseDbModel;
import com.guohao.utils.CollectionUtil;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import net.qiujuer.genius.kit.reflect.Reflector;

/**
 *
 * 主要任务就是作为观察者，监听数据库的改动，
 * 若有改动，则 onDataSave 函数便被调用，
 * 该函数由 DbHelper.ChangedListener 接口约束；
 *
 *
 * 同时，也可主动发起数据库查询，可在开放的 load 接口内实现，需要接口 DbDataSource 来约束，
 * 外部将通过接口 DbDataSource 来依赖；
 * 查询的数据表由泛型的确定，
 * @param <Data> 泛型，BaseModel的子类，该类查询的表，对应的实体类都是BaseModel的子类；
 *
 * 查询数据库的结果通过 onListQueryResult 接口回传，
 * 该函数由 QueryTransaction.QueryResultListCallback 接口约束，
 * 接着会间接的传递到 onDataSave 函数；
 *
 * 反正，不管是被动还是主动的改动数据库，都会有结果传到 onDataSave 函数，
 * 最总，统一由 onDataSave 函数再回调给 P 端，通过 P 端的引用，即 SucceedCallback 接口，
 * P 端在实例化该类型时，便传入 SucceedCallback 接口实例进行绑定。
 *
 */
public abstract class BaseDbRepository<Data extends BaseDbModel>
        implements
        DbDataSource<Data>,
        QueryTransaction.QueryResultListCallback<Data>,
        DbHelper.ChangedListener<Data>{


    // 此处持有的成功回调接口，是由 P 端实现的，
    // 也就是由此接口，回调传递接口给 P 端。
    SucceedCallback<List<Data>> callback;
    private final List<Data> dataList = new LinkedList<>(); // 当前缓存的数据
    private Class<Data> dataClass; // 当前范型对应的真实的Class信息


    public BaseDbRepository() {

        // 拿当前类的范型数组信息
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class, this.getClass());
        dataClass = (Class<Data>) types[0];
    }

    /**
     * 开放给 P 端
     * @param callback 加载成功后返，通过 Callback 返回给 P 端
     */
    @Override
    public void load(DataSource.SucceedCallback<List<Data>> callback) {
        this.callback = callback;
        // 进行数据库监听操作
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        // 取消监听，销毁数据
        this.callback = null;
        DbHelper.removeChangedListener(dataClass, this);
        dataList.clear();
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        // 数据库加载数据成功
        if (tResult.size() == 0) {
            dataList.clear();
            notifyDataChange();// 数据为0，直接通知
            return;
        }

        // 转变为数组
        Data[] users = CollectionUtil.toArray(tResult, dataClass);
        // 回到数据集更新的操作中
        onDataSave(users);
    }

    @Override
    public void onDataSave(Data... list) {
        boolean isChanged = false;
        // 当数据库数据变更的操作
        for (Data data : list) {
            // 是关注的人，同时不是我自己
            if (isRequired(data)) {
                insertOrUpdate(data);
                isChanged = true;
            }
        }
        // 有数据变更，则进行界面刷新
        if (isChanged)
            notifyDataChange();
    }

    @Override
    public void onDataDelete(Data... list) {

    }


    // 维护缓存数据的一些列函数 start

    // 插入或者更新，
    private void insertOrUpdate(Data data) {
        int index = indexOf(data);
        if (index >= 0) {
            replace(index, data);
        } else {
            insert(data);
        }
    }

    // 更新操作，更新某个坐标下的数据
    protected void replace(int index, Data data) {
        dataList.remove(index);
        dataList.add(index, data);
    }

    // 添加方法
    protected void insert(Data data) {
        dataList.add(data);
    }


    // 查询一个数据是否在当前的缓存数据中，如果在则返回坐标
    protected int indexOf(Data newData) {
        int index = -1;
        for (Data data : dataList) {
            index++;
            if (data.isSame(newData)) {
                return index;
            }
        }
        return -1;
    }

    // 维护缓存数据的一些列函数 end

    /**
     * 检查一个User是否是我需要关注的数据
     *
     * @param data Data
     * @return True是我关注的数据
     */
    protected abstract boolean isRequired(Data data);

    /**
     * 添加数据库的监听操作
     */
    protected void registerDbChangedListener() {
        DbHelper.addChangedListener(dataClass, this);
    }

    // 通知界面刷新的方法
    private void notifyDataChange() {
        SucceedCallback<List<Data>> callback = this.callback;
        if (callback != null)
            callback.onDataLoaded(dataList);
    }

}

package com.guohao.factory.data.helper;


import com.guohao.factory.Factory;
import com.guohao.factory.data.DataSource;
import com.guohao.factory.model.api.RspModel;
import com.guohao.factory.model.api.group.GroupCreateModel;
import com.guohao.factory.model.card.GroupCard;
import com.guohao.factory.model.db.Group;
import com.guohao.factory.net.Network;
import com.guohao.factory.net.RemoteService;
import com.guohao.xtalker.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 对群的一个简单的辅助工具类
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class GroupHelper {
    public static Group find(String groupId) {
        // TODO 查询群的信息，先本地，后网络
        return null;
    }

    public static Group findFromLocal(String groupId) {
        // TODO 本地找群信息
        return null;
    }

    // 群的创建
    public static void create(GroupCreateModel model, final DataSource.Callback<GroupCard> callback) {
        RemoteService service = Network.remote();
        service.groupCreate(model)
                .enqueue(new Callback<RspModel<GroupCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<GroupCard>> call, Response<RspModel<GroupCard>> response) {
                        RspModel<GroupCard> rspModel = response.body();
                        if (rspModel.success()) {
                            GroupCard groupCard = rspModel.getResult();
                            // 唤起进行保存的操作
                            Factory.getGroupCenter().dispatch(groupCard);
                            // 返回数据
                            callback.onDataLoaded(groupCard);
                        } else {
                            Factory.decodeRspCode(rspModel, callback);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<GroupCard>> call, Throwable t) {
                        callback.onDataNotAvailable(R.string.data_network_error);
                    }
                });
    }
}

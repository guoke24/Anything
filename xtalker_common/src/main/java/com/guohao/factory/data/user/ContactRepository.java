package com.guohao.factory.data.user;



import com.guohao.factory.data.BaseDbRepository;
import com.guohao.factory.data.DataSource;
import com.guohao.factory.data.helper.DbHelper;
import com.guohao.factory.model.db.User;
import com.guohao.factory.model.db.User_Table;
import com.guohao.factory.persistence.Account;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.List;

/**
 * 联系人仓库，数据表 User 的观察者
 *
 *
 */
public class ContactRepository extends BaseDbRepository<User>
        implements
        ContactDataSource,
        QueryTransaction.QueryResultListCallback<User>,
        DbHelper.ChangedListener<User> {

    private DataSource.SucceedCallback<List<User>> callback;


    @Override
    public void load(DataSource.SucceedCallback<List<User>> callback) {
        super.load(callback);

        // 加载本地数据库数据
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    /**
     * 检查一个User是否是我需要关注的数据
     *
     * @param user User
     * @return True是我关注的数据
     */
    @Override
    protected boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }
}

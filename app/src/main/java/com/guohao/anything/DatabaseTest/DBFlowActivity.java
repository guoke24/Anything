package com.guohao.anything.DatabaseTest;

import android.os.Bundle;
import android.view.View;

import com.guohao.anything.BaseTestActivity;
import com.guohao.anything.R;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import java.util.UUID;

public class DBFlowActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_dbflow);
        super.onCreate(savedInstanceState);
    }

    // 初始化，可同时创建数据库和数据表
    public void test_1(View view){
        showMessage("DBFlowActivity test_1:");

        // 第一种初始化方式
        FlowManager.init(this);

        // 第二种初始化方式
//        FlowManager.init(new FlowConfig.Builder(this)
//                .openDatabasesOnInit(true) // 加上该行，初始化时就创建 .db 的数据库文件并建表
//                .build());
    }

    // 添加数据，若数据库和数据表未创建，就先创建再增加数据
    public void test_2(View view){
        showMessage("DBFlowActivity test_2:");
        // 添加数据

        DBFlowDataTable user = new DBFlowDataTable();
        user.id = UUID.randomUUID();
        user.name = "Andrew Grosner";
        user.age = 27;

        ModelAdapter<DBFlowDataTable> adapter =
                FlowManager.getModelAdapter(DBFlowDataTable.class);

        adapter.insert(user);

    }

    public void test_3(View view){
        showMessage("DBFlowActivity test_3:");
        // 插入数据

    }
}

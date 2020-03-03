package com.guohao.anything.DatabaseTest;


import android.os.Bundle;
import android.view.View;

import com.guohao.anything.BaseTestActivity;
import com.guohao.anything.R;

public class SQLiteActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sqlite);
        super.onCreate(savedInstanceState);
    }

    // 测试建表
    public void test_1(View view){
        showMessage("SQLiteActivity test_1:");

        // 执行此句，创建数据库，并创建数据表
        // 数据库名 和 版本号 在 DBManger 内部静态指定
        DBManger manger = DBManger.getInstance(this);

        // 执行此句，建表
        manger.createTable();

    }

    // 测试增加
    public void test_2(View view){
        // 执行此句，创建数据库，并创建数据表
        // 数据库名 和 版本号 在 DBManger 内部静态指定
        DBManger manger = DBManger.getInstance(this);

        // 执行此句，增加数据
        manger.add();
    }

}

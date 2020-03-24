package com.guohao.anything.DatabaseTest;

import com.raizlabs.android.dbflow.annotation.Database;



/**
 * 定义一个数据库
 *
 * 包括数据库名和版本号
 *
 */
@Database(name = DBFlowDatabase.NAME, version = DBFlowDatabase.VERSION)
public class DBFlowDatabase {
    public static final String NAME = "DBFlowDatabase";
    public static final int VERSION = 1;
}
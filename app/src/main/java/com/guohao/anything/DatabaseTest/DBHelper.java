package com.guohao.anything.DatabaseTest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.guohao.anything.LogUtil;


/**
 * Android 数据库的原生框架，是拓展类的使用套路
 *
 * SQLiteOpenHelper 的拓展类
 *
 * 用于创建数据库，指明数据库的名字和版本号
 *
 * 其 getWritableDatabase 函数返回的 SQLiteDatabase 实例用于操作数据库
 *
 * 可以封装多个类似 DBHelper 的类，统一委托此类创建数据库
 */
public class DBHelper extends SQLiteOpenHelper {

    // 数据库默认名字
    //public static final String db_name = "test_original.db";

    // 构造函数中指明 数据库名 和 版本
    // 创建一个实例的时候，就会创建一个数据库（如果不存在的话）
    public DBHelper(Context context, String db_name,int db_version) {
        super(context, db_name, null, db_version);
    }


    // 名字为 db_name 的数据库存在，就不调用了。
    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.e("");

        // 创建库的时候，跟着建表
        // 因为这个时候，肯定不存在表，所以不加 "if not exists" 的判断
//        db.execSQL("create table table1 (" +
//                " _byte byte," +
//                " _long long," +
//                " _text text," +
//                " _short short," +
//                " _int int," +
//                " _float float," +
//                " _double double," +
//                " _boolean boolean," +
//                " _blob blob" +
//                ")");

        // 可以建多个表
//        db.execSQL("create table if not exists person (" +
//                "personid integer primary key autoincrement ," +
//                "name varchar(30) ," +
//                "age integer(3) )");
    }

    // 若名字为 db_name 的数据库存，且版本号大于原来，就会调用此函数
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.e("");

    }

}

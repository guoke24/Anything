package com.guohao.anything.DatabaseTest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.guohao.anything.LogUtil;

/**
 * 数据库管理者（额外的一层封装）
 *
 * 持有自己的单例
 * 单例内有唯一的 SQLiteDatabase 实例 writableDatabase ，用于操作数据库
 *
 * 对数据库的操作，都封装在此类，
 * 依赖 writableDatabase 执行数据库的操作
 *
 * 由于采用单例模式，该类只能创建一个实例，则只能创建一个数据库
 * 所以数据库名和版本号，定义在此处，静态final的形式
 *
 */
public class DBManger {

    public static final String def_db_name = "test_original.db";
    public static final int def_db_version = 1;

    private Context context;
    private static DBManger instance;
    // 操作表的对象，进行增删改查
    private SQLiteDatabase writableDatabase;

    // 一个单例带有一个 writableDatabase 引用
    private DBManger(Context context) {
        this.context = context;
        DBHelper dbHelper = new DBHelper(context, def_db_name, def_db_version);
        writableDatabase = dbHelper.getWritableDatabase();
    }

    // 单例模式
    public static DBManger getInstance(Context context) {
        if (instance == null) {
            synchronized (DBManger.class) {
                if (instance == null) {
                    instance = new DBManger(context);
                }
            }
        }
        return instance;
    }

    // ----- 对数据库的操作在此 -----

    // 尝试在此建表，通过 writableDatabase 实例！！！
    public void createTable(){

        // 若 table1 存在，不加 if not exists 会报错 table1 already exists
        writableDatabase.execSQL("create table if not exists table1 (" +
                " _byte byte," +
                " _long long," +
                " _text text," +
                " _short short," +
                " _int int," +
                " _float float," +
                " _double double," +
                " _boolean boolean," +
                " _blob blob" +
                ")");

        LogUtil.e("操作完成！");

    }

    // 增加操作
    public void add() {

        ContentValues contentValues = new ContentValues();

        byte _byte = Byte.MAX_VALUE;
        contentValues.put("_byte", _byte);

        long _long = Long.MAX_VALUE;
        contentValues.put("_long", _long);

        String _text = "字符串";
        contentValues.put("_text", _text);

        short _short = Short.MAX_VALUE;
        contentValues.put("_short", _short);

        int _int = Integer.MAX_VALUE;
        contentValues.put("_int", _int);

        float _float = Float.MAX_VALUE;
        contentValues.put("_float", _float);

        double _double = Double.MAX_VALUE;
        contentValues.put("_double", _double);

        boolean _boolean = true;
        contentValues.put("_boolean", _boolean);

        byte[] _byteArr = {Byte.MIN_VALUE, Byte.MAX_VALUE};
        contentValues.put("_blob", _byteArr);

        writableDatabase.insert("table1", null, contentValues);

        LogUtil.e("操作完成！");
    }

}
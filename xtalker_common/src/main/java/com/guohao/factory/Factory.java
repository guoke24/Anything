package com.guohao.factory;

import android.support.annotation.StringRes;
import android.util.Log;


import com.guohao.common.app.Application;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class Factory {
    private static final String TAG = Factory.class.getSimpleName();
    // 单例模式
    private static final Factory instance;
    // 全局的线程池
    private final Executor executor;
    // 全局的Gson
    //private final Gson gson;


    static {
        instance = new Factory();
    }

    private Factory() {
        // 新建一个4个线程的线程池
        executor = Executors.newFixedThreadPool(4);
//        gson = new GsonBuilder()
//                // 设置时间格式
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
//                // 设置一个过滤器，数据库级别的Model不进行Json转换
//                .setExclusionStrategies(new DBFlowExclusionStrategy())
//                .create();
    }

    /**
     * Factory 中的初始化
     */
//    public static void setup() {
//        // 初始化数据库
//        FlowManager.init(new FlowConfig.Builder(app())
//                .openDatabasesOnInit(true) // 数据库初始化的时候就开始打开
//                .build());
//
//        // 持久化的数据进行初始化
//        Account.load(app());
//    }

    /**
     * 返回全局的Application
     *
     * @return Application
     */
    public static Application app() {
        return Application.getInstance();
    }


    /**
     * 异步运行的方法
     *
     * @param runnable Runnable
     */
    public static void runOnAsync(Runnable runnable) {
        // 拿到单例，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }


}

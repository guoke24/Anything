package com.guohao.anything.memory;

import android.content.Context;

public class SingleInstance {

    private Context context;

    // 单例的全局生命周期决定了，创建之后持有的 context 也是全局生命周期，不能回收
    private SingleInstance(Context context) {
        this.context = context;
    }

    // 静态内部类
    public static class Holder {
        // 私有静态字段，全局生命周期，全局唯一
        private static SingleInstance INSTANCE;

        public static SingleInstance newInstance(Context context) {
            if (INSTANCE == null) {
                INSTANCE = new SingleInstance(context);
            }
            return INSTANCE;
        }
    }
}

package com.guohao.anything.sync;

public class ThreadUtils {

    public static void p(String content){
        System.out.println(Thread.currentThread().getName() + ": " + content);

    }
}

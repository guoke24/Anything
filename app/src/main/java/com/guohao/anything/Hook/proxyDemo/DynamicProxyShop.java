package com.guohao.anything.Hook.proxyDemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理
 *
 * 运行时通过反射，来动态生成代理类对象，并确定到底代理谁
 *
 * Java 提供来动态代理接口 InvocationHandler
 *
 */
public class DynamicProxyShop implements InvocationHandler {

    private Object mObject;

    public DynamicProxyShop(Object obj){
        mObject = obj;
    }

    /**
     *
     * 当 被代理人的某个方法被调用一次，就会分发到此处做处理
     *
     * 同时会得知：被代理人是谁？proxy；被调用的方法是哪个？method；参数是什么？args。
     *
     * @param proxy 被代理人实例
     * @param method 被代理人实例 proxy 的方法
     * @param args 被代理人实例的方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 通过反射调用 mObject 的 函数 method，并传入参数 args
        Object result = method.invoke(mObject,args);

        if(method.getName().equals("buy")){ // 若是 buy 函数，提示一下
            System.out.println("Guoke is Buying");
        }

        return null;
    }
}

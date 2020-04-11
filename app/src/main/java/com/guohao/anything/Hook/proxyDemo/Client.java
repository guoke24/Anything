package com.guohao.anything.Hook.proxyDemo;


import java.lang.reflect.Proxy;

/**
 * 测试了 静态代理 和 动态代理 的实现方法，代码写法；
 *
 * 静态代理demo 是为了做一个对比参考
 *
 * 真正重点在于动态代理，通过 「InvocationHandler」 和 「反射」 实现；
 * 帮助了解动态代理的概念，未具有实际应用的意义
 *
 */
public class Client {
    public static void main(String args[]){

    }

    /**
     * 静态代理demo
     */
    void testStaticProxy(){

        IShop guoke = new Guoke();

        // 静态代理者
        IShop staticProxy = new StaticProxyShop(guoke);

        // 代理执行，内部是直接调用
        staticProxy.buy();

    }

    /**
     * 动态代理demo，通过 「InvocationHandler」 和 「反射」 实现
     */
    void testDynamicProxy(){

        IShop guoke = new Guoke();

        // InvocationHandler 的子类
        DynamicProxyShop dynamicProxyShop =  new DynamicProxyShop(guoke);

        // 动态代理者
        IShop dynamicProxy = (IShop) Proxy.newProxyInstance(
                guoke.getClass().getClassLoader(), // 指定 被代理对象 的类加载器
                new Class[]{IShop.class}, // 被代理人的类型所实现的接口
                dynamicProxyShop // 执行「被代理人的所实现的接口函数」的「代理接口」
        );

        // 代理执行，内部是反射调用
        dynamicProxy.buy();// -> dynamicProxyShop.invoke( 被代理的类实例：guoke , 函数：buy() , 参数：无 )
    }
    // 需要加深理解的两个知识点：
    // InvocationHandler
    // Proxy.newProxyInstance( 被代理对象 的类加载器，被代理人的类型所实现的接口，InvocationHandler-调用被代理人函数的 Handler )

    // 补充，看了 P358 的代码，也是该链接代码：https://blog.csdn.net/itachi85/article/details/80574390
    //
    // Proxy.newProxyInstance( Thread.currentThread().getContextClassLoader(),new Class<?>[] { iActivityManagerClazz }, new IActivityManagerProxy(iActivityManager));
    //
    // 产生的疑惑：为何是 Thread.currentThread().getContextClassLoader()
    //
    // 回答：我的误区在于，以为一个类会独有一个类加载器；
    // 而其实，一个线程下，或者一个环境下，一个APP内，所用的加载器可能都是用一个
    // 所以，Thread.currentThread().getContextClassLoader() 的意思是使用了当前线程的所在环境的加载器，没问题

}

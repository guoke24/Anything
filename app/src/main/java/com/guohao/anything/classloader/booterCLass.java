package com.guohao.anything.classloader;

/**
 * 测试静态内部类和外部类的加载循序
 */

/**
 * 一个类在被使用到的时候，就会被加载；
 * 所谓的使用到一个类，就是指访问到该类的静态成员变量或静态成员函数，或者 new 该类实例
 *
 * 若一个类有内部类，则分两种情况：
 * 一，静态内部类
 * 使用到 静态内部类 的时候，只会加载静态内部类；而不会加载外部类
 *
 * 二，动态内部类
 * 被 new 时，才会加载该动态内部类
 *
 * 若只是用到外部类，是不会加载内部类的！
 *
 */
public class booterCLass {

    public static void main(String[] atgs){
        System.out.println("booterClass");

        // new 一个 外部类时，
        // 加载结果：
        // 外部类静态代码块加载
        // 外部类代码块加载
        // 不会加任何的载内部类
        //new OutClass();

        // 访问外部类的静态函数时，
        // 加载结果：
        // 外部类静态代码块加载
        // 外部类函数-say
        // 不会加任何的载内部类
        //OutClass.say();

        // 测试访问静态内部类
        //testStaticClassLoader();

        // 测试访问动态内部类
        //new booterCLass().testDynamicClassLoader();
    }

    // 访问静态内部类
    public  static void testStaticClassLoader(){
        // 使用到 静态内部类 的时候，只会加载静态内部类
        // 加载结果：
        // 静态内部类的静态代码块加载
        //new OutClass.InnerStaticClass();

        // 静态内部类 的静态函数使用到了 外部类的静态变量的时候，
        // 外部类也会被加载
        // 加载结果：
        // 静态内部类的静态代码块加载
        // 外部类静态代码块加载
        // 静态内部类函数-say，外部类变量 i = 1
        OutClass.InnerStaticClass.say();
    }

    // 创建动态内部类，即非静态内部类
    public void testDynamicClassLoader(){
        // 直接通过「外部类名.内部类名」的声明方式会报错
        //new OutClass.InnerDynamicClass();

        // 需要先创建一个外部类实例
        OutClass outClass = new OutClass();
        // 再通过「外部类实例引用名.new 内部类名()」的方式创建内部类
        outClass.new InnerDynamicClass().say();
        // 加载结果：
        // 外部类静态代码块加载
        // 外部类代码块加载
        // 动态内部类加载
        // 动态内部类函数-say，外部类变量 i = 1
    }
}

package com.guohao.anything.classloader;

/**
 * 测试静态内部类和外部类的加载循序
 */
public class booterCLass {

    public static void main(String[] atgs){
        System.out.println("booterClass");

        testStaticClassLoader();

        //new booterCLass().testDynamicClassLoader();
    }

    public  static void testStaticClassLoader(){
        //OutClass.say();
        System.out.println("after OutClass.say");
        // 使用到 静态内部类 的时候，只会加载静态内部类
        // 但 静态内部类 的静态函数使用到了 外部类的静态变量的时候，
        // 外部类也会被加载。
        // 所谓的使用到一个类，就是指访问到该类的成员变量或成员函数。
        OutClass.InnerStaticClass.say();
    }

    public void testDynamicClassLoader(){
        // 直接通过「外部类名.内部类名」的声明方式会报错
        //new OutClass.InnerDynamicClass();

        // 需要先创建一个外部类实例
        OutClass outClass = new OutClass();
        // 再通过「外部类实例引用名.new 内部类名()」的方式创建内部类
        //outClass.new InnerDynamicClass().say();

    }
}

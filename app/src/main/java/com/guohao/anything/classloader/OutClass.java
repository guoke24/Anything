package com.guohao.anything.classloader;

public class OutClass {

    // 访问到静态成员变量或静态成员函数，类就会被加载
    private static int i =1;

    // 使用到静态成员，或者动态实例化两种方式
    // 引起的类加载的时候调用
    static {
        System.out.println("外部类静态代码块加载");
    }

    // 仅仅创建类实例引起的类加载的时候，调用
    // 先调用 static 的代码块，再调用此处的普通代码块
    {
        System.out.println("外部类代码块加载");
    }



    public static void say(){
        System.out.println("外部类函数-say");
    }


    static class InnerStaticClass{

        int k = 0;//静态内部类，可以带有非静态的成员变量

        public void cry(){//静态内部类，可以带有非静态的成员函数

        }

        //
        static {
            System.out.println("静态内部类的静态代码块加载");
        }

        public static void say(){
            System.out.println("静态内部类函数-say，外部类变量 i = " + i);
        }
    }

    class InnerDynamicClass{

        //public static int j = 0;//非静态内部类，不能再带有静态的成员

        // new 的时候才会加载类
        // 类加载的时候调用
        {
            System.out.println("动态内部类加载");
        }

        public void say(){
            System.out.println("动态内部类函数-say，外部类变量 i = " + i);
        }
    }

}

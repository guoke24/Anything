package com.anything.guohao.anything.jvm;

// 参考：https://blog.csdn.net/Wang_1997/article/details/52267688
public class StaticTest {

    public static int k = 0;
    public static StaticTest t1 = new StaticTest("创建 t1");
    public static StaticTest t2 = new StaticTest("创建 t2");
    public static int i = print("赋值类静态变量i");
    public static int n = 99;
    public int j = print("赋值成员变量j");


    {
        print("构造块"); // 构造代码快优先于构造方法
    }

    static{
        print("静态块");
    }

    public StaticTest(String str) {
        System.out.println((++k) + ": StaticTest(" + str + ")，i=" + i + "，n=" + n);
        ++n;
        ++i;
    }

    public static int print(String str) {
        System.out.println((++k) + ": print(" + str + ")，i=" + i + "，n=" + n);
        ++i;
        return ++n;
    }

}




//注释两行代码：
//public static StaticTest t1 = new StaticTest("创建 t1");
//public static StaticTest t2 = new StaticTest("创建 t2");
//运行log：
//2019-08-20 14:40:38.171 5225-5225/com.anything.guohao.anything I/System.out: 创建 out_t1
//2019-08-20 14:40:38.172 5225-5225/com.anything.guohao.anything I/System.out: 1: print(赋值类静态变量i)，i=0，n=0
//2019-08-20 14:40:38.173 5225-5225/com.anything.guohao.anything I/System.out: 2: print(静态块)，i=1，n=99
//2019-08-20 14:40:38.173 5225-5225/com.anything.guohao.anything I/System.out: 3: print(赋值成员变量j)，i=2，n=100
//2019-08-20 14:40:38.173 5225-5225/com.anything.guohao.anything I/System.out: 4: print(构造块)，i=3，n=101
//2019-08-20 14:40:38.173 5225-5225/com.anything.guohao.anything I/System.out: 5: StaticTest(new out_t1)，i=4，n=102


//加上两行代码：
//public static StaticTest t1 = new StaticTest("创建 t1");
//public static StaticTest t2 = new StaticTest("创建 t2");
//运行log：
//2019-08-20 14:35:54.982 5113-5113/com.anything.guohao.anything I/System.out: 创建 out_t1

//2019-08-20 14:35:54.984 5113-5113/com.anything.guohao.anything I/System.out: 1: print(赋值成员变量j)，i=0，n=0
//2019-08-20 14:35:54.984 5113-5113/com.anything.guohao.anything I/System.out: 2: print(构造块)，i=1，n=1
//2019-08-20 14:35:54.984 5113-5113/com.anything.guohao.anything I/System.out: 3: StaticTest(创建 t1)，i=2，n=2

//2019-08-20 14:35:54.984 5113-5113/com.anything.guohao.anything I/System.out: 4: print(赋值成员变量j)，i=3，n=3
//2019-08-20 14:35:54.984 5113-5113/com.anything.guohao.anything I/System.out: 5: print(构造块)，i=4，n=4
//2019-08-20 14:35:54.984 5113-5113/com.anything.guohao.anything I/System.out: 6: StaticTest(创建 t2)，i=5，n=5

//2019-08-20 14:35:54.984 5113-5113/com.anything.guohao.anything I/System.out: 7: print(赋值类静态变量i)，i=6，n=6
//2019-08-20 14:35:54.984 5113-5113/com.anything.guohao.anything I/System.out: 8: print(静态块)，i=7，n=99
//2019-08-20 14:35:54.985 5113-5113/com.anything.guohao.anything I/System.out: 9: print(赋值成员变量j)，i=8，n=100
//2019-08-20 14:35:54.985 5113-5113/com.anything.guohao.anything I/System.out: 10: print(构造块)，i=9，n=101
//2019-08-20 14:35:54.985 5113-5113/com.anything.guohao.anything I/System.out: 11: StaticTest(new out_t1)，i=10，n=102


// 对比两段log
// 可得总结：
// 1-3 对应的是 创建 out_t1 的过程中，给其类变量t1赋值为 new StaticTest("创建 t1")
// 1: print(赋值成员变量j)，i=0，n=0
// 2: print(构造块)，i=1，n=1
// 3: StaticTest(创建 t1)，i=2，n=2

// 4-6 对应的是 创建 out_t1 的过程中，给其类变量t2赋值为 new StaticTest("创建 t2")
// 4: print(赋值成员变量j)，i=3，n=3
// 5: print(构造块)，i=4，n=4
// 6: StaticTest(创建 t2)，i=5，n=5

// 7-10 是 创建 out_t1 时执行的
// 7: print(赋值类静态变量i)，i=6，n=6
// 8: print(静态块)，i=7，n=99
// 9: print(赋值成员变量j)，i=8，n=100
// 10: print(构造块)，i=9，n=101
// 11: StaticTest(new out_t1)，i=10，n=102


// 1.加载的顺序：
// 先父类的static成员变量 ->
// 子类的static成员变量 ->
// 父类的成员变量 ->
// 父类构造 ->
// 子类成员变量 ->
// 子类构造

// 2.static只会加载一次，所以通俗点讲：
// 第一次new的时候，所有的static都先会被全部载入(以后再有new都会忽略)，进行默认初始化。
// 在从上往下进行显示初始化。这里静态代码块和静态成员变量没有先后之分，谁在上，谁就先初始化
// 需要注意的一点是，在1-3，4-6这两个过程中，由于不是第一次new了，所以会跳过静态类变量的赋值。
// 所以判断是否要加载 static 的标准不是【是否加载过了】，而是【是否是第一次new】

// 3.构造代码块是什么？
// 把所有构造方法中相同的内容抽取出来，定义到构造代码块中，将来在调用构造方法的时候，会去自动调用构造代码块。
// 构造代码快优先于构造方法。
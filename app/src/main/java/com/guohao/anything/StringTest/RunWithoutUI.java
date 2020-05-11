package com.guohao.anything.StringTest;
// 如何分析java中字符串缓冲池:http://www.kokojia.com/article/14774.html
public class RunWithoutUI {

    // 首先说明一点，在java 中，直接使用==操作符，比较的是两个字符串的引用地址，并不是比较内容，
    // 比较内容请用String.equals()。

    public static void main(String[] args){
//        String Monday = "Monday";
//        String Mon = "Mon";
//        String day = "day";
//        System.out.println(Monday == "Mon" + "day");
//        System.out.println(Monday == Mon + day);
//        // 分析：
//        // 第一个为什么等于true我们已经说过了，因为两者都是常量所以在编译阶段就已经能确定了，
//        // 在第二个中，day是一个变量，所以不能提前确定他的值，所以两者不相等，
//        // 从这个例子我们可以看出，只有+连接的两边都是字符串常量时，引用才会指向字符串缓冲池，
//        // 否则都是指向内存中的其他地址

        String Monday = "Monday";
        String Mon = "Mon";
        final String day = "day";
        System.out.println(Monday == "Mon" + "day");
        System.out.println(Monday == "Mon" + day);
    }

}

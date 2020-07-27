package com.guohao.anything.ADateStructure;

public class StringTes {

    public static void main(String[] args){

        String s1 = "1231";
        String s2 = "123";

        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());

        String s3 = s1.substring(1); // 调用 native 函数 fastSubstring()
        System.out.println(s3);

        String s4 = s1.replace("1","s");// 两个下标记录匹配下标，填字符到 StringBuilder
        // 内部持有到数据结构是 new char[capacity]
        // append 函数内部调用 System.arraycopy 函数来拷贝字符串
        System.out.println(s1);
        System.out.println(s4);


        User user = new User();
        user.age = 1;
        user.name = "hahaha";

        System.out.println(user.age + user.name);

        user.age = 2;
        user.name = "vvvv";

        System.out.println(user.age + user.name);

        User user1 = new User();
        user = user1;
        System.out.println(user.age + user.name);



    }

}

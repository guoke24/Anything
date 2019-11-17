package com.guohao.anything.jvm;

import com.guohao.anything.LogUtil;

public class People {

    public String name;
    public int age;
    public static String country;


    public People(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public People(String name, int age,String country) {
        this.name = name;
        this.age = age;
        this.country = country;
    }

    public void show(){
        LogUtil.e("name = " + name + "," + "age = " + age + "," + "country = " + country);
    }
}

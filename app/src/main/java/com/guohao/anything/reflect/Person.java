package com.guohao.anything.reflect;

public class Person {
    private int age;
    private String name;

    public Person() {

    }

    public Person(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void fly() {
        System.out.println("走你~~");
    }


    public void smoke(int count) {
        System.out.println("抽了" + count + "支烟");
    }
}
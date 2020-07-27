package com.guohao.anything.Algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class ArrayListTest {

    static int out;// 类的静态变量，被初始化，默认为 0；

    public static void main(String[] args){


        int j;// 静态函数中的临时变量，不赋值

        System.out.println(out);

        Test1 test1 = new Test1();

        System.out.println(test1.age); // 类的实例的成员变量才会 test1.age 被初始化，默认为 0；

        ArrayList<Integer> arrayList = new ArrayList<>();

        arrayList.add(1);
        arrayList.add(-3);
        arrayList.add(5);
        arrayList.add(-7);
        arrayList.add(9);


        // 这种写法会报错
//        for(Integer i : arrayList){
//            System.out.println(i);
//        }

        Iterator<Integer> integerIterator = arrayList.iterator();

        System.out.println(arrayList.iterator().getClass().getName());

        while(integerIterator.hasNext()){
            int tmp = integerIterator.next();
            System.out.println(tmp);
            if(tmp < 0) integerIterator.remove();
        }

        System.out.println("---");

        // 写法 ok
//        for (int i = 0;i < arrayList.size();i++){
//            System.out.println(arrayList.get(i));
//        }

        // 写法 ok
//        for(Integer i : arrayList){
//            System.out.println(i);
//        }

        // 写法 ok
//        arrayList.forEach(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) {
//                System.out.println(integer);
//            }
//        });

        // 写法 ok
//        Iterator<Integer> integerIterator2 = arrayList.iterator();
//        while(integerIterator2.hasNext()){
//            int tmp = integerIterator2.next();
//            System.out.println(tmp);
//        }

    }

    private ArrayList<Integer> removeNegativeNumber(ArrayList<Integer>  arrayList){


        //Iterable iterator =  arrayList.iterator();




        return arrayList;
    }

}

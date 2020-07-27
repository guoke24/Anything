package com.guohao.anything.Algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class HashMapSort {

    public static void main(String[] args){



    }

    // 对一个 hashmap 排序
    // 时间复杂度为：O(N * log(N))，排序的算法复杂度嘛，最快就这样
    // 空间复杂度为：O(3N)，创建了 linkedHashMap，entrySet，arrayList
    private static HashMap<Integer,User> sortHashmap(HashMap<Integer,User> hashMap){

        LinkedHashMap<Integer,User> linkedHashMap = new LinkedHashMap<>();

        Set<Map.Entry<Integer,User>> entrySet = hashMap.entrySet();

        ArrayList<Map.Entry<Integer,User>> arrayList = new ArrayList<>(entrySet);

        // 排序的平均复杂度为 O(N * log(N))
        Collections.sort(arrayList, new Comparator<Map.Entry<Integer,User>>() {

            @Override
            public int compare(Map.Entry<Integer, User> o1, Map.Entry<Integer, User> o2) {

                return o2.getValue().getAge() - o1.getValue().getAge();
            }
        });

        for(int i = 0;i < arrayList.size(); i++){
            Map.Entry<Integer,User> entry = arrayList.get(i);
            linkedHashMap.put(entry.getKey(),entry.getValue());
        }

        return linkedHashMap;

    }

    class User{
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

        int age;
        String name;
    }
}


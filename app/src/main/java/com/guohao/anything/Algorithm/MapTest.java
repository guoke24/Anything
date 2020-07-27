package com.guohao.anything.Algorithm;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MapTest {

    public static void main(String[] args){

        HashMap<Integer,Object> hashMap = new HashMap<>();

        // 该类型的特点是，不冲突
        // 这种存储方式好像叫链地址法

        // 当存储的数据的位置和其本身的数值没有关系的时候，
        // 需要查找的时候，只能采用逐一对比的思路

        // 当 位置 和 数据 有关系的时候，
        // 查找时可采用映射法，此时则时间复杂度为 O(1)

        LinkedHashMap map;


        // SynchronizedMap 是 Collections 的私有静态内部类
        // 只能通过函数获取
        Map<Object, Object> m = Collections.synchronizedMap(new HashMap<>());


        ConcurrentHashMap concurrentHashMap;

    }

}

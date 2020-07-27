package com.guohao.anything.Algorithm;

import java.util.HashSet;

public class SingleNumTest {

    HashSet<Integer> hs = new HashSet<>();

    public int singleNumber(int[] nums) {
        for(int i : nums ){
            if(hs.contains(i)){
                hs.remove(i);
            }else{
                hs.add(i);
            }
        }

        return hs.iterator().next();
    }

}

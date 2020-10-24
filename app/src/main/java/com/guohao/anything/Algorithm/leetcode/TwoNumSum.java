package com.guohao.anything.Algorithm.leetcode;

import java.util.HashMap;

public class TwoNumSum {
    // 时间复杂度：n + n
    // 空间复杂度，额外使用了空间：n
    public int[] twoSum(int[] nums, int target) {

        HashMap<Integer, Integer> map = new HashMap();

        for(int i = 0; i < nums.length; i++){

            if(map.get(nums[i]) == null){
                map.put(nums[i],i);
            }else{
                // 若有两个相同的数，要么是答案，要么都不是答案
                if(nums[i] + nums[i] == target){
                    // 是答案
                    return new int[]{i, map.get(nums[i])};
                }
            }
        }

        // 遍历
        for(int key : map.keySet()){
            int left = target - key;
            // left 是否在 hashmap 中？
            if(map.get(left) != null){
                // 找到答案
                int[] res = new int[]{map.get(key), map.get(left)};
            }
        }

        return new int[]{};
    }
}

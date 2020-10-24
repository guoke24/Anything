package com.guohao.anything.Algorithm;

import java.util.Arrays;

// 小扣在秋日市集选择了一家早餐摊位，一维整型数组 staple 中记录了每种主食的价格，一维整型数组 drinks 中记录了每种饮料的价格。小扣的计划选择一份主食和一款饮料，且花费不超过 x 元。请返回小扣共有多少种购买方案。
//
// 注意：答案需要以 1e9 + 7 (1000000007) 为底取模，如：计算初始结果为：1000000008，请返回 1
//
// 来源：力扣（LeetCode）
// 链接：https://leetcode-cn.com/problems/2vYnGI
public class Breakfast {

    public void main(String[] args) {
        int[] staple = new int[]{2, 1, 1};
        int[] drinks = new int[]{8, 9, 5, 1};
        int x = 9;
        Solution s = new Solution();
        int ans = s.breakfastNumber(staple, drinks, x);
        System.out.println("答案 = " + ans);
    }


    class Solution {
        // 方法一，复杂度：m + n + x
        // 空间复杂度：x
        // 核心思路：用一个新是数组，arr，来统计出，s 块钱以下的早餐有几个
        // 这使得，可以一步到位的拿到 s 块钱以下的早餐的数量，
        // 典型的空间换时间
        public int breakfastNumber(int[] staple, int[] drinks, int x) {

            long answer = 0;
            int[] arr = new int[x + 1];

            for (int s : staple) {
                if (s < x) arr[s] += 1; // 可算出 s 块钱的早餐有几个
            }

            for (int i = 2; i <= x; i++) {
                arr[i] += arr[i - 1];  // 可算出 i 块钱以下的有几个
            }

            for (int d : drinks) { // 遍历每个饮料的价格
                int n = x - d;
                if (n < 0) continue;
                // n 块钱以下的有几个？有这个多：arr[n]
                // 这是一个一步到位的操作，n 块钱以下的早餐的数量，被放在来一个 arr 数组里，下标对应价格，value 对应数量
                // 典型的空间换时间
                answer += arr[n];
            }

            return (int) (answer % (1000000007));
        }
        // 解题思路来源：
        // 作者：zzh-28
        // 链接：https://leetcode-cn.com/problems/2vYnGI/solution/shi-jian-fu-za-du-omn-by-zzh-28/
        // 来源：力扣（LeetCode）

        // 方法二，复杂度 n * logn + n + m
        // 两个数组排好序，两个指针，一个从小，一个从大开始扫描
        // 核心思路，排好序的素组，其下标 + 1，就是该价格以下的数量
        // 这样可得到：在 某个主食 价格下，所有符合条件饮料的搭配数量
        // 之后再遍历各个主食价格，即可
        // 相比方法一，这个方法没有使用额外空间 x
        // 但是，对两个数组进行排序，则多花费了 2 个 n * logn 的时间
        public int breakfastNumber2(int[] staple, int[] drinks, int x) {
            Arrays.sort(staple);
            Arrays.sort(drinks);
            int count = 0;
            // 复杂度，n + m
            for (int i = 0, j = drinks.length - 1; i < staple.length && j >= 0;) {
                if (staple[i] + drinks[j] > x) {
                    j--;
                } else {
                    count += j + 1;
                    count = count % 1000000007;
                    i++;
                }
            }
            return count;
        }
        //作者：jaychang
        //链接：https://leetcode-cn.com/problems/2vYnGI/solution/pai-xu-shuang-zhi-zhen-by-jaychang/
        //来源：力扣（LeetCode）

        // 还有一种，对一个数组排序，再使用二分查找，
        // 也能做到一步获取：某个价格下，有多少种商品对效果
        // 具体做法：
        // 对主食排序
        // 对饮料遍历，每个饮料，都去二分查找允许对最高早餐价格下，有几中早餐；
        // 最后累加即可
    }

}

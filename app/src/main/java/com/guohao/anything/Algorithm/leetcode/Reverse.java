package com.guohao.anything.Algorithm.leetcode;

import java.util.Stack;

// 给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。
// https://leetcode-cn.com/problems/reverse-integer/
public class Reverse {

    public static void main(String[] args){
        System.out.println(Integer.reverse(123));
        System.out.println(new Solution().reverse(-123));
    }

    static class Solution {

        public int reverse(int x) {

            String num = String.valueOf(x);
            Stack<Character> stack = new Stack<>();

            int i = 0;
            boolean positave = true;
            if(x < 0) {
                positave = false;
                i = 1;
            }

            for(; i < num.length(); i++){
                stack.push(num.charAt(i));// 只有数字
            }

            int len = stack.capacity() + (positave?0:1);
            StringBuffer sb = new StringBuffer(len);
            if(!positave) sb.append("-");
            while (!stack.empty()){
                sb.append(stack.pop());// 先进后出，造成反转效果
            }

            try {
                return Integer.valueOf(sb.toString());
            }catch (NumberFormatException e){

            }
            return 0;

        }

        // 时间消耗最优秀的方案
        public int reverse2(int x) {
            int rev = 0;
            while (x != 0) {
                int pop = x % 10; // 这是能取到个位数的，也是我忽略的
                x = x / 10; // 各位数以取到，可以去掉当前的个位数了

                // 为何要 Integer.MAX_VALUE / 10 来判断？
                // 因为下面 rev 会有乘 10 的操作；
                // 在 rev 赋值前就判断，是为了防溢出。
                if (rev > Integer.MAX_VALUE / 10 || (rev == Integer.MAX_VALUE / 10 && pop > 7)) {
                    return 0;
                }
                if (rev < Integer.MIN_VALUE / 10 || (rev == Integer.MIN_VALUE / 10 && pop < -8)) {
                    return 0;
                }

                rev = rev * 10 + pop;
            }
            return rev;
        }

        // 空间消耗最优秀的方案
        public int reverse3(int x)
        {
            int ans = 0;
            while(x != 0)
            {
                int pop = x%10;
                // 这个判断的精髓在于：溢出了就不相等！
                if((ans * 10 + pop - pop)/ 10 != ans)
                {
                    return 0;
                }
                x /= 10;
                ans = ans * 10 + pop;
            }
            return ans;
        }
    }

}

package com.guohao.anything.ADateStructure;

import java.util.regex.Pattern;

/**
 * 正则表达式到样例
 */
public class RegexSample {

    public static void main(String[] args){
//        test2();
//        test3();
        test4();
    }

    static void test1(){
        String content = "I am noob " +
                "from runoob.com.";

        String pattern = ".*runoob.*";

        boolean isMatch = Pattern.matches(pattern, content);
        System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);
    }

    // 非贪婪模式
    // . 任意一个字符
    // * 前边的一个字符，出现0次到多次，而且会尽可能多的匹配，简称贪婪模式
    // ? 前边的一个字符，出现零次或一次
    // .* 任一字符，重复出现0次到多次
    // .*？(任一字符，重复出现0次到多次)这个模式，出现零次或一次，非贪婪模式
    static void test2(){
        String content = "caabataaat";

        String pattern = "c.*?t";

        // 匹配，但不完全匹配，只匹配到 caaat
        // 说明 .*? 中到 ？，使得第一次匹配到 "c.*?t" 这种模式，就停下来
        // .* 匹配任意多个字符串
        boolean isMatch = Pattern.matches(pattern, content);

        String result1 = content.replaceFirst(pattern,"0");// 从替换中可以看出

        System.out.println(" " + result1);
    }

    // 贪婪模式
    static void test3(){
        String content = "caaataaat";

        String pattern = "c.*t";// 尽可能得匹配最大到字符串

        boolean isMatch = Pattern.matches(pattern, content);// 匹配全部

        String result1 = content.replaceFirst(pattern,"0");// 从替换中可以看出

        System.out.println(" " + result1);
    }

    // ^ 以什么作开头；
    static void test4(){
        String content = "caaat";

        String pattern = "^a";

        boolean isMatch = Pattern.matches(pattern, content);// 匹配全部


        String result1 = content.replaceFirst(pattern,"0");// 从替换中可以看出

        System.out.println(" " + isMatch);
    }

}

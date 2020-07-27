package com.guohao.anything.Algorithm;

import java.util.Arrays;

public class QuickSort {

    public static void main(String[] args) {

        int[] arr = {6, 1, 2, 7, 9, 11, 4, 5, 10, 8};
        System.out.println("原始数据: " + Arrays.toString(arr));
        customQuickSort(arr, 0, arr.length - 1);
        System.out.println("快速排序: " + Arrays.toString(arr));

    }

    public static void customQuickSort(int[] arr, int low, int high) {
        int i, j, temp, t;

        if (low >= high) {
            return;
        }

        i = low;

        j = high;

        temp = arr[low];

        while (i < j) {

            // 先看右边，依次往左递减，直到找到小于 temp，停下来
            while (temp <= arr[j] && i < j) {
                j--;
            }
            // 再看左边，依次往右递增，直到找到大于 temp，停下来
            while (temp >= arr[i] && i < j) {
                i++;
            }

            // 两边交换
            t = arr[j];
            arr[j] = arr[i];
            arr[i] = t;
        }

        // 此时 i == j，low 位跟 i，j 位交换

        arr[low] = arr[i];
        arr[i] = temp;

        // 递归调用左半数组
        customQuickSort(arr, low, j - 1);
        // 递归调用右半数组
        customQuickSort(arr, j + 1, high);
    }
}
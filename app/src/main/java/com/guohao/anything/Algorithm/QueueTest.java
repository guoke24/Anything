package com.guohao.anything.Algorithm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;
import java.util.Stack;

public class QueueTest {


    Queue<Integer> queue = new LinkedList<>();
    ArrayList<Integer> arrayList;
    LinkedList<Integer> linkedList;
    Stack<Integer> stack;
    LinkedHashMap<Integer,Object> linkedHashMap;



    // ---

    int count = 0;

    // PriorityQueue类在Java1.5中引入。
    // PriorityQueue是基于优先堆的一个无界队列，
    // 这个优先队列中的元素可以默认自然排序或者通过提供的Comparator（比较器）在队列实例化的时排序。
    // PriorityQueue 的参考：https://baijiahao.baidu.com/s?id=1665383380422326763&wfr=spider&for=pc

    static PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    static PriorityQueue<Integer> maxHeap = new PriorityQueue<>(new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo(o1);
        }
    });
    public void Insert(Integer num) {
        if (count % 2 == 0) {
            minHeap.offer(num);
            maxHeap.offer(minHeap.poll());
        } else {
            maxHeap.offer(num);
            minHeap.offer(maxHeap.poll());
        }
        count++;
        System.out.println(QueueTest.GetMedian());
    }
    public static int GetMedian() {
        return maxHeap.peek();
    }
    public static void main(String[] args) {
        QueueTest t = new QueueTest();
        t.Insert(1);
        t.Insert(2);
        t.Insert(0);
        t.Insert(20);
        t.Insert(10);
        t.Insert(22);
    }


    PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();

    int k = 5;

    public void InsertTest(Integer num) {
        if(priorityQueue.size() < k){
            priorityQueue.offer(num);
        }else{
            Integer tmp = priorityQueue.peek();
            if( num < tmp ){
                priorityQueue.poll();
                priorityQueue.offer(num);
            }
        }
    }



}


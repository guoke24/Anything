package com.LagouAndroidShare.class2;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

// 软引用的一个隐藏问题，当有强可达指向时，软引用对象本身不会被回收
// 运行方式，在终端中来 cd 到 com.LagouAndroidShare.class2 中的 com 的 上一级路径：
// 编译：$ javac com/LagouAndroidShare/class2/SoftReferenceTest.java
// 运行：$ java -Xms4M -Xmx4M -Xmn2M com/LagouAndroidShare/class2/SoftReferenceTest
public class SoftReferenceTest {

    @SuppressWarnings("unchecked")
    public static class MyBigObject{
        byte[] data = new byte[1024];// 1KB
    }

    public static int removedSoftRef = 0;
    public static int capacity = 100 * 1024; // 100 * 1MB = 100MB

    public static Set<SoftReference<MyBigObject>> cache = new HashSet<>(capacity);
    public static Set<WeakReference<MyBigObject>> cache2 = new HashSet<>(capacity);
    public static ReferenceQueue<MyBigObject> referenceQueue = new ReferenceQueue<>();

    public static void main(String[] args){

        // 循环 100 * 1024 = 102400 次（约十万次）
        // 累计占内存 100 * 1024 * 1Kb = 100 * 1MB = 100MB
        // 设定虚拟机内存 4M
        // 每循环一千次得 1M，四千次占满内存
        for(int i = 0; i < capacity; i++){
            MyBigObject obj = new MyBigObject();
            //new SoftReference<>(obj);// 默认队列为空
            cache.add(new SoftReference<>(obj,referenceQueue));
            // WeakReference 是 unchecked 类型，这样会编译不过
            //cache2.add(new WeakReference(obj));
            // 每创建一个对象，就会占用 1KB 的内存
            // 10000次就会累计占用 10000KB = 10MB 的内存
            // 如果虚拟机的内存为 4MB，那么这期间肯定会触发多次 GC
            // 内存不足引起的 GC，会回收软引用所关联的对象，
            // 而软引用本身，如果没有强引用，也会变回收，证明，参考这个语句：new SoftReference<>(obj)
            // 上述新建软引用对象的时候，附带了一个引用队列，作用为：
            // 在软引用所关联的对象被 GC 回收的时候，将软引用本身加入该引用队列

            // 每次循环，都尝试清理无用的软引用，当软引用有强可达才需要这个步骤
            //cleanUseUselessSoftReferences();// 注释这行会报错

         //
            if(i % 1000 == 0){

                //当前分配的总内存
                //float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0/ (1024 * 1024));
                //剩余内存
                //float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0/ (1024 * 1024));
                //System.out.println("totalMemory: "+totalMemory);
                //System.out.println("freeMemory: "+freeMemory);

                System.out.println("size of cache = " +  cache.size());
            }

            if(i > 30000 && i % 100 == 0 ){

                // 通过这个log，可以查看一次 gc 能回收多少内存
                // 但注意这是在系统自动 gc 的前提下，我们自己触发 gc 看到的数据
                // 通过这个可以发现，当软引用被强引用而无法回收的时候，每次 gc 能回收的内存越来越少
                // 3.5 M * 2% = 0.07M
                // 如果每次回收的小于这个 0.07M，并且 JVM 花费 98% 以上的事件来进行垃圾回收，就会报出异常：
                // java.lang.OutOfMemoryError: GC overhead limit exceeded

                float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0/ (1024 * 1024));
                System.out.println("freeMemory: "+freeMemory);
                System.gc();
                float freeMemory2 = (float) (Runtime.getRuntime().freeMemory() * 1.0/ (1024 * 1024));
                System.out.println("freeMemory2: "+freeMemory2);

                System.out.println("size of cache = " +  cache.size());

            }

        }

        System.out.println("End，removedSoftRef = " + removedSoftRef );
        // 移除的软引用对象大约 102361
        // 循环 100 * 1024 = 102400 次（约十万次）

    }

    /**
     * 每次循环调用该函数，清理的是那些「所关联的对象已经被回收的软引用对象本身」；
     * 这样做能防止「虚拟机一直在不断回收软引用，回收进行的速度过快」这个问题吗？
     * 还是说这样能防止「无用的软引用累计占内存过多」这个问题呢？
     *
     *
     * 软引用所引用的对象大小是1KB，那么软引用本身的大小是多少呢？其实小的多，单个的话，可以忽略不计。
     */
    public static void cleanUseUselessSoftReferences(){
        Reference<? extends MyBigObject> ref = referenceQueue.poll();
        while(ref != null){
            if(cache.remove(ref)){ // 移除 set 对 软引用对象本身 的强引用
                removedSoftRef++;
            }
            ref = referenceQueue.poll();// 这样就没有任何引用指向 软引用对象本身了
        }
    }
}

package com.guohao.anything.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 测试可重入 读写锁
 * @author ConstXiong
 * @date 2019-06-10 11:19:42
 *
 * https://blog.csdn.net/meism5/article/details/91366723
 *
 */
public class TestReentrantReadWriteLock {

    private Map<String, Object> map = new HashMap<String, Object>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 根据 key 获取 value
     * @param key
     * @return
     */
    public Object get(String key) {
        Object value = null;
        lock.readLock().lock();
        try {
            Thread.sleep(50L);
            value = map.get(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        return value;
    }

    /**
     * 设置key-value
     * @param key
     * @return
     */
    public void set(String key, Object value) {
        lock.writeLock().lock();
        try {
            Thread.sleep(50L);
            map.put(key, value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    //测试5个线程读数据，5个线程写数据
    public static void main(String[] args) {
        final TestReentrantReadWriteLock test = new TestReentrantReadWriteLock();
        final String key = "lock";
        final Random r = new Random();
        for (int i = 0; i < 5; i++) {
            new Thread(){
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        System.out.println(Thread.currentThread().getName() + " read value=" + test.get(key));
                    }
                }
            }.start();

            new Thread(){
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        int value = r.nextInt(1000);
                        test.set(key, value);
                        System.out.println(Thread.currentThread().getName() + " write value=" + value);
                    }
                }
            }.start();
        }
    }

}


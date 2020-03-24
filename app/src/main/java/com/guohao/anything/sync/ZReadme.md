
[TOC]

### 下面介绍每个类测试的内容


- class ObjectForLock 
  - 测试了对象锁的获取的使用，以及线程从 NEW 到 TERMINATED 的所有状态切换。
    - wait、notify、join、sleep等函数 
  - 测试了线程的中断
    - isInterrupted、interrupted、interrupt等函数
    
- class AtomicityTestDemo
  - 本demo内容概括：主要做：原子性测试，顺便做一个可见性测试
  - volatile，synchronized，Lock，AtomicInteger 的原子性测试
  - volatile 可见性测试
  
- class Alipay
  - 本 Demo 的内容概括
  - 采用了三种方法，实现同步操作：
  - 1，ReentrantLock
  - 2，同步函数
  - 3，同步代码块的方式
  
- class LockDemo
  - 简单的上锁和解锁的例子，确保只有一个人上班
  - 关于 Lock 和 Condition 的使用
  
- class LockCountDemo
  - 本demo内容概括：
  - 综合使用 ReentrantLock、Condition 和 AtomicInteger，实现累加2次后再唤醒输出结果
  
- 其他一些
  - 列出：所有活跃的线程，和当前线程组的线程
    - RunWithoutUI.testListCurThreadName()
  - SyncTestActivity 还有一些测试
    - AsyncTask 用法的简单示例
    - 线程池中的线程的复用
    
- 备注
  - 调用测试函数的地方有两个：
    - class RunWithoutUI
    - class SyncTestActivity
  - 其中 class RunWithoutUI 可以不需要模拟器运行，方便临时测试
# 说明

- MemoryLeakActivity
    - 内存泄漏测试
      - MemoryLeakActivity 销毁后，然后 GC Root 可达该 MemoryLeakActivity
      - 静态接口引用：CallBackManager 的 sCallBacks
      - 非静态内部类默认持有外部类引用：MemoryLeakActivity 中的 LeakThread
      - 静态单例引用：SingleInstance.Holder.newInstance(this)
            
    - 检测内存泄露
      - 通过 memory profiler 的方式，
        - dump 出 hprof 文件，转换格式在用 mat 文件分析
      - LeakCanary 的方式
        - 2.0 以后，直接引入就可以生效了
        - debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.2'
        - 不过注意自己 hook 的 ams 的代码要先注释，否则会起冲突
        
- MemoryUtils
    - 五种获取内存信息的方法：
    - ActivityManager 
    - Runtime.getRuntime()
    - Debug.MemoryInfo
    - 读取 /proc/meminfo 节点
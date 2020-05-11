
- ClassLoaderActivity
    - Android 中的类加载测试
    - 主要就是两个类加载其：
    - dalvik.system.PathClassLoader
    - java.lang.BootClassLoader
    - 为何没有 DexClassLoader，因为 xx..class.getClassLoader() 返回的是 class 的加载器
    - DexClassLoader 是加载 dex 的
    
- ClassLoaderTest
    - Java 中的类加载测试
    - AppClassLoader（应用程序类加载器，ClassLoaderTest 的类加载器）
    - ExtClassLoader（拓展类加载器）
    - 没打印出 BootstrapClassLoader(引导类加载器)，因为它是 C/C++ 编写的
    
- booterCLass、OutClass
    - 外部类、内部类的加载时机和加载顺序
    
- BaseCls、TestCls
    - 简单的基类和子类的泛型声明测试
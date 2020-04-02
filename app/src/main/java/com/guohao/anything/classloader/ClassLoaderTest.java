package com.guohao.anything.classloader;

/**
 * Java 中的类加载测试
 */
public class ClassLoaderTest {
    public static void main(String args[]){
        ClassLoader loader = ClassLoaderTest.class.getClassLoader();
        while( loader != null){
            System.out.println(loader);
            loader = loader.getParent();
        }
        // 输出：
        // sun.misc.Launcher$AppClassLoader@18b4aac2（ClassLoaderTest 的类加载器）
        // sun.misc.Launcher$ExtClassLoader@4dc63996
        // 没打印出 BootstrapClassLoader，因为它是 C/C++ 编写的
    }
}

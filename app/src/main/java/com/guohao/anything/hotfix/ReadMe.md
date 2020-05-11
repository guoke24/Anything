# 说明

- 调用测试的类为：
    - {根目录}/app/src/main/java/com/guohao/anything/MainActivity.java
    - test_1：调用 BugTest 的 getBug 函数，会报错
    - 接着手动修改 BugTest 类，再编译成 class 文件后，按照其内部说明打包成 dex 文件，放到 Assets 目录
    - test_3：调用 AssetsUtils.fileOpt("classes.dex", this); 把 Assets 中的 classes.dex 加载到程序私有目录
        - 即：/data/user/0/com.anything.guohao.anything/files
    - test_2：调用 FixDexUtil.isGoingToFix 判断该路径有没有补丁
        - 有补丁，就调用 FixDexUtil.loadFixedDex 去加载补丁并替换
        - FixDexUtil.loadFixedDex 内部实现就是本篇的重点

- BugTest
    - 内含一段有bug的代码
    - 修复后编译成 class 再替换原来的 class
    
- FixDexUtil
    - 替换 class 的工具类


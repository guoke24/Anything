# droidassist 动态替换说明

本来希望把 Handler 类替换为 SuperHandler，监控 msg 的执行耗时；  

但是没有实现，貌似 droidassist 不支持类的替换；

所以先简单的做一个替换测试，把

int android.util.Log.d(java.lang.String,java.lang.String)

替换为

com.guohao.anything.LogUtil.dxx($1,$2);

运行成功，这个替换是在 app/droidassist.xml 文件中配置的。

关于 droidassist 框架，可以去其 GitHub 上看看：https://github.com/didi/DroidAssist

监控 msg 的执行耗时另一种思路：

[Android打印主线程所有方法执行时间](https://blog.csdn.net/cpcpcp123/article/details/104447490)

备注：

记得搭配 app/build.gradle 中打开 droidassist：

```
droidAssistOptions {
    enable false // true
```
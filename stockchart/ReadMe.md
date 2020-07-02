# 说明

代码来自该项目 https://github.com/SlamDunk007/StockChart

并在此基础上做一些修改。

stockchart 这个 module 是一个可以独立运行的模块。

独立运行 module，参考：[Module作为App单独运行的步骤](https://www.jianshu.com/p/9565528b39eb)

除此之外，还得建一个 Application 在 app/src/main/AndroidManifest.xml 中引用。

由于 Android Studio 版本为 3.4，不支持 androidx，故而作出替换：

```

//testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    
//implementation 'androidx.appcompat:appcompat:1.0.2'
implementation 'com.android.support:appcompat-v7:28.0.0'
  
  
```

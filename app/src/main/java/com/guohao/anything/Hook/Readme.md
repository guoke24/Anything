## Hook 实现插件 Activity、Service 的 Demo


#### 参考

[Android插件化原理（一）Activity插件化](https://blog.csdn.net/itachi85/article/details/80574390)

[《Android进阶解密》第15章](https://github.com/henrymorgen/android-advanced-decode/tree/master/chapter_15)

[《Android进阶解密》全书源码](https://github.com/henrymorgen/android-advanced-decode)

[刘望舒的 Github ](https://github.com/henrymorgen)

#### 说明 

本 Demo 的目的就是通过 Hook 的方式，启动未经注册的插件 Activity、Service。
代码出自 [《Android进阶解密》第15章](https://github.com/henrymorgen/android-advanced-decode/tree/master/chapter_15)
 。

---

主要组件的逻辑：

- MyApplication
    - 自定义的 Application
    - 用于在所有组件的生命周期之前，替换一些组件，以 Hook 的方式
    - 本Demo中，要替换的组件为：IActivityManagerProxy、HCallbackProxy

- IActivityManagerProxy： 用于替换 IActivityManager
    - 拦截 startActivity 函数，启动占坑的 StubActivity
        - 使得调用到 AMS 时，认为启动的是 StubActivity
    - 拦截 startService 函数，启动代理的 ProxyService
        - 后续的插件 TargetService，通过 ProxyService 来启动
    - 上述两步的目的都在于：帮助未注册的组件通过 AndroidManifest.xml 的验证

- HCallbackProxy：用于替换 H 的 callback 字段
    - 使得从 AMS 调用回来后，启动的是 TargetActivity

- HookMain_Activity：调用测试的 Activity

---

组件类：

- HookActivity 包
    - StubActivity：占坑 Activity，注册了
    - TargetActivity：插件 Activity，没注册

- HookService 包：
    - ProxyService：代理 Service，注册了
    - TargetService：插件 Service，没注册

---

工具类：

- HookHelper：封装了发起 Hook 的静态方法，主要在 MyApplication 中调用
    - 目前有 hookAMS、hookHandler 两个函数

- FieldUtil：封装了反射得到字段的操作


---

- HookSimpleDemo 包
    - 该包独立，用于测试 Hook 的基本操作
    
- proxyDemo 包
    - 该包独立，测试了 静态代理 和 动态代理 的代码写法
    
---

Hook 操作，启动插件 Activity 的思路

APP -＞ AMS 的具体过程
Activity - Instrument - IActivityManager - Binder - AMS

IActivityManager 作为 Hook 点，替换为 IActivityManagerProxy ，拦截某些个函数，执行自己的逻辑，再继续 IActivityManager 原来的逻辑。

AMS -＞ APP 的具体过程
AMS - IApplicationThread - Binder - ActivityThread - H - ActivityThread

H 作为 Hook 点，拦截其 dispatchMessage 函数，

```
 public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }
```

在 dispatchMessage 到 handleMessage 之间，
还会调用 Handler 的 mCallback.handleMessage(..) 函数，
所以选择 H 的 mCallback 字段作为 Hook 点，
拦截其 handleMessage(..) 函数，加入自己的逻辑；
然后，手动调用 H 的 handleMessage 函数，再返回 true，相当于继续原来的逻辑。
代码：

```
        mHandler.handleMessage(msg);
        return true;
```



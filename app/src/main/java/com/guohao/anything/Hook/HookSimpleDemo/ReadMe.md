
## 这是一个 Hook 操作的基本写法

 - 注意：该包独立于包外的内容，即没有引用包外的东西
 
 class HookTestActivity
 * 测试了 Hook 的两种写法
    * Hook 了 Activity 的 Instrumentation，替换为 InstrumentationProxy
    * Hook 了 Context 的 Instrumentation，替换为 InstrumentationProxy
 
 class InstrumentationProxy
 * 作为 Instrumentation 的代理类，替换 Instrumentation
    * 重写了 execStartActivity 函数
        * 加入一行 Log 输出
        * 然后调用 Instrumentation 的 execStartActivity 函数 继续原来的逻辑
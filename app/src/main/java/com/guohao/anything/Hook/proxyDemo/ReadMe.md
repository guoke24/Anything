 - 注意：该包独立于包外的内容，即没有引用包外的东西
 
 class Client
 * 调用测试了 静态代理 和 动态代理 的代码写法；
 *
 * StaticProxyShop：
 * 静态代理demo 是为了做一个对比参考，内部直接是引用调用
 *
 * DynamicProxyShop：
 * 真正重点在于动态代理，通过 「InvocationHandler」 和 「反射」 实现；
 * 帮助了解动态代理的概念，未具有实际应用的意义
 * 
 * 注意，此处没有 Hook 操作
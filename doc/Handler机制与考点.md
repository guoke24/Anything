问题一，先看这段代码：
```
public final class Looper {
    ......
    private static final String TAG = "Looper";

    // sThreadLocal.get() will return null unless you've called prepare().
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
    ......
```
Looper类中的变量 sThreadLocal 是 static 的，那么我 Looper looper = new Looper();
对象 looper 持有的 sThreadLocal 跟 Looper 类中的 sThreadLocal 是同一个吗？
答：静态变量，只能通过类名去访问，而不能通过对象名去访问，所以 对象 looper 不持有 sThreadLocal 的引用。
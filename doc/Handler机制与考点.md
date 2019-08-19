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


问题二，handler 发消息，怎么发给 Looper的？
这可以从 handler 的构造函数看起：
代码段二：
```
    // 无参构造函数
    public Handler() {
        this(null, false);
    }

    ......

    // 双参构造函数
    public Handler(Callback callback, boolean async) {
        if (FIND_POTENTIAL_LEAKS) {
            final Class<? extends Handler> klass = getClass();
            if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
                    (klass.getModifiers() & Modifier.STATIC) == 0) {
                Log.w(TAG, "The following Handler class should be static or leaks might occur: " +
                    klass.getCanonicalName());
            }
        }

        mLooper = Looper.myLooper(); //2-1 获得 Looper 引用
        if (mLooper == null) {
            throw new RuntimeException(
                "Can't create handler inside thread " + Thread.currentThread()
                        + " that has not called Looper.prepare()");
        }
        mQueue = mLooper.mQueue; //2-2 获得 队列引用
        mCallback = callback;
        mAsynchronous = async;
    }
```
由注释2-1，2-2可以看出，在其双参的构造函数里，获取了 Looper 的引用 mLooper，再从 mLooper 获取队列的引用 mQueue。
由此可以引申一个结论，在构造 handler 的示例之前，需要构造好 Looper，也就是 Looper.prepare() 要在构造 handler 之前。


问题三，Looper.prepare() 干了些什么？
问题四，Looper.myLooper() 返回的是哪个 Looper 实例？


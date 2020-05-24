# 说明

课程：[第17讲：Android OkHttp 全面详解](https://kaiwu.lagou.com/course/courseInfo.htm?courseId=67#/detail/pc?id=1856)

源码：https://github.com/McoyJiang/LagouAndroidShare

OkHttp 使用扩展的实例之「网络请求的进度」：

在 SquareUtils.java 中自定义 ProgressResponseBody，加入自定义的进度监听器，通过它向上层汇报网络请求的进度，并根据进度绘制图片加载的进度 by PieImageView；

通过添加网络拦截器的方式，在 chain.proceed(chain.request()) 之后，拿到 response，在替换成 ProgressResponseBody；

最终返回给 RealCall 持有。

---

注意，此处添加的是「网络拦截器」而不是「普通拦截器」！

添加「网络拦截器」：addNetworkInterceptor

添加「普通拦截器」：addInterceptor

两者有何区别呢？

源码：

```
    public List<Interceptor> interceptors() {
      return interceptors;
    }

    public Builder addInterceptor(Interceptor interceptor) {
      if (interceptor == null) throw new IllegalArgumentException("interceptor == null");
      interceptors.add(interceptor);
      return this;
    }

    public List<Interceptor> networkInterceptors() {
      return networkInterceptors;
    }

    public Builder addNetworkInterceptor(Interceptor interceptor) {
      if (interceptor == null) throw new IllegalArgumentException("interceptor == null");
      networkInterceptors.add(interceptor);
      return this;
    }
```

可见添加到的位置不一样；

进而可知道：这两者在执行顺序上也有差别：

```
// RealCall.java

  Response getResponseWithInterceptorChain() throws IOException {
    // Build a full stack of interceptors.
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.addAll(client.interceptors());
    interceptors.add(retryAndFollowUpInterceptor);
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    interceptors.add(new CacheInterceptor(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());
    }
    interceptors.add(new CallServerInterceptor(forWebSocket));

    Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0,
        originalRequest, this, eventListener, client.connectTimeoutMillis(),
        client.readTimeoutMillis(), client.writeTimeoutMillis());

    return chain.proceed(originalRequest);
  }
```

可见，普通拦截器对应的 interceptors 会被先执行，然后等其他拦截器都执行完返回，自己才能返回；

网络拦截器对应的 networkInterceptors 会被后执行，先返回；

---

从这个例子可推出一个结论：获取 response 时，不是一次性获取完毕的，而是一点一点获取的，也就是多次调用 read 函数；


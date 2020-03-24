package com.guohao.anything.net.xtalker;


import com.guohao.anything.BuildConfig;
import com.google.gson.GsonBuilder;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 网络请求的封装
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class NetworkUtil {
    private static NetworkUtil instance;
    private Retrofit retrofit;
    private OkHttpClient client;

    static {
        instance = new NetworkUtil();
    }

    private NetworkUtil() {
    }

    public static OkHttpClient getClient() {
        if (instance.client != null)
            return instance.client;

        // 存储起来
        instance.client = new OkHttpClient.Builder()
                // 超时
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                // 给所有的请求添加一个拦截器
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        // 拿到我们的请求
                        Request original = chain.request();
                        // 重新进行build
                        Request.Builder builder = original.newBuilder();
//                        if (!TextUtils.isEmpty(Account.getToken())) {
//                            // 注入一个token
//                            builder.addHeader("token", Account.getToken());
//                        }
                        builder.addHeader("Content-Type", "application/json");
                        Request newRequest = builder.build();
                        // 返回
                        return chain.proceed(newRequest);
                    }
                })
                // 打印 log
                .addInterceptor(new LoggingInterceptor.Builder()
                        .loggable(BuildConfig.DEBUG)
                        .log(Platform.INFO)
                        .setLevel(Level.BASIC)
                        .request("请求")
                        .response("响应")
                        .build())
                .build();
        return instance.client;
    }

    // 构建一个Retrofit
    public static Retrofit getRetrofit() {
        if (instance.retrofit != null)
            return instance.retrofit;

        // 得到一个OK Client
        OkHttpClient client = getClient();

        // Retrofit
        Retrofit.Builder builder = new Retrofit.Builder();

        // 设置电脑链接
        instance.retrofit = builder.baseUrl("http://192.168.0.107:8080/")
                // 设置client
                .client(client)
                // 配置转换器，可直接接收String类型的结果
                .addConverterFactory(ScalarsConverterFactory.create())
                // 设置Json解析器
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        // 设置时间格式
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS") //作用于Date类型，User类的modifyat属性
                        // 设置一个过滤器，数据库级别的Model不进行Json转换
                        //.setExclusionStrategies(new DBFlowExclusionStrategy())
                        .create()))
                .build();

        return instance.retrofit;

    }

    /**
     * 静态内部类
     * 用于返回一个请求代理
     *
     * @return RemoteService
     */
    public static XTalkerService remote() {
        return NetworkUtil.getRetrofit().create(XTalkerService.class);
    }

}

package com.guohao.anything.net;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.anything.guohao.anything.BuildConfig;
import com.anything.guohao.anything.R;
import com.google.gson.Gson;
import com.guohao.anything.BaseTestActivity;
import com.guohao.anything.net.gsonDemo.Cart;
import com.guohao.anything.net.gsonDemo.LineItem;
import com.guohao.anything.net.xtalker.AccountRspModel;
import com.guohao.anything.net.xtalker.NetworkUtil;
import com.guohao.anything.net.xtalker.RegisterModel;
import com.guohao.anything.net.xtalker.RspModel;
import com.guohao.anything.net.xtalker.XTalkerService;
import com.guohao.anything.net.xtalker_self.JsonsRootBean;
import com.guohao.anything.net.xtalker_self.PostBean;
import com.guohao.anything.net.xtalker_self.ResultBean;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RetrofitActivity extends BaseTestActivity {

    static String TAG = "guohao-retrofit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_retrofit);
        super.onCreate(savedInstanceState);
    }

    /**
     * 最简单的 retroofit 对 github 请求，
     * 不使用转换器，直接用 okhttp 的 ResponseBody 作为接收结果
     */
    public void test_1(View view) {
        showMessage("initActivity test_1:");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build();

        GithubService service = retrofit.create(GithubService.class);

        Call<ResponseBody> call = service.listRepos("moyilan13");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.i("guohao-retrofit", "onResponse:" + response.body().string() + "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("guohao-retrofit", "onFailure");

            }
        });
    }

    // test gson 的使用
    public void test_2(View view) {
        Gson gson = new Gson();
        Cart cart = buildCart();
        StringBuilder sb = new StringBuilder();
        sb.append("Gson.toJson() example: \n");
        sb.append("  Cart Object: ").append(cart).append("\n");
        // 把对象转换为json：gson.toJson
        sb.append("  Cart JSON: ").append(gson.toJson(cart)).append("\n");

        sb.append("\n\nGson.fromJson() example: \n");
        String json = "{buyer:'Happy Camper',creditCard:'4111-1111-1111-1111',"
                + "lineItems:[{name:'nails',priceInMicros:100000,quantity:100,currencyCode:'USD'}]}";
        sb.append("Cart JSON: ").append(json).append("\n");
        // 把json转换为对象：gson.fromJson
        sb.append("Cart Object: ").append(gson.fromJson(json, Cart.class)).append("\n");

        //Log.d("guohao-gson",sb.toString());
        System.out.println(sb.toString());
    }

    private Cart buildCart() {
        List<LineItem> lineItems = new ArrayList<LineItem>();
        lineItems.add(new LineItem("hammer", 1, 12000000, "USD"));
        return new Cart(lineItems, "Happy Buyer", "4111-1111-1111-1111");
    }

    /**
     * 对 xtalker 的首页发起GET请求，
     * 配合 ScalarsConverterFactory 接收String结果
     */
    public void test_3(View v) {
        showMessage("initActivity test_3:");

        // 得到retrofit实例
        Retrofit retrofit = new Retrofit.Builder()
                .client(getClient())// 设置client
                .baseUrl("http://192.168.0.107:8080/")// 虚拟机上不能使用本机地址
                .addConverterFactory(ScalarsConverterFactory.create())// 配置转换器
                .build();

        // 得到接口实例，准备创建不同url的call
        XTalkerService xTalkerService = retrofit.create(XTalkerService.class);

        // 得到call实例，url拼接完整
        Call<String> call = xTalkerService.indnex();

        // 发起异步调用
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.i("guohao-retrofit", "onResponse:" + response.body().toString() + "");

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("guohao-retrofit", "onFailure");

            }
        });
    }

    /**
     * 对 xtalker 的注册接口发起POST请求，
     * 配合 ScalarsConverterFactory 接收String结果
     */
    public void test_4(View v) {
        showMessage("initActivity test_3:");


//        OkHttpClient client = getClient();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://192.168.0.107:8080/")// 虚拟机上不能使用本机地址
//                .client(client)// 设置client
//                .addConverterFactory(ScalarsConverterFactory.create())// 配置转换器
//                .build();
        // OkHttpClient 和 Retrofit 的构建，已经封装到 NetworkUtil 模块中

        // 得到接口代理
        XTalkerService xTalkerService = NetworkUtil.remote();

        // Json格式字串，不换行也可以
        String post_body = "{\n" +
                "  \"account\":\"802802\",\n" +
                "  \"password\":\"111111\",\n" +
                "  \"name\":\"modasa\"\n" +
                "}";

        // 得到call实例，得到完整的 url 和 完整的 post 参数
        Call<String> call = xTalkerService.accountRegister(post_body);


        // 发起异步调用
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response != null){
                    Log.i("guohao-retrofit", "headers = " + response.headers() + "");
                    Log.i("guohao-retrofit", "response.raw() = " + response.raw().toString() + "");
                }

                if (response.body() != null){
                    // toString().toString() 才可以输出body的文本
                    Log.i("guohao-retrofit", "onResponse:" + response.body().toString().toString() + "");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("guohao-retrofit", "onFailure");

            }
        });


    }

    /**
     * 构建一个 OkHttpClient ，设置一些超时信息和拦截器等
     */
    private OkHttpClient getClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                // 链式调用
                .connectTimeout(15, TimeUnit.SECONDS)
                // 设置请求头，放在log拦截器的前面就行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public okhttp3.Response intercept(Chain chain) throws IOException {
//                        // 拿到我们的请求
//                        Request original = chain.request();
//                        // 重新进行build
//                        Request.Builder builder = original.newBuilder()
//                                // 配置请求头
//                                .header("Content-Type", "application/json");
//                        // build一个新的请求
//                        Request request = builder.build();
//                        return chain.proceed(request);
//                    }
//                })
                // 打印log的拦截器
                .addInterceptor(new LoggingInterceptor.Builder()
                        .loggable(BuildConfig.DEBUG)
                        .log(Platform.INFO)
                        .setLevel(Level.BASIC)
                        .request("请求")
                        .response("响应")
                        .build())
        ;


        return httpClientBuilder.build();
    }

    /**
     * 移植 italker 项目的 注册接口请求
     */
    public void test_5(View view){
        // 构造Model，进行请求调用
        RegisterModel model = new RegisterModel("612612", "111111", "sheshe", null);

        // 进行网络请求，并设置回送接口为自己，这一步相当于下面三步
        //AccountHelper.register(model, this);

        // 得到一个请求代理
        XTalkerService xTalkerService = NetworkUtil.remote();

        // 得到一个具体的call
        Call<RspModel<AccountRspModel>> call = xTalkerService.accountRegister(model);

        //
        call.enqueue(new Callback<RspModel<AccountRspModel>>() {
            @Override
            public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {

                if (response != null){
                    Log.i("guohao-retrofit", "headers = " + response.headers() + "");
                    Log.i("guohao-retrofit", "response.raw() = " + response.raw().toString() + "");
                }


                if (response.body() != null){
                    Log.i("guohao-retrofit", "onResponse:" + response.body().toString() + "");
                }

            }

            @Override
            public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
                Log.i("guohao-retrofit", "onFailure");

            }
        });

    }

    /**
     * 自己在写一个简化版的 xtalker 的 注册接口请求
     * bean类只需要 getter 和 setter 就可以了
     */
    public void test_6(View c){
        // json 转换成 实体类

        // 配置一个代理请求
        PostBean model = new PostBean("909909", "111111", "guogou");

        // 得到一个请求代理
        XTalkerService xTalkerService = NetworkUtil.remote();

        Call<JsonsRootBean<ResultBean>> call = xTalkerService.accountRegisterSelf(model);

        call.enqueue(new Callback<JsonsRootBean<ResultBean>>() {
            @Override
            public void onResponse(Call<JsonsRootBean<ResultBean>> call, Response<JsonsRootBean<ResultBean>> response) {

                if (response != null){
                    Log.i("guohao-retrofit", "headers = " + response.headers() + "");
                    Log.i("guohao-retrofit", "response.raw() = " + response.raw().toString() + "");
                }


                if (response.body() != null){
                    Log.i("guohao-retrofit", "onResponse:" + response.body().toString() + "");
                }

            }

            @Override
            public void onFailure(Call<JsonsRootBean<ResultBean>> call, Throwable t) {
                Log.i("guohao-retrofit", "onFailure");

            }
        });


    }

}

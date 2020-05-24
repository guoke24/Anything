package com.guohao.anything.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.guohao.anything.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * 发起okhttp请求需要四步：
 * 1. 拿到okhtttpclient对象，设置超时等信息 
 * 2. 构造Request，传入请求的url和提交的参数 
 * 3. 将Request封装为call，用于发起请求 
 * 4. call.execute() 或 call.enqueue(new callBback(){})，定义好回调逻辑 
 * 备注：其中3和4可以在代码中写成一步。
 */
public class OkHttpActivity extends AppCompatActivity {

    static String TAG = "guohao-okhttp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);

        // 测试，跳转源码
        Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        MessageQueue messageQueue = h.getLooper().getQueue();
        //messageQueue.nex
    }

    // GET请求
    public void test1(View view) {

        String url = "http://wwww.baidu.com";

        // 第一，新建客户端
        OkHttpClient okHttpClient = new OkHttpClient();

        /// 第二，构建请求
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();

        // 第三，构建call
        // 新建Call，实现响应成功的逻辑
        Call call = okHttpClient.newCall(request);

        // 第四，发起调用
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });

        // 上述的 Call 声明可以省略简写成：
        // okHttpClient.newCall(request).enqueue(new Callback(){ ... })
    }

    // POST方式提交String
    public void test2(View view) {
        // 第一，新建客户端
        OkHttpClient okHttpClient = new OkHttpClient();

        // 构建提交的参数内容
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        String requestBody = "I am Jdqm.";// 要提交的string

        // 第二，构建请求
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(mediaType, requestBody)) // 提交的参数都是在post函数
                .build();

        // 第三，构建call
        // 第四，发起调用
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    // POST方式提交 流
    public void test3(View view) {
        // 第一，新建客户端
        OkHttpClient okHttpClient = new OkHttpClient();

        // 构建提交的参数内容
        RequestBody requestBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MediaType.parse("text/x-markdown; charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("I am Jdqm."); // string 写入 流
            }
        };

        // 第二，构建请求
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)// 提交的参数都是在post函数
                .build();

        // 第三，构建call
        // 第四，发起调用
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    // POST提交 文件
    public void test4(View view) throws IOException {
        // 测试路径
        String filePath =  getBaseContext().getFilesDir().getPath();
        Log.d(TAG,"filePath = " + filePath );
        // /data/user/0/com.example.guohao.frameworklearn/files

        // 先生成一个文件
        fileOpt();

        // 获得文件实例
        File file = new File("/data/data/com.example.guohao.frameworklearn/files/test.txt");

        // 第一，新建客户端
        OkHttpClient okHttpClient = new OkHttpClient();

        // 构建提交的参数内容
        // 新建媒体类型，传文件需要
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");

        // 第二，构建请求
        // 新建请求，确定url 和 所提交的参数
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(mediaType, file))// 提交的参数都是在post函数
                .build();

        // 第三，构建call
        // 第四，发起调用
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    // POST方式提交 表单
    public void test5(View view) {
        // 第一，新建客户端
        OkHttpClient okHttpClient = new OkHttpClient();

        // 构建提交的参数内容
        RequestBody requestBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();

        // 第二，构建请求
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(requestBody) // 要提交的参数
                .build();

        // 第三，构建call
        // 第四，发起调用
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    // POST方式提交 分块请求
    public void test6(View view) {

        // 将 Assets 路径中的文件写到本应用的路径下：
        // /data/data/com.example.guohao.frameworklearn/files
        try {
            InputStream in = getAssets().open("ic_launcher.png");

            FileOutputStream fos = this.openFileOutput("ic_launcher.png", MODE_PRIVATE);//获得FileOutputStream

            byte[]  bytes = new byte[in.available()];
            in.read(bytes);

            fos.write(bytes);//将byte数组写入文件

            fos.close();//关闭文件输出流

        } catch (IOException e) {
            e.printStackTrace();
        }// end 将 Assets 路径中的文件写到本应用的路径下

        // 第一，新建客户端
        OkHttpClient client = new OkHttpClient();

        // 构建提交的参数内容
        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
        MultipartBody body = new MultipartBody.Builder("AaB03x")
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"title\""),
                        RequestBody.create(null, "Square Logo"))
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"image\""),
                        RequestBody.create(MEDIA_TYPE_PNG, new File("/data/data/com.example.guohao.frameworklearn/files/ic_launcher.png")))
                .build();

        // 第二，构建请求
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url("https://api.imgur.com/3/image")
                .post(body)
                .build();

        // 第三，构建call
        Call call = client.newCall(request);
        // 第四，发起调用
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //System.out.println(response.body().string());
                Log.d(TAG, "onResponse: " + response.body().string());

            }

        });

    }

    // 读取 assets 目录的文件，保存到file
    // 调用方式： writeBytesToFile(getAssets().open("time.jpg"),file);
    public static void writeBytesToFile(InputStream is, File file) throws IOException{
        FileOutputStream fos = null;
        try {
            byte[] data = new byte[2048];
            int nbread = 0;
            fos = new FileOutputStream(file);
            while((nbread=is.read(data))>-1){
                fos.write(data,0,nbread);
            }
        }
        catch (Exception ex) {
            Log.e(TAG,ex.toString());
        }
        finally{
            if (fos!=null){
                fos.close();
            }
        }
    }

    //文件名称
    String fileName = "test.txt";

    //写入和读出的数据信息
    String content = "demo";


    /**
     * 生成并读取一个图片
     */
    public void fileOpt(){
        writeFileData(fileName,  content); // 写入文件

        String result = readFileData(fileName); // 读取文件

        Log.d(TAG,"result = " + result);

    }

    // 向指定的文件中写入指定的数据
    // 创建的文件保存在/data/data/<package name>/files目录
    public void writeFileData(String filename, String content){

        try {

            FileOutputStream fos = this.openFileOutput(filename, MODE_PRIVATE);//获得FileOutputStream

            //将要写入的字符串转换为byte数组

            //byte[]  bytes = content.getBytes();

            //String name = readFileData();
            byte[]  bytes = content.getBytes();

            fos.write(bytes);//将byte数组写入文件

            fos.close();//关闭文件输出流

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打开指定文件，读取其数据，返回字符串对象
    public String readFileData(String fileName){

        String result="";

        try{

            FileInputStream fis = this.openFileInput(fileName);

            //获取文件长度
            int lenght = fis.available();

            byte[] buffer = new byte[lenght];

            fis.read(buffer);

            //将byte数组转换成指定格式的字符串
            result = new String(buffer, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  result;
    }

    // 应用拦截器 addInterceptor
    public void test7(View v){
        String url = "http://wwww.baidu.com";

        // 第一，新建客户端
//        OkHttpClient okHttpClient = new OkHttpClient();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();

        /// 第二，构建请求
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();

        // 第三，构建call
        // 新建Call，实现响应成功的逻辑
        Call call = okHttpClient.newCall(request);

        // 第四，发起调用
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.d(TAG, "onResponse: " + response.body().string());
                Log.d(TAG, "onResponse: " );
            }
        });
    }

    // 网络拦截器 addNetworkInterceptor
    // 参考链接：https://www.jianshu.com/p/ba6e219a0af6
    public void test8(View view){
        String url = "http://www.publicobject.com/helloworld.txt";

        // 第一，新建客户端
//        OkHttpClient okHttpClient = new OkHttpClient();

        Log.i(TAG,"111\n");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new LoggingInterceptor())
                .build();

        Log.i(TAG,"222\n");
        /// 第二，构建请求
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();

        Log.i(TAG,"333\n");
        // 第三，构建call
        // 新建Call，实现响应成功的逻辑
        Call call = okHttpClient.newCall(request);

        Log.i(TAG,"444\n");
        // 第四，发起调用
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.d(TAG, "onResponse: " + response.body().string());
                Log.d(TAG, "onResponse: " );
            }
        });
    }

    /**
     * 拦截器，触发拦截的时机是在 发起调用之后（即 call.enqueue 之后），
     * 回调 Callback 之前。
     *
     */
    class LoggingInterceptor implements Interceptor {
        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
//            logger.info(String.format("Sending request %s on %s%n%s",
//                    request.url(), chain.connection(), request.headers()));

            Log.i(TAG,"=========\n");
            Log.i(TAG,String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
//            logger.info(String.format("Received response for %s in %.1fms%n%s",
//                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            Log.i(TAG,"=========\n");
            Log.i(TAG,String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }

}

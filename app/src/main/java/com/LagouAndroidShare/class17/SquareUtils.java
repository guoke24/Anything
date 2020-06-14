package com.LagouAndroidShare.class17;

import android.app.Application;
import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.WorkerThread;


import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;


import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;



/**
 * getPicasso -> getProgressBarClient -> getClient
 */
public class SquareUtils {

    static private OkHttpClient client;

    /**
     * 返回普通客户端
     * @param application
     * @return
     */
    static public synchronized OkHttpClient getClient(Application application) {
        if (client == null) {
            final File cacheDir = application.getExternalCacheDir();
            client = new OkHttpClient.Builder()
                    //Interceptor -> cache -> NetworkInterceptor
                    //.addNetworkInterceptor(getLogger())
                    .cache(new Cache(new File(cacheDir, "okhttp"), 60 * 1024 * 1024))
                    //.dispatcher(getDispatcher())
                    //.dns(HTTP_DNS)
                    .build();
        }
        return client;
    }

    /**
     * Not singleton
     * 返回代进度bar的客户端
     */
    private static OkHttpClient getProgressBarClient(final ProgressListener listener,Application application) {
        return getClient(application).newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), listener))
                        .build();
            }
        }).build();
    }

    /**
     * Download Big Image only, Not singleton but shared cache
     * 返回 Picasso 实例
     */
    static public Picasso getPicasso(Context context, ProgressListener listener,Application application) {
        OkHttpClient client = getProgressBarClient(listener,application);
        OkHttp3Downloader downloader = new OkHttp3Downloader(client);
        return new Picasso.Builder(context).downloader(downloader)
                .memoryCache(com.squareup.picasso.Cache.NONE)
                .build();
    }

    public interface ProgressListener {
        @WorkerThread
        void update(@IntRange(from = 0, to = 100) int percent);
    }

    /**
     * 响应体的拓展类
     * 关键在重写的 source 函数里增加回传进度的逻辑
     */
    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {

            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    if (progressListener != null) {
                        progressListener.update(
                                ((int) ((100 * totalBytesRead) / responseBody.contentLength())));
                    }
                    return bytesRead;
                }
            };
        }
    }
}
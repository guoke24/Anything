package com.guohao.anything.jetpackTest;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.guohao.anything.R;
import android.arch.core.util.Function;

// Android Jetpack架构组件（四）带你了解LiveData(使用篇）
// http://liuwangshu.cn/application/jetpack/4-livedata-use.html
public class Main5Activity extends AppCompatActivity {

    private static final String TAG = "guohao-jetpack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLifecycle().addObserver(new MyObserver());//1

        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);


    }

    public class MyObserver implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        void onResume(){
            Log.d(TAG, "Lifecycle call onResume");
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        void onPause(){
            Log.d(TAG, "Lifecycle call onPause");
        }
    }

    public void test_1(View view){

        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

        // 倒置观察
        mutableLiveData.observe(this, new Observer<String>() {//1
            @Override
            public void onChanged(@Nullable final String s) {
                Log.d(TAG, "onChanged:"+s);
            }
        });

        // 更新信息
        mutableLiveData.postValue("Android进阶三部曲");//2
    }

    public void test_2(View c){

        MutableLiveData<String> mutableLiveData  = new MutableLiveData<>();

        mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {
                Log.d(TAG, "onChanged1:"+s);
            }
        });
        // new Observer<String> 添加到实例字段 mObserver 中


        // mutableLiveData source
        // transformedLiveData result
        // source -> Function -> result
        // 一旦 source 有变，经过 Function 转变，
        // 就会执行到 result 的 setValue 函数，
        // 最终倒置 result 执行 mObserver.onChanged((T) mData);
        LiveData transformedLiveData = Transformations.map(mutableLiveData, new Function<String, Object>() {
            @Override
            public Object apply(String name) {
                return name + "+Android进阶解密";
            }
        });
        // 在 MediatorLiveData 的 addSource 函数中的 e.plug() 语句中，
        // 把自己添加到 mutableLiveData 实例字段 mObserver 中。

        transformedLiveData.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                Log.d(TAG, "onChanged2:"+o.toString());
            }
        });// new Observer<String> 添加到实例字段 mObserver 中

        mutableLiveData.postValue("Android进阶之光");
        // 主线程执行
        // setValue - dispatchingValue - considerNotify -
        // observer.mObserver.onChanged((T) mData);
    }

    public void test_3(View v){

        MutableLiveData<String> mutableLiveData1 = new MutableLiveData<>();

        MutableLiveData<String> mutableLiveData2 = new MutableLiveData<>();

        MutableLiveData<Boolean> liveDataSwitch = new MutableLiveData<Boolean>();//1

        LiveData transformedLiveData= Transformations.switchMap(liveDataSwitch, new Function<Boolean, LiveData<String>>() {
            @Override
            public LiveData<String> apply(Boolean input) {
                if (input) {
                    return mutableLiveData1;
                } else {
                    return mutableLiveData2;
                }
            }
            // 内部调用了 transformedLiveData(MediatorLiveData)实例 的 addSource 函数；
            // 把自己添加到 liveDataSwitch 的字段 mObserver 中；
            // 每当 liveDataSwitch 发数据 ，经过 apply 函数处理，
            // 结果赋值给 transformedLiveData(MediatorLiveData)实例 中的 mSource 字段；
            // 接着再次调用了 transformedLiveData(MediatorLiveData)实例 的 addSource 函数；
            // 把自己添加到 mSource 的字段 mObserver 中；
            // 每当 mSource 发数据 ，触发自身的 onChanged 函数，在触发自身的 setValue 函数；
            // 接着就触发自身的字段 mObserver 的 onChanged 函数，
            // 也就是下面定义的 new Observer<String> 中的 onChanged 函数。

            // 经过复杂封装，实现简单使用！
            // 偷懒思想！
        });


        transformedLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {
                Log.d(TAG, "onChanged:" + s);
            }
        });


        liveDataSwitch.postValue(false);//2
        mutableLiveData1.postValue("Android进阶之光");
        mutableLiveData2.postValue("Android进阶解密");
    }
}

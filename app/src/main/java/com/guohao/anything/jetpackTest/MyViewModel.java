package com.guohao.anything.jetpackTest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private MutableLiveData<String> name;
    public LiveData<String> getName() {
        if (name == null) {
            name = new MutableLiveData<String>();
            addName();
        }
        return name;
    }
    private void addName() {
        name.setValue("Android进阶解密");
    }
}

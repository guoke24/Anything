package com.guohao.anything.MvvmTest;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

//import com.guohao.anything.BR;

public class User extends BaseObservable {

    public User(String name) {
        this.name = name;
    }

    private String name;

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        //notifyPropertyChanged(BR.user);
    }
}

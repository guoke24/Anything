package com.guohao.anything.FragmentDemo;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.guohao.anything.R;

public class MyFragment extends Fragment {

    int flag = -1;

    boolean DEBUG = false;

    public MyFragment() {
    }

    public Fragment setFlags(int i ){

        flag = i;

        return this;
    }

    // Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onPause() {
        super.onPause();
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onStop() {
        super.onStop();
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(DEBUG) Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));

        View root = inflater.inflate(R.layout.activity_main3, container, false);

        Button button = root.findViewById(R.id.bt1);

        button.setText("" + flag);

        return root;
    }
}

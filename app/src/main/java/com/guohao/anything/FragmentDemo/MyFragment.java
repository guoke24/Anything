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
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.i("guohao-fg",Log.getStackTraceString(new Throwable()));

        View root = inflater.inflate(R.layout.activity_main3, container, false);

        Button button = root.findViewById(R.id.bt1);

        button.setText("" + flag);

        return root;
    }
}

package com.guohao.anything;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.guohao.anything.R;

import java.util.List;

public class PackagesListActivity extends AppCompatActivity {

    RecyclerView mRecycerView;
    PackageManager mPm;
    List<PackageInfo> packageInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages_list);
        initVIew();
    }

    public void initVIew(){
        mPm = getPackageManager();
        packageInfoList = mPm.getInstalledPackages(0);

        mRecycerView = findViewById(R.id.recycler_view);
        PackagesListAdapter adapter = new PackagesListAdapter(getPackageManager(),getApplicationContext());//list数据源在此传进去
        //mRecycerView.click
        mRecycerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycerView.setLayoutManager(manager);



    }
}

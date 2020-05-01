package com.guohao.anything.CollapsingToolbar;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;

import com.guohao.anything.viewtest.scrollConflict.PackagesListAdapter;
import com.guohao.anything.R;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

/**
 * 一个简单的移植，具体参考 DemoActivity
 *
 * RecycerView 加载了已安装的 app 的列表
 *
 * 布局的占位用来加载 CollapsingToolbarLayout
 *
 */
public class CollapsingActivity extends AppCompatActivity {

    RecyclerView mRecycerView;
    PackageManager mPm;
    List<PackageInfo> packageInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.collapsing_activity);
        setContentView(R.layout.collapsing_activity);

        initVIew();

        // 设置状态栏透明
        StatusBarUtil.setTransparent(CollapsingActivity.this);
    }

    public void initVIew(){
        mPm = getPackageManager();
        packageInfoList = mPm.getInstalledPackages(0);

        // 初始化 RecycerView，加载已安装 app
        mRecycerView = findViewById(R.id.recycler_view);
        PackagesListAdapter adapter = new PackagesListAdapter(getPackageManager(),getApplicationContext());//list数据源在此传进去
        //mRecycerView.click
        mRecycerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycerView.setLayoutManager(manager);

        // 加载占位布局
        ViewStub stub = (ViewStub) findViewById(R.id.view_stub_header);
        stub.setLayoutResource(R.layout.lay_chat_header_user);
        stub.inflate();

    }

}

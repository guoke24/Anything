package com.guohao.anything.jvm;

import android.os.Bundle;
import android.view.View;

import com.guohao.anything.BaseTestActivity;
import com.guohao.anything.R;

public class Main2Activity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main2);
        super.onCreate(savedInstanceState);

        // 立马实例化 StaticTest 类，观察 static 修饰的变量、构造快、构造函数的加载情况
        System.out.println("创建 out_t1");
        StaticTest out_t1 = new StaticTest("new out_t1");

    }

    // 测试 静态的类变量 的访问
    public void test_1(View view){
        showMessage("Main2Activity test_1:");

        People p1 = new People("小明",20);
        People p2 = new People("小红",18,"中国");

        p1.country = "非洲";// 可以访问到静态的类变量！！！

        p1.show();
        p2.show();


    }
}

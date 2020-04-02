package com.guohao.anything.classloader;


import android.os.Bundle;
import android.view.View;

import com.guohao.anything.BaseTestActivity;
import com.guohao.anything.LogUtil;
import com.guohao.anything.R;

/**
 * Android 中的类加载测试
 */
public class ClassLoaderActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_class_loader);
        super.onCreate(savedInstanceState);
    }

    public void test_1(View v){
        showMessage("test1");

        ClassLoader loader = ClassLoaderActivity.class.getClassLoader();
        while( loader != null){
            LogUtil.e(loader.toString());
            loader = loader.getParent();
        }
        // 输出：
        // E/LogUtil ClassLoaderActivity:  test_1: dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/base.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_dependencies_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_resources_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_0_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_1_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_2_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_3_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_4_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_5_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_6_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_7_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_8_apk.apk", zip file "/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_9_apk.apk"],nativeLibraryDirectories=[/data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/base.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_dependencies_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_resources_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_0_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_1_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_2_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_3_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_4_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_5_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_6_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_7_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_8_apk.apk!/lib/x86, /data/app/com.guohao.anything-x7oFPP0hmf2wYaEBIenYHA==/split_lib_slice_9_apk.apk!/lib/x86, /system/lib, /vendor/lib]]]
        // E/LogUtil ClassLoaderActivity:  test_1: java.lang.BootClassLoader@768f9fa
        // 主要就是两个类加载其：
        // dalvik.system.PathClassLoader
        // java.lang.BootClassLoader
    }

}

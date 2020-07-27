package com.guohao.anything;

import android.app.IntentService;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class EnterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        // begin 借地一用

        ByteArrayOutputStream byteArrayOutputStream;

        IntentService intentService;

        AtomicInteger atomicInteger;

        EnterActivity.class.getName();
        EnterActivity.class.getClass().getName();
        EnterActivity.class.getCanonicalName();
        EnterActivity.class.getSimpleName();


        Canvas canvas = new Canvas();
        canvas.translate(1,1);
        //canvas.drawBitmap(bitmap, new Matrix(), null);

        SparseArray<String> array = new SparseArray<>(2);
        //array.put(65530,"123");
        array.put(1500,"123");
        array.put(3000,"123");
        array.put(4500,"123");
        array.put(4500,"123");
        // end 借地一用


    }

    public void test_1(View view){
        //Log.e("12345678901234567890123456789012345678901234567890123456789012345678901234567890","123");
        //(this,getPackageName());
        Log.e("123","" + EnterActivity.class.getName());
        Log.e("123","" + EnterActivity.class.getClass().getName());
        Log.e("123","" + EnterActivity.class.getCanonicalName());
        Log.e("123","" + EnterActivity.class.getSimpleName());
    }

    public static void a(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {

            packageInfo = packageManager.getPackageInfo(
                    packageName, PackageManager.GET_ACTIVITIES);

            for (ActivityInfo activity : packageInfo.activities) {
                String acn = activity.name; //activity名称
                Class<?> clazz= Class.forName(acn);
                LogUtil.e("name = " + clazz.getName());
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

}

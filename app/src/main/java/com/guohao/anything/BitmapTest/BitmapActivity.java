package com.guohao.anything.BitmapTest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.guohao.anything.R;
import com.guohao.utils.BitmapUtil;

public class BitmapActivity extends AppCompatActivity {

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        mImageView = findViewById(R.id.imageView);

        initData();
    }

    private void initData(){
        //bg_src_morning.jpg
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bg_src_morning);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.DONUT) {
//            Drawable drawable = new BitmapDrawable(getResources(),bitmap);
//        }

        Bitmap blurBitma = BlurBitmapUtil.blurBitmap(this,bitmap,15);
        //mImageView.setImageBitmap(blurBitma);
    }

}

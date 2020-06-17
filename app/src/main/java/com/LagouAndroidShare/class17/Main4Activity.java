package com.LagouAndroidShare.class17;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.guohao.anything.R;
import com.squareup.picasso.Picasso;

public class Main4Activity extends AppCompatActivity {

    String pic_path = "http://app.xrlaxs.com/upload/image/20190426/1556244587404435.jpg";
    String pic_path2= "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1590338517491&di=aa7c645291b70f150f60f33c236783f0&imgtype=0&src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201312%2F06%2F211410sxjtbyaj9abo5qzh.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

    }

    public void click(View c){
        ImageView imageView = findViewById(R.id.image);
        PieImageView pieImageView = findViewById(R.id.image2);

//        Picasso.get()
//                .load("https://s0.lgstatic.com/i/image3/M01/07/F1/CgoCgV6ipL2ADfX5AABS0mM49Mo779.jpg")
//                .resize(50, 50)
//                .centerCrop()
//                .into(imageView);



        Picasso picasso = SquareUtils.getPicasso(getBaseContext(),new SquareUtils.ProgressListener(){

            @Override
            public void update(int percent) {
                Log.e("SquareUtils","percent = " + percent);
                Log.e("SquareUtils","update：" + Thread.currentThread().getName());//这是在子线程哦

                //主线程更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("SquareUtils","runOnUiThread：" + Thread.currentThread().getName());//这是在主线程哦
                        pieImageView.setProgress(percent);
                    }
                });

            }

        },getApplication());

        picasso.load(pic_path2)
                //.resize(50, 50)
                //.centerCrop()
                //.placeholder(R.drawable.bg_circle_40)
                .into(pieImageView);
    }
}

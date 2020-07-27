package com.guohao.anything.glideTest;

import android.media.MediaPlayer;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.guohao.anything.R;

import org.java_websocket.client.WebSocketClient;

public class GlideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = new ImageView(this);

        Glide.with(this).load("").into(imageView);

    }
}

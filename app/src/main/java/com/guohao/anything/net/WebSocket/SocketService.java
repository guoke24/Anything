package com.guohao.anything.net.WebSocket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class SocketService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    WebSocketClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            client = new WebSocketClient(new URI("填入与后台连接的url")) {

                @Override
                public void onWebsocketPong(WebSocket conn, Framedata f) {
                    super.onWebsocketPong(conn, f);
//                    LogUtils.showLog(f.getPayloadData().toString());
//                    LogUtils.showLog("socket ping"+f.getOpcode());
                }

                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    // 连接打开以后的回调

                }


                @Override
                public void onMessage(String message) {
                    // 收到消息的回调
                    //LogUtils.showLog("socket接收服务器发来的消息：" + message);
                }

                @Override
                public void onError(Exception ex) {
                    // 连接出错的回调
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    // 连接关闭的回调， remote如果为true表示被服务端cut掉了

                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    // 返回的字节流消息
                    //LogUtils.showLog("socket bytebuffer bytes");
                }
            };

            client.connect();
        } catch (Exception e) {

        }
    }


    /**
     * 重连websocket
     * 此方法在handler中调用，由于重新连接websocket的线程如果与原先连接websocket的线程相同，会报异常，
     * 所以切换到HandlerThread的线程中重连
     */
//    private void reConnectWebSocket() {
//        if (null != client && !client.isOpen() && !isReConnecting) {
//            //LogUtils.showLog("socket onStartConnect");
//            client.reconnect();
//        }
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            //LogUtils.showLog("socket service onDestroy()");
            client.close();
        } catch (Exception e) {

        }
    }


}
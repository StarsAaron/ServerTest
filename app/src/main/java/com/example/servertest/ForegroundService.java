package com.example.servertest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * 前台进程
 */
public class ForegroundService extends Service {
    public static final String TAG = "ForegroundService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        Log.d(TAG, "ForegroundService thread id is " + Thread.currentThread().getId());

        // 设置了点击通知后就打开MainActivity
        Intent notificationIntent = new Intent(ForegroundService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = null;
        // 低于 API 11 写法
//        notification = new Notification(R.mipmap.ic_launcher,"有通知到来", System.currentTimeMillis());
//        notification.setLatestEventInfo(this, "这是通知的标题", "这是通知的内容",pendingIntent);

        // 高于 API 11 低于 API 16
        Notification.Builder builder = new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("有通知到来")
                .setContentText("describe")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true);
        notification = builder.getNotification();

        // 高于 API 16
//        notification = new Notification.Builder(this)
//                .setAutoCancel(true)
//                .setContentTitle("有通知到来")
//                .setContentText("describe")
//                .setContentIntent(pendingIntent)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setWhen(System.currentTimeMillis())
//                .setOngoing(true)
//                .build();

        // 调用startForeground()方法就可以让MyService变成一个前台Service
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 销毁一些不再使用的资源
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

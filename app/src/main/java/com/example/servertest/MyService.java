package com.example.servertest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * 没使用aidl跨进程通讯
 */
public class MyService extends Service {
    public static final String TAG = "MyService";
    private MyBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        Log.d(TAG, "MyService thread id is " + Thread.currentThread().getId());
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
        //因为当前服务跟Activity是在同一个进程里，耗时操作应该另起线程处理，避免ANR
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "startDownload() executed");
                Log.d(TAG, "Download thread id is " + Thread.currentThread().getId());
                // 执行具体的下载任务
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class MyBinder extends Binder {

        public void startDownload() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "startDownload() executed");
                    Log.d(TAG, "Download thread id is " + Thread.currentThread().getId());
                    // 执行具体的下载任务
                }
            }).start();
        }

    }
}

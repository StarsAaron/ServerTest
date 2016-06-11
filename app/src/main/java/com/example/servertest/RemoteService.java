package com.example.servertest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * 远程服务
 *
 * Activity和Service运行在两个不同的进程当中，这时就不能再使用传统的建立关联的方式，要使用aidl跨进程通讯
 * 在清单文件中为该服务添加 android:process=":remote"属性，可以变成远程服务，另起进程
 *
 * 其他应用想访问这个Server，先在清单文件设置该server可以处理的action
 * ，然后把aidl文件复制过去工程目录，注意要将aidl文件原有的包路径一起拷贝，
 * 其他的写法都一样，只是要使用隐式Intent
 * Intent intent = new Intent("com.example.servicetest.MyAIDLService");
 * bindService(intent, serviceConnection, BIND_AUTO_CREATE);
 *
 * 注意：由于这是在不同的进程之间传递数据，Android对这类数据的格式支持是非常有限的
 * ，基本上只能传递Java的基本数据类型、字符串、List或Map等。那么如果我想传递一个自
 * 定义的类该怎么办呢？这就必须要让这个类去实现Parcelable接口，并且要给这个类也定义一个同名的AIDL文件。
 */
public class RemoteService extends Service {
    public static final String TAG = "RemoteService";

    private IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {
        @Override
        public int plus(int a, int b) throws RemoteException {
            return a+b;
        }

        @Override
        public String toUpperCase(String str) throws RemoteException {
            return str.toUpperCase();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        Log.d(TAG, "RemoteService thread id is " + Thread.currentThread().getId());
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
        return mBinder;
    }
}

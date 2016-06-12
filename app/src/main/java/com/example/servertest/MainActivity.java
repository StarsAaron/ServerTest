package com.example.servertest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * 减少服务被杀概率
 * 1、提升server进程优先级,在AndroidManifest.xml文件中对于intent-filter
 * 可以通过android:priority = "1000"这个属性设置最高优先级，1000是最高值
 * ，如果数字越小则优先级越低，同时适用于广播。
 * 2、使用startForeground(int, Notification)方法来将service设置为前台状态
 * 3、onStartCommand方法，返回START_STICKY
 * 4、service +broadcast 方式
 * 5、清单文件的 Application 标签添加 android:persistent="true" 属性，
 * 同时符合FLAG_SYSTEM(app放在/system/app目录)及FLAG_PERSISTENT(android:persistent="true")
 * 才不会被lowmemorykiller处理，单设置该属性无效。
 *
 * http://blog.csdn.net/u012861467/article/details/51646935
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = "MainActivity";
    private Button btn_start,btn_close,btn_bind,btn_unbind;
    private MyService myService;
    private ServiceConnection serviceConnection;
    private MyService.MyBinder myBinder;
    private IMyAidlInterface iMyAidlService;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity thread id is " + Thread.currentThread().getId());

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_close = (Button)findViewById(R.id.btn_close);
        btn_bind = (Button)findViewById(R.id.btn_bind);
        btn_unbind = (Button)findViewById(R.id.btn_unbind);
        btn_start.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        btn_bind.setOnClickListener(this);
        btn_unbind.setOnClickListener(this);

        // 简单服务
        intent = new Intent(MainActivity.this,MyService.class);
        // 前台服务
//        intent = new Intent(MainActivity.this,ForegroundService.class);
        // 远程服务 AIDL 跨进程通讯
//        intent = new Intent(MainActivity.this,RemoteService.class);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                // 简单服务
                myBinder = (MyService.MyBinder) iBinder;
                myBinder.startDownload();

                //AIDL 跨进程通讯
                iMyAidlService = IMyAidlInterface.Stub.asInterface(iBinder);
                try {
                    int result = iMyAidlService.plus(3, 5);
                    String upperStr = iMyAidlService.toUpperCase("hello world");
                    Log.d("iMyAidlService", "result is " + result);
                    Log.d("iMyAidlService", "upperStr is " + upperStr);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

    }

    @Override
    public void onClick(View view) {
        // 销毁服务要满足1、与Activity断开关联 2、处于停止状态
        switch(view.getId()){
            case R.id.btn_start:
                //服务默认跟Activity在同一个进程
                startService(intent);//startService 开启的服务要使用stopService 关闭
                break;
            case R.id.btn_close:
                Log.d(TAG, "click Stop Service button");
                stopService(intent);
                break;
            case R.id.btn_bind:
                bindService(intent,serviceConnection,BIND_AUTO_CREATE); //bindService 要记得unbindService

//                // 跨应用调用写法，隐式
                intent = new Intent("com.example.servicetest.MyAIDLService");
                bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind:
                Log.d(TAG, "click Unbind Service button");
                unbindService(serviceConnection);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity destroy");
        super.onDestroy();
    }

    private boolean mIsExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                },2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

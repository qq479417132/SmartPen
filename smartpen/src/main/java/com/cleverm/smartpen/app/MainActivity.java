package com.cleverm.smartpen.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.cleverm.smartpen.R;
import com.cleverm.smartpen.service.ScreenLockListenService;
import com.cleverm.smartpen.service.penService;

public class MainActivity extends BaseActivity implements penService.MessageListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private penService mpenService;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mpenService = ((penService.penServiceBind) service).getService();
            mpenService.setMessageListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ScreenLockListenService.ScreenLockListenServiceStub mStub;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mStub = (ScreenLockListenService.ScreenLockListenServiceStub) service;
            mStub.setWindow(getWindow());
            mStub.setTaskId(getTaskId());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bindService(new Intent(this,penService.class), mConn, BIND_AUTO_CREATE);
        bindService(new Intent(this, ScreenLockListenService.class), mConnection, BIND_AUTO_CREATE);
    }

    public void eDriver(View v){
        startActivity(new Intent(this, DriverActivity.class));
    }

    public void Demo(View v){
        startActivity(new Intent(this, DemoActivity.class));
    }


    public void pay(View v){

    }

    public void service(View v){
        startActivity(new Intent(this, EvaluateActivity.class));
    }

    public void Future(View v){
        startActivity(new Intent(this, FutureActivity.class));
    }

    public void Video(View v){
        startActivity(new Intent(this, VideoActivity.class));
    }

    public void SelectTable(View v){
        startActivity(new Intent(this, SelectTableActivity.class));
    }

    @Override
    public void receiveData(int id) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
        unbindService(mConnection);
    }

}

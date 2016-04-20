package com.cleverm.smartpen.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cleverm.smartpen.app.DemoActivity;
import com.cleverm.smartpen.app.DiscountActivity;
import com.cleverm.smartpen.app.DriverActivity;
import com.cleverm.smartpen.app.EvaluateActivity;
import com.cleverm.smartpen.app.FutureActivity;
import com.cleverm.smartpen.app.GameActivity;
import com.cleverm.smartpen.app.LocalDiscountActivity;
import com.cleverm.smartpen.app.PayActivity;
import com.cleverm.smartpen.app.SelectTableActivity;
import com.cleverm.smartpen.app.VideoActivity;
import com.cleverm.smartpen.bean.TemplateIDState;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.IntentUtil;
import com.cleverm.smartpen.util.NetWorkUtil;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.ScanUtil;
import com.cleverm.smartpen.util.StatisticsUtil;
import com.cleverm.smartpen.util.WeakHandler;

import java.util.HashMap;

/**
 * Created by 95 on 2015/12/21.
 */
public class penService extends Service implements WandAPI.OnScanListener, WandAPI.onConnectListener {

    public static final String DEMO = "DEMO";


    public static final String TAG = penService.class.getSimpleName();
    private WandAPI mWandAPI;
    private MessageListener messageListener;
    public static String mActivityFlag = "VideoActivity";
    public static final String VIDEO_ACTIVITY_KEY = "video_activity_key";
    public static final String VIDEO_ACTIVITY_ISSEND = "video_activity_isSend";
    public static final String WEATHER = "weather";
    public static final String HEADLINE = "headline";
    public static final String HAPPY = "happy";
    public static final String SHOP = "shop";
    public static final String DISCOUNT = "discount";
    public static final String MAGAZINE = "magazine";
    public static final String VIDEO_ENTERTAINMENT = "video_entertainment";
    public static final String UN_KNOW = "un_know";
    public static final String GAME_ACTIVITY = "game_activity";
    public static final String GAME_URL = "game_url";

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }



    @Override
    public void onScan(int id) {
        ScanUtil.getInstance().onScan(penService.this,id,true);
    }

    public void setActivityFlag(String flag) {
        mActivityFlag = flag;
    }

    public String getActivityFlag() {
        return mActivityFlag;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWandAPI = new WandAPI(this, this);
        mWandAPI.onCreate();
        mWandAPI.setOnConnectListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new penServiceBind();
    }


    @Override
    public void onDestroy() {
        mWandAPI.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onDisconnect() {
        QuickUtils.sendSMSToService(Constant.PEN_PULL_OUT);
    }

    public class penServiceBind extends Binder {
        public penService getService() {
            return penService.this;
        }
    }


    public interface MessageListener {
        void receiveData(int id, boolean isSend);
    }









}

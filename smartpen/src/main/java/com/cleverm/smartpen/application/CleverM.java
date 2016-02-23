package com.cleverm.smartpen.application;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.cleverm.smartpen.database.DatabaseHelper;
import com.cleverm.smartpen.log.CrashHandler;
import com.cleverm.smartpen.modle.TableType;
import com.cleverm.smartpen.modle.impl.TableImpl;
import com.cleverm.smartpen.modle.impl.TableTypeImpl;
import com.cleverm.smartpen.service.CommunicationService;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.RememberUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 95 on 2016/1/13.
 */
public class CleverM extends Application {

    private static Application application;

    public static final String TAG = CleverM.class.getSimpleName();
    public static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/logFile/log";
    private static final String PREFS_NAME = "com.Clever.myapp";

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        CrashHandler.getInstance().init(this, PATH);

        MobclickAgent.setCatchUncaughtExceptions(true);
        RememberUtil.init(getApplicationContext(), PREFS_NAME);


        initNet();
    }

    public static Application getApplication() {
        return application;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        onExit();
    }


    private void initNet() {
        Intent intent = new Intent(this, CommunicationService.class);
        intent.setAction(Constant.ACTION_CONNECT_SOCKET);
        startService(intent);
        Log.v(TAG, "initNet()");
    }

    private void closeNet() {
        Intent intent = new Intent(this, CommunicationService.class);
        intent.setAction(Constant.ACTION_CONNECT_SOCKET);
        stopService(intent);
    }

    public void onExit() {
        closeNet();
        System.exit(0);
    }

}

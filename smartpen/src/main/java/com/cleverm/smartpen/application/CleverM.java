package com.cleverm.smartpen.application;

import android.app.Application;
import android.os.Environment;

import com.cleverm.smartpen.log.CrashHandler;
import com.cleverm.smartpen.util.RememberUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 95 on 2016/1/13.
 */
public class CleverM extends Application {
    public static final String PATH= Environment.getExternalStorageDirectory().getPath()+"/logFile/log";
    private static final String PREFS_NAME = "com.Clever.myapp";
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this, PATH);

        MobclickAgent.setCatchUncaughtExceptions(true);
        RememberUtil.init(getApplicationContext(), PREFS_NAME);

    }

}

package com.cleverm.smartpen.application;

import android.app.Application;
import android.os.Environment;

import com.cleverm.smartpen.database.DatabaseHelper;
import com.cleverm.smartpen.log.CrashHandler;
import com.cleverm.smartpen.modle.TableType;
import com.cleverm.smartpen.modle.impl.TableImpl;
import com.cleverm.smartpen.modle.impl.TableTypeImpl;
import com.cleverm.smartpen.util.RememberUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 95 on 2016/1/13.
 */
public class CleverM extends Application {

    private static Application application;


    public static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/logFile/log";
    private static final String PREFS_NAME = "com.Clever.myapp";

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        CrashHandler.getInstance().init(this, PATH);

        MobclickAgent.setCatchUncaughtExceptions(true);
        RememberUtil.init(getApplicationContext(), PREFS_NAME);
        insertTableData();
    }

    public static Application getApplication() {
        return application;
    }

    /**
     * Terry Test
     */
    private void insertTableData() {
        DatabaseHelper databaseHelper = DatabaseHelper.getsInstance(this);
        databaseHelper.insertTableType(new TableTypeImpl(1L, "2 People", 2, 2, "1"));
        databaseHelper.insertTableType(new TableTypeImpl(2L, "3 People", 3, 3, "2"));
        databaseHelper.insertTableType(new TableTypeImpl(3L, "4 People", 4, 4, "3"));
        databaseHelper.insertTableType(new TableTypeImpl(4L, "5 People", 5, 5, "4"));
        databaseHelper.insertTableType(new TableTypeImpl(5L, "6 People", 6, 6, "5"));

        databaseHelper.insertTable(new TableImpl(1L, 1L, "A01"));
        databaseHelper.insertTable(new TableImpl(12L, 1L, "A02"));
        databaseHelper.insertTable(new TableImpl(13L, 1L, "A03"));

        databaseHelper.insertTable(new TableImpl(21L, 2L, "B01"));
        databaseHelper.insertTable(new TableImpl(22L, 2L, "B02"));
        databaseHelper.insertTable(new TableImpl(23L, 2L, "B03"));
    }

}

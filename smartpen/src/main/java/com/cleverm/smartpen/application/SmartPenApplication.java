package com.cleverm.smartpen.application;

import android.app.Application;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;

import com.cleverm.smartpen.broadcast.PowerReceiver;
import com.cleverm.smartpen.log.CrashHandler;
import com.cleverm.smartpen.service.CommunicationService;
import com.cleverm.smartpen.statistic.dao.StatsDao;
import com.cleverm.smartpen.ui.windows.MyWindowManager;
import com.cleverm.smartpen.util.service.ApolloUtil;
import com.cleverm.smartpen.util.parts.DoBlePart;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.RememberUtil;
import com.cleverm.smartpen.util.StatisticsUtil;
import com.cleverm.smartpen.util.cache.FileRememberUtil;
import com.cleverm.smartpen.util.evnet.BroadcastEvent;
import com.cleverm.smartpen.util.service.PenUtil;
import com.cleverm.smartpen.util.service.ScreenLockUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.thin.downloadmanager.ThinDownloadManager;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 95 on 2016/1/13.
 */
public class SmartPenApplication extends Application {
    public static final String TAG=SmartPenApplication.class.getSimpleName();

    private static Application application;
    private static ThinDownloadManager thinDownloadManager;
    private static SQLiteDatabase db;
    private static StatsDao statsDao;

    public static final String PATH= Environment.getExternalStorageDirectory().getPath()+"/logFile/log";
    private static final String PREFS_NAME = "com.Clever.myapp";



    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        initNet();
        DoBlePart.openBLEPen(this);
        CrashHandler.getInstance().init(this, PATH);
        MobclickAgent.setCatchUncaughtExceptions(true);
        RememberUtil.init(getApplicationContext(), PREFS_NAME);
        FileRememberUtil.init(getApplicationContext());
        initImageLoader();
        initDownloader();
        initEvnet();
        initDataBase();
        PowerReceiver.getInstance().register(this);
    }



    private void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .cacheInMemory(false)
                .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.EXACTLY)// 设置图片的缩放方式:图像将被二次采样的整数倍
                .displayer(new FadeInBitmapDisplayer(300))// 正常显示一张图片,不是圆角RoundedBitmapDisplayer和渐变FadeInBitmapDisplayer
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).threadPriority(Thread.NORM_PRIORITY - 2)// 线程池内加载数量
                .denyCacheImageMultipleSizesInMemory()
                .discCacheSize(50 * 1024 * 1024)// 硬盘缓存的最大大小30MB
                .discCacheFileCount(150)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5
                        // 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(options).build();//LIFO队列

        ImageLoader.getInstance().init(config);
    }

    private void initDataBase() {
        StatisticsUtil.DaoReturnValue daoReturnValue = StatisticsUtil.getInstance().onInit(this);
        db=daoReturnValue.getDb();
        statsDao = daoReturnValue.getStatsDao();
    }

    private void initEvnet() {
        BroadcastEvent.init(this);
    }

    private void initDownloader() {
        this.thinDownloadManager = new ThinDownloadManager(4);
    }

    public static StatsDao getStatsDao() {
        return statsDao;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    public static ThinDownloadManager getThinDownloadManager() {
        return thinDownloadManager;
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
        PenUtil.getInstance().bindService(this);
        ScreenLockUtil.getInstance().bindService(this);
        ApolloUtil.getInstance().bindService(this);
    }

    private void closeNet() {
        Intent intent = new Intent(this, CommunicationService.class);
        intent.setAction(Constant.ACTION_CONNECT_SOCKET);
        stopService(intent);
        PenUtil.getInstance().unbindServcie(this);
        ApolloUtil.getInstance().unbindService(this);
        ScreenLockUtil.getInstance().unbindServcie(this);
    }

    public void onExit(){
        closeNet();
        PowerReceiver.getInstance().unregister(this);
        System.exit(0);
    }

}

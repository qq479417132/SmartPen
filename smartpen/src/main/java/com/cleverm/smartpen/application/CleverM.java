package com.cleverm.smartpen.application;

import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.bleframe.library.BleManager;
import com.bleframe.library.bundle.OnChangedBundle;
import com.bleframe.library.bundle.OnLeScanBundle;
import com.bleframe.library.bundle.OnWriteReadBundle;
import com.bleframe.library.bundle.onReadRemoteRssiBundle;
import com.bleframe.library.callback.BlatandAPICallback;
import com.bleframe.library.callback.SimpleBlatandAPICallback;
import com.bleframe.library.config.BleConfig;
import com.bleframe.library.log.BleLog;
import com.bleframe.library.profile.SmartPenProfile;
import com.bleframe.library.util.BleUtils;
import com.cleverm.smartpen.bean.TemplateIDState;
import com.cleverm.smartpen.log.CrashHandler;
import com.cleverm.smartpen.net.InfoSendSMSVo;
import com.cleverm.smartpen.net.RequestNet;
import com.cleverm.smartpen.service.CommunicationService;
import com.cleverm.smartpen.service.ScreenLockListenService;
import com.cleverm.smartpen.service.penService;
import com.cleverm.smartpen.statistic.dao.StatsDao;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.RememberUtil;
import com.cleverm.smartpen.util.ScanUtil;
import com.cleverm.smartpen.util.StatisticsUtil;
import com.cleverm.smartpen.util.TimerPlanUtil;
import com.cleverm.smartpen.util.Toastor;
import com.cleverm.smartpen.util.cache.FileRememberUtil;
import com.cleverm.smartpen.util.evnet.BroadcastEvent;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.thin.downloadmanager.ThinDownloadManager;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by 95 on 2016/1/13.
 */
public class CleverM extends Application {
    public static final String TAG=CleverM.class.getSimpleName();

    private static Application application;
    private static ThinDownloadManager thinDownloadManager;
    private static SQLiteDatabase db;
    private static StatsDao statsDao;

    public static final String PATH= Environment.getExternalStorageDirectory().getPath()+"/logFile/log";
    private static final String PREFS_NAME = "com.Clever.myapp";
    private boolean mSendLOWPOWER=true;
    public static final int LOW_POWER =20;
    public static final int LOW_POWER_ACTION = 6;
    public static final String SELECTEDTABLEID="SelectedTableId";
    private Object object=new Object();
    private PowerReceiver mPowerReceiver = new PowerReceiver();

    private static final String sOidText = "oid.txt";
    private static final String sPath= Environment.getExternalStorageDirectory().getAbsolutePath() + "/muyeoid";
    private static final String  sDefaultOid="001";
    private Toastor toastor;
    private TimerPlanUtil timer;


    private penService mpenService;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mpenService = ((penService.penServiceBind) service).getService();
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
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        initTostar();
        initNet();
        initWirelessPen();
        initWirelessTimer();
        CrashHandler.getInstance().init(this, PATH);
        MobclickAgent.setCatchUncaughtExceptions(true);
        RememberUtil.init(getApplicationContext(), PREFS_NAME);
        FileRememberUtil.init(getApplicationContext());
        initImageLoader();
        initDownloader();
        initEvnet();
        initDataBase();
        mPowerReceiver.register();
    }

    private void initWirelessTimer() {
        timer = new TimerPlanUtil.Builder()
                .listener(new TimerPlanUtil.OnTickListener() {
                    @Override
                    public void onTick(long timestampInMilliseconds) {
                        BleManager.getInstance().startTimerScan();
                    }
                })
                .looper(Looper.getMainLooper())
                .timerIntervalInSeconds(3)
                .build();
        timer.start();
    }

    private void initTostar() {
         toastor = new Toastor(this);
    }

    private void initWirelessPen() {
        String name= getOidName();
        BleConfig config = new BleConfig.Builder(this).
                setDefaultValue(false).
                setBluetoothName(name).
                setMaxScanTime(15000).
                setAutoConnect().
                setAutoScan().
                setAutoNotify().
                setConfigUUID(true).
                setProfile(new SmartPenProfile()).
                build();
        BleManager.getInstance().init(config);
        BleManager.getInstance().onCreate(this, wirelessCallback);

    }

    /**
     * 从muye-oid中读取值
     * 如果没有值,默认为Pen_001
     * @return
     */
    public String getOidName() {
        String oidName;
        try {
            oidName = BleUtils.rmSpecialSymbol("Pen_" + String.valueOf(getFileContext(sPath, sOidText, sDefaultOid).trim()));
        } catch (IOException e) {
            e.printStackTrace();
            oidName=sDefaultOid;
        }
        return oidName;
    }

        public BlatandAPICallback wirelessCallback = new SimpleBlatandAPICallback() {
        @Override
        public void onNotifyValue(OnChangedBundle onChangedBundle) {
            BleLog.e(TAG+"onNotifyValue="+onChangedBundle.getValue());
            getpenService().onScan(QuickUtils.hexToString(onChangedBundle.getValue()));
        }

        @Override
        public void onStartScan() {
            super.onStartScan();
            BleLog.e(TAG + "onStartScan");
        }

        @Override
        public boolean onDeviceFound(OnLeScanBundle info) {
            BleLog.e(TAG + "发现设备");
            return super.onDeviceFound(info);
        }

        @Override
        public void onScanTimeOut() {
            BleLog.e(TAG+"搜索超时");
            super.onScanTimeOut();
        }

        @Override
        public boolean onServiceDiscover(BluetoothGatt gatt) {
            toastor.showSingleLongToast("蓝牙服务发现");
            BleLog.e(TAG+"蓝牙服务发现");
            return super.onServiceDiscover(gatt);
        }

        @Override
        public void onWriteValue(OnWriteReadBundle info) {
            super.onWriteValue(info);
        }

        @Override
        public void onReadValue(OnWriteReadBundle info) {
            super.onReadValue(info);
        }

        @Override
        public void onConnectOff() {
            toastor.showSingleLongToast("断开连接");
            BleLog.e(TAG+"断开连接");
            super.onConnectOff();
        }

        @Override
        public void onConnectOn() {
            BleLog.e(TAG+"连接中");
            super.onConnectOn();
        }

        @Override
        public void onReadRemoteRssi(onReadRemoteRssiBundle info) {
            super.onReadRemoteRssi(info);
            BleLog.e(TAG + "onReadRemoteRssi"+info.getRssi());
        }
    };

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
        /**
         * 启动长连接
         */
        Intent intent = new Intent(this, CommunicationService.class);
        intent.setAction(Constant.ACTION_CONNECT_SOCKET);
        startService(intent);
        Log.v(TAG, "initNet()");


        bindService(new Intent(this, penService.class), mConn, BIND_AUTO_CREATE);
        bindService(new Intent(this, ScreenLockListenService.class), mConnection, BIND_AUTO_CREATE);
    }

    private void closeNet() {
        /**
         * 关闭长连接
         */
        Intent intent = new Intent(this, CommunicationService.class);
        intent.setAction(Constant.ACTION_CONNECT_SOCKET);
        stopService(intent);
        unbindService(mConn);
        unbindService(mConnection);
    }

    public void onExit(){
        closeNet();
        mPowerReceiver.unregister();
        System.exit(0);
    }

    public penService getpenService(){
        return mpenService;
    }

    public ScreenLockListenService.ScreenLockListenServiceStub getStub(){
        return mStub;
    }


    class PowerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v(TAG,"action="+action);
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                final  int powerLevel = intent.getIntExtra("level", 0);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging =((status == BatteryManager.BATTERY_STATUS_CHARGING)?true:false);
                Log.v(TAG, "powerLevel" + powerLevel+" isCharging="+isCharging+ "   action="+action);
                //没有在充电且电量少于20%则提示服务员
                if (powerLevel < LOW_POWER && isCharging==false) {
                    if(mSendLOWPOWER){
                        mSendLOWPOWER=false;
                        sendPowerWarning(powerLevel);
                        Log.v(TAG, "powerLevel=sendPowerWarning(powerLevel);");
                    }
                }else {
                    mSendLOWPOWER=true;
                    Log.v(TAG, "powerLevel" + powerLevel+" isCharging="+isCharging+ "   action===="+action);
                }
            }
        }

        public void register() {
            IntentFilter mFilter = new IntentFilter();
            mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(this, mFilter);
        }

        public void unregister() {
            unregisterReceiver(this);
        }
    };


    private void sendPowerWarning(int powerLevel) {
        long deskId = RememberUtil.getLong(SELECTEDTABLEID, Constant.DESK_ID_DEF_DEFAULT);;
        if(deskId==Constant.DESK_ID_DEF_DEFAULT){
            return;
        }
        final InfoSendSMSVo infoSendSMSVo = new InfoSendSMSVo();
        infoSendSMSVo.setTemplateID(LOW_POWER_ACTION);
        infoSendSMSVo.setTableID(deskId);
        synchronized (object){
            new Thread(){
                @Override
                public void run() {
                    RequestNet.getData(infoSendSMSVo);
                    Log.v(TAG,"sendPowerWarning=");
                }
            }.start();
        }
    }

    public String getFileContext(String directory,String txtName,String defalutOid) throws IOException {
        File file=new File(directory,txtName);
        File parent = file.getParentFile();
        if(parent!=null&&!parent.exists()){
            parent.mkdirs();
        }
        if(!file.exists()){
            file.createNewFile();
        }
        if(file.length()<=0){
            return defalutOid;
        }
        if(!file.exists()||file.isDirectory()) throw new FileNotFoundException();
        String result = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result = result + "\n" +s;
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }


    /**
     * ************************************************
     * 30s内呼叫结账二级菜单短信发送一次
     * ************************************************
     */
    private static HashMap<Integer, Boolean> mHashMap = new HashMap<Integer, Boolean>();

    public static TemplateIDState getTemplate(int templateID) {
        TemplateIDState templateIDState = new TemplateIDState();
        boolean isSend = getTemplateIDState(templateID);
        if (isSend) {
            templateIDState.setId(templateID);
            templateIDState.setIsSend(true);
        } else {
            setTemplateIDState(templateID);
            templateIDState.setId(templateID);
            templateIDState.setIsSend(false);
        }
        return templateIDState;
    }

    private static boolean getTemplateIDState(int TemplateID) {
        Boolean flag = mHashMap.get(TemplateID);
        if (flag == null) {
            return false;
        } else {
            return flag;
        }
    }

    private static void setTemplateIDState(int TemplateID) {
        mHashMap.put(TemplateID, true);
        mSMSHand.sendEmptyMessageDelayed(TemplateID, Constant.TEMPLATEID_DELAY);
    }

    private static Handler mSMSHand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.CASH_PAY_SMS: {
                    mHashMap.put(Constant.CASH_PAY_SMS, false);
                    break;
                }
                case Constant.UNION_CARD_PAY_SMS: {
                    mHashMap.put(Constant.UNION_CARD_PAY_SMS, false);
                    break;
                }
                default:{
                    break;
                }
            }
        }
    };

}

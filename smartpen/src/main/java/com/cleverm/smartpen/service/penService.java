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
import com.cleverm.smartpen.util.NetWorkUtil;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.StatisticsUtil;

import java.util.HashMap;

/**
 * Created by 95 on 2015/12/21.
 */
public class penService extends Service implements WandAPI.OnScanListener, WandAPI.onConnectListener {

    public static final String DEMO = "DEMO";


    public static final String TAG = penService.class.getSimpleName();
    private WandAPI mWandAPI;
    private MessageListener messageListener;
    private String mActivityFlag = "VideoActivity";
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
        Log.v(TAG, "onScan() id=" + id);
        if (id == 0) {
            return;
        }
        switch (id) {
            //5个服务
            case Constant.ORDER_DISHES1:
            case Constant.ADD_WATER1:
            case Constant.TISSUE1:
            case Constant.OTHER1:
            case Constant.ORDER_DISHES2:
            case Constant.ADD_WATER2:
            case Constant.TISSUE2:
            case Constant.OTHER2:
            case Constant.ORDER_DISHES3:
            case Constant.ADD_WATER3:
            case Constant.TISSUE3:
            case Constant.OTHER3:
            case Constant.ORDER_DISHES4:
            case Constant.ADD_WATER4:
            case Constant.TISSUE4:
            case Constant.OTHER4:
            case Constant.ORDER_DISHES5:
            case Constant.ADD_WATER5:
            case Constant.TISSUE5:
            case Constant.OTHER5:
            case Constant.CLEAN_DESK: {
                TemplateIDState templateIDState = ChoiceTemplateIDState(id);
                if (!"VideoActivity".equals(mActivityFlag)) {
                    Intent intent = new Intent(this, VideoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(VIDEO_ACTIVITY_KEY, templateIDState.getId());
                    intent.putExtra(VIDEO_ACTIVITY_ISSEND, templateIDState.isSend());
                    startActivity(intent);
                    mActivityFlag = "VideoActivity";
                } else {
                    if (NetWorkUtil.hasNetwork()) {
                        messageListener.receiveData(templateIDState.getId(), templateIDState.isSend());
                    } else {
                        QuickUtils.toast("网络异常，请直接找服务员～");
                    }
                }
                break;
            }
            //呼叫服务员
            case Constant.PAY1:
            case Constant.PAY2:
            case Constant.PAY3:
            case Constant.PAY4:
            case Constant.PAY5: {
                if (!"PayActivity".equals(mActivityFlag)) {
                    Intent intent = new Intent(this, PayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    mActivityFlag = "PayActivity";
                }
                break;
            }
            case Constant.DEMO1:
            case Constant.DEMO2:
            case Constant.DEMO3:
            case Constant.DEMO4:
            case Constant.DEMO5: {

                if (!"DemoActivity".equals(mActivityFlag)) {
                    Intent intent = new Intent(this, DemoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    mActivityFlag = "DemoActivity";
                }
                break;
            }
            case Constant.EVALUATE1:
            case Constant.EVALUATE2:
            case Constant.EVALUATE3:
            case Constant.EVALUATE4:
            case Constant.EVALUATE5: {
                if (!"EvaluateActivity".equals(mActivityFlag)) {
                    Intent intent = new Intent(this, EvaluateActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    mActivityFlag = "EvaluateActivity";
                }
                break;
            }
            case Constant.E_JIA1:
            case Constant.E_JIA2:
            case Constant.E_JIA3:
            case Constant.E_JIA4:
            case Constant.E_JIA5: {
                if (!"DriverActivity".equals(mActivityFlag)) {
                    Intent intent = new Intent(this, DriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    mActivityFlag = "DriverActivity";
                }
                break;
            }
            case Constant.SET1:
            case Constant.SET2:
            case Constant.SET3:
            case Constant.SET4:
            case Constant.SET5: {
                if (!"SelectTableActivity".equals(mActivityFlag)) {
                    Intent intent = new Intent(this, SelectTableActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    mActivityFlag = "SelectTableActivity";
                }
                break;
            }
            case Constant.YOU_HUI1:
            case Constant.YOU_HUI2:
            case Constant.YOU_HUI3:
            case Constant.YOU_HUI4:
            case Constant.YOU_HUI5: {
                if (!"DiscountActivity".equals(mActivityFlag)) {
                    Intent intent = new Intent(this, DiscountActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    mActivityFlag = "DiscountActivity";
                }
                break;
            }

            case Constant.RECOMMEND1:
            case Constant.RECOMMEND2:
            case Constant.RECOMMEND3:
            case Constant.RECOMMEND4:
            case Constant.RECOMMEND5: {
                if (!"LocalDiscountActivity".equals(mActivityFlag)) {
                    Intent intent = new Intent(this, LocalDiscountActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    mActivityFlag = "LocalDiscountActivity";
                }
                break;
            }
            case Constant.MO_JI1:
            case Constant.MO_JI2:
            case Constant.MO_JI3:
            case Constant.MO_JI4:
                //今日天气
            case Constant.MO_JI5: {
                LauncherApp(Constant.MO_JI_PACKAGE_NAME, StatisticsUtil.APP_WEATHER, StatisticsUtil.APP_WEATHER_DESC);
                mActivityFlag = WEATHER;
                break;
            }
            case Constant.TOU_TIAO1:
            case Constant.TOU_TIAO2:
            case Constant.TOU_TIAO3:
            case Constant.TOU_TIAO4:
            case Constant.TOU_TIAO5: {
                //今日头条
                LauncherApp(Constant.TOU_TIAO_PACKAGE_NAME, StatisticsUtil.APP_NEWS, StatisticsUtil.APP_NEWS_DESC);
                mActivityFlag = HEADLINE;
                break;
            }
            case Constant.BAI_DU1:
            case Constant.BAI_DU2:
            case Constant.BAI_DU3:
            case Constant.BAI_DU4:
            case Constant.BAI_DU5: {
                //周边玩乐
                LauncherApp(Constant.BAI_DU_PACKAGE_NAME, StatisticsUtil.APP_AROUNDPLAY, StatisticsUtil.APP_AROUNDPLAY_DESC);
                mActivityFlag = HAPPY;
                break;
            }
            case Constant.ONE_SHOP1:
            case Constant.ONE_SHOP2:
            case Constant.ONE_SHOP3:
            case Constant.ONE_SHOP4:
            case Constant.ONE_SHOP5: {
                //在线商场
                LauncherApp(Constant.ONE_SHOP_PACKAGE_NAME, StatisticsUtil.APP_ONLINEBUY, StatisticsUtil.APP_ONLINEBUY_DESC);
                mActivityFlag = SHOP;
                break;
            }
            case Constant.DA_ZONG1:
            case Constant.DA_ZONG2:
            case Constant.DA_ZONG3:
            case Constant.DA_ZONG4:
            case Constant.DA_ZONG5: {
                //周边优惠
                if (!DISCOUNT.equals(mActivityFlag)) {
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(GAME_URL, Constant.NEARBY_DISCOUNT_URL);
                    startActivity(intent);
                    mActivityFlag = DISCOUNT;
                }
                break;
            }
            case Constant.ZHI_ZHU1:
            case Constant.ZHI_ZHU2:
            case Constant.ZHI_ZHU3:
            case Constant.ZHI_ZHU4:
            case Constant.ZHI_ZHU5: {
                //电子杂志
                LauncherApp(Constant.ZHIZ_ZHU_PACKAGE_NAME, StatisticsUtil.APP_MAGAZINE, StatisticsUtil.APP_MAGAZINE_DESC);
                mActivityFlag = MAGAZINE;
                break;
            }
            case Constant.AMUSEMENTFRAGMENT1:
            case Constant.AMUSEMENTFRAGMENT2:
            case Constant.AMUSEMENTFRAGMENT3:
            case Constant.AMUSEMENTFRAGMENT4:
            case Constant.AMUSEMENTFRAGMENT5: {
                //视频娱乐
                LauncherApp(Constant.VIDEO_ENTERTAINMENT, StatisticsUtil.APP_VIDEO, StatisticsUtil.APP_VIDEO_DESC);
                mActivityFlag = VIDEO_ENTERTAINMENT;
                break;
            }
            case Constant.WEB1:
            case Constant.WEB2:
            case Constant.WEB3:
            case Constant.WEB4:
            case Constant.WEB5: {
                //手游试玩
                if (!"GAME_ACTIVITY".equals(mActivityFlag)) {
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(GAME_URL, Constant.URL);
                    startActivity(intent);
                    mActivityFlag = "GAME_ACTIVITY";
                }
                break;
            }
            //自主买单
            case Constant.TWO_DIMENSION_CODE1:
            case Constant.TWO_DIMENSION_CODE2:
            case Constant.TWO_DIMENSION_CODE3:
            case Constant.TWO_DIMENSION_CODE4:
            case Constant.TWO_DIMENSION_CODE5: {
                if (!"FutureActivity".equals(mActivityFlag)) {
                    Intent intent = new Intent(this, FutureActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(StatisticsUtil.FUTURE_INTNET_EVENTID, StatisticsUtil.SERVICE_BUY_MYSELF);
                    intent.putExtra(StatisticsUtil.FUTRUE_INTENT_EVENTDESC, StatisticsUtil.SERVICE_BUY_MYSELF_DESC);
                    startActivity(intent);
                    mActivityFlag = "FutureActivity";
                }
                break;
            }
            //火锅加汤
            case Constant.ADD_SOUP:
                //更换餐具
            case Constant.CHANGE_TABLEWARE:
                //打赏
            case Constant.AWARD:
                //金融
            case Constant.FINANCIAL:
                //未知功能
            case Constant.UN_KNOW1:
            case Constant.UN_KNOW2:
            case Constant.UN_KNOW3:
            case Constant.UN_KNOW4:{
                if (!UN_KNOW.equals(mActivityFlag)) {
                    Intent intent = new Intent(this, FutureActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(StatisticsUtil.FUTURE_INTNET_EVENTID, StatisticsUtil.UN_KNOW);
                    intent.putExtra(StatisticsUtil.FUTRUE_INTENT_EVENTDESC, StatisticsUtil.UN_KNOW_DESC);
                    startActivity(intent);
                    mActivityFlag = UN_KNOW;
                }
                break;
            }
        }
    }

    public void setActivityFlag(String flag) {
        mActivityFlag = flag;
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

    /**
     * ************************************************
     * 30s内短信发送一次
     * ************************************************
     */
    private TemplateIDState ChoiceTemplateIDState(int id) {
        TemplateIDState templateIDState = new TemplateIDState();
        int templateID = 0;
        switch (id) {
            case Constant.ORDER_DISHES1:
            case Constant.ORDER_DISHES2:
            case Constant.ORDER_DISHES3:
            case Constant.ORDER_DISHES4:
            case Constant.ORDER_DISHES5: {
                templateID = Constant.FOOD_ADD;
                break;
            }
            case Constant.ADD_WATER1:
            case Constant.ADD_WATER2:
            case Constant.ADD_WATER3:
            case Constant.ADD_WATER4:
            case Constant.ADD_WATER5: {
                templateID = Constant.WATER_ADD;
                break;
            }
            case Constant.PAY1:
            case Constant.PAY2:
            case Constant.PAY3:
            case Constant.PAY4:
            case Constant.PAY5: {
                templateID = Constant.PAY_MONRY;
                break;
            }
            case Constant.TISSUE1:
            case Constant.TISSUE2:
            case Constant.TISSUE3:
            case Constant.TISSUE4:
            case Constant.TISSUE5: {
                templateID = Constant.TISSUE_ADD;
                break;
            }
            case Constant.OTHER1:
            case Constant.OTHER2:
            case Constant.OTHER3:
            case Constant.OTHER4:
            case Constant.OTHER5: {
                templateID = Constant.OTHER_SERVICE;
                break;
            }
            case Constant.CLEAN_DESK: {
                templateID = Constant.CLEAN;
                break;
            }
            default: {
                break;
            }
        }
        boolean isSend = getTemplateIDState(templateID);
        if (isSend) {
            templateIDState.setId(id);
            templateIDState.setIsSend(true);
        } else {
            setTemplateIDState(templateID);
            templateIDState.setId(id);
            templateIDState.setIsSend(false);
        }
        return templateIDState;
    }

    private HashMap<Integer, Boolean> mHashMap = new HashMap<Integer, Boolean>();

    public void setTemplateIDState(int TemplateID) {
        mHashMap.put(TemplateID, true);
        mSMSHand.sendEmptyMessageDelayed(TemplateID, Constant.TEMPLATEID_DELAY);
    }

    public boolean getTemplateIDState(int TemplateID) {
        Boolean flag = mHashMap.get(TemplateID);
        if (flag == null) {
            return false;
        } else {
            return flag;
        }
    }

    private Handler mSMSHand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.FOOD_ADD: {
                    mHashMap.put(Constant.FOOD_ADD, false);
                    break;
                }
                case Constant.WATER_ADD: {
                    mHashMap.put(Constant.WATER_ADD, false);
                    break;
                }
                case Constant.TISSUE_ADD: {
                    mHashMap.put(Constant.TISSUE_ADD, false);
                    break;
                }
                case Constant.PAY_MONRY: {
                    mHashMap.put(Constant.PAY_MONRY, false);
                    break;
                }
                case Constant.OTHER_SERVICE: {
                    mHashMap.put(Constant.OTHER_SERVICE, false);
                    break;
                }
                case Constant.CLEAN: {
                    mHashMap.put(Constant.CLEAN, false);
                    break;
                }
            }
        }
    };


    private void LauncherApp(String packageName, int eventId, String eventDesc) {
        try {
            //弹跳APP的统计代码
            StatisticsUtil.getInstance().insert(eventId, eventDesc);
            String topActivityAppName = getTopActivityAppName();
            if(topActivityAppName.equals(packageName)){
                return;
            }
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Log.v(TAG, "no this APK packageName=" + packageName);
            e.printStackTrace();
            if (!"FutureActivity".equals(mActivityFlag)) {
                Intent intent = new Intent(this, FutureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(StatisticsUtil.FUTURE_INTNET_EVENTID, eventId);
                intent.putExtra(StatisticsUtil.FUTRUE_INTENT_EVENTDESC, eventDesc);
                startActivity(intent);
                mActivityFlag = "FutureActivity";
            }
        }
    }


    private String getTopActivityAppName(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        String packageName = info.topActivity.getPackageName(); //包名
        return packageName;
    }
}

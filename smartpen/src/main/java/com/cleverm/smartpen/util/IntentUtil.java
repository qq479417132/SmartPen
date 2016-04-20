package com.cleverm.smartpen.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import com.cleverm.smartpen.service.penService;

import java.util.HashMap;

/**
 * Created by xiong,An android project Engineer,on 2016/2/19.
 * Data:2016-02-19  13:41
 * Base on clever-m.com(JAVA Service)
 * Describe: Activity跳转帮助类
 * Version:1.0
 * Open source
 */
public class IntentUtil {

    /**
     * 从VideoActivity向其他的界面做跳转
     * @param activity 原Activity.this
     * @param clazz 要跳转的界面
     */
    public static void startPenddingActivity(Activity activity,Class clazz){
        Intent intent=new Intent(activity, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
    }

    public static void goCallService(Context service,int id,boolean isShouldListener){
        TemplateIDState templateIDState = ChoiceTemplateIDState(id);
        if (!"VideoActivity".equals(penService.mActivityFlag)) {
            Intent intent = new Intent(service, VideoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(penService.VIDEO_ACTIVITY_KEY, templateIDState.getId());
            intent.putExtra(penService.VIDEO_ACTIVITY_ISSEND, templateIDState.isSend());
            service.startActivity(intent);
            penService.mActivityFlag = "VideoActivity";
        } else {
            if (NetWorkUtil.hasNetwork()) {
                if(isShouldListener){
                    VideoActivity.getVideoActivity().receiveData(templateIDState.getId(), templateIDState.isSend());
                }else{
                   VideoActivity.getVideoActivity().receiveData(templateIDState.getId(), templateIDState.isSend());
                }


            } else {
                QuickUtils.toast("网络异常，请直接找服务员～");
            }
        }
    }

    /**
     * ************************************************
     * 30s内短信发送一次
     * ************************************************
     */
    private static TemplateIDState ChoiceTemplateIDState(int id) {
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


    private static HashMap<Integer, Boolean> mHashMap = new HashMap<Integer, Boolean>();

    public static void setTemplateIDState(int TemplateID) {
        mHashMap.put(TemplateID, true);
        mSMSHand.sendEmptyMessageDelayed(TemplateID, Constant.TEMPLATEID_DELAY);
    }

    public static boolean getTemplateIDState(int TemplateID) {
        Boolean flag = mHashMap.get(TemplateID);
        if (flag == null) {
            return false;
        } else {
            return flag;
        }
    }

    private static Handler mSMSHand = new Handler() {
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
    /**
     * ************************************************
     * 30s内短信发送一次
     * ************************************************
     */


    /**
     * 呼叫结账
     * @param service
     */
    public static void goToPayActivity(Context service){
        if (!"PayActivity".equals(penService.mActivityFlag)) {
            Intent intent = new Intent(service, PayActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            service.startActivity(intent);
            penService.mActivityFlag = "PayActivity";
        }
    }

    /**
     * Demo
     * @param service
     */
    public static void goToDemoActivity(Context service){
        if (!"DemoActivity".equals(penService.mActivityFlag)) {
            Intent intent = new Intent(service, DemoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            service.startActivity(intent);
            penService.mActivityFlag = "DemoActivity";
        }
    }

    public static void goToEvaluateActivity(Context service){
        if (!"EvaluateActivity".equals(penService.mActivityFlag)) {
            Intent intent = new Intent(service, EvaluateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            service.startActivity(intent);
            penService.mActivityFlag = "EvaluateActivity";
        }
    }

    /**
     * 代驾服务
     * @param service
     */
    public static void goToDriverActivity(Context service){
        if (!"DriverActivity".equals(penService.mActivityFlag)) {
            Intent intent = new Intent(service, DriverActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            service.startActivity(intent);
            penService.mActivityFlag = "DriverActivity";
        }
    }

    /**
     * 设置桌号
     * @param service
     */
    public static void goToSelectTableActivity(Context service){
        if (!"SelectTableActivity".equals(penService.mActivityFlag)) {
            Intent intent = new Intent(service, SelectTableActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            service.startActivity(intent);
            penService.mActivityFlag = "SelectTableActivity";
        }
    }


    /**
     * 优惠专区
     * @param service
     */
    public static void goToDiscountActivity(Context service){
        if(QuickUtils.checkDiscountEmptyData()){
            if (!"FutureActivity".equals(penService.mActivityFlag)) {
                Intent intent = new Intent(service, FutureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(StatisticsUtil.FUTURE_INTNET_EVENTID, StatisticsUtil.SERVICE_DISCOUNT);
                intent.putExtra(StatisticsUtil.FUTRUE_INTENT_EVENTDESC, StatisticsUtil.SERVICE_DISCOUNT_DESC);
                service.startActivity(intent);
                penService.mActivityFlag = "FutureActivity";
            }
        }else{
            if (!"DiscountActivity".equals(penService.mActivityFlag)) {
                Intent intent = new Intent(service, DiscountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                service.startActivity(intent);
                penService.mActivityFlag = "DiscountActivity";
            }
        }
    }

    /**
     * 本店推荐
     * @param service
     */
    public static void goToLocalDiscountActivity(Context service){
        if (!"LocalDiscountActivity".equals(penService.mActivityFlag)) {
            Intent intent = new Intent(service, LocalDiscountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            service.startActivity(intent);
            penService.mActivityFlag = "LocalDiscountActivity";
        }
    }

    /**
     * 启动APP
     */
    public static void goToLauncherApp(Context context,String packName,int statId,String statDesc){
        LauncherApp(context,packName,statId,statDesc);
        penService.mActivityFlag = packName;

    }

    /**
     * 手游试玩
     * @param context
     */
    public static void goToH5Game(Context context){
        //手游试玩
        if (!"GAME_ACTIVITY".equals(penService.mActivityFlag)) {
            Intent intent = new Intent(context, GameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(penService.GAME_URL, Constant.URL);
            context.startActivity(intent);
            penService.mActivityFlag = "GAME_ACTIVITY";
        }
    }

    public static void goToH5Round(Context context){
        if (!penService.DISCOUNT.equals(penService.mActivityFlag)) {
            Intent intent = new Intent(context, GameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(penService.GAME_URL, Constant.NEARBY_DISCOUNT_URL);
            context.startActivity(intent);
            penService.mActivityFlag = penService.DISCOUNT;
        }
    }

    public static void goToBuyMyself(Context context){
        if (!"FutureActivity".equals(penService.mActivityFlag)) {
            Intent intent = new Intent(context, FutureActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(StatisticsUtil.FUTURE_INTNET_EVENTID, StatisticsUtil.SERVICE_BUY_MYSELF);
            intent.putExtra(StatisticsUtil.FUTRUE_INTENT_EVENTDESC, StatisticsUtil.SERVICE_BUY_MYSELF_DESC);
            context.startActivity(intent);
            penService.mActivityFlag = "FutureActivity";
        }
    }

    public static void goToUnknow(Context context){
        if (!penService.UN_KNOW.equals(penService.mActivityFlag)) {
            Intent intent = new Intent(context, FutureActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(StatisticsUtil.FUTURE_INTNET_EVENTID, StatisticsUtil.UN_KNOW);
            intent.putExtra(StatisticsUtil.FUTRUE_INTENT_EVENTDESC, StatisticsUtil.UN_KNOW_DESC);
            context.startActivity(intent);
            penService.mActivityFlag = penService.UN_KNOW;
        }
    }

    private static void LauncherApp(Context context,String packageName, int eventId, String eventDesc) {
        try {
            //弹跳APP的统计代码
            StatisticsUtil.getInstance().insert(eventId, eventDesc);
            String topActivityAppName = getTopActivityAppName(context);

            if(isSameFlag(packageName)){
                return;
            }

            if(topActivityAppName.equals(packageName)){
                return;
            }

            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.v("LauncherApp", "no this APK packageName=" + packageName);
            e.printStackTrace();
            if (!"FutureActivity".equals(penService.mActivityFlag)) {
                Intent intent = new Intent(context, FutureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(StatisticsUtil.FUTURE_INTNET_EVENTID, eventId);
                intent.putExtra(StatisticsUtil.FUTRUE_INTENT_EVENTDESC, eventDesc);
                context.startActivity(intent);
                penService.mActivityFlag = "FutureActivity";
            }
        }
    }

    /**
     * 这个函数在台电平板中得到的总是点点笔package
     * @return
     */
    private static String getTopActivityAppName(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        String packageName = info.topActivity.getPackageName(); //包名
        return packageName;
    }

    private static boolean isSameFlag(String packageName){
        if(penService.mActivityFlag!=null||penService.mActivityFlag!=penService.UN_KNOW){
            if(packageName==penService.mActivityFlag){
                return true;
            }
        }
        return false;
    }

}

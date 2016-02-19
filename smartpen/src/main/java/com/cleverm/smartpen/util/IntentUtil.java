package com.cleverm.smartpen.util;

import android.app.Activity;
import android.content.Intent;

import com.cleverm.smartpen.app.DriverActivity;
import com.cleverm.smartpen.app.VideoActivity;

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
        Intent intent=new Intent(activity, DriverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
    }

}

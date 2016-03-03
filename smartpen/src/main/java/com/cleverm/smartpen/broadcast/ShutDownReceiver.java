package com.cleverm.smartpen.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.RememberUtil;

/**
 * Created by xiong,An android project Engineer,on 2016/3/2.
 * Data:2016-03-02  18:07
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class ShutDownReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
            Log.d("DEBUG", "关机自动服务自动启动...");
            RememberUtil.putBooleanSync(Constant.BROADCAST_RESATRT_EVENT, true);
        }
    }
}

package com.cleverm.smartpen.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.cleverm.smartpen.net.InfoSendSMSVo;
import com.cleverm.smartpen.net.RequestNet;
import com.cleverm.smartpen.util.Constant;
import com.cleverm.smartpen.util.RememberUtil;
import com.cleverm.smartpen.util.service.ApolloUtil;

/**
 * Created by xiong,An android project Engineer,on 17/5/2016.
 * Data:17/5/2016  下午 02:28
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class PowerReceiver extends BroadcastReceiver {

    private static final String TAG="PowerReceiver";

    private boolean mSendLOWPOWER=true;
    public static final int LOW_POWER =20;
    public static final int LOW_POWER_ACTION = 6;
    public static final String SELECTEDTABLEID="SelectedTableId";
    private Object object=new Object();

    private static PowerReceiver INSTANCE = new PowerReceiver();

    private PowerReceiver(){

    }

    public static PowerReceiver getInstance(){
        return INSTANCE;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "action=" + action);
        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            final  int powerLevel = intent.getIntExtra("level", 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging =((status == BatteryManager.BATTERY_STATUS_CHARGING)?true:false);
            Log.v(TAG, "powerLevel" + powerLevel+" isCharging="+isCharging+ "   action="+action);
            //没有在充电且电量少于20%则提示服务员
            if (powerLevel < LOW_POWER && isCharging==false) {
                if(mSendLOWPOWER){
                    mSendLOWPOWER=false;
                    sendPowerWarning();
                    Log.v(TAG, "powerLevel=sendPowerWarning(powerLevel);");
                }
            }else {
                mSendLOWPOWER=true;
                Log.v(TAG, "powerLevel" + powerLevel+" isCharging="+isCharging+ "   action===="+action);
            }
        }
    }

    public void register(Context context) {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(this, mFilter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }

    private void sendPowerWarning() {
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
}

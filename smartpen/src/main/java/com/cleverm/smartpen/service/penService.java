package com.cleverm.smartpen.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cleverm.smartpen.app.DemoActivity;
import com.cleverm.smartpen.app.DiscountActivity;
import com.cleverm.smartpen.app.DriverActivity;
import com.cleverm.smartpen.app.EvaluateActivity;
import com.cleverm.smartpen.app.FutureActivity;
import com.cleverm.smartpen.app.SelectTableActivity;
import com.cleverm.smartpen.app.VideoActivity;
import com.cleverm.smartpen.util.Constant;

import java.util.List;

/**
 * Created by 95 on 2015/12/21.
 */
public class penService extends Service implements WandAPI.OnScanListener {
    public static final String TAG = penService.class.getSimpleName();
    private WandAPI mWandAPI;
    private MessageListener messageListener;

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
        if(id==0){
           return;
        }
        switch (id) {
            //5个服务
            case Constant.ORDER_DISHES1:
            case Constant.ADD_WATER1:
            case Constant.PAY1:
            case Constant.TISSUE1:
            case Constant.OTHER1:
            case Constant.ORDER_DISHES2:
            case Constant.ADD_WATER2:
            case Constant.PAY2:
            case Constant.TISSUE2:
            case Constant.OTHER2:
            case Constant.ORDER_DISHES3:
            case Constant.ADD_WATER3:
            case Constant.PAY3:
            case Constant.TISSUE3:
            case Constant.OTHER3:
            case Constant.ORDER_DISHES4:
            case Constant.ADD_WATER4:
            case Constant.PAY4:
            case Constant.TISSUE4:
            case Constant.OTHER4:
            case Constant.ORDER_DISHES5:
            case Constant.ADD_WATER5:
            case Constant.PAY5:
            case Constant.TISSUE5:
            case Constant.OTHER5: {
                Intent intent = new Intent(this, VideoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                messageListener.receiveData(id);
                break;
            }
            case Constant.DEMO1:
            case Constant.DEMO2:
            case Constant.DEMO3:
            case Constant.DEMO4:
            case Constant.DEMO5: {
                Intent intent = new Intent(this, DemoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case Constant.EVALUATE1:
            case Constant.EVALUATE2:
            case Constant.EVALUATE3:
            case Constant.EVALUATE4:
            case Constant.EVALUATE5: {
                Intent intent = new Intent(this, EvaluateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case Constant.E_JIA1:
            case Constant.E_JIA2:
            case Constant.E_JIA3:
            case Constant.E_JIA4:
            case Constant.E_JIA5: {
                Intent intent = new Intent(this, DriverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case Constant.SET1:
            case Constant.SET2:
            case Constant.SET3:
            case Constant.SET4:
            case Constant.SET5: {
                Intent intent = new Intent(this, SelectTableActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case Constant.YOU_HUI1:
            case Constant.RECOMMEND1:
            case Constant.YOU_HUI2:
            case Constant.RECOMMEND2:
            case Constant.YOU_HUI3:
            case Constant.RECOMMEND3:
            case Constant.YOU_HUI4:
            case Constant.RECOMMEND4:
            case Constant.YOU_HUI5:
            case Constant.RECOMMEND5:{
                Intent intent = new Intent(this, DiscountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }

            case Constant.MO_JI1:
            case Constant.TOU_TIAO1:
            case Constant.BAI_DU1:
            case Constant.ONE_SHOP1:
            case Constant.DA_ZONG1:
            case Constant.ZHI_ZHU1:
            case Constant.TWO_DIMENSION_CODE1:
            case Constant.AMUSEMENTFRAGMENT1:
            case Constant.WEB1:

            case Constant.MO_JI2:
            case Constant.TOU_TIAO2:
            case Constant.BAI_DU2:
            case Constant.ONE_SHOP2:
            case Constant.DA_ZONG2:
            case Constant.ZHI_ZHU2:
            case Constant.TWO_DIMENSION_CODE2:
            case Constant.AMUSEMENTFRAGMENT2:
            case Constant.WEB2:

            case Constant.MO_JI3:
            case Constant.TOU_TIAO3:
            case Constant.BAI_DU3:
            case Constant.ONE_SHOP3:
            case Constant.DA_ZONG3:
            case Constant.ZHI_ZHU3:
            case Constant.TWO_DIMENSION_CODE3:
            case Constant.AMUSEMENTFRAGMENT3:
            case Constant.WEB3:

            case Constant.MO_JI4:
            case Constant.TOU_TIAO4:
            case Constant.BAI_DU4:
            case Constant.ONE_SHOP4:
            case Constant.DA_ZONG4:
            case Constant.ZHI_ZHU4:
            case Constant.TWO_DIMENSION_CODE4:
            case Constant.AMUSEMENTFRAGMENT4:
            case Constant.WEB4:

            case Constant.MO_JI5:
            case Constant.TOU_TIAO5:
            case Constant.BAI_DU5:
            case Constant.ONE_SHOP5:
            case Constant.DA_ZONG5:
            case Constant.ZHI_ZHU5:
            case Constant.TWO_DIMENSION_CODE5:
            case Constant.AMUSEMENTFRAGMENT5:
            case Constant.WEB5: {
                Intent intent = new Intent(this, FutureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
        }
    }

        @Override
        public void onCreate () {
            super.onCreate();
            mWandAPI = new WandAPI(this, this);
            mWandAPI.onCreate();
        }

        @Override
        public IBinder onBind (Intent intent){
            return new penServiceBind();
        }


        @Override
        public void onDestroy () {
            mWandAPI.onDestroy();
            super.onDestroy();
        }

        public class penServiceBind extends Binder {

            public penService getService() {
                return penService.this;
            }

        }


    private void gotoFront(String PackageName) {
        ActivityManager mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);

        for (ActivityManager.RunningTaskInfo rti : taskList) {
            Log.v(TAG, rti.topActivity.getPackageName() + "============");
            if (rti != null && rti.topActivity.getPackageName().equals(PackageName)) {
                Log.v(TAG, "######################");
                mAm.moveTaskToFront(rti.id, 0);
                return;
            }
        }
    }

    public interface MessageListener {
        void receiveData(int id);
    }


}

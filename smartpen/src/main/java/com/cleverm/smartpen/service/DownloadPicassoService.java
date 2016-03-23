package com.cleverm.smartpen.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.bean.DiscountAdInfo;
import com.cleverm.smartpen.bean.DiscountInfo;
import com.cleverm.smartpen.bean.DiscountRollInfo;
import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.ServiceUtil;
import com.cleverm.smartpen.util.WeakHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

/**
 * Created by xiong,An android project Engineer,on 2016/3/19.
 * Data:2016-03-19  12:01
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class DownloadPicassoService extends Service {

    public static final String PICASSO_JSON = "PICASSO_JSON";

    private String json = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getIntent(intent);
        startDownloader();
        return Service.START_REDELIVER_INTENT;
    }


    private void getIntent(Intent intent) {
        json = intent.getStringExtra(PICASSO_JSON);
    }


    /**
     * 定时1分钟后开启执行任务
     */
    private void startDownloader() {
        new WeakHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                parserJosn();
            }
        }, 60000);


    }

    private void parserJosn() {
        if (json != null) {
            try {
                List<DiscountInfo> discountInfos = ServiceUtil.getInstance().parserDiscountData(json);
                for (int i = 0; i < discountInfos.size(); i++) {
                    String  out_image = QuickUtils.spliceUrl(discountInfos.get(i).getPictruePath(),discountInfos.get(i).getQiniuPath());
                    QuickUtils.loadImage(out_image);
                    List<DiscountAdInfo> advertisementList = discountInfos.get(i).getAdvertisementList();
                    List<DiscountRollInfo> rollDetailList = discountInfos.get(i).getRollDetailList();
                    if (advertisementList != null && advertisementList.size() > 0) {
                        for (int j = 0; j < advertisementList.size(); j++) {
                            String image = QuickUtils.spliceUrl(advertisementList.get(j).getPictruePath(),advertisementList.get(j).getQiniuPath());
                            QuickUtils.loadImage(image);
                        }
                    }
                    if (rollDetailList != null && rollDetailList.size() > 0) {
                        for (int j = 0; j < rollDetailList.size(); j++) {
                            String image = QuickUtils.spliceUrl(rollDetailList.get(j).getPictruePath(),advertisementList.get(j).getQiniuPath());
                            QuickUtils.loadImage(image);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }


}

package com.cleverm.smartpen.app;

import android.content.Intent;

import com.cleverm.smartpen.application.CleverM;
import com.cleverm.smartpen.service.penService;
import com.cleverm.smartpen.util.StatisticsUtil;

/**
 * Created by xiong,An android project Engineer,on 2016/2/19.
 * Data:2016-02-19  11:25
 * Base on clever-m.com(JAVA Service)
 * Describe: 优惠专区
 * Version:1.0
 * Open source
 */
public class DiscountActivity extends BaseDiscountActivity{

    @Override
    protected boolean getSonDiscountArea() {
        return true;
    }

    @Override
    protected int onGetEventId() {
        return StatisticsUtil.SERVICE_DISCOUNT;
    }

    @Override
    protected String onGetDesc() {
        return StatisticsUtil.SERVICE_DISCOUNT_DESC;
    }

    @Override
    protected void onResume() {
        super.onResume();
        penService penService = ((CleverM) getApplication()).getpenService();
        if(penService!=null){
            penService.setActivityFlag("DiscountActivity");
        }
    }
}

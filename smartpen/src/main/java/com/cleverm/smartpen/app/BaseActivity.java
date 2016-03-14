package com.cleverm.smartpen.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cleverm.smartpen.util.StatisticsUtil;

/**
 * Created by 95 on 2016/2/3.
 */
public abstract class BaseActivity extends Activity {


    protected StatisticsUtil.TimeValue timeValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        hideKeyBord();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initSonIntent();
        timeValue = StatisticsUtil.getInstance().onCreate(onGetEventId(),onGetDesc());
    }

    /**
     * 子类复写,用于传递Intent的情况
     */
    protected void initSonIntent() {

    }

    protected abstract int onGetEventId();

    protected abstract String onGetDesc();

    private void hideKeyBord(){
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatisticsUtil.getInstance().onDestory(timeValue);
    }
}
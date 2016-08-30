package com.cleverm.smartpen.app;

import android.os.Bundle;

import com.cleverm.smartpen.R;

/**
 * Created by xiong,An android project Engineer,on 29/8/2016.
 * Data:29/8/2016  下午 06:56
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class LuckyDrawActivity extends BaseActivity {
    @Override
    protected int onGetEventId() {
        return 0;
    }

    @Override
    protected String onGetDesc() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_draw);

    }
}
